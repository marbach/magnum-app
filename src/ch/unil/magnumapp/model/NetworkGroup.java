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

import ch.unil.magnumapp.App;
import edu.mit.magnum.net.Network;
import javafx.scene.control.TreeItem;

/**
 * Represents a group of networks displayed together (e.g., the 4 PPI nets, 
 * the tissue-specific nets, etc.), optionally with a clustering dendrogram.
 */
public class NetworkGroup {

	/** The name of this network group */
	//private String name;
	/** The root for the tree view */
	private TreeItem<NetworkModel> treeViewRoot;
	
	
	// ============================================================================
	// PUBLIC METHODS
	    
	/** Constructor */
	public NetworkGroup(String name, String filename) {
		//this.name = name;
		treeViewRoot = new TreeItem<>(new NetworkModel(name, filename, true));
	}

	
    // ----------------------------------------------------------------------------

	public void add(NetworkModel next) {
		treeViewRoot.getChildren().add(new TreeItem<>(next));
	}
	
	
    // ----------------------------------------------------------------------------

	/** Add network model (without loading the network) */
	public void addNetwork(File file, boolean directed, boolean weighted, boolean removeSelf) {
		
		NetworkModel network = new NetworkModel(file, directed, weighted, removeSelf);
		treeViewRoot.getChildren().add(new TreeItem<>(network));
	}

	
    // ----------------------------------------------------------------------------

	/** Load the network, add the model (not the network!) to the collection */
	public void loadNetworkAddModel(File file, boolean directed, boolean weighted, boolean removeSelf) {
		
		Network network = new Network(App.mag, file, directed, removeSelf, weighted, 0);
		TreeItem<NetworkModel> next = new TreeItem<>(new NetworkModel(network));
		treeViewRoot.getChildren().add(next);
	}
    

    // ----------------------------------------------------------------------------

//	/** Sets network dir for the group and all its networks */
//	public void initDirectory(File parentDir) {
//		
//		File dir = null;
//		if (parentDir != null)
//			dir = parentDir.toPath().resolve(name).toFile();
//		treeViewRoot.getValue().setFile(dir);
//		
//		for (TreeItem<NetworkModel> item : treeViewRoot.getChildren())
//			item.getValue().initFile(dir);
//	}



	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS

	public TreeItem<NetworkModel> getTreeViewRoot() {
		return treeViewRoot;
	}

	public void setTreeViewRoot(TreeItem<NetworkModel> treeViewRoot) {
		this.treeViewRoot = treeViewRoot;
	}

}
