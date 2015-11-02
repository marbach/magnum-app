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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class for settings
 */
final public class AppSettings {

	// ============================================================================
	// GENERAL

	/** MagnumApp version */
	final static public String magnumAppVersion = "Magnum v1.0";
	
	/** Website */
	final static public String regulatoryCircuitsLink = "http://regulatorycircuits.org";
	/** Paper */
	final static public String marbachEtAlLink = "http://regulatorycircuits.org/papers.html";
	/** Daniel Marbach email */
	final static public String danielContactLink = "mailto:daniel.marbach@gmail.com";
	
	// ============================================================================
	// HELP DIALOG
	
	/** User guide */
	final static public String helpLink = "http://regulatorycircuits.org/help.html";
	/** GitHub wiki */
	final static public String gitHubWikiLink = "https://github.com/marbach/magnum-app/wiki";
	/** GitHub issues */
	final static public String gitHubIssuesLink = "https://github.com/issues";
	
	// ============================================================================
	// ABOUT DIALOG

	/** Daniel Marbach link */
	final static public String danielLink = "http://www2.unil.ch/cbg/index.php?title=User:Daniel";
	/** Sven Bergmann link */
	final static public String svenLink = "http://www2.unil.ch/cbg/index.php?title=User:Sven";
	/** MIT license */
	final static public String mitLicenseLink = "https://github.com/marbach/magnum-app/wiki/License";

	// ============================================================================
	// NETWORK DOWNLOAD DIALOG
	
	/** Partial network compendium */
	final static public String networkCompendiumLink = "http://www2.unil.ch/cbg/regulatorycircuits/Network_compendium.zip";
	/** Full network compendium */
	final static public String individualNetworksLink = "http://www2.unil.ch/cbg/regulatorycircuits/FANTOM5_individual_networks.zip";

	// ============================================================================
	// ENRICHEMENT PANEL

	/** Download gene scores */
	final static public String geneScoresLink = "http://regulatorycircuits.org/data/GWAS_gene_scores_v1.zip";
	/** Download PASCAL */
	final static public String pascalLink = "http://www2.unil.ch/cbg/index.php?title=Pascal";
	/** Example p-value file */
	final static public String examplePvalFileLink = "http://regulatorycircuits.org/data/psychiatric_cross_disorder.pvals.txt";
	/** Download R-scripts */
	final static public String downloadRScriptsLink = "http://regulatorycircuits.org/data/Magnum-1.0_command-line.zip";
	
	
	// ============================================================================
	// PUBLIC METHODS
	
    /** Copy a file from the jar to the file system */
    public static File exportResource(String resource, File targetDir) {
    	
		String fileName = new File(resource).getName();
		InputStream inStream = App.class.getClassLoader().getResourceAsStream(resource);
		Path dest = new File(targetDir, fileName).toPath();
		
		try {
			if (!dest.toFile().exists())
				Files.copy(inStream, dest);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return dest.toFile();
    }

    

}
