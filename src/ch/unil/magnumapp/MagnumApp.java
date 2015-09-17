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
package ch.unil.magnumapp;

import java.io.File;
import java.nio.file.Paths;

import ch.unil.magnumapp.model.*;
import ch.unil.magnumapp.view.*;
import edu.mit.magnum.FileExport;
import edu.mit.magnum.Magnum;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * The main class starting the JavaFX app
 */
public class MagnumApp extends Application {

	/** Reference to unique instance of MagnumApp (singleton design pattern) */
	private static MagnumApp instance;
	/** Reference to magnum */
	@SuppressWarnings("unused")
	private static Magnum magnum;
	
	/** The main stage */
    private Stage primaryStage;
    /** The root layout */
    private BorderPane rootLayout;
    
    /** The collection of networks */
    private NetworkCollection networkCollection = null;
    
    /** Root layout controller */
    private RootLayoutController rootLayoutController;
    /** "Settings" controller */
    private PreferencesController preferencesController;
    /** "Other networks" controller */
    private OtherNetworksController otherNetworksController;
    /** "Connectivity enrichment" controller */
    private EnrichmentController enrichmentController;
    

	// ============================================================================
	// STATIC METHODS

	/** Main */
	public static void main(String[] args) {
		
		// Initialize magnum
		magnum = new Magnum(args);
		MagnumApp.getInstance();
		Magnum.log.println(AppSettings.magnumAppVersion);
		
		// Calls start()
		launch(args);
		
		// Save settings
		AppSettings.saveSettings();
	}

	
	// ----------------------------------------------------------------------------

	/** Print the stack trace of the exception and exit */
	static public void error(Exception e) {
		
		e.printStackTrace();
		System.exit(-1);
	}

	
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public MagnumApp() {
			
		if (instance != null)
			throw new RuntimeException("There should be only one instance of MagnumApp");
		else
			instance = this;	
		
		AppSettings.loadSettings();
		networkCollection = new NetworkCollection();
	}
	
	
	// ----------------------------------------------------------------------------

	/** Called when the App is launched */
	@Override
	public void start(Stage primaryStage) {
		
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(AppSettings.magnumAppVersion);

        // The root layout
        initRootLayout();

        // Panes on the left side
        showPreferences();
        showOtherNetworks();
        
        // Panes on the right side
        showConnetivityEnrichmentPane();
        
	}
	
	
	// ============================================================================
	// PRIVATE METHODS

    /** Initializes the root layout */
    private void initRootLayout() {
    	
    	rootLayoutController = (RootLayoutController) RootLayoutController.loadFxml("view/RootLayout.fxml");
    	rootLayout = (BorderPane) rootLayoutController.getRoot();
        //rootLayout.setStyle("-fx-focus-color: transparent;");
        
    	// Show the scene containing the root layout.
    	Scene scene = new Scene(rootLayout);

    	// Add css -- doesn't work yet, I also set them in scene builder, remove...
    	String css = MagnumApp.class.getResource("view/MagnumAppStyle.css").toExternalForm();
    	scene.getStylesheets().clear();
    	scene.getStylesheets().add(css);

    	primaryStage.setScene(scene);
    	primaryStage.show();
    }

    
	// ----------------------------------------------------------------------------

    /** "Preferences" pane */
    private void showPreferences() {

    	preferencesController = (PreferencesController) ViewController.loadFxml("view/Preferences.fxml");
    	rootLayoutController.getLeftSide().getChildren().add((TitledPane) preferencesController.getRoot());  
    }

	// ----------------------------------------------------------------------------

    /** "My networks" pane */
    private void showOtherNetworks() {

    	// Initialize user networks pane
    	otherNetworksController = (OtherNetworksController) ViewController.loadFxml("view/OtherNetworks.fxml");
    	otherNetworksController.setNetworkCollection(networkCollection);
    	// Add to root layout
    	rootLayoutController.getLeftSide().getChildren().add(otherNetworksController.getRoot());  
    }

    
	// ----------------------------------------------------------------------------

    /** "Connectivity enrichment" pane */
    private void showConnetivityEnrichmentPane() {

    	// Initialize user networks pane
    	enrichmentController = (EnrichmentController) ViewController.loadFxml("view/ConnectivityEnrichment.fxml");
    	// Add to root layout
    	rootLayoutController.getRightSide().getChildren().add(enrichmentController.getRoot());  
    }

    
	// ============================================================================
	// SETTERS AND GETTERS

	public static MagnumApp getInstance() {
		return instance;
	}

    public Stage getPrimaryStage() { return primaryStage; }
    
    public NetworkCollection getNetworkCollection() { return networkCollection; }

    public PreferencesController getPreferencesController() { return preferencesController; }
	public EnrichmentController getEnrichmentController() {	return enrichmentController; }
	public OtherNetworksController getOtherNetworksController() { return otherNetworksController; }
    
}
