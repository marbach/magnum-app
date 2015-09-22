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
import java.nio.file.Path;
import java.util.LinkedHashSet;

import ch.unil.magnumapp.AppSettings;
import ch.unil.magnumapp.ConnectivityEnrichmentLauncher;
import ch.unil.magnumapp.MagnumApp;
import ch.unil.magnumapp.model.NetworkModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.converter.NumberStringConverter;

/**
 * Controller for "Connectivity Enrichment" pane 
 */
public class EnrichmentController extends ViewController {

	/** Reference to the selected networks */
	final private LinkedHashSet<TreeItem<NetworkModel>> selectedNetworks = 
			MagnumApp.getInstance().getOtherNetworksController().getSelectedNetworks();
	
	/** Bound to geneScoreTextField */
	private ObjectProperty<File> geneScoreFileProperty = new SimpleObjectProperty<File>();
	/** Bound to outputDirTextField */
	private ObjectProperty<File> outputDirProperty = new SimpleObjectProperty<File>();
	/** Bound to numPermutationsTextField */
	private IntegerProperty numPermutationsProperty = new SimpleIntegerProperty();
	

	// ============================================================================
	// FXML

    /** Input */
    @FXML
    private TextField networksTextField;
    @FXML
    private TextField geneScoreTextField; // bound
    @FXML
    private Button geneScoreBrowseButton;
    @FXML
    private CheckBox usePrecomputedKernelsCheckBox;
    @FXML
    private Hyperlink geneScoreDownloadLink;
    @FXML
    private Hyperlink pascalDownloadLink;
    
    /** Output */
    @FXML
    private TextField outputDirTextField; // bound
    @FXML
    private Button outputDirBrowseButton;
    @FXML
    private CheckBox deleteKernelsCheckBox;
    
    /** Parameters */
    @FXML
    private TextField numPermutationsTextField; // bound
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
    
    /** Initialize, called after the fxml file has been loaded */
    @Override
    protected void init() {

    	numPermutationsTextField.textProperty().addListener(new ChangeListener<String>() {
    	    @Override 
    	    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    	        if (!newValue.matches("\\d*")) {
    	        	numPermutationsTextField.setText(oldValue);
    	        	numPermutationsTextField.positionCaret(numPermutationsTextField.getLength());
    	        }
    	    }
    	});
    	
        Bindings.bindBidirectional(geneScoreTextField.textProperty(), geneScoreFileProperty, new FileStringConverter());
        Bindings.bindBidirectional(outputDirTextField.textProperty(), outputDirProperty, new FileStringConverter());
        Bindings.bindBidirectional(numPermutationsTextField.textProperty(), numPermutationsProperty, new NumberStringConverter("###"));
        //numPermutationsTextField.textProperty().bindBidirectional(numPermutationsProperty, new NumberStringConverter());        
    }
    
    
    /** Initialize with settings from AppSettings */
    @Override
    public void loadPreferences() {
   	
        geneScoreFileProperty.set(getFilePreference("geneScoreFile"));
        outputDirProperty.set(getFilePreference("outputDir"));
        
        usePrecomputedKernelsCheckBox.setSelected(prefs.getBoolean("usePrecomputedKernels", true));
        deleteKernelsCheckBox.setSelected(prefs.getBoolean("deleteKernels", true));
        excludeHlaGenesCheckBox.setSelected(prefs.getBoolean("excludeHlaGenes", true));
        excludeXYChromosomesCheckBox.setSelected(prefs.getBoolean("excludeXYChromosomes", true));

        numPermutationsProperty.set(prefs.getInt("numPermutations", 10000));   
    }

    
    // ----------------------------------------------------------------------------

    /** Initialize with settings from AppSettings */
    @Override
    public void savePreferences() {

    	saveFilePreference("geneScoreFile", geneScoreFileProperty.get());
    	saveFilePreference("outputDir", outputDirProperty.get());

    	prefs.putBoolean("usePrecomputedKernels", usePrecomputedKernelsCheckBox.isSelected());
    	prefs.putBoolean("deleteKernels", deleteKernelsCheckBox.isSelected());
    	prefs.putBoolean("excludeHlaGenes", excludeHlaGenesCheckBox.isSelected());
    	prefs.putBoolean("excludeXYChromosomes", excludeXYChromosomesCheckBox.isSelected());

    	prefs.putInt("numPermutations", numPermutationsProperty.get());
    }


    // ----------------------------------------------------------------------------

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
    
	
    // ----------------------------------------------------------------------------

		
    /** Get kernel dir based on current output dir */
    public File getKernelDir() {
    	
    	if (outputDirProperty.get() == null)
    		return null;
    	Path outputDirPath = outputDirProperty.get().toPath();
    	Path kernelDirPath = outputDirPath.resolve("tmp_network_kernels"); 
    	return kernelDirPath.toFile();
    }

    
	// ============================================================================
	// HANDLES

    /** Gene score browse button */
    @FXML
    private void handleGeneScoreBrowseButton() {
        
    	// File chooser
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select a gene score file");
    	
    	// Set initial directory to that of current file
    	if (geneScoreFileProperty.get() != null && geneScoreFileProperty.get().exists())
    		fileChooser.setInitialDirectory(geneScoreFileProperty.get().getParentFile());

    	// Open dialog and set file
    	File file = fileChooser.showOpenDialog(app.getPrimaryStage());
    	geneScoreFileProperty.set(file);
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
    	
    	// Directory chooser
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Choose output directory");
    	// Set directory
    	File dir = dirChooser.showDialog(app.getPrimaryStage());
    	outputDirProperty.set(dir);
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
    		ConnectivityEnrichmentLauncher launcher = new ConnectivityEnrichmentLauncher(this, item_i.getValue());
    		launcher.launch();    		
    	}

    }

    
	// ============================================================================
	// PRIVATE METHODS

    /** Construct commands for command-line tool based on specified options */
    private boolean checkOptions() {
    	
    	String errors = "";
    	if (selectedNetworks.isEmpty())
    		errors += "- No networks selected\n";
    	if (geneScoreFileProperty.get() == null)
    		errors += "- No GWAS gene score file selected\n";
    	if (outputDirProperty.get() == null)
    		errors += "- No output directory selected\n";
    	
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

    /** Construct commands for command-line tool based on specified options */
    private String constructCommands() {
    	
    	String commands = "";
    	
    	return commands;
    }

    
	// ============================================================================
	// SETTERS AND GETTERS

    public File getGeneScoreFile() { return geneScoreFileProperty.get(); }
    public File getOutputDir() { return outputDirProperty.get(); }
    
    public int getNumPermutations() { return numPermutationsProperty.get(); }
    
    public boolean getExcludeHlaGenes() { return excludeHlaGenesCheckBox.isSelected(); }
    public boolean getExcludeXYChromosomes() { return excludeXYChromosomesCheckBox.isSelected(); }
    
    public boolean getUsePrecomputedKernels() { return usePrecomputedKernelsCheckBox.isSelected(); }
    public boolean getDeleteKernels() { return deleteKernelsCheckBox.isSelected(); }
}
