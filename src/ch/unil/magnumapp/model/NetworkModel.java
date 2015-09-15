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

import java.io.File;
import java.nio.file.Path;

import edu.mit.magnum.MagnumUtils;
import edu.mit.magnum.net.Network;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Represents a network
 */
public class NetworkModel {

	/** Name used for display */
	private StringProperty name;
	
	/** Filename */
	private StringProperty filename;
	/** The file */
	private File file;
	/** Flag showing if file exists */
	private BooleanProperty fileExists;

	/** Notes */
	private StringProperty notes;
	
	/** Directed network */
	private BooleanProperty isDirected;
	/** Weighted network */
	private BooleanProperty isWeighted;
	/** Remove self */
	private BooleanProperty removeSelf;

	/** Number of regulators */
	private IntegerProperty numRegulators;
	/** Number of nodes */
	private IntegerProperty numNodes;
	/** Number of edges */
	private IntegerProperty numEdges;

	
	// ============================================================================
	// PUBLIC METHODS
	    
	/** Constructor with given network */
	public NetworkModel(Network network) {
		initialize(network);
	}

	
	/** Constructor initializing only the name (useful for root nodes in tree view) */
	public NetworkModel(String name) {
		this.name = new SimpleStringProperty(name);
		this.notes = new SimpleStringProperty();
	}

	
	/** Constructor initializing most fields */
	public NetworkModel(String name, String filename, boolean isDirected, boolean isWeighted, boolean removeSelf) {
		
		this.name = new SimpleStringProperty(name);
		this.filename = new SimpleStringProperty(filename);
		this.isDirected = new SimpleBooleanProperty(isDirected);
		this.isWeighted = new SimpleBooleanProperty(isWeighted);
		this.removeSelf = new SimpleBooleanProperty(removeSelf);
		this.notes = new SimpleStringProperty();
	}

	
	/** Constructor from file */
	public NetworkModel(File file, boolean isDirected, boolean isWeighted, boolean removeSelf) {
		
		this.name = new SimpleStringProperty(MagnumUtils.extractBasicFilename(file.getName(), false));
		this.filename = new SimpleStringProperty(file.getName());
		this.file = file;
		this.fileExists = new SimpleBooleanProperty(file.exists());
		this.isDirected = new SimpleBooleanProperty(isDirected);
		this.isWeighted = new SimpleBooleanProperty(isWeighted);
		this.removeSelf = new SimpleBooleanProperty(removeSelf);
	}

	
    // ----------------------------------------------------------------------------

	/** Set network and initialize fields of the model accordingly */
	public void initialize(Network network) {
		
		String name = MagnumUtils.extractBasicFilename(network.getFilename(), false);
		this.name = new SimpleStringProperty(name);
		filename = new SimpleStringProperty(network.getFilename());
		
		isWeighted = new SimpleBooleanProperty(network.getIsWeighted());
		isDirected = new SimpleBooleanProperty(network.getIsDirected());
		
		numRegulators = new SimpleIntegerProperty(network.getNumRegulators());
		numNodes = new SimpleIntegerProperty(network.getNumNodes());
		numEdges = new SimpleIntegerProperty(network.getNumEdges());
	}


    // ----------------------------------------------------------------------------

	/** Initializes the file given the directory and filename */
	public void initFile(Path directory) {

		setFile(directory.resolve(filename.get()).toFile());
	}
	
		
    // ----------------------------------------------------------------------------

	/** Set file, initialize fileExists and notes with error message */
	public void setFile(File file) {
		this.file = file;
		
		// Use notes property to display warning if file does not exist
		fileExists = new SimpleBooleanProperty(file.exists());
		if (!fileExists.get()) {
			if (isDirected == null) // hack to know if this is a group node
				notes.set("Directory not found");
			else
				notes.set("File not found");
		}
	}
	

	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS
	
	public String getName() { return name.getValue(); }
	public File getFile() { return file; }
	
	public StringProperty filenameProperty() { return filename; }
	public StringProperty nameProperty() { return name; }	
	public BooleanProperty fileExistsProperty() { return fileExists; }
	public StringProperty notesProperty() { return notes; }
	public IntegerProperty numRegulatorsProperty() { return numRegulators; }
	public IntegerProperty numNodesProperty() { return numNodes; }
	public IntegerProperty numEdgesProperty() { return numEdges; }
	public BooleanProperty isWeightedProperty() { return isWeighted; }
	public BooleanProperty isDirectedProperty() { return isDirected; }

}
