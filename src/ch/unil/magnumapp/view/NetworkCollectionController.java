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
import java.util.LinkedHashSet;
import java.util.List;

import ch.unil.magnumapp.App;
import ch.unil.magnumapp.JobEnrichment;
import ch.unil.magnumapp.JobMagnum;
import ch.unil.magnumapp.model.NetworkCollection;
import ch.unil.magnumapp.model.NetworkModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Controller for "Other networks" pane 
 */
public class NetworkCollectionController extends ViewController {

	/** The network collection */
	private NetworkCollection networkCollection;
	
	/** Files selected using Browse button */
	private List<File> filesToBeAdded;
	/** Disables the selection handle (used when programmatically changing the selection */
	private boolean enableHandleSelection = true;
	/** The selected networks */
	private LinkedHashSet<TreeItem<NetworkModel>> selectedNetworks = new LinkedHashSet<>();
	
	/** Bound to networkDirTextField */
	private ObjectProperty<File> networkDirProperty = new SimpleObjectProperty<File>();
	
	
	// ============================================================================
	// FXML

	/** Network collection directory */
	@FXML
	private TextField networkDirTextField; // bound
	
	@FXML
	private Button networkDirBrowseButton;
	@FXML
	private Hyperlink networkDirDownloadLink;
	
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
    @FXML
    private Label numNetworksSelectedLabel;

    /** Load */
    @FXML
    private TextField fileTextField;
    @FXML
    private Button fileBrowseButton;
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
    @Override
    protected void init() {

    	networkCollection = app.getNetworkCollection();
    	
    	// Initialize the network tree
    	TreeItem<NetworkModel> tree = networkCollection.getNetworkTree();
    	tree.setExpanded(true);
    	// Expand the two main branches
    	tree.getChildren().get(0).setExpanded(true); // My networks
    	tree.getChildren().get(1).setExpanded(true); // Network collection
    	// Add to table
    	networksTable.setRoot(tree);
        networksTable.setShowRoot(false);
        
        // Enable selection of multiple networks 
        networksTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Add selection change listener
        networksTable.getSelectionModel().getSelectedItems().addListener(
        		(ListChangeListener.Change<? extends TreeItem<NetworkModel>> c) -> {
        			handleSelectionChange();
        		});
        
    	// Initialize columns
        // For TableView there's two ways to do it: 
        // (1) Java lambdas, the first one (should look up the details, supposed to be elegant)
        // (2) Create property value factory
        // For strings, both work. For Integers, I only get it to work with (2), for checkboxes only with (1)...
        
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
        //numNodesColumn.setCellValueFactory(new PropertyValueFactory<NetworkModel, Integer>("numNodes"));
        //numEdgesColumn.setCellValueFactory(new PropertyValueFactory<NetworkModel, Integer>("numEdges"));

        directedColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().isDirectedProperty());
        // This is the simple way, but shows a checkbox for each row
        //directedColumn.setCellFactory(tc -> new CheckBoxTreeTableCell<>());
        
        // Custom rendering of the table cell
        directedColumn.setCellFactory(column -> { 
        	return new CheckBoxTreeTableCell<NetworkModel, Boolean>() {
        		@Override
        		public void updateItem(Boolean item, boolean empty) {
        			super.updateItem(item, empty);

        			if (item == null || empty)
        				setGraphic(null);
        			//else
        			//	setStyle(...);
        		}
        	};
        });

        weightedColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().isWeightedProperty());
        // Custom rendering of the table cell
        weightedColumn.setCellFactory(column -> { 
        	return new CheckBoxTreeTableCell<NetworkModel, Boolean>() {
        		@Override
        		public void updateItem(Boolean item, boolean empty) {
        			super.updateItem(item, empty);

        			if (item == null || empty)
        				setGraphic(null);
        			//else
        			//	setStyle(...);
        		}
        	};
        });
        
        // isDirected icon -- Now set directly in SceneBuilder
		//InputStream in = App.class.getClassLoader().getResourceAsStream("ch/unil/magnumapp/resources/icons/isDirectedIcon.png");
        //Image icon = new Image(in);
        //isDirectedIcon.setImage(icon);
        
        notesColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().notesProperty());
        
        // Bindings
        Bindings.bindBidirectional(networkDirTextField.textProperty(), networkDirProperty, new FileStringConverter());
    }


    // ----------------------------------------------------------------------------

    /** Load saved/default preferences */
    @Override
    public void loadPreferences() {

        // Initialize network collection directory based on saved setting
        networkDirProperty.setValue(getFilePreference("networkCollectionDir"));
        networkCollection.initDirectory(networkDirProperty.getValue());
        
        // Clear selected networks
    	Platform.runLater(() -> {
    		networksTable.getSelectionModel().clearSelection();
    	});
    }

    
    // ----------------------------------------------------------------------------

    /** Save preferences */
    @Override
    public void savePreferences() {
    	saveFilePreference("networkCollectionDir", networkDirProperty.get());
    }

    
	// ============================================================================
	// HANDLES

    /** Network directory browse button */
    @FXML
    private void handleNetworkDirBrowseButton() {

    	// Directory chooser
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Locate network collection directory");
    	// Set directory
    	File dir = dirChooser.showDialog(app.getPrimaryStage());
    	networkDirProperty.setValue(dir);    	
    	networkCollection.initDirectory(dir);
    }
    	

    // ----------------------------------------------------------------------------

    /** Network directory download link */
    @FXML
    private void handleNetworkDirDownloadLink() {

    	// Disable the main window
    	app.getRootLayout().setDisable(true);

    	// Create the thread controller / dialog
    	DownloadNetworksController controller = (DownloadNetworksController) ViewController.loadFxml("view/DownloadNetworks.fxml");

		// The alert
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Download networks");
    	// Header text
    	alert.setHeaderText("Installing the network compendium");
    	
    	// The content
    	alert.getDialogPane().setContent(controller.getDownloadNetworksVBox());

    	// Show the dialog
    	alert.showAndWait();
    	
		// Enable the main window
    	app.getRootLayout().setDisable(false);


    }

    	
    // ----------------------------------------------------------------------------

    /** Browse button */
    @FXML
    private void handleFileBrowseButton() {
        
    	// Open file chooser
    	final FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select network files");
    	filesToBeAdded = fileChooser.showOpenMultipleDialog(app.getPrimaryStage());
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
    	//ThreadLoadNetworks threadLoad = new ThreadLoadNetworks(
    	//		networkCollection.getMyNetworks(), 
    	//		filesToBeAdded, directed, weighted, removeSelf);
    	
    	// The thread controller / dialog
    	//ThreadController threadController = new ThreadController(threadLoad);
    	//threadController.start();
    	
    	// Add the network without loading
    	networkCollection.addNetworks(filesToBeAdded, directed, weighted, removeSelf);
    }


    // ----------------------------------------------------------------------------

    /** Called when networks were selected */
    @FXML
    private void handleSelectionChange() {
    	
    	// Return if the handle is disabled
    	if (!enableHandleSelection)
    		return;
    	
    	TreeTableViewSelectionModel<NetworkModel> selectionModel = networksTable.getSelectionModel();
    	ObservableList<Integer> selection = selectionModel.getSelectedIndices();
    	selectedNetworks.clear();
    	if (selection == null)
    		return;

    	for (Integer i : selection) {
    		assert i != null;
    		TreeItem<NetworkModel> item = selectionModel.getModelItem(i);
    		// Sometimes that happens, don't ask me why
    		if (item == null)
    			continue;
    		
    		String name = item.getValue().getName();
    		if (networkCollection.selectionDisabled(name))
    			continue;

    		if (!item.getValue().getFileExists()) {
    			// If no directory has been set
    			if (item.getValue().notesProperty().get() == null) {
    				if (item.isLeaf()) {
    					Alert alert = new Alert(AlertType.WARNING);
    					//alert.setWidth(1000); does not seem to work
    					alert.setTitle("Warning");
    					alert.setHeaderText("Network collection directory not set!");
    					alert.setContentText("Use the 'Browse' button to locate the 'Network collection' directory.\n\n" + 
    							"If you haven't done so already, download it:\n" +
    							"- click the 'Download' link or\n" +
    							"- visit regulatorycircuits.org");
    					alert.showAndWait();
    					break;
    				} else {
    					// Expand
    					Platform.runLater(() -> item.setExpanded(true));
    					break;
    				}

    				// Directory was set but the file / directory was not found
    			} else {
    				Alert alert = new Alert(AlertType.WARNING);
    				//alert.setWidth(1000); does not seem to work
    				alert.setTitle("Warning");

    				// Get info on the file / dir
    				File file = item.getValue().getFile();
    				String fileOrDir = "File";
    				if (file.isDirectory())
    					fileOrDir = "Directory";

    				// Set text
    				alert.setHeaderText(fileOrDir + " not found!");
    				alert.setContentText(fileOrDir + " not found in the selected 'Network collection' directory:\n" + 
    						file.getAbsolutePath() + "\n\n" +
    						"Verify that the 'Network collection' directory was selected. " +
    						"If the problem persists, download it again by clicking the link.");

    				// Show the dialog
    				alert.showAndWait();
    				break;
    			}
    		}
    		
    		// Add leafs
    		if (item.isLeaf()) {
    			selectedNetworks.add(item);
    			
   			// Add all children
    		} else {
    			// Expand
    			if (!item.isExpanded())
    				Platform.runLater(() -> item.setExpanded(true));
    			// Add kids
    			for (TreeItem<NetworkModel> child : item.getChildren()) {
    				if (!child.isLeaf())
    					throw new RuntimeException("Did not except nested categories in network tree: " + child.getValue().getName());
    				if (child.getValue().getFileExists())
    					selectedNetworks.add(child);
    			}
    		}
    	}
    	
    	// Perform selection, set texts, update EnrichmentController
    	Platform.runLater(() -> {
        	// Disable the handle for updates made below to avoid recursion
    		enableHandleSelection = false;
    		selectionModel.clearSelection();
    		for (TreeItem<NetworkModel> item : selectedNetworks)
    			selectionModel.select(item);
    		enableHandleSelection = true;
    		
    		if (selectedNetworks.size() == 0)
    			numNetworksSelectedLabel.setText("No networks selected");
    		else if (selectedNetworks.size() == 1)
    			numNetworksSelectedLabel.setText("1 network selected");
    		else
    			numNetworksSelectedLabel.setText(selectedNetworks.size() + " networks selected");
    	});
    	
		// Let the enrichment controller know
		app.getEnrichmentController().networkSelectionUpdated();
    }


	public LinkedHashSet<TreeItem<NetworkModel>> getSelectedNetworks() {
		return selectedNetworks;
	}

        
    
	// ============================================================================
	// PRIVATE METHODS

	
	// ============================================================================
	// SETTERS AND GETTERS


	  
}
