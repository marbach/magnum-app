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
package ch.unil.magnumapp.view;

import java.io.PrintWriter;
import java.io.StringWriter;

import ch.unil.magnumapp.ThreadMagnum;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Runnable class for loading networks
 */
public class ThreadController {

	/** Status of thread */
	public enum Status { ONGOING, SUCCESS, ERROR };
	
	/** The thread */
	private ThreadMagnum thread;
    /** Status for progress indicator (-1: undetermined) */
	private double progress;
	/** Status of thread (0: ongoing, 1: success, -1: error) */
	private Status status;
	
	/** The javafx alert / dialog */
	private Alert alert;
	/** The ok button */
	private Node okButton;
	/** The cancel button */
	private Node cancelButton;
	/** The progress indicator */
	private ProgressIndicator progressIndicator;
	/** The text area */
	private TextArea console; 
	/** The dialog content (status message) */
	private TextField statusMessage;

	
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public ThreadController(ThreadMagnum thread) {

		this.thread = thread;
		progress = -1;
		status = Status.ONGOING;
		
		thread.setController(this);
	}

	
	// ----------------------------------------------------------------------------

	/** Open the dialog, start the thread */
	public void start() {
		
		// Open the dialog
		initDialog();
		// Start the thread
    	thread.start();
    	alert.showAndWait();
    	
	}
	
	
	// ----------------------------------------------------------------------------

	/** Called by the thread in case of success (not part of the FX thread!) */
	public void success() {
		
		status = Status.SUCCESS;
		okButton.setDisable(false);
		cancelButton.setDisable(true);
		progressIndicator.setVisible(false);
		statusMessage.textProperty().setValue("Status: DONE!");
    	statusMessage.setStyle("-fx-text-fill: green; -fx-font-weight: bold");
		console.appendText("\nSuccess!");
	}

	
	// ============================================================================
	// PRIVATE

	/** Creates the alert dialog */
	private void initDialog() {
		
    	alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Job launched");
    	alert.setHeaderText("Loading networks...");
    	statusMessage = new TextField("Status: ONGOING");
    	statusMessage.setStyle("-fx-text-fill: blue");
    	alert.getDialogPane().setContent(statusMessage);
    	
    	// Set the icon
    	progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1);
    	alert.setGraphic(progressIndicator);

    	// Disable the ok button
        okButton = alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        cancelButton = alert.getDialogPane().lookupButton(ButtonType.CANCEL);

        Exception ex = new RuntimeException("Could not find file blabla.txt");

    	// Create expandable Exception.
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	ex.printStackTrace(pw);
    	String exceptionText = sw.toString();

    	// The console
    	console = new TextArea(exceptionText);
    	console.setEditable(false);
    	console.setWrapText(true);

    	console.setMaxWidth(Double.MAX_VALUE);
    	console.setMaxHeight(Double.MAX_VALUE);
    	console.setPrefWidth(700);
    	GridPane.setVgrow(console, Priority.ALWAYS);
    	GridPane.setHgrow(console, Priority.ALWAYS);

    	// This could be a pane with tabs for multiple threads
    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	Label label = new Label("Console");
    	expContent.add(label, 0, 0);
    	expContent.add(console, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);
    	alert.getDialogPane().setExpanded(true);
	}
	
	
	// ============================================================================
	// SETTERS AND GETTERS


}
