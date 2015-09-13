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
package ch.unil.magnumapp.view;

import ch.unil.magnumapp.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;


/**
 * Controller for the Overview 
 */
public class NetworksTreeTableController extends ViewController {
    
	/** The networks that are shown in this table */
	private NetworkGroup networks;
	/** The root item */
	private TreeItem<NetworkModel> root;
	
    /** Network table */
    @FXML
    private TreeTableView<NetworkModel> networksTable;
    @FXML
    private TreeTableColumn<NetworkModel, Boolean> directedColumn;
    @FXML
    private TreeTableColumn<NetworkModel, Boolean> weightedColumn;
    @FXML
    private TreeTableColumn<NetworkModel, String> nameColumn;
    @FXML
    private TreeTableColumn<NetworkModel, String> referenceColumn;
    
	
	// ============================================================================
	// PUBLIC METHODS
	    
    /** Initialize, called after the fxml file has been loaded */
    public void setNetworks(NetworkGroup networks) {

    	// Add observable list data to the table
    	this.networks = networks;
    	
    	// Create and set the root
    	root = networks.getTreeViewRoot();
    	root.setExpanded(true);
    	networksTable.setRoot(root);
    	root.getChildren().setAll(networks.getNetworks());
    	
        // Enable selection of multiple networks 
        networksTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    	// Initialize columns
        
        // For TableView there's two ways to do it: 
        // (1) Java lambdas, the first one (should look up the details, supposed to be elegant)
        // (2) Create property value factory
        // For strings, both work. For Integers, I only get it to work with (2), for checkboxes only with (1)...
        
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
//        numNodesColumn.setCellValueFactory(new PropertyValueFactory<NetworkModel, Integer>("numNodes"));
//        numEdgesColumn.setCellValueFactory(new PropertyValueFactory<NetworkModel, Integer>("numEdges"));
//
//        directedColumn.setCellValueFactory(cellData -> cellData.getValue().isDirectedProperty());
//        directedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
//
//        weightedColumn.setCellValueFactory(cellData -> cellData.getValue().isWeightedProperty());
//        weightedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
}

    
	// ============================================================================
	// HANDLES

	// ============================================================================
	// SETTERS AND GETTERS

	public NetworkGroup getNetworks() {
		return networks;
	}
	  
}
