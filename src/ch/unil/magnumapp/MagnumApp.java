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

import java.io.IOException;

import ch.unil.magnumapp.model.*;
import ch.unil.magnumapp.view.*;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * The main class starting the JavaFX app
 */
public class MagnumApp extends Application {

	/** The main stage */
    private Stage primaryStage;
    /** The root layout */
    private BorderPane rootLayout;
    
    /** The collection of networks */
    private NetworkCollection networkCollection_ = null;
    
    /** Root layout controller */
    private RootLayoutController rootLayoutController;
    
	// ============================================================================
	// STATIC METHODS

	/** Main */
	public static void main(String[] args) {
		// Calls start()
		launch(args);
	}

	
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public MagnumApp() {
	
		networkCollection_ = new NetworkCollection();
	}
	
	
	// ----------------------------------------------------------------------------

	/** Called when the App is launched */
	@Override
	public void start(Stage primaryStage) {
		
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Magnum");

        // The root layout
        initRootLayout();
        
        // Panes on the left side
        showMyNetworks();
	}
	
	
	// ----------------------------------------------------------------------------

    /** Initializes the root layout */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MagnumApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            rootLayoutController = loader.getController();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
	// ----------------------------------------------------------------------------

    /** "My networks" pane */
    public void showMyNetworks() {
        try {
            // Load fxml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MagnumApp.class.getResource("view/MyNetworks.fxml"));
            TitledPane myNetworks = (TitledPane) loader.load();          
            
            // Add to root layout
            rootLayoutController.getLeftSide().getChildren().add(myNetworks);
            
            // Give the controller access to the main app.
            MyNetworksController controller = loader.getController();
            controller.setMagnumApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
	// ============================================================================
	// DATA ACCESS

    public ObservableList<NetworkModel> getUserNetworks() {
    	return networkCollection_.getUserNetworks().getNetworks();
    }
    
    
	// ============================================================================
	// SETTERS AND GETTERS

    public Stage getPrimaryStage() { return primaryStage; }
    
    public NetworkCollection getNetworkCollection() { return networkCollection_; }
    
}
