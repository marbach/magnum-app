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
import edu.mit.magnum.Magnum;
import javafx.application.Platform;

/**
 * Runnable class for loading networks
 */
abstract public class ThreadMagnum extends Thread {

	/** The view controller of the dialog/alert of this thread */
	protected ThreadController controller;
	/** The id of this thread */
	protected long id;
    
    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public ThreadMagnum() {
		
	}


	// ----------------------------------------------------------------------------

	/** The main method called by the thread */
	@Override
	public void run() {
		
		try {
			id = Thread.currentThread().getId();
			runJob();
    			
		} catch (Exception e) {
			if (!controller.getInterrupted()) {
				Platform.runLater(() ->	controller.jobFinished(e));
				// Should the exception be thrown on...? Printed to console?
				return;
			}	
		}
		// Aha! Beautiful synchronization solution, this queues the update in the FX thread,
		// avoiding potential collision of multiple threads finishing at the same time!
		Platform.runLater(() ->	controller.jobFinished(null));
	}
	
	
	// ============================================================================
	// PROTECTED METHODS

	/** The method called by run() */
	protected abstract void runJob();
	
	
	// ============================================================================
	// SETTERS AND GETTERS

	public void setController(ThreadController controller) {
		this.controller = controller;
	}


}
