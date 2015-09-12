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

import ch.unil.magnumapp.MagnumAppLogger;
import ch.unil.magnumapp.ThreadLoadNetworks;
import edu.mit.magnum.Magnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * Controller for "My networks" pane 
 */
public class NetworksMyController extends ViewController {

	/** The networks table view controller */
	private NetworksTableController networksTableController;
	
	/** Files selected using Browse button */
	private List<File> filesToBeAdded;
	
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

	
	// ============================================================================
	// PUBLIC METHODS

    /** Add the network table to the pane */
	public void showNetworksTable(NetworksTableController networksTableController) {
		
		this.networksTableController = networksTableController;
        TitledPane root = (TitledPane) this.root;
        BorderPane borderPane = (BorderPane) root.getContent();
        borderPane.setCenter(networksTableController.getRoot());

	}


	// ============================================================================
	// HANDLES

    /** Browse button */
    @FXML
    private void handleBrowseButton() {
        
    	// Open file chooser
    	final FileChooser fileChooser = new FileChooser();
    	filesToBeAdded = fileChooser.showOpenMultipleDialog(magnumApp.getPrimaryStage());
    	if (filesToBeAdded == null)
    		return;
    	
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
    			networksTableController.getNetworks(), 
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
