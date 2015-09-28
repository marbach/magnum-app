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
import edu.mit.magnum.FileExport;
import edu.mit.magnum.MagnumLogger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;


/**
 * Logger supporting separate outputs for different threads
 */
public class AppLogger extends MagnumLogger {

	/** Set this flag if the thread should check for interrupt */
	private boolean checkInterrupt = false;
	
	/** Flag sets whether output is printed to System.out */
	private boolean systemOut = true;
	/** A text area / console */
	private TextArea console;
	/** A log file */
	private FileExport logFile;
//	/** A string copy of the log */
//	private String logCopy;
		
	
	// ============================================================================
	// PUBLIC METHODS

	/** Overrides MagnumLogger.print(): Write string to stdout, consoles and files */
	public void print(String msg) {

		// Exit the current thread if it was interrupted -- conveniently added here
		// because print() is called periodically... Disadvantage, we never do any
		// cleanup like closing files, but an interrupt is disruptive by definition.
		// A solution would be a static registry for anything that's closable, then
		// close all of them at the end...
		if (checkInterrupt && JobController.interrupted) {
			printAll("\nJOB INTERRUPTED!\n");
			throw new RuntimeException("Thread interrupted");
		}

		printAll(msg);
	}

	
	// ----------------------------------------------------------------------------

	/** Create a new log file and start writing to it */
	public void createLogFile(File file) {
		logFile = new FileExport(this, file);
	}
	
	/** Close the log file */
	public void closeLogFile() {
		logFile.close();;
	}

	
	// ----------------------------------------------------------------------------

//	/** Write logCopy to a file */
//	public void printLogCopy(File file) {
//		FileExport writer = new FileExport(this, file);
//		writer.print(logCopy);
//		writer.close();
//	}


	// ============================================================================
	// PRIVATE METHODS
	
	/** Print message to the different outputs */
	private void printAll(String msg) {
		
		if (systemOut)
			System.out.print(msg);
		
		if (console != null)
			Platform.runLater(() -> {
				console.appendText(msg);
			});
		
		if (logFile != null)
			logFile.print(msg);
		
//		if (logCopy != null)
//			logCopy += msg;
	}

	
	// ============================================================================
	// SETTERS AND GETTERS
	
	public void setCheckInterrupt(boolean checkInterrupt) {
		this.checkInterrupt = checkInterrupt;
	}


	public void setSystemOut(boolean systemOut) {
		this.systemOut = systemOut;
	}


	public void setConsole(TextArea console) {
		this.console = console;
	}
	
//	public void keepLogCopy() {
//		logCopy = "";
//	}
}
