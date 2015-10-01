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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;

import ch.unil.magnumapp.AppSettings;
import ch.unil.magnumapp.App;
import ch.unil.magnumapp.JobMagnum;
import ch.unil.magnumapp.JobEnrichment;
import ch.unil.magnumapp.model.NetworkModel;
import edu.mit.magnum.FileExport;
import javafx.application.Platform;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;


/**
 * Controller for "Connectivity Enrichment" pane 
 */
public class EnrichmentController extends ViewController {

	/** Reference to the selected networks */
	final private LinkedHashSet<TreeItem<NetworkModel>> selectedNetworks = 
			App.app.getOtherNetworksController().getSelectedNetworks();
	
	/** Bound to geneScoreTextField */
	private ObjectProperty<File> geneScoreFileProperty = new SimpleObjectProperty<>();
	/** Bound to outputDirTextField */
	private ObjectProperty<File> outputDirProperty = new SimpleObjectProperty<>();
	/** Bound to numPermutationsTextField */
	private IntegerProperty numPermutationsProperty = new SimpleIntegerProperty();
	
	/** The enrichment score / p-value file */
	private ObjectProperty<File> pvalFileProperty = new SimpleObjectProperty<>();
	
	/** Writes the enrichment scores for each job */
	private FileExport scoreWriter;
	
	/** Flag to disable the warning for multiple cores */
	private boolean disableNumCoresWarning = false;
	

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
    private CheckBox exportKernelsCheckBox;
    
    /** Parameters */
    @FXML
    private TextField numPermutationsTextField; // bound
    @FXML
    private CheckBox excludeHlaGenesCheckBox;
    @FXML
    private CheckBox excludeXYChromosomesCheckBox;
    @FXML
    private ChoiceBox<Integer> numCoresChoiceBox;
    @FXML
    private Button exportSettingsButton;
    @FXML
    private Button runButton;
    
    /** Results */
    @FXML
    private TextField pvalFileTextField;
    @FXML
    private Button pvalFileBrowseButton;
    @FXML
	private Button plotButton;
    @FXML
    private CheckBox bonferroniCheckBox;
    
    
	// ============================================================================
	// PUBLIC METHODS
    
    /** Initialize, called after the fxml file has been loaded */
    @Override
    protected void init() {

    	// Number of permutations
    	numPermutationsTextField.textProperty().addListener(new ChangeListener<String>() {
    	    @Override 
    	    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    	        if (!newValue.matches("\\d*")) {
    	        	numPermutationsTextField.setText(oldValue);
    	        	numPermutationsTextField.positionCaret(numPermutationsTextField.getLength());
    	        }
    	    }
    	});
    	
    	// Number of cores
    	int numCoresSyst = Runtime.getRuntime().availableProcessors();
    	for (int i=1; i<=numCoresSyst; i++)
    		numCoresChoiceBox.getItems().add(i);
    	numCoresChoiceBox.getSelectionModel().selectFirst();
    	numCoresChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    		if (!disableNumCoresWarning && newValue > 1) {
    			Alert alert = new Alert(AlertType.WARNING);
    			alert.setTitle("Warning");
    			alert.setHeaderText("Are you sure your computer can handle " + newValue + " parallel jobs?");
    			alert.setContentText("The analysis is both COMPUTE and MEMORY intensive. Rule of thumb:\n\n"
    					+ "\t1 job = 1 core and <8 GB memory (depends on network size).\n\n"
    					+ "Example: on a high-end laptop with 4 cores and 16 GB memory, you can run 2 parallel jobs. "
    					+ "If you launch more jobs, performance will degrade. If you have access to a cluster,"
    					+ "we recommend that you use the magnum command-line tool to run jobs in parallel."); 
    			alert.showAndWait();
    		}
    	});
    	
    	// Bindings
        Bindings.bindBidirectional(geneScoreTextField.textProperty(), geneScoreFileProperty, new FileStringConverter());
        Bindings.bindBidirectional(outputDirTextField.textProperty(), outputDirProperty, new FileStringConverter());
        Bindings.bindBidirectional(numPermutationsTextField.textProperty(), numPermutationsProperty, new NumberStringConverter("###"));
        //numPermutationsTextField.textProperty().bindBidirectional(numPermutationsProperty, new NumberStringConverter());
        Bindings.bindBidirectional(pvalFileTextField.textProperty(), pvalFileProperty, new FileStringConverter());
        
        // Tooltips
        initTooltips();
    }
    
    
    // ----------------------------------------------------------------------------

    /** Initialize with settings from AppSettings */
    @Override
    public void loadPreferences() {
   	
        geneScoreFileProperty.set(getFilePreference("geneScoreFile"));
        outputDirProperty.set(getFilePreference("outputDir"));
        
        usePrecomputedKernelsCheckBox.setSelected(prefs.getBoolean("usePrecomputedKernels", true));
        exportKernelsCheckBox.setSelected(prefs.getBoolean("exportKernels", false));
        excludeHlaGenesCheckBox.setSelected(prefs.getBoolean("excludeHlaGenes", true));
        excludeXYChromosomesCheckBox.setSelected(prefs.getBoolean("excludeXYChromosomes", true));
        bonferroniCheckBox.setSelected(prefs.getBoolean("bonferroni", true));
        numPermutationsProperty.set(prefs.getInt("numPermutations", 10000));
        
        Platform.runLater(() -> {
        	disableNumCoresWarning = true;
            numCoresChoiceBox.getSelectionModel().select(prefs.getInt("numCores", 0));
            disableNumCoresWarning = false;
        }); 
    }

    
    // ----------------------------------------------------------------------------

    /** Initialize with settings from AppSettings */
    @Override
    public void savePreferences() {

    	saveFilePreference("geneScoreFile", geneScoreFileProperty.get());
    	saveFilePreference("outputDir", outputDirProperty.get());

    	prefs.putBoolean("usePrecomputedKernels", usePrecomputedKernelsCheckBox.isSelected());
    	prefs.putBoolean("exportKernels", exportKernelsCheckBox.isSelected());
    	prefs.putBoolean("excludeHlaGenes", excludeHlaGenesCheckBox.isSelected());
    	prefs.putBoolean("excludeXYChromosomes", excludeXYChromosomesCheckBox.isSelected());
    	prefs.putBoolean("bonferroni", bonferroniCheckBox.isSelected());

    	prefs.putInt("numPermutations", numPermutationsProperty.get());
    	prefs.putInt("numCores", numCoresChoiceBox.getSelectionModel().getSelectedIndex());    	
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
    	Path kernelDirPath = outputDirPath.resolve("network_kernels"); 
    	return kernelDirPath.toFile();
    }

    
    // ----------------------------------------------------------------------------

    /** Get the job name for this network (geneScoreName--networkName) */
    public void writeScore(String networkName, double score, String settingsFile) {
    	
    	if (scoreWriter == null)
    		initScoreWriter();
    	
    	scoreWriter.println(networkName + "\t" + App.mag.utils.toStringScientific10(score) + "\t" + settingsFile);
    	scoreWriter.flush();
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

    /** Export settings button */
    @FXML
    private void handleExportSettingsButton() {
    	
    	if (!checkOptions())
    		return;
    			
		if (showExportSettingsConfirmation(selectedNetworks.size()) != ButtonType.OK)
			return;
    	
    	for (TreeItem<NetworkModel> item_i : selectedNetworks) {
    		JobEnrichment job = new JobEnrichment(null, getJobName(item_i.getValue()), this, item_i.getValue());
    		job.writeSettingsFile(App.log);
    	}
    }

    
    // ----------------------------------------------------------------------------

    /** Run button */
    @FXML
    private void handleRunButton() {

    	// Check that required options are set
    	if (!checkOptions())
    		return;
    	    	
    	if (selectedNetworks.size() > 1) {
    		if (showMultipleNetworksWarning(selectedNetworks.size()) != ButtonType.OK)
    			return;
    	}
    	
    	// Disable the main window
    	app.getRootLayout().setDisable(true);

    	// Create the thread controller / dialog
    	JobController jobManager = (JobController) ViewController.loadFxml("view/ThreadStatus.fxml");
    	jobManager.setOutputDir(outputDirProperty.get()); // Has to be done before creating the jobs

    	// Create a job for each network
    	ArrayList<JobMagnum> jobs = new ArrayList<>();
    	for (TreeItem<NetworkModel> item_i : selectedNetworks) {
    		JobEnrichment job_i = new JobEnrichment(jobManager, getJobName(item_i.getValue()), this, item_i.getValue());
    		jobs.add(job_i);
    	}
    	
    	// A new score writer will be created when the first result is ready
    	scoreWriter = null;

    	// Start the jobs
    	int numCores = numCoresChoiceBox.getSelectionModel().getSelectedItem();
		jobManager.start(jobs, numCores); 
		
		// Cleanup
		if (scoreWriter != null) {
			scoreWriter.close();
			pvalFileProperty.set(scoreWriter.getFile());
		}
    	app.getRootLayout().setDisable(false);
    	plotButton.setDisable(false);
    	System.gc();
    }

    
    // ----------------------------------------------------------------------------

    /** Run button */
    @FXML
    private void handlePvalFileBrowseButton() {

    	// File chooser
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select an enrichment p-value file");
    	
    	// Set initial directory to the output dir
    	if (outputDirProperty.get() != null && outputDirProperty.get().exists())
    		fileChooser.setInitialDirectory(outputDirProperty.get());

    	// Open dialog and set file
    	File file = fileChooser.showOpenDialog(app.getPrimaryStage());
    	pvalFileProperty.set(file);
    	
    	// Enable plot button
    	plotButton.setDisable(false);
    }
    
    
    // ----------------------------------------------------------------------------

    /** Run button */
    @FXML
    private void handlePlotButton() {

    	EnrichmentPlotController controller = new EnrichmentPlotController(pvalFileProperty.get(), bonferroniCheckBox.isSelected());
    	controller.show();
    }
    

    // ----------------------------------------------------------------------------

    /** Gene score download link */
    @FXML
    private void handleExampleFileLink() {  	
    	openWebpage(AppSettings.examplePvalFileLink);
    }

    /** PASCAL download link */
    @FXML
    private void downloadRScriptsLink() {
    	openWebpage(AppSettings.downloadRScriptsLink);
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
			App.log.closeLogFile();
    		return false;
    	} else
    		return true;
    }

    
    // ----------------------------------------------------------------------------

    /** Show a warning before launching job with multiple networks */
    private ButtonType showExportSettingsConfirmation(int numNetworks) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.getDialogPane().setPrefWidth(540);
		alert.setTitle("Export settings");
		String s = (numNetworks > 1) ? "s" : "";
		alert.setHeaderText("Writing settings file" + s + " for " + numNetworks + " network" + s);
		alert.setContentText(
				"Output directory:\n" +
				outputDirProperty.get().getPath() + "\n\n" +
				"Settings files can be used to:\n\n" + 
				"(1) Run jobs from the command line (typically on a computing cluster)\n" +
				"(2) Reload the settings in the App (click the \"Settings\" button)\n\n" +
				"TIP: Settings files are also saved when launching a run, keeping them together with your results ensures reproducibility!\n\n" +
				"See the exported settings file and user guide for further instructions.");

		Optional<ButtonType> result = alert.showAndWait();
		return result.get();
    }
    
    
    // ----------------------------------------------------------------------------

    /** Show a warning before launching job with multiple networks */
    private ButtonType showMultipleNetworksWarning(int numNetworks) {
    	
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.getDialogPane().setPrefWidth(540);
		alert.setTitle("Start job");
		alert.setHeaderText("Compute connectivity enrichment for " + numNetworks + " networks?");
		alert.setContentText(
				"Runtime is ~15min for 1 network on high-end laptop using default settings.\n\n" +
				"Multiple networks can be run in parallel (using >1 cores)\n\n" +
				"With the command-line tool you can run these jobs on your compting cluster:\n\n" + 
				"1. Use the \"Export settings\" button to save a text file with your settings\n" +
				"2. Download the Magnum command-line tool from regulatorycircuits.org\n" +
				"3. Run Magnum from the command line with the option \"--set <settings_file>\"\n\n" +
				"See the exported settings file and user guide for further instructions.");

		Optional<ButtonType> result = alert.showAndWait();
		return result.get();
    }
    

    // ----------------------------------------------------------------------------

    /** Get the job name for this network (geneScoreName--networkName) */
    private String getJobName(NetworkModel network) {
    	String gwasName = App.mag.utils.extractBasicFilename(geneScoreFileProperty.get().getName(), false);
    	String jobName = gwasName	+ "--" + App.mag.utils.extractBasicFilename(network.getFile().getName(), false);
    	return jobName;
    }

    
    // ----------------------------------------------------------------------------

    /** Get the job name for this network (geneScoreName--networkName) */
    private void initScoreWriter() {
    	
    	App.log.println("Creating gene score result file ...");
    	// The output file
    	String gwasName = App.mag.utils.extractBasicFilename(geneScoreFileProperty.get().getName(), false);
    	String fileprefix = gwasName + ".pvals";
    	File file = new File(outputDirProperty.get(), fileprefix + ".txt");
    	
    	// If it already exists, don't overwrite - add index
    	if (file.exists()) {
    		for (int i=1; i<Integer.MAX_VALUE; i++) {
    			file = new File(outputDirProperty.get(), fileprefix + "." + i + ".txt");
    			if (!file.exists())
    				break;
    		}
    	}
    	scoreWriter = new FileExport(App.log, file);
    	
    	// Write header
    	scoreWriter.println("# GWAS = " + gwasName);
    	scoreWriter.println("Network\tPvalue\tSettings");
    	scoreWriter.flush();
    }

    
    // ----------------------------------------------------------------------------

    /** Add tooltips */
    private void initTooltips() {
    	
    	networksTextField.setTooltip(new Tooltip(
    			"Select at least one network\n" +
    			"in the table on the left"));

    	Tooltip tip = new Tooltip("Choose your GWAS results file (gene-level" +
    			"p-values, e.g., computed using PASCAL)");
    	geneScoreTextField.setTooltip(tip);
    	geneScoreBrowseButton.setTooltip(tip);
    	geneScoreDownloadLink.setTooltip(new Tooltip(
    			"Download a collection of GWAS gene\n"
    			+ "scores (37 traits used in our paper)"));

    	pascalDownloadLink.setTooltip(new Tooltip(
    			"Download the PASCAL tool to compute gene scores\n"
    			+ "from GWAS summary statistics (SNP p-values)"));
    	
    	usePrecomputedKernelsCheckBox.setTooltip(new Tooltip(
    			"Check if precomputed random-walk kernels are available for\n" +
    			"the selected networks in the directory \"network_kernels\" in\n" +
    			"the output directory (speed gain is not substantial, it is\n" +
    			"only relevant when running many GWASs with the same network)"));

    	exportKernelsCheckBox.setTooltip(new Tooltip(
    			"Export the random-walk kernels to compressed text files." +
    			"WARNING: File sizes can be >1GB!"));
    	
    	tip = new Tooltip(
    			"Select directory for writing\n" +
    			"all result and log files");
    	outputDirTextField.setTooltip(tip);
    	outputDirBrowseButton.setTooltip(tip);
    	
//        /** Parameters */
//        @FXML
//        private TextField numPermutationsTextField; // bound
//        @FXML
//        private CheckBox excludeHlaGenesCheckBox;
//        @FXML
//        private CheckBox excludeXYChromosomesCheckBox;
//        @FXML
//        private ChoiceBox<Integer> numCoresChoiceBox;
//        @FXML
//        private Button exportSettingsButton;
//        @FXML
//        private Button runButton;
//        
//        /** Results */
//        @FXML
//        private TextField pvalFileTextField;
//        @FXML
//        private Button pvalFileBrowseButton;
//        @FXML
//    	private Button plotButton;
//        @FXML
//        private CheckBox bonferroniCheckBox;
        

    }

    
	// ============================================================================
	// SETTERS AND GETTERS

    public File getGeneScoreFile() { return geneScoreFileProperty.get(); }
    public File getOutputDir() { return outputDirProperty.get(); }
    
    public int getNumPermutations() { return numPermutationsProperty.get(); }
    
    public boolean getExcludeHlaGenes() { return excludeHlaGenesCheckBox.isSelected(); }
    public boolean getExcludeXYChromosomes() { return excludeXYChromosomesCheckBox.isSelected(); }
    
    public boolean getUsePrecomputedKernels() { return usePrecomputedKernelsCheckBox.isSelected(); }
    public boolean getExportKernels() { return exportKernelsCheckBox.isSelected(); }
}
