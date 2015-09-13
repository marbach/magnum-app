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

import java.io.File;
import java.util.List;

import ch.unil.magnumapp.ThreadLoadNetworks;
import ch.unil.magnumapp.model.NetworkCollection;
import ch.unil.magnumapp.model.NetworkModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Controller for "Other networks" pane 
 */
public class OtherNetworksController extends ViewController {

	/** The network collection */
	private NetworkCollection networkCollection;
	/** The root item */
	private TreeItem<NetworkModel> tree;

	/** Files selected using Browse button */
	private List<File> filesToBeAdded;
	
	
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
    private TreeTableColumn<NetworkModel, String> notesColumn;

    /** Load */
    @FXML
    private TextField fileTextField;
    @FXML
    private Button browseButton;
    @FXML
    private Button addButton;
    @FXML
    private RadioButton directedRadio;
    @FXML
    private RadioButton undirectedRadio;
    @FXML
    private RadioButton weightedRadio;
    @FXML
    private RadioButton unweightedRadio;
    @FXML
    private CheckBox removeSelfCheckBox;
    @FXML
    private VBox contentVBox;

	
	// ============================================================================
	// PUBLIC METHODS

    /** Initialize, called after the fxml file has been loaded */
    public void setNetworkCollection(NetworkCollection networkCollection) {

    	this.networkCollection = networkCollection;
    	
    	// Create and set the root
    	tree = networkCollection.getNetworkTree();
    	tree.setExpanded(true);
    	// Expand the PPI networks as an example
    	tree.getChildren().get(0).setExpanded(true);
    	tree.getChildren().get(1).setExpanded(true);
    	
    	networksTable.setRoot(tree);
        networksTable.setShowRoot(false);
        
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
        directedColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().isDirectedProperty());
        directedColumn.setCellFactory(tc -> new CheckBoxTreeTableCell<>());

        weightedColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().isWeightedProperty());
        weightedColumn.setCellFactory(tc -> new CheckBoxTreeTableCell<>());
}


	// ============================================================================
	// HANDLES

    /** Browse button */
    @FXML
    private void handleBrowseButton() {
        
    	// Open file chooser
    	final FileChooser fileChooser = new FileChooser();
    	filesToBeAdded = fileChooser.showOpenMultipleDialog(magnumApp.getPrimaryStage());
    	if (filesToBeAdded == null) {
    		fileTextField.setText(null);
    		return;
    	}
    	
    	// Set file text field
    	int numFiles = filesToBeAdded.size();
    	if (numFiles == 0)
    		return;
    	else if (numFiles == 1)
    		fileTextField.setText(filesToBeAdded.get(0).getName());
    	else
    		fileTextField.setText(numFiles + " files selected");
    	
    	// Enable add networks button
    	addButton.setDisable(false);
    }

    
    // ----------------------------------------------------------------------------

    /** Add network button */
    @FXML
    private void handleAddNetworkButton() {
        
    	// Disable add networks button
    	addButton.setDisable(true);
    	// Reset file text field
    	fileTextField.setText(null);
    	
    	if (filesToBeAdded == null)
    		return;
    	
    	// Get relevant options
    	boolean directed = directedRadio.isSelected();
    	boolean weighted = weightedRadio.isSelected();
    	boolean removeSelf = removeSelfCheckBox.isSelected();
    			
    	// The thread responsible for loading the networks
    	ThreadLoadNetworks threadLoad = new ThreadLoadNetworks(
    			networkCollection.getMyNetworks(), 
    			filesToBeAdded, directed, weighted, removeSelf);
    	
    	// The thread controller / dialog
    	ThreadController threadController = new ThreadController(threadLoad);
    	threadController.start();
    	
    }



    
	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS


	  
}
