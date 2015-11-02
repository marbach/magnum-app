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
import java.util.prefs.BackingStoreException;

import ch.unil.magnumapp.App;
import edu.mit.magnum.Magnum;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.FileChooser;


/**
 * Abstract class for controllers 
 */
public class PreferencesDialogController extends ViewController {

	/** The dialog pane (that's the root) */
	private DialogPane dialogPane;
	/** The dialog */
	private Dialog<ButtonType> dialog;
			
	
	// ============================================================================
	// FXML

	@FXML
    private CheckBox rememberSettingsCheckBox;
	@FXML
    private Button resetToDefaultsButton;
	@FXML
    private Button loadFromFileButton;

	
	// ============================================================================
	// PUBLIC METHODS
	
    /** Load saved/default preferences */
	@Override
	public void loadPreferences() {
        rememberSettingsCheckBox.setSelected(prefs.getBoolean("rememberSettings", true));
	}
	
	/** Save preferences */
	@Override
	public void savePreferences() {
		prefs.putBoolean("rememberSettings", rememberSettingsCheckBox.isSelected());
	}
	
	
	// ----------------------------------------------------------------------------

	/** 
	 * Initialize settings based on the loaded settings file (do not call this initialize(),
	 * otherwise it get's called by the FXML loader...)
	 */
	@Override
	public void init() {
		
    	// The dialog pane defined in the fxml file
    	dialogPane = (DialogPane) root;
    	// (The OK button is not available in scene builder...)
    	dialogPane.getButtonTypes().add(ButtonType.OK);
    	
    	// Construct dialog with this pane
    	// (Only the DialogPane, not the Dialog can be constructed in scene builder) 
    	dialog = new Dialog<>();
    	dialog.setTitle("Settings");
    	dialog.setDialogPane(dialogPane);
	}
	    
	
	// ----------------------------------------------------------------------------

    /** Show the preferences dialog */
    public void show() {

    	// Note, we update the settings independently of how the window was closed
    	dialog.showAndWait(); // Controls remember their status after closing the dialog...
    }

    
	// ============================================================================
	// HANDLES
    
    /** Reset button handle */
    @FXML
    private void handleResetToDefaultsButton() {
    	
        try {
			prefs.clear();
		} catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}
    	app.loadPreferences();
    }

    
	// ----------------------------------------------------------------------------

    /** Load from file button handle */
    @FXML
    private void handleLoadFromFileButton() {
    	
    	// File chooser
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select a settings file");
    	File file = fileChooser.showOpenDialog(app.getPrimaryStage());
    	if (file == null)
    		return;
    	
    	// Load settings
    	Magnum mag = new Magnum(App.log);
    	mag.set.loadSettings(file.getAbsolutePath());
    	// Apply settings
    	App.app.applySettings(mag.set);
    	dialog.close();
    }

    
	// ============================================================================
	// GETTERS AND SETTERS

    
}
