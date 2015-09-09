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

import edu.mit.magnum.net.Network;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a group of networks displayed together (e.g., the 4 PPI nets, 
 * the tissue-specific nets, etc.), optionally with a clustering dendrogram.
 */
public class NetworkGroup {

	/** The directory where the networks are located */
	private StringProperty networkDir = null;

	/** All networks have been successfully loaded, statistics are set */
	private BooleanProperty networksLoaded = null;
	
	/** The network models */
	private ObservableList<NetworkModel> networks = null;
	
	
	// ============================================================================
	// PUBLIC METHODS
	    
	/** Constructor */
	public NetworkGroup() {

		networks = FXCollections.observableArrayList();
	}

	
    // ----------------------------------------------------------------------------

	/** Load the network, add the model (not the network!) to the collection */
	public void loadNetworkAddModel(File file, boolean directed, boolean removeSelf, boolean weighted) {
		
		String path = file.getAbsolutePath();
		Network network = new Network(path, directed, removeSelf, weighted, 0);
		NetworkModel networkModel = new NetworkModel(network);
		networks.add(networkModel);
	}
    



	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS

	public ObservableList<NetworkModel> getNetworks() {
		return networks;
	}

}
