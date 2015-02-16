package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionType;
import org.eclipse.remote.core.IRemoteServicesManager;
import org.eclipse.remote.core.launch.IRemoteLaunchConfigService;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.internal.launch.ArduinoLaunchConfigurationDelegate;
import ca.cdtdoug.wascana.arduino.core.internal.remote.ArduinoRemoteConnection;
import ca.cdtdoug.wascana.arduino.core.remote.Board;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoBoardManager;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SuppressWarnings("restriction")
public class ArduinoProjectGenerator {

	public static final String BOARD_OPTION_ID = "ca.cdtdoug.wascana.arduino.core.option.board";
	public static final String AVR_TOOLCHAIN_ID = "ca.cdtdoug.wascana.arduino.toolChain.avr";

	private final IProject project;
	private IFile sourceFile;
	
	public ArduinoProjectGenerator(IProject project) {
		this.project = project;
	}
	
	public void setupArduinoProject(IProgressMonitor monitor) throws CoreException {
		// create the CDT-ness of the project
		IProjectDescription projDesc = project.getDescription();
		CCorePlugin.getDefault().createCDTProject(projDesc, project, monitor);
		
		String[] oldIds = projDesc.getNatureIds();
		String[] newIds = new String[oldIds.length + 3];
		System.arraycopy(oldIds, 0, newIds, 0, oldIds.length);
		newIds[newIds.length - 1] = ArduinoProjectNature.ID;
		newIds[newIds.length - 2] = CCProjectNature.CC_NATURE_ID;
		newIds[newIds.length - 3] = CProjectNature.C_NATURE_ID;
		projDesc.setNatureIds(newIds);
		project.setDescription(projDesc, monitor);

		ICProjectDescription cprojDesc = CCorePlugin.getDefault().createProjectDescription(project, false);
		ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
		ManagedProject mProj = new ManagedProject(cprojDesc);
		info.setManagedProject(mProj);

		Board board = null;
		
		IRemoteServicesManager remoteManager = Activator.getService(IRemoteServicesManager.class);
		IRemoteLaunchConfigService remoteLaunchService = Activator.getService(IRemoteLaunchConfigService.class);
		IRemoteConnection remoteConnection = remoteLaunchService.getLastActiveConnection(ArduinoLaunchConfigurationDelegate.getLaunchConfigurationType());
		if (remoteConnection != null) {
			IArduinoRemoteConnection arduinoRemote = remoteConnection.getService(IArduinoRemoteConnection.class);
			board = arduinoRemote.getBoard();
		} else {
			IRemoteConnectionType connectionType = remoteManager.getConnectionType(ArduinoRemoteConnection.TYPE_ID);
			Collection<IRemoteConnection> connections = connectionType.getConnections();
			if (!connections.isEmpty()) {
				IRemoteConnection firstConnection = connections.iterator().next();
				IArduinoRemoteConnection firstArduino = firstConnection.getService(IArduinoRemoteConnection.class);
				board = firstArduino.getBoard();
			}
		}
		
		if (board == null) {
			IArduinoBoardManager boardManager = Activator.getService(IArduinoBoardManager.class);
			board = boardManager.getBoard("uno"); // the default
		}
		
		createBuildConfiguration(cprojDesc, board);

		CCorePlugin.getDefault().setProjectDescription(project, cprojDesc, true, monitor);

		// Generate files
		try {
			freemarker.template.Configuration fmConfig = new freemarker.template.Configuration();
			URL templateDirURL = FileLocator.find(Activator.getContext().getBundle(), new Path("/templates"), null);
			fmConfig.setDirectoryForTemplateLoading(new File(FileLocator.toFileURL(templateDirURL).toURI()));

			final Map<String, Object> fmModel = new HashMap<>();
			fmModel.put("projectName", project.getName());
			
			generateFile(fmModel, fmConfig.getTemplate("Makefile"), project.getFile("Makefile"));
			
			sourceFile = project.getFile(project.getName() + ".cpp");
			generateFile(fmModel, fmConfig.getTemplate("arduino.cpp"), sourceFile); 
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		} catch (URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		} catch (TemplateException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
		
		// Do the initial build
		project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
	}

	private static void generateFile(Object model, Template template, final IFile outputFile) throws TemplateException, IOException, CoreException {
		final PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);
		final Writer writer = new OutputStreamWriter(out);
		Job job = new Job("Write file") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					outputFile.create(in, true, monitor);
				} catch (CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(outputFile.getProject());
		job.schedule();
		template.process(model, writer);
		writer.close();
		try {
			job.join();
		} catch (InterruptedException e) {
			// TODO anything?
		}
		IStatus status = job.getResult();
		if (!status.isOK())
			throw new CoreException(status);
	}
	
	public static ICConfigurationDescription createBuildConfiguration(ICProjectDescription projDesc, Board board) throws CoreException {
		ManagedProject managedProject = new ManagedProject(projDesc);
		String configId = ManagedBuildManager.calculateChildId(AVR_TOOLCHAIN_ID, null);
		IToolChain avrToolChain = ManagedBuildManager.getExtensionToolChain(AVR_TOOLCHAIN_ID);
		org.eclipse.cdt.managedbuilder.internal.core.Configuration newConfig = new org.eclipse.cdt.managedbuilder.internal.core.Configuration(managedProject, (ToolChain) avrToolChain, configId, board.getId());
		IToolChain newToolChain = newConfig.getToolChain();
		IOption newOption = newToolChain.getOptionBySuperClassId(BOARD_OPTION_ID);
		ManagedBuildManager.setOption(newConfig, newToolChain, newOption, board.getId());

		CConfigurationData data = newConfig.getConfigurationData();
		return projDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
	}

	public static Board getBoard(IConfiguration configuration) throws CoreException {
		try {
			IToolChain toolChain = configuration.getToolChain();
			IOption boardOption = toolChain.getOptionBySuperClassId(BOARD_OPTION_ID);
			String boardId = boardOption.getStringValue();
			
			IArduinoBoardManager boardManager = Activator.getService(IArduinoBoardManager.class);
			Board board = boardManager.getBoard(boardId);
			if (board == null) {
				board = boardManager.getBoard("uno");
			}
			return board;
		} catch (BuildException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
		
	}

	public IFile getSourceFile() {
		return sourceFile;
	}
	
}
