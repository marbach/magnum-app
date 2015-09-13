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

import ch.unil.magnumapp.view.ThreadController;
import edu.mit.magnum.MagnumLogger;


/**
 * Logger supporting separate outputs for different threads
 */
public class MagnumAppLogger extends MagnumLogger {

	/** The number of threads */
	//private int numThreads;
	/** The log file for each thread */
	//private FileExport[] logFiles;
	/** Manager of the threads, responsible for printing to consoles */
	private ThreadController threadController;

	
	// ============================================================================
	// STATIC FUNCTIONS


	
	// ============================================================================
	// PUBLIC METHODS
	    
	/** Constructor */
	public MagnumAppLogger(ThreadController threadController) {

		this.threadController = threadController;
	}


	// ----------------------------------------------------------------------------

	/** Overrides MagnumLogger.print(): Write string to stdout, consoles and files */
	public void print(String msg) {
		
		System.out.print(msg);
		threadController.print(msg);
	}

		
	// ============================================================================
	// PRIVATE METHODS


	
	// ============================================================================
	// SETTERS AND GETTERS

		
}
