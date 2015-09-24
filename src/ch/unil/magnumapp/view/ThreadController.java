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
import java.util.ArrayList;


import javafx.scene.shape.Rectangle;
import ch.unil.magnumapp.MagnumAppLogger;
import ch.unil.magnumapp.ThreadMagnum;
import edu.mit.magnum.Magnum;
import edu.mit.magnum.MagnumLogger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller for a single "launch job" dialog managing multiple threads
 */
public class ThreadController extends ViewController {

	/** Flag indicates if jobs are interrupted */
	volatile private boolean interrupted = false;

	/** Number of cores to be used for jobs */
	private int numCores;
	/** Thread id, used to prevent reacting self from interrupt flag */
	private long threadId;
	
	/** The thread -- this will become an array! */
	private ArrayList<ThreadMagnum> jobs;
	/** The next job in line */
	volatile private int nextJob;
	/** No more jobs are running (they finished with success, error or interrupt) */
	private boolean allDone;
	
	/** Jobs in queue */
	volatile private IntegerProperty numJobsQueued = new SimpleIntegerProperty();
	/** Jobs running */
	volatile private IntegerProperty numJobsRunning = new SimpleIntegerProperty();
	/** Jobs finished successfully */
	volatile private IntegerProperty numJobsFinished = new SimpleIntegerProperty();
	/** Jobs aborted (interrupt or error) */
	volatile private IntegerProperty numJobsAborted = new SimpleIntegerProperty();
	
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
	
	
	// ============================================================================
	// FXML -- thread status

	@FXML
	private VBox threadStatusVBox;
	@FXML
	private Label statusLabel;
	@FXML
	private GridPane statusGridPane;

	@FXML
	private Rectangle numQueuedRectangle;
	@FXML
	private Rectangle numRunningRectangle;
	@FXML
	private Rectangle numFinishedRectangle;
	@FXML
	private Rectangle numAbortedRectangle;
	
	@FXML
	private Label numQueuedLabel;
	@FXML
	private Label numRunningLabel;
	@FXML
	private Label numFinishedLabel;
	@FXML
	private Label numAbortedLabel;
	
	
	// ============================================================================
	// PUBLIC METHODS

	/** Open the dialog, start the thread */
	public void start(ArrayList<ThreadMagnum> jobs, int numCores) {
		
		// Initialize
		initializeJobs(jobs, numCores);
		
		// Copy stdout to the console
		Magnum.log = new MagnumAppLogger(this);
		// Create the dialog
		initializeDialog();
		
		// Start the first job (upon completion, success() will start the next job)
		startNextJob();
    	
    	// Show the dialog
    	alert.showAndWait();
		
    	// Remove the custom logger
		Magnum.log = new MagnumLogger();
	}
	
	private void startNextJob() {

		// Update counts before start for good measure
		int index = nextJob;
    	nextJob++;
    	reduce(numJobsQueued);
    	increment(numJobsRunning);
    	assert assertJobCountsConsistency();
    	
    	// Print info
    	//Magnum.log.println("STARTING JOB: ");
    	
    	// Start the job
    	jobs.get(index).start();
	}
	
	private void increment(IntegerProperty prop) {
		prop.set(prop.get() + 1);
	}
	
	private void reduce(IntegerProperty prop) {
		prop.set(prop.get() - 1);
	}
	
	
	/** Always returns true, asserts that job counts are consistent */
	private boolean assertJobCountsConsistency() {
		
		int total = numJobsQueued.get() + numJobsRunning.get() + numJobsFinished.get() + numJobsAborted.get();
		assert total == jobs.size();
		assert nextJob == total - numJobsQueued.get();
		return true;
	}

	
	// ----------------------------------------------------------------------------

	/** JavaFX thread: scheduled using Platform.runLater() by the launched jobs upon completion */
	public void jobFinished(Exception e) {
		
		reduce(numJobsRunning);

		if (interrupted) {
			increment(numJobsAborted);
			if (numJobsRunning.get() == 0)
				allJobsDone();

		} else {
			if (e == null)
				increment(numJobsFinished);
			else
				increment(numJobsAborted);
			
			if (numJobsQueued.get() != 0)
				startNextJob();
			else if (numJobsRunning.get() == 0)
				allJobsDone();
		}
	}
	
	
	// ----------------------------------------------------------------------------

	/** Finished, release the ok button etc. */
	public void allJobsDone() {

		// Update controls
		okButton.setDisable(false);
		stopButton.setDisable(true);
		progressIndicator.setVisible(false);
		allDone = true;
		
		if (interrupted) {
			updateStatusLabel("Status: JOBS STOPPED!", "status-error-label");
			statusGridPane.setDisable(true);
		
		} else if (numJobsAborted.get() > 0) {
			updateStatusLabel("Status: FINISHED WITH ERRORS! (See console and log files for details)", "status-error-label");
		
		} else {
			updateStatusLabel("Status: DONE!", "status-success-label");
		}
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
	private void initializeDialog() {
		
		// The alert
    	alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Connectivity enrichment");
    	alert.getDialogPane().setPrefHeight(650);
    	
    	// Header text
    	String networkS = ((jobs.size() == 1) ? "" : "s");
    	String coreS = ((numCores == 1) ? "" : "s");
    	alert.setHeaderText("Running: " + jobs.size() + " network" + networkS + "... " 
    			+ "(using " + numCores + " core" + coreS + ")");
    	
    	// The content
    	//alert.getDialogPane().setContent(statusMessage);
    	alert.getDialogPane().setContent(threadStatusVBox);
    	updateStatusLabel("Status: ONGOING", "status-ongoing-label");
    	
    	// Set the icon
    	progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1);
    	alert.setGraphic(progressIndicator);

    	// Disable the ok button
    	ButtonType stopButtonType = new ButtonType("Stop", ButtonData.CANCEL_CLOSE);
    	//ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
    	alert.getButtonTypes().setAll(stopButtonType, ButtonType.OK);
    	
        okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        stopButton = (Button) alert.getDialogPane().lookupButton(stopButtonType);
        stopButton.setOnAction((event) -> {
        	Magnum.log.println();
            Magnum.log.warning("INTERRUPT SENT -- WAITING FOR THREADS ...");
            updateStatusLabel("Status: STOPPING JOBS ...", "status-error-label");
            interrupted = true;
        });

        // Allow the dialog to be closed only if the thread stopped (success, error, or interrupt)
        alert.setOnCloseRequest(event -> {
        	if (!allDone)
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
    	
    	// Bindings
    	numQueuedLabel.textProperty().bind(Bindings.convert(numJobsQueued));
    	numRunningLabel.textProperty().bind(Bindings.convert(numJobsRunning));
    	numFinishedLabel.textProperty().bind(Bindings.convert(numJobsFinished));
    	numAbortedLabel.textProperty().bind(Bindings.convert(numJobsAborted));
    	
    	double maxWidth = 175;
    	numQueuedRectangle.widthProperty().bind(numJobsQueued.multiply(maxWidth/jobs.size()).add(1));
    	numRunningRectangle.widthProperty().bind(numJobsRunning.multiply(maxWidth/jobs.size()).add(1));
    	numFinishedRectangle.widthProperty().bind(numJobsFinished.multiply(maxWidth/jobs.size()).add(1));
    	numAbortedRectangle.widthProperty().bind(numJobsAborted.multiply(maxWidth/jobs.size()).add(1));
	}
	
	
	// ----------------------------------------------------------------------------

	/** Initialize with a list of jobs */
	private void initializeJobs(ArrayList<ThreadMagnum> jobs, int numCores) {

		if (jobs == null || jobs.isEmpty())
			throw new RuntimeException("Null or empty job list");
		
		this.jobs = jobs;
		this.numCores = numCores;
		allDone = false;
		threadId = Thread.currentThread().getId();
		
		numJobsQueued.set(jobs.size());
		numJobsRunning.set(0);
		numJobsFinished.set(0);
		numJobsAborted.set(0);
		nextJob = 0;

		for (ThreadMagnum job_i : jobs)
			job_i.setController(this);
	}

	
	// ----------------------------------------------------------------------------

	/** Update text and style of status label */
	private void updateStatusLabel(String msg, String styleClass) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add(styleClass);
	}
	
	
	// ============================================================================
	// SETTERS AND GETTERS

	public long getThreadId() {
		return threadId;
	}


	public boolean getInterrupted() {
		return interrupted;
	}


	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

}
