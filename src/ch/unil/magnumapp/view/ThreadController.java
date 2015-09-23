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

import ch.unil.magnumapp.MagnumAppLogger;
import ch.unil.magnumapp.ThreadMagnum;
import edu.mit.magnum.Magnum;
import edu.mit.magnum.MagnumLogger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Controller for a single "launch job" dialog managing multiple threads
 */
public class ThreadController {

	/** Status of thread */
	public enum Status { ONGOING, INTERRUPTED, SUCCESS, ERROR };
	
	/** The thread -- this will become an array! */
	private ThreadMagnum thread;
	/** Status of thread */
	private Status status;
	
	/** The javafx alert / dialog */
	private Alert alert;
	/** The ok button */
	private Button okButton;
	/** The cancel button */
	private Button stopButton;
	/** The progress indicator */
	private ProgressIndicator progressIndicator;
	/** The text area -- this will become an arrya! */
	private TextArea console; 
	/** The dialog content (status message) */
	private TextField statusMessage;

	
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public ThreadController(ThreadMagnum thread) {

		this.thread = thread;
		status = Status.ONGOING;
		
		thread.setController(this);
	}

	
	// ----------------------------------------------------------------------------

	/** Open the dialog, start the thread */
	public void start() {
		
		// Copy stdout to the console
		Magnum.log = new MagnumAppLogger(this);
		// Create the dialog
		initDialog();
		// Start the thread
    	thread.start();
    	// Show the dialog
    	alert.showAndWait();
		// Remove the custom logger
		Magnum.log = new MagnumLogger();
	}
	
	
	// ----------------------------------------------------------------------------

	/** Called by the thread in case of success (not part of the FX thread!) */
	public void success() {
		
		status = Status.SUCCESS;
		okButton.setDisable(false);
		stopButton.setDisable(true);
		progressIndicator.setVisible(false);
		statusMessage.textProperty().setValue("Status: DONE!");
    	statusMessage.setStyle("-fx-text-fill: green; -fx-font-weight: bold");
		print("\nSuccess yo!");
	}

	
	// ----------------------------------------------------------------------------

	/** Called by the thread in case of error (not part of the FX thread!) */
	public void error(Exception e) {
		
		printException(e);
		status = Status.ERROR;
		okButton.setDisable(false);
		stopButton.setDisable(true);
		progressIndicator.setVisible(false);
		statusMessage.textProperty().setValue("ERROR: " + e.toString());
    	statusMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold");
	}

	
	// ----------------------------------------------------------------------------

	/** Called by the thread in case of error (not part of the FX thread!) */
	public void interrupt() {
		
		Magnum.log.println("JOB STOPPED!");
		status = Status.INTERRUPTED;
		okButton.setDisable(false);
		stopButton.setDisable(true);
		progressIndicator.setVisible(false);
		statusMessage.textProperty().setValue("Status: JOB INTERRUPTED");
    	statusMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold");
	}


	
	// ----------------------------------------------------------------------------

	/** Print to console of this thread */
	public void print(String msg) {
		
		Platform.runLater(new Runnable() {
		    @Override 
		    public void run() {
		    	console.appendText(msg);
		    }
		});
	}

	
	/** Prints the exception to the custom outputs (not stdout) */
	public void printException(Exception e) {
		
		// Print exception to string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();
		
		// Print exception text to console
		print(exceptionText + "\nABORTED WITH ERROR!");
	}	


	
	// ============================================================================
	// PRIVATE

	/** Creates the alert dialog */
	private void initDialog() {
		
    	alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Job launched");
    	alert.setHeaderText("Running connectivity enrichment analysis...");
    	statusMessage = new TextField("Status: ONGOING");
    	statusMessage.setStyle("-fx-text-fill: blue");
    	alert.getDialogPane().setContent(statusMessage);
    	
    	// Set the icon
    	progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1);
    	alert.setGraphic(progressIndicator);

    	// Disable the ok button
    	ButtonType stopButtonType = new ButtonType("Stop", ButtonData.OTHER);
    	//ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
    	alert.getButtonTypes().setAll(stopButtonType, ButtonType.OK);
    	
        okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        stopButton = (Button) alert.getDialogPane().lookupButton(stopButtonType);
        stopButton.setOnAction((event) -> {
        	Magnum.log.println();
            Magnum.log.warning("INTERRUPT SENT, WAITING FOR THREAD TO RESPOND...");
            thread.interrupt();
        });

        // Allow the dialog to be closed only if the thread stopped (success, error, or interrupt)
        alert.setOnCloseRequest(event -> {
        	if (status == Status.ONGOING)
        		event.consume();
        });
        
    	// The console
    	console = new TextArea();
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
