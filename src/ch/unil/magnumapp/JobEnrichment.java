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

import ch.unil.magnumapp.model.NetworkModel;
import ch.unil.magnumapp.view.EnrichmentController;
import ch.unil.magnumapp.view.JobController;
import edu.mit.magnum.FileExport;
import edu.mit.magnum.MagnumLogger;

/**
 * Runnable class for loading networks
 */
public class JobEnrichment extends JobMagnum {

	/** The network */
    private NetworkModel network;
    /** The controller with the settings */
    private EnrichmentController controller;
    
    /** Output directory */
    private File outputDir;
    
    /** The settings file */
    private File settingsFile;
    
    
	// ============================================================================
	// PUBLIC METHODS

	/** Constructor */
	public JobEnrichment(JobController jobManager, String jobName, EnrichmentController controller, NetworkModel network) {

		super(jobManager, jobName);
		this.controller = controller;
		this.network = network;
		
    	outputDir = controller.getOutputDir();
	}

	
	// ----------------------------------------------------------------------------

	/** Main method called by the thread */
	@Override
	protected void runJob() {
		
		myMag.log.println("\nRunning job: " + jobName);

    	// Write settings
    	writeSettingsFile(myMag.log);

		// Load settings file
		myMag.set.loadSettings(settingsFile.getAbsolutePath(), false);
		// Run
		myMag.run();
	}
	
	
	// ----------------------------------------------------------------------------

    /** Write settings file for magnum */
    public void writeSettingsFile(MagnumLogger log) {
    	
    	log.println("Writing settings file...");
    	
    	// The content of the file
    	String text = "##########################################################################\n"
    			+ "# SETTINGS FILE --- Magnum v1.0\n"
    			+ "#\n"
    			+ "# This file can be used to:\n"
    			+ "# (1) Run the job from the command line (typically on a computing cluster)\n"
    			+ "# (2) Reload the settings in the App (click the \"Settings\" button)\n"
    			+ "#\n"
    			+ "# TIP: Keeping this file together with your results ensures that they can\n"
    			+ "#      always be reproduced!\n"
    			+ "#\n"
    			+ "# INSTRUCTIONS:\n"
    			+ "# To run the job from the command line, download the command-line tool\n"
    			+ "# (magnum_v1.0.jar) from regulatorycircuits.org and use the option --set\n"
    			+ "# to load this file (see user guide for details):\n"
    			+ "#\n"
    			+ "#    java -Xmx6g -jar magnum_v1.0.jar --set <settings_file>\n"
    			+ "#\n"
    			+ "# NOTE: If your run the job on a cluster, you have to edit the file paths\n"
    			+ "#       below so that they point to the right location.\n"
    			+ "##########################################################################\n"
    			+ "\n"
    			+ "############\n"
    			+ "# FILE PATHS\n"
    			+ "\n"
    			+ "# NOTE: spaces in path/file names are allowed (no need to escape)\n"
    			+ "\n"
    			+ "# The input network file [--net <file>]\n"
    			+ "networkFile = " + network.getFile().getAbsolutePath() + "\n"
    			+ "# Defines if the network should be interpreted as directed or undirected [--dir]\n"
    			+ "isDirected = " + network.getIsDirected() + "\n"
    			+ "# Set true to treat the network as weighted [--weighted]\n"
    			+ "isWeighted = " + network.getIsWeighted() + "\n"
    			+ "# Defines if self loops should be removed from the network [--noself]\n"
    			+ "removeSelfLoops = " + network.getRemoveSelf() + "\n"
    			+ "# The GWAS gene score file [--scores <file>]\n"
    			+ "geneScoreFile = " + controller.getGeneScoreFile().getAbsolutePath() + "\n"
    			+ "# Output directory to save files (empty = home directory; '.' = working directory) [--outdir]\n"
    			+ "outputDirectory = " + outputDir.getAbsolutePath() + "\n"
    			+ "\n"
    			+ "############\n"
    			+ "# PARAMETERS\n"
    			+ "\n"
    			+ "# Number of random permutations used to compute empirical p-values [--permut <int>]\n"
    			+ "numPermutations = " + controller.getNumPermutations() + "\n"
    			+ "\n"
    			+ "# Exclude HLA genes\n"
    			+ "excludeHlaGenes = " + controller.getExcludeHlaGenes() + "\n"
    			+ "# Exclude X and Y chromosomes\n"
    			+ "excludeXYChromosomes = " + controller.getExcludeXYChromosomes() + "\n"
    			+ "\n"
    			+ "# Use precomputed network kernels if available in networkKernelDir\n"
    			+ "usePrecomputedKernels = " + controller.getUsePrecomputedKernels() + "\n"
    			+ "# Directory for network kernels (default: <outputDir>/network_kernels/)\n"
    			+ "networkKernelDir = " + controller.getKernelDir().getAbsolutePath() + "\n"
    			+ "# Save network kernels for use in subsequent runs (takes a lot of space!)\n"
    			+ "exportKernels = " + controller.getExportKernels() + "\n"
    			+ "\n"
    			+ "# Tell magnum to launch connectivity enrichment analysis\n"
    			+ "mode = 3\n";
    			
    	// The settings file
    	String filename = jobName + ".settings.txt";
    	settingsFile = new File(outputDir, filename);

    	// Write the file
    	FileExport out = new FileExport(log, settingsFile);
    	out.print(text);  	
    	out.close();
    }

	
	// ============================================================================
	// PRIVATE METHODS


	// ============================================================================
	// SETTERS AND GETTERS



}
