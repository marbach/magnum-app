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

import ch.unil.magnumapp.AppSettings;
import ch.unil.magnumapp.MagnumAppLogger;
import ch.unil.magnumapp.ThreadLoadNetworks;
import ch.unil.magnumapp.model.NetworkModel;
import edu.mit.magnum.Magnum;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * Controller for "My networks" pane 
 */
public class EnrichmentController extends ViewController {

	/** The selected networks */
	private ObservableList<NetworkModel> networks;
	/** Files selected using Browse button */
	private File geneScoreFile;
	
    /** Load */
    @FXML
    private TextField networksTextField;
    @FXML
    private TextField geneScoreTextField;
    @FXML
    private Button geneScoreBrowseButton;
    @FXML
    private Hyperlink geneScoreDownloadLink;
    @FXML
    private Hyperlink pascalDownloadLink;
    @FXML
    private TextField outputDirTextField;
    @FXML
    private Button outputDirBrowseButton;
    @FXML
    private TextField kernelDirTextField;
    @FXML
    private Button kernelDirBrowseButton;
    @FXML
    private CheckBox userPrecomputedKernelsCheckBox;
    @FXML
    private CheckBox deleteKernelsCheckBox;
    @FXML
    private TextField numPermutationsTextField;
    @FXML
    private CheckBox excludeHlaGenesCheckBox;
    
	
	// ============================================================================
	// PUBLIC METHODS


	// ============================================================================
	// HANDLES

    /** Gene score browse button */
    @FXML
    private void handleGeneScoreBrowseButton() {
        
    	// Open file chooser
    	final FileChooser fileChooser = new FileChooser();
    	geneScoreFile = fileChooser.showOpenDialog(magnumApp.getPrimaryStage());
    	if (geneScoreFile == null) {
    		geneScoreTextField.setText(null);
    		return;
    	}
    	
    	// Set file text field
    	geneScoreTextField.setText(geneScoreFile.getName());
    }


    // ----------------------------------------------------------------------------

    /** Gene score download link */
    @FXML
    private void handleGeneScoreDownloadLink() {  	
    	openWebpage(AppSettings.geneScoresLink);
    }

    
    // ----------------------------------------------------------------------------

    /** PASCAL download link */
    @FXML
    private void handlePascalDownloadLink() {
    	openWebpage(AppSettings.pascalLink);
    }


    // ----------------------------------------------------------------------------

    /** Output directory browse button */
    @FXML
    private void handleOutputDirBrowseButton() {
    	
    }

    
    // ----------------------------------------------------------------------------

    /** Kernel directory browse button */
    @FXML
    private void handleKernelDirBrowseButton() {
    	
    }

    
	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS


	  
}
