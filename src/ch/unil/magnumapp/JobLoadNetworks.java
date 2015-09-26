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
import java.util.List;

import ch.unil.magnumapp.model.NetworkGroup;

/**
 * Runnable class for loading networks
 */
public class JobLoadNetworks extends JobMagnum {

	/** The network group where the files will be added */
	NetworkGroup networkGroup;
	
	/** Network files */
    private List<File> files;
    /** Directed network */
    private boolean directed;
    /** Weighted network */
    private boolean weighted;
    /** Remove self loops */
    private boolean removeSelf;
    
    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public JobLoadNetworks(NetworkGroup networkGroup, List<File> files, boolean directed, boolean weighted, boolean removeSelf) {

		super(null, "TBD!");
		this.networkGroup = networkGroup;
		this.files = files;
		this.directed = directed;
		this.weighted = weighted;
		this.removeSelf = removeSelf;
	}

	
	// ----------------------------------------------------------------------------

	/** Called by MagnumThread.run() */
	@Override
	protected void runJob() {

		for (File file : files)
			networkGroup.loadNetworkAddModel(file, directed, weighted, removeSelf);
	}
	
	
	// ============================================================================
	// SETTERS AND GETTERS



}
