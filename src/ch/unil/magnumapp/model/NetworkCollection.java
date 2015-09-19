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
package ch.unil.magnumapp.model;

import java.io.File;

import javafx.scene.control.TreeItem;

/**
 * The collection of available networks
 */
public class NetworkCollection {

    /** My networks */
    private NetworkGroup myNetworks;
    /** PPI networks */
    private NetworkGroup ppiNetworks;

    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public NetworkCollection() {
		
		myNetworks = new NetworkGroup("My networks");
		initPpiNetworks();
	}

	
    // ----------------------------------------------------------------------------

	/** Get a tree representation of the network collection for the view */
	public TreeItem<NetworkModel> getNetworkTree() {

		// Network Collection (Note: if you change the name of this node, also update handleSelectionChange()
		TreeItem<NetworkModel> collection = new TreeItem<>(new NetworkModel("Network collection"));
		collection.getChildren().add(ppiNetworks.getTreeViewRoot());
		
		// Add both to a root node
		TreeItem<NetworkModel> root = new TreeItem<>(new NetworkModel("Root"));
		root.getChildren().add(myNetworks.getTreeViewRoot());
		root.getChildren().add(collection);
		
		return root;
	}

	
	/** Initialize the directory, subdirectories and network files */
	public void initDirectory(File directory) {
		
		ppiNetworks.initDirectory(directory);
	}

	
	
	// ============================================================================
	// PRIVATE METHODS
	
	/** Initialize PPI networks */
	private void initPpiNetworks() {

		ppiNetworks = new NetworkGroup("Protein-protein interaction");
		NetworkModel inWeb3 = new NetworkModel("InWeb", "InWeb3.txt.gz", false, true, true);
		NetworkModel biogrid = new NetworkModel("BioGRID", "biogrid-3.2.116.txt.gz", false, false, true);
		NetworkModel entrez = new NetworkModel("Entrez GeneRIF", "entrez_geneRIF-2014-09-25.txt.gz", false, false, true);
		NetworkModel hi = new NetworkModel("Human Interactome", "HI_2012_PRE.txt.gz", false, false, true);
		
		ppiNetworks.add(inWeb3);
		ppiNetworks.add(biogrid);
		ppiNetworks.add(entrez);
		ppiNetworks.add(hi);
	}
	
	
	// ============================================================================
	// SETTERS AND GETTERS

	public NetworkGroup getMyNetworks() {
		return myNetworks;
	}

}
