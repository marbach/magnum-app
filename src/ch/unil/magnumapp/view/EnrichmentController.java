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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import ch.unil.magnumapp.AppSettings;
import ch.unil.magnumapp.MagnumApp;
import ch.unil.magnumapp.ThreadConnectivityEnrichment;
import ch.unil.magnumapp.model.NetworkModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Controller for "My networks" pane 
 */
public class EnrichmentController extends ViewController {

	/** Reference to the selected networks */
	final private LinkedHashSet<TreeItem<NetworkModel>> selectedNetworks = 
			MagnumApp.getInstance().getOtherNetworksController().getSelectedNetworks();
	
	/** Gene score file selected using Browse button */
	private File geneScoreFile;
	/** Output directory selected using Browse button */
	private File outputDir;
	/** Kernel directory selected using Browse button */
	private File kernelDir;
	
    /** Input */
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
    
    /** Output */
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
    
    /** Parameters */
    @FXML
    private TextField numPermutationsTextField;
    @FXML
    private CheckBox excludeHlaGenesCheckBox;
    @FXML
    private CheckBox excludeXYChromosomesCheckBox;
    //@FXML
    //private TextField numCoresTextField;
    @FXML
    private Button showCommandButton;
    @FXML
    private Button runButton;
	
    
	// ============================================================================
	// PUBLIC METHODS

    /** Called when networks have been selected */
    public void networkSelectionUpdated() {

    	int numNetworks = selectedNetworks.size();
    	if (numNetworks == 0)
    		networksTextField.setText(null);
    	else if (numNetworks == 1)
			networksTextField.setText(selectedNetworks.iterator().next().getValue().getName());
		else
			networksTextField.setText(numNetworks + " networks selected");
    }
    

	// ============================================================================
	// HANDLES

    /** Gene score browse button */
    @FXML
    private void handleGeneScoreBrowseButton() {
        
    	// File chooser
    	final FileChooser fileChooser = new FileChooser();
    	// Set initial directory to that of current file
    	if (geneScoreFile != null && geneScoreFile.exists())
    		fileChooser.setInitialDirectory(geneScoreFile.getParentFile());
    	fileChooser.setTitle("Select a gene score file");
    	// Open dialog
    	geneScoreFile = fileChooser.showOpenDialog(magnumApp.getPrimaryStage());

    	if (geneScoreFile == null)
    		geneScoreTextField.setText(null);
    	else
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
    	
    	// Open directory chooser
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Choose output directory");
    	outputDir = dirChooser.showDialog(magnumApp.getPrimaryStage());
    	
    	// Set text field
    	if (outputDir == null) {
    		outputDirTextField.setText(null);
    	
    	} else {
    		outputDirTextField.setText(outputDir.getPath());
    		// Set kernel dir if it was not set already
    		if (kernelDir == null) {
    			kernelDir = getDefaultKernelDir();
    			kernelDirTextField.setText(kernelDir.getPath());
    		}
    	}
    }

    
    // ----------------------------------------------------------------------------

    /** Kernel directory browse button */
    @FXML
    private void handleKernelDirBrowseButton() {
    	
    	// Open directory chooser
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Choose network kernel directory");
    	kernelDir = dirChooser.showDialog(magnumApp.getPrimaryStage());
    	if (kernelDir == null)
    		kernelDir = getDefaultKernelDir();
    	
    	// Set text field
    	if (kernelDir == null)
    		kernelDirTextField.setText(null);
    	else
    		kernelDirTextField.setText(kernelDir.getPath());    	
    }
    
    
    // ----------------------------------------------------------------------------

    /** Show command button */
    @FXML
    private void handleShowCommandButton() {
    	
    	if (!checkOptions())
    		return;
    	
		Alert alert = new Alert(AlertType.INFORMATION);
		//alert.setWidth(1000); does not seem to work
		alert.setTitle("Information");
		alert.setHeaderText("Run jobs on your computing cluster");
		alert.setContentText("Use the Magnum command-line tool to run jobs on your computing cluster:\n" + 
				"1. Download the tool from regulatorycircuits.org\n" +
				"2. Run the commands below (adapt file paths where necessary)");
		
    	// The console
    	TextArea console = new TextArea();
    	console.setEditable(false);
    	console.setWrapText(false);
    	console.setText(constructCommands());
    	
    	console.setMaxWidth(Double.MAX_VALUE);
    	console.setMaxHeight(Double.MAX_VALUE);
    	console.setPrefWidth(700);
    	GridPane.setVgrow(console, Priority.ALWAYS);
    	GridPane.setHgrow(console, Priority.ALWAYS);
    	
    	// This could be a pane with tabs for multiple threads
    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	Label label = new Label("Commands with your specified options");
    	expContent.add(label, 0, 0);
    	expContent.add(console, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);
    	alert.getDialogPane().setExpanded(true);

		alert.showAndWait();

    }

    
    // ----------------------------------------------------------------------------

    /** Run button */
    @FXML
    private void handleRunButton() {

    	// Check that required options are set
    	if (!checkOptions())
    		return;
    	
    	for (TreeItem<NetworkModel> item_i : selectedNetworks) {
    		
    		String[] args = buildMagnumArgs(item_i.getValue());
    		
    		// The thread responsible for loading the networks
    		ThreadConnectivityEnrichment thread = new ThreadConnectivityEnrichment(args);
    	
    		// The thread controller / dialog
    		ThreadController threadController = new ThreadController(thread);
    		threadController.start();
    	}

    }

    
	// ============================================================================
	// PRIVATE METHODS

    /** Construct commands for command-line tool based on specified options */
    private boolean checkOptions() {
    	
    	String errors = "";
    	if (selectedNetworks.isEmpty())
    		errors += "- No networks selected\n";
    	if (geneScoreFile == null)
    		errors += "- No GWAS gene score file selected\n";
    	if (outputDir == null)
    		errors += "- No output directory selected\n";
    	if (kernelDir == null)
    		errors += "- No network-kernel directory selected\n";
    	
    	if (!errors.equals("")) {
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("Not all required options are set!");
    		alert.setContentText("Errors:\n" + errors); 
    		alert.showAndWait();
    		return false;
    		
    	} else
    		return true;
    }

    
    // ----------------------------------------------------------------------------

    /** Get command-line arguments for the given network, with the specified options */
    private String[] buildMagnumArgs(NetworkModel network) {
    	
    	// Command line options
    	ArrayList<String> args = new ArrayList<>();
    	
    	// Mode
    	args.add("--mode");
    	args.add("3");
    	// Output directory
    	args.add("--outdir");
    	args.add(outputDir.getAbsolutePath());
    	
    	// Network
    	args.add("--net");
    	args.add(network.getFile().getAbsolutePath());
    	// isDirected
    	args.add("--dir");
    	args.add(network.getIsDirected() ? "1" : "0");
    	// isWeighted
    	args.add("--weighted");
    	args.add(network.getIsWeighted() ? "1" : "0");
    	// removeSelf
    	args.add("--noself");
    	args.add(network.getRemoveSelf() ? "1" : "0");
    	
    	// Gene scores
    	args.add("--scores");
    	args.add(geneScoreFile.getAbsolutePath());

    	// TBD
    	// check gene coords
    	// check excl genes

    	
    	// 
    	return args.toArray(new String[args.size()]);
    }

    
    // ----------------------------------------------------------------------------

    /** Construct commands for command-line tool based on specified options */
    private String constructCommands() {
    	
    	String commands = "";
    	
    	return commands;
    }

    
    // ----------------------------------------------------------------------------

    /** Get the default kernel directory: outputDir/tmp_network_kernels */
    private File getDefaultKernelDir() {
    	if (outputDir == null)
    		return null;
    	else
    		return outputDir.toPath().resolve("tmp_network_kernels").toFile();
    }
    	
    
	// ============================================================================
	// SETTERS AND GETTERS

	public TextField getNetworksTextField() {
		return networksTextField;
	}

	  
}
