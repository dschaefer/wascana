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
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
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
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ArduinoProjectNature implements IProjectNature {

	private IProject project;
	public static final String ID = Activator.getId() + ".arduinoNature";
	
	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static boolean hasNature(IProject project) throws CoreException {
		IProjectDescription projDesc = project.getDescription();
		for (String id : projDesc.getNatureIds()) {
			if (id.equals(ID))
				return true;
		}
		return true;
	}

	public static void setupArduinoProject(IProject project, IProgressMonitor monitor) throws CoreException {
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
		ArduinoTarget target = targetRegistry.getActiveTarget();
		if (target == null) {
			ArduinoTarget[] targets = targetRegistry.getTargets();
			if (targets.length > 0)
				target = targets[0];
		}
		
		if (target != null) {
			target.createBuildConfigurationForTarget(cprojDesc);
		} else {
			String configId = ManagedBuildManager.calculateChildId(ArduinoLaunchDescriptorType.avrToolChainId, null);
			IToolChain avrToolChain = ManagedBuildManager.getExtensionToolChain(ArduinoLaunchDescriptorType.avrToolChainId);
			Configuration newConfig = new Configuration(mProj, (ToolChain) avrToolChain, configId, "uno");
			IToolChain newToolChain = newConfig.getToolChain();
			IOption newOption = newToolChain.getOptionBySuperClassId("ca.cdtdoug.wascana.arduino.core.option.board");
			ManagedBuildManager.setOption(newConfig, newToolChain, newOption, "uno");

			CConfigurationData data = newConfig.getConfigurationData();
			cprojDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
		}

		CCorePlugin.getDefault().setProjectDescription(project, cprojDesc, true, monitor);

		// Generate files
		try {
			freemarker.template.Configuration fmConfig = new freemarker.template.Configuration();
			URL templateDirURL = FileLocator.find(Activator.getContext().getBundle(), new Path("/templates"), null);
			fmConfig.setDirectoryForTemplateLoading(new File(FileLocator.toFileURL(templateDirURL).toURI()));

			final Map<String, Object> fmModel = new HashMap<>();
			fmModel.put("projectName", project.getName());
			
			generateFile(fmModel, fmConfig.getTemplate("Makefile"), project.getFile("Makefile"));
			generateFile(fmModel, fmConfig.getTemplate("arduino.cpp"), project.getFile(project.getName() + ".cpp"));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		} catch (URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		} catch (TemplateException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
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
	
	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

}
