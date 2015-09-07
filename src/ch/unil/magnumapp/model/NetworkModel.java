/*
Copyright (c) 2013 Daniel Marbach

We release this software open source under an MIT license (see below). If this
software was useful for your scientific work, please cite our paper available at:
http://networkinference.org

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
package ch.unil.magnumapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a network
 */
public class NetworkModel {

	/** Filename */
	private StringProperty filename_ = null;
	/** Name used for display */
	private StringProperty name_ = null;

	/** Network has been successfully loaded, statistics are set */
	private BooleanProperty networkLoaded_ = null;
	/** Number of nodes */
	private IntegerProperty numNodes_ = null;
	/** Number of edges */
	private IntegerProperty numEdges_ = null;
	/** Weighted network */
	private BooleanProperty isWeighted_ = null;
	/** Directed network */
	private BooleanProperty isDirected_ = null;
	/** Number of regulators */
	private IntegerProperty numRegulators_ = null;

	/** The network */
	//private Network network_ = null;
	
	
	// ============================================================================
	// PUBLIC METHODS
	    
	/** Constructor */
	public NetworkModel() {

	}
	
	
    // ----------------------------------------------------------------------------

	/**  */
	public void myMethod() {
		
	}


	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS

	public StringProperty nameProperty() { return name_; }
		
}
