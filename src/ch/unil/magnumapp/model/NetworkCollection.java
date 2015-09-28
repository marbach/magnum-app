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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import ch.unil.magnumapp.App;
import ch.unil.magnumapp.AppSettings;
import edu.mit.magnum.FileParser;
import javafx.scene.control.TreeItem;

/**
 * The collection of available networks
 */
public class NetworkCollection {

    
    /** Do not allow these nodes in the tree to be selected */
    private HashSet<String> selectionDisabled  = new HashSet<>();
    
    /** My networks */
    private NetworkGroup myNetworks;

    /** 32 high-level networks */
    private NetworkGroup fantom5HighLevel;
    /** 394 individual networks */
    private ArrayList<NetworkGroup> fantom5Individual;
    
    /** PPI networks */
    private NetworkGroup ppiNetworks;
    /** Co-expression GTEx */
    private NetworkGroup coExpressionGtex;
    /** Global regulatory networks from ENCODE */
    private NetworkGroup globalRegulatoryEncode;

    /** The tree of the network collection */
    private TreeItem<NetworkModel> networkTree;

    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public NetworkCollection() {
		
		initRegulatoryCircuits();
		initPpiNetworks();
		initCoexpressionGtex();
		initGlobalRegulatoryEncode();
		// Build the tree
		initNetworkTree();
	}

	
    // ----------------------------------------------------------------------------

	/** Initialize the directory, subdirectories and network files */
	public void initDirectory(File directory) {
		for (TreeItem<NetworkModel> child : networkTree.getChildren())
			initDirectory(child, directory);
	}

	
    // ----------------------------------------------------------------------------

	/** Get a tree representation of the network collection for the view */
	public boolean selectionDisabled(String name) {
		return selectionDisabled.contains(name);
	}

	
    // ----------------------------------------------------------------------------

	/** Get a tree representation of the network collection for the view */
	public void addNetworks(List<File> files, boolean directed, boolean weighted, boolean removeSelf) {

		if (myNetworks == null) {
			myNetworks = new NetworkGroup("My networks", null);
			networkTree.getChildren().add(myNetworks.getTreeViewRoot());
		}
		
		for (File file : files)
			myNetworks.addNetwork(file, directed, weighted, removeSelf);
	}

	
	// ============================================================================
	// PRIVATE METHODS
	
	/** Load network/cluster id-name table */
	private LinkedHashMap<String, String> loadIdNameTable(String resourceName) {
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		InputStream in = AppSettings.class.getClassLoader().getResourceAsStream(resourceName);
		FileParser reader = new FileParser(in);
		while (true) {
			// Read next line
			String[] nextLine = reader.readLine();
			if (nextLine == null)
				break;

			// Check format
			if (nextLine.length != 2)
				reader.error("Excepted 2 columns, found " + nextLine.length);

			// Add entry
			map.put(nextLine[0], nextLine[1]);
		}
		reader.close();
		return map;
	}

	
    // ----------------------------------------------------------------------------

	/**  */
	private void initRegulatoryCircuits() {

		// Initialize the the tables used for the network / cluster names
		LinkedHashMap<String, String> clusterNames = loadIdNameTable("ch/unil/magnumapp/resources/fantom5_networks/32_cluster_names.txt");
		LinkedHashMap<String, String> networkNames = loadIdNameTable("ch/unil/magnumapp/resources/fantom5_networks/394_network_names.txt");
		
		LinkedHashMap<String, ArrayList<String>> clusters = new LinkedHashMap<>();
		InputStream in = AppSettings.class.getClassLoader().getResourceAsStream("ch/unil/magnumapp/resources/fantom5_networks/32_clusters.txt");
		FileParser reader = new FileParser(in);
		while (true) {
			// Read next line
			String[] nextLine = reader.readLine();
			if (nextLine == null)
				break;

			// Check format
			if (nextLine.length != 2)
				reader.error("Excepted 2 columns, found " + nextLine.length);

			// Add entry
			String clustId = nextLine[1];
			if (!clusters.containsKey(clustId))
				clusters.put(clustId, new ArrayList<>());
			clusters.get(clustId).add(nextLine[0]);
		}
		reader.close();
		assert clusters.size() == 32;

		// Initialize the 32 high-level networks
		fantom5HighLevel = new NetworkGroup("32 high-level networks", "32_high-level_networks");
		for (Entry<String, String> entry : clusterNames.entrySet()) {
			NetworkModel net = new NetworkModel(entry.getValue(), entry.getKey() + ".txt.gz", true, true, true);
			fantom5HighLevel.add(net);
		}
		
		// Initialize the 394 individual networks
		fantom5Individual = new ArrayList<>();
		for (Entry<String, String> entry : clusterNames.entrySet()) {
			// Create a new group for this cluster
			String clusterId = entry.getKey();
			String clusterName = entry.getValue();
			NetworkGroup group = new NetworkGroup(clusterName, ".");
			fantom5Individual.add(group);
			
			// Add the networks of this cluster
			for (String netId : clusters.get(clusterId)) {
				String netName = networkNames.get(netId);
				NetworkModel net = new NetworkModel(netName, netId + ".txt.gz", true, true, true);
				group.add(net);
			}
		}
	}
	
	
    // ----------------------------------------------------------------------------

	/** Initialize PPI networks */
	private void initPpiNetworks() {

		ppiNetworks = new NetworkGroup("Protein-protein interaction", "Protein-protein_interaction");
		NetworkModel inWeb3 = new NetworkModel("InWeb", "InWeb3.txt.gz", false, true, true);
		NetworkModel biogrid = new NetworkModel("BioGRID", "biogrid-3.2.116.txt.gz", false, false, true);
		NetworkModel entrez = new NetworkModel("Entrez GeneRIF", "entrez_geneRIF-2014-09-25.txt.gz", false, false, true);
		NetworkModel hi = new NetworkModel("Human Interactome", "HI_2012_PRE.txt.gz", false, false, true);
		
		ppiNetworks.add(inWeb3);
		ppiNetworks.add(biogrid);
		ppiNetworks.add(entrez);
		ppiNetworks.add(hi);
	}
	
	
    // ----------------------------------------------------------------------------

	/**  */
	private void initCoexpressionGtex() {

		// Links files to network names
		LinkedHashMap<String, String> networkNames = loadIdNameTable("ch/unil/magnumapp/resources/fantom5_networks/pierson2015_network_names.txt");
		// Initialize the 32 high-level networks
		coExpressionGtex = new NetworkGroup("Tissue-specific co-expression (GTEx)", "Tissue-specific_co-expression_GTEx");
		for (Entry<String, String> entry : networkNames.entrySet()) {
			NetworkModel net = new NetworkModel(entry.getValue(), entry.getKey() + ".txt.gz", false, true, true);
			coExpressionGtex.add(net);
		}
	}
	
	
    // ----------------------------------------------------------------------------

	/** Initialize PPI networks */
	private void initGlobalRegulatoryEncode() {

		globalRegulatoryEncode = new NetworkGroup("Regulatory networks (ENCODE)", "Global_regulatory_ENCODE");
		NetworkModel raw = new NetworkModel("ChIP-seq network (raw)", "ENCODE-nets.proximal_raw.distal.txt.gz", true, false, true);
		NetworkModel filtered = new NetworkModel("ChIP-seq network (filtered)", "ENCODE-nets.proximal_filtered.distal.txt.gz", true, false, true);
		
		globalRegulatoryEncode.add(raw);
		globalRegulatoryEncode.add(filtered);
	}


    // ----------------------------------------------------------------------------

	/** Initialize the tree representation of the network collection for the view */
	private void initNetworkTree() {

		// The root node
		String name = "Root";
		String filename = null;
		networkTree = new TreeItem<>(new NetworkModel(name, filename, true));
		selectionDisabled.add(name);

		// My networks
		//networkTree.getChildren().add(myNetworks.getTreeViewRoot());

		// Regulatory networks
		name = "Tissue-specific regulatory networks (FANTOM5)";
		filename = "Tissue-specific_regulatory_networks_FANTOM5-v1";
		selectionDisabled.add(name);
		TreeItem<NetworkModel> regulatoryNetworks = new TreeItem<>(new NetworkModel(name, filename, true));
		networkTree.getChildren().add(regulatoryNetworks);
		
		// 32 high-level
		regulatoryNetworks.getChildren().add(fantom5HighLevel.getTreeViewRoot());

		// 394 individual
		name = "394 individual networks";
		filename = "394_individual_networks";
		selectionDisabled.add(name);
		TreeItem<NetworkModel> individualNetworks = new TreeItem<>(new NetworkModel(name, filename, true));
		regulatoryNetworks.getChildren().add(individualNetworks);
		
		for (NetworkGroup clust_i : fantom5Individual)
			individualNetworks.getChildren().add(clust_i.getTreeViewRoot());

		// Other networks
		name = "Other networks";
		filename = "Other_networks";
		selectionDisabled.add(name);
		TreeItem<NetworkModel> otherNetworks = new TreeItem<>(new NetworkModel(name, filename, true));
		networkTree.getChildren().add(otherNetworks);
		// Add groups
		otherNetworks.getChildren().add(ppiNetworks.getTreeViewRoot());
		otherNetworks.getChildren().add(coExpressionGtex.getTreeViewRoot());
		otherNetworks.getChildren().add(globalRegulatoryEncode.getTreeViewRoot());
	}

	
    // ----------------------------------------------------------------------------


	/** Initialize the directory, subdirectories and network files */
	public void initDirectory(TreeItem<NetworkModel> item, File parentDir) {
				
		NetworkModel net = item.getValue();		
		net.initFile(parentDir);
		
		for (TreeItem<NetworkModel> child : item.getChildren())
			initDirectory(child, net.getFile());

	}

	
	
	// ============================================================================
	// SETTERS AND GETTERS

	public TreeItem<NetworkModel> getNetworkTree() {
		return networkTree;
	}

	public NetworkGroup getMyNetworks() {
		return myNetworks;
	}
	

}
