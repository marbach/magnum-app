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

import edu.mit.magnum.Magnum;

/**
 * Runnable class for loading networks
 */
public class ThreadConnectivityEnrichment extends ThreadMagnum {

	/** The settings file for this job */
    private File settingsFile;
    
    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public ThreadConnectivityEnrichment(File settingsFile) {
		this.settingsFile = settingsFile;
	}

	
	// ----------------------------------------------------------------------------

	/** Main method called by the thread */
	@Override
	protected void runJob() {
		
		Magnum magnum = new Magnum();
		// Load settings file
		Magnum.set.loadSettings(settingsFile.getAbsolutePath(), false);
		// Run
		magnum.run();
	}
	
	
	// ============================================================================
	// SETTERS AND GETTERS



}
