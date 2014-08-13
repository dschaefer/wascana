package ca.cdtdoug.wascana.arduino.ui.launch;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.progress.UIJob;

import ca.cdtdoug.wascana.arduino.core.launch.ArduinoLaunchConsoleService;

public class ArduinoLaunchConsole implements ArduinoLaunchConsoleService {
	
	private MessageConsole console;

	public ArduinoLaunchConsole() {
		console = new MessageConsole("Arduino Launch", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
	}
	
	@Override
	public void monitor(final Process process) {
		console.clearConsole();
		console.activate();

		new UIJob("Start Arduino Console") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IOConsoleOutputStream out = console.newOutputStream();
				out.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				new Thread("Arduino Launch Console Output") {
					public void run() {
						try (InputStream processOut = process.getInputStream()) {
							for (int c = processOut.read(); c >= 0; c = processOut.read()) {
								out.write(c);
							}
						} catch (IOException e) {
							// Nothing. Just exit
						}
					}
				}.start();

				final IOConsoleOutputStream err = console.newOutputStream();
				err.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				new Thread("Arduino Launch Console Output") {
					public void run() {
						try (InputStream processErr = process.getErrorStream()) {
							for (int c = processErr.read(); c >= 0; c = processErr.read()) {
								err.write(c);
							}
						} catch (IOException e) {
							// Nothing. Just exit
						}
					}
				}.start();

				return Status.OK_STATUS;
			}
		}.schedule();
	}

}