package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
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
import org.osgi.util.tracker.ServiceTracker;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.launch.ArduinoLaunchDescriptorType;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;
import ca.cdtdoug.wascana.arduino.core.target.Board;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ArduinoProjectGenerator {

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
		
		ServiceTracker<ArduinoTargetRegistry, ArduinoTargetRegistry> targetRegistryServiceTracker = new ServiceTracker<>(Activator.getContext(), ArduinoTargetRegistry.class, null);
		targetRegistryServiceTracker.open();
		ArduinoTargetRegistry targetRegistry = targetRegistryServiceTracker.getService();
		Board board = null;
		
		if (targetRegistry.getActiveTarget() != null) {
			board = targetRegistry.getActiveTarget().getBoard();
		} else {
			ArduinoTarget[] targets = targetRegistry.getTargets();
			if (targets.length > 0)
				board = targets[0].getBoard();
		}
		
		if (board == null) {
			board = targetRegistry.getBoard("uno"); // the default
		}
		
		createBuildConfigurationForTarget(cprojDesc, board);

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
	
	public static ICConfigurationDescription createBuildConfigurationForTarget(ICProjectDescription projDesc, Board board) throws CoreException {
		ManagedProject managedProject = new ManagedProject(projDesc);
		String configId = ManagedBuildManager.calculateChildId(ArduinoLaunchDescriptorType.avrToolChainId, null);
		IToolChain avrToolChain = ManagedBuildManager.getExtensionToolChain(ArduinoLaunchDescriptorType.avrToolChainId);
		org.eclipse.cdt.managedbuilder.internal.core.Configuration newConfig = new org.eclipse.cdt.managedbuilder.internal.core.Configuration(managedProject, (ToolChain) avrToolChain, configId, board.getId());
		IToolChain newToolChain = newConfig.getToolChain();
		IOption newOption = newToolChain.getOptionBySuperClassId("ca.cdtdoug.wascana.arduino.core.option.board");
		ManagedBuildManager.setOption(newConfig, newToolChain, newOption, board.getId());

		CConfigurationData data = newConfig.getConfigurationData();
		return projDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
	}

	public IFile getSourceFile() {
		return sourceFile;
	}
	
}
