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
package edu.mit.magnumapp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MagnumApp extends Application {

	/** The primary stage */
    private Stage primaryStage_;
    /** The root layout */
    private BorderPane rootLayout_;
    
    
	// ============================================================================
	// STATIC METHODS

	/** Main */
	public static void main(String[] args) {
		// Calls start()
		launch(args);
	}

	
	// ============================================================================
	// PUBLIC METHODS

	/** Called when the App is launched */
	@Override
	public void start(Stage primaryStage) {
		
        primaryStage_ = primaryStage;
        primaryStage_.setTitle("oiseauApp");

        initRootLayout();

        showPersonOverview();
	}
	
	
	// ----------------------------------------------------------------------------

    /** Initializes the root layout */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MagnumApp.class.getResource("view/RootLayout.fxml"));
            rootLayout_ = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout_);
            primaryStage_.setScene(scene);
            primaryStage_.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
	// ----------------------------------------------------------------------------

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MagnumApp.class.getResource("view/Overview.fxml"));
            SplitPane personOverview = (SplitPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout_.setCenter(personOverview);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage_;
    }
    
}
