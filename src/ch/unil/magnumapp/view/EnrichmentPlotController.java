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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.unil.magnumapp.App;
import edu.mit.magnum.FileParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

/**
 * Controller for a single "launch job" dialog managing multiple threads
 */
public class EnrichmentPlotController {

	/** The GWAS name */
	private String gwasName;
	/** The pvals */
	private ArrayList<Double> pvals;
	/** The networks */
	private ObservableList<String> networks;
	
	/** The dialog */
	private Dialog<ButtonType> dialog;
	/** The dialog pane */
	private DialogPane dialogPane;

    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
    public EnrichmentPlotController(File pvalFile, boolean bonferroni) {
		
    	// Read the p-value file
    	App.log.println("Reading p-value file ...");
    	FileParser reader;
    	if (pvalFile != null)
    		reader = new FileParser(App.log, pvalFile);
    	else {
    		InputStream in = App.class.getClassLoader().getResourceAsStream("ch/unil/magnumapp/resources/fantom5_networks/psychiatric_cross_disorder.pvals.txt");
    		reader = new FileParser(in);
    	}
    	
    	// The header
    	String[] header = reader.readLine();
    	if (header.length < 1)
    		reader.error("Empty header");
    	
    	header = header[0].split("=");
    	if (header.length != 2)
    		reader.error("Expected header: '# GWAS = <gwas_name>'");
    	// Remove trailing whitespace
    	gwasName = header[1].trim();
    	
    	// Skip the column names
    	reader.skipLine();
    	
    	// Put values in a map
    	LinkedHashMap<String, Double> map = new LinkedHashMap<>();
    	while (true) {
    		String[] nextLine = reader.readLine();
    		if (nextLine == null)
    			break;
    		
    		if (nextLine.length < 2)
    			reader.error("Expected at least two columns");
    		map.put(nextLine[0], Double.parseDouble(nextLine[1]));
    	}
    	reader.close();

    	// Sort the map and fill pvals and networks
    	pvals = new ArrayList<>();
    	networks = FXCollections.observableArrayList();
    	map.entrySet().stream()
    		.sorted(Map.Entry.comparingByValue())
    		.forEach(entry -> {
        		networks.add(entry.getKey());
        		pvals.add(entry.getValue());
    		});
    	Collections.reverse(pvals);
    	Collections.reverse(networks);
    	
    	// Bonferroni correction
    	if (bonferroni) {
    		for (int i=0; i<pvals.size(); i++)
    			pvals.set(i, pvals.get(i)*pvals.size());
    	}
	}
	    
	
	// ----------------------------------------------------------------------------

    /** Show the preferences dialog */
    public void show() {

    	initialize();
    	dialog.showAndWait();
    }

	
	// ============================================================================
	// PRIVATE

    /** Initialize the dialog */
    private void initialize() {
    	
		// Create dialog
    	dialog = new Dialog<>();
    	dialogPane = dialog.getDialogPane();
    	dialog.setTitle("Connectivity enrichment scores");
    	dialog.setResizable(true);

    	// Add buttons
    	dialogPane.getButtonTypes().add(ButtonType.CLOSE);

    	// Create the axes
    	NumberAxis xAxis = new NumberAxis();
    	CategoryAxis yAxis = new CategoryAxis();
    	xAxis.setLabel("–log10(p-value)");  
    	xAxis.setMinorTickVisible(false);
    	//xAxis.setTickLabelRotation(90);
    	yAxis.setLabel("Network");        
    	yAxis.setCategories(networks);

    	// Create the chart
    	BarChart<Number,String> chart = new BarChart<>(xAxis,yAxis);
    	chart.setTitle(gwasName);
    	chart.setLegendVisible(false);
    	chart.setCategoryGap(2);
    	chart.setBarGap(0);
    	dialogPane.setContent(chart);

    	// Add the data
    	XYChart.Series<Number, String> series = new XYChart.Series<>();
    	double maxScore = 0;
    	for (int i=0; i<pvals.size(); i++) {
    		double score = -Math.log10(pvals.get(i));
    		if (score > maxScore)
    			maxScore = score;
    		Data<Number, String> data = new XYChart.Data<Number, String>(score, networks.get(i));
    		series.getData().add(data);
    	}
    	chart.getData().add(series);

    	// Set x-range
    	xAxis.setAutoRanging(false);
    	xAxis.setLowerBound(0);
    	xAxis.setUpperBound(Math.max(2, Math.ceil(2*maxScore)/2.0));
    	xAxis.setTickUnit(0.5);

    	// Determine size
    	dialogPane.setPrefWidth(600);
    	dialogPane.setPrefHeight(networks.size()*22.5 + 180);

    }
	
	// ============================================================================
	// SETTERS AND GETTERS


}
