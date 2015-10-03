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

import ch.unil.magnumapp.App;
import ch.unil.magnumapp.AppSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller for "Other networks" pane 
 */
public class SimpleInfoController extends ViewController {

	/** Load a simple alert, the content of which is defined in the fxml file */
	public static void show(String fxmlPath, String title, String header) {
		
    	// Disable the main window
    	App.app.getRootLayout().setDisable(true);

    	// Load the content
    	SimpleInfoController controller = (SimpleInfoController) ViewController.loadFxml(fxmlPath);

		// The alert
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle(title);
    	// Header text
    	alert.setHeaderText(header);    	
    	// The content
    	alert.getDialogPane().setContent(controller.getRoot());

    	// Show the dialog
    	alert.showAndWait();
		// Enable the main window
    	App.app.getRootLayout().setDisable(false);

	}
	
	// ============================================================================
	// GENERAL HANDLES

    @FXML
    private void handleRegulatoryCircuitsLink() {
    	openWebpage(AppSettings.regulatoryCircuitsLink);
    }

    @FXML
    private void handleMarbachEtAlLink() {
    	openWebpage(AppSettings.marbachEtAlLink);
    }

    @FXML
    private void handleDanielEmailLink() {
    	openWebpage(AppSettings.danielContactLink);
    }

    
	// ============================================================================
	// ABOUT HANDLES

    @FXML
    private void handleDanielLink() {
    	openWebpage(AppSettings.danielLink);
    }

    @FXML
    private void handleSvenLink() {
    	openWebpage(AppSettings.svenLink);
    }

    @FXML
    private void handleMITLicenseLink() {
    	openWebpage(AppSettings.mitLicenseLink);
    }


    
	// ============================================================================
	// HELP HANDLES

    @FXML
    private void handleHelpLink() {
    	openWebpage(AppSettings.helpLink);
    }

    @FXML
    private void handleGitHubWikiLink() {
    	openWebpage(AppSettings.gitHubWikiLink);
    }

    @FXML
    private void handleGitHubIssuesLink() {
    	openWebpage(AppSettings.gitHubIssuesLink);
    }
    
    
	// ============================================================================
	// NETWORK DOWNLOAD HANDLES

    @FXML
    private void handleNetworkCompendiumLink() {
    	openWebpage(AppSettings.networkCompendiumLink);
    }

    @FXML
    private void handleIndividualNetworksLink() {
    	openWebpage(AppSettings.individualNetworksLink);
    }


}
