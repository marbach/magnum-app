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
import java.util.prefs.BackingStoreException;

import ch.unil.magnumapp.model.*;
import ch.unil.magnumapp.view.*;
import edu.mit.magnum.Magnum;
import edu.mit.magnum.MagnumSettings;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * The main class starting the JavaFX app
 */
public class App extends Application {

	/** For convenicen, a magnum reference -- do not use in threads, they need their own one! */
	public static Magnum mag; 
	/** Reference to unique instance of MagnumApp (singleton design pattern) */
	public static App app;
	/** The logger (also set for mag) */
	public static AppLogger log;
	
	/** The main stage */
    private Stage primaryStage;
    /** The root layout */
    private BorderPane rootLayout;
    
    /** The collection of networks */
    private NetworkCollection networkCollection = null;
    
    /** Root layout controller */
    private RootLayoutController rootLayoutController;
    /** "Settings" controller */
    private PreferencesDialogController preferencesController;
    /** "Other networks" controller */
    private NetworkCollectionController networksController;
    /** "Connectivity enrichment" controller */
    private EnrichmentController enrichmentController;
    /** "Console" controller */
    private ConsoleController consoleController;
    /** "Credits" controller */
    private CreditsController creditsController;
    

	// ============================================================================
	// STATIC METHODS

	/** Main */
	public static void main(String[] args) {

		try {
			// The logger
			log = new AppLogger();
			log.keepLogCopy();
			File logFile = new File(System.getProperty("user.home"), ".magnum-app.log.txt");
			log.createLogFile(logFile);
			// Say hello
			log.println(AppSettings.magnumAppVersion);

			// Initialize magnum with our logger
			mag = new Magnum(args, log);

			// Calls start()
			launch(args);

			// Save settings
			log.println("Saving preferences...");
			app.savePreferences();
			log.println("Bye!");
			
		} catch (Throwable e) {
			log.setConsole(null);
			log.printStackTrace(e);

		} finally {
			log.closeLogFile();
		}
	}

		
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public App() {
			
		if (app != null)
			throw new RuntimeException("There should be only one instance of MagnumApp");
		else
			app = this;			
	}
	
	
	// ----------------------------------------------------------------------------

	/** Called when the App is launched */
	@Override
	public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(AppSettings.magnumAppVersion);

        // Set icons
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("resources/icons/magnumIcon16.png"))); 
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("resources/icons/magnumIcon32.png"))); 
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("resources/icons/magnumIcon64.png"))); 
        
        // Load preferences controller -- needs to be done first because of rememberSettings
    	preferencesController = (PreferencesDialogController) ViewController.loadFxml("view/PreferencesDialog.fxml");
        // The root layout
        initRootLayout();
        // Panes on the left side
        showNetworkCollection();
        // Panes on the right side
        showConnetivityEnrichmentPane();
        showConsolePane();
        showCreditsPane();
        
        // Load saved/default settings
        loadPreferences();
	}
	

	// ----------------------------------------------------------------------------
	
    /** Initialize the controls with saved/default settings */
    public void loadPreferences() {
    	
    	try {
    		preferencesController.loadPreferences();
    		networksController.loadPreferences();
    		enrichmentController.loadPreferences();
    	} catch (Exception e) {
    		// Corrupted preferences, clear them
    		try {
    			log.warning("Failed to load preferences");
    			log.printStackTrace(e);
    			log.println("Clearing corrupted preferences...");
				ViewController.prefs.clear();
			} catch (BackingStoreException e1) {
				log.warning("Failed to clear prefences");
				throw new RuntimeException(e1);
			}
    	}
    }

    
	// ----------------------------------------------------------------------------
	
    /** Initialize the controls with saved/default settings */
    public void savePreferences() {
    	
    	preferencesController.savePreferences();
    	networksController.savePreferences();
    	enrichmentController.savePreferences();
    }
    
    
	// ----------------------------------------------------------------------------
	
    /** Apply settings from the given magnum settings instance */
    public void applySettings(MagnumSettings set) {
    	
    	enrichmentController.applySettings(set);
    }


	// ============================================================================
	// PRIVATE METHODS

    /** Initializes the root layout */
    private void initRootLayout() {
    	
    	rootLayoutController = (RootLayoutController) ViewController.loadFxml("view/RootLayout.fxml");
    	rootLayout = (BorderPane) rootLayoutController.getRoot();
        
    	// Show the scene containing the root layout.
    	Scene scene = new Scene(rootLayout);
    	primaryStage.setScene(scene);
    	primaryStage.show();
    }

    
	// ----------------------------------------------------------------------------

    /** "My networks" pane */
    private void showNetworkCollection() {

    	// Initialize network collection, needs to be done before loading controller
		networkCollection = new NetworkCollection();
    	// Initialize user networks pane
    	networksController = (NetworkCollectionController) ViewController.loadFxml("view/NetworkCollection.fxml");
    	
    	// Add to root layout
    	Node child = networksController.getRoot();
    	VBox.setVgrow(child, Priority.ALWAYS);
    	rootLayoutController.getLeftSide().getChildren().add(child);  
    }

    
	// ----------------------------------------------------------------------------

    /** "Connectivity enrichment" pane */
    private void showConnetivityEnrichmentPane() {

    	// Initialize user networks pane
    	enrichmentController = (EnrichmentController) ViewController.loadFxml("view/Enrichment.fxml");
    	// Add to root layout
    	rootLayoutController.getRightSide().getChildren().add(enrichmentController.getRoot());  
    }

    
	// ----------------------------------------------------------------------------

    /** "Console" pane */
    private void showConsolePane() {

    	consoleController = (ConsoleController) ViewController.loadFxml("view/Console.fxml");
    	// Add to root layout
    	TitledPane pane = (TitledPane) consoleController.getRoot();
    	pane.setExpanded(false);
    	rootLayoutController.getRightSide().getChildren().add(pane);
    	VBox.setVgrow(pane, Priority.ALWAYS);

    	// Link to logger
    	log.setConsole(consoleController.getConsoleTextArea());
    	// Copy previous output to console
    	consoleController.getConsoleTextArea().setText(log.getLogCopy());
    	log.disableLogCopy();
    }
    
    
	// ----------------------------------------------------------------------------

    /** "Credits" pane */
    private void showCreditsPane() {

    	creditsController = (CreditsController) ViewController.loadFxml("view/Credits.fxml");
    	// Add to root layout
    	HBox credits = (HBox) creditsController.getRoot();
    	rootLayoutController.getRightSide().getChildren().add(credits);
    	VBox.setVgrow(credits, Priority.NEVER);
    }


    
	// ============================================================================
	// SETTERS AND GETTERS

    public Stage getPrimaryStage() { return primaryStage; }
    public BorderPane getRootLayout() { return rootLayout; }
    
    public NetworkCollection getNetworkCollection() { return networkCollection; }

    public PreferencesDialogController getPreferencesController() { return preferencesController; }
	public EnrichmentController getEnrichmentController() {	return enrichmentController; }
	public NetworkCollectionController getOtherNetworksController() { return networksController; }
    
}
