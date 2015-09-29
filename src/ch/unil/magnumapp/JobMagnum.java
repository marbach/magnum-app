/*
Copyright (c) 2013-2015 Daniel Marbach

We release this software open source under an MIT license (see below). If this
software was useful for your scientific work, please cite our paper available at:
http://regulatorycircuits.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package ch.unil.magnumapp;

import java.io.File;

import ch.unil.magnumapp.view.JobController;
import edu.mit.magnum.Magnum;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Runnable class for loading networks
 */
abstract public class JobMagnum extends Thread {

	/** This thread's personal magnum instance */
	protected Magnum myMag;
	/** This thread's personal logger */
	protected AppLogger myLog;
	
	/** The view controller of the dialog/alert of this thread */
	protected JobController jobManager;
	/** Write stdout to the console */
	protected TextArea console;
    
    /** Job name, used as basis for output filenames */
    protected String jobName;
    /** The runtime */
    protected long runtime = -1;

    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public JobMagnum(JobController jobManager, String jobName) {

		this.jobManager = jobManager;
		// Remove spaces for valid filenames
		this.jobName = jobName.replace(" ", "_");
	}


	// ----------------------------------------------------------------------------

	/** The main method called by the thread */
	@Override
	public void run() {
		
		// Note, I tried to group all the cleanup in a finally block at the end, but just
		// didn't get it to work because of weird variable scope problems --- so keep as is!
		try {
			// Personal logger
			myLog = new AppLogger();
			myLog.setVerbose(false); // TODO add a checkbox in the gui, note this also needs to be set in the settings file!
			
			File logFile = new File(jobManager.getOutputDir(), this.jobName + ".log.txt");
			myLog.createLogFile(logFile);
			if (console != null)
				myLog.setConsole(console);
			// Check for interrupts
			myLog.setCheckInterrupt(true);			
			// Personal Magnum
			myMag = new Magnum(null, myLog);
			
			// Do the job, take runtime
			runtime = -1;
			long t0 = System.currentTimeMillis();
			runJob();
			long t1 = System.currentTimeMillis();
			runtime = t1-t0;

		} catch (Exception e) {
			if (!jobManager.getInterrupted()) {
				// Print stack trace
				myLog.printStackTrace(e);
				myLog.closeLogFile();
				myMag = null;
				myLog = null;
				// Tell controller
				Platform.runLater(() ->	jobManager.jobFinished(this, e));
				return;
			}	
			// else we start the next job below
		
		} catch (OutOfMemoryError e) {
			// Print error
			myLog.printStackTrace(e);
			myLog.println("ERROR: OUT OF MEMORY!\n\n" +
					//"For large networks, Magnum requires a lot of memory because the kernels are dense matrices of size N*N, where N is the number of genes.\n\n" +
					"Solutions:\n" +
					"- reduce the number of cores (parallel jobs) or\n" +
					"- export settings and run jobs with the command-line tool (increase memory: e.g. \"-Xmx8g\" for 8GB)\n\n" +
					"See the user guide for further instructions.");
			myLog.closeLogFile();
			myMag = null;
			myLog = null;
			// TODO: Interrupt jobs or quit?
			// Tell controller
			Platform.runLater(() ->	jobManager.jobFinished(this, e));
			return;
        }
		
		// Cleanup
		myLog.closeLogFile();
		myMag = null;
		myLog = null;

		// Aha! Beautiful synchronization solution, this queues the update in the FX thread,
		// avoiding potential collision of multiple threads finishing at the same time!
		Platform.runLater(() ->	jobManager.jobFinished(this, null));
	}
	
	
	// ============================================================================
	// PROTECTED METHODS

	/** The method called by run() -- also has to extract all results, because myMag will be deleted after that to save space!!! */
	protected abstract void runJob();
	
	
	// ============================================================================
	// SETTERS AND GETTERS

	public void setController(JobController controller) {
		this.jobManager = controller;
	}

	public String getJobName() {
		return jobName;
	}
	
	public long getRuntime() {
		return runtime;
	}
	
	public void setConsole(TextArea console) {
		this.console = console;
	}

}
