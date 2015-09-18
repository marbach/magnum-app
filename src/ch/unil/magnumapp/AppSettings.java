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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import edu.mit.magnum.FileExport;
import edu.mit.magnum.Magnum;
import edu.mit.magnum.MagnumUtils;
import edu.mit.magnum.Settings;

/**
 * Class for settings
 */
final public class AppSettings {


	// ============================================================================
	// STATIC

	/** The preferences */
	final static Preferences prefs = Preferences.userNodeForPackage(MagnumApp.class);

	/** The magnum app instance*/
	final static private MagnumApp app = MagnumApp.getInstance();
	/** MagnumApp version */
	final static public String magnumAppVersion = "Magnum v1.0";

	/** Download gene scores */
	final static public String geneScoresLink = "http://regulatorycircuits.org/data/GWAS%20gene%20scores%20v1.zip";
	/** Download PASCAL */
	final static public String pascalLink = "http://www2.unil.ch/cbg/index.php?title=Pascal";

	/** The configuration file with the settings (leave empty for default settings) */
	//final static public File settingsFile = Paths.get(System.getProperty("user.home"), ".magnum.txt").toFile();	

	// SETTINGS VARIABLES
	// Strategy: We don't keep them synced with the controllers => update before accessing any values
	// => other classes should take values from the controllers, not from here
	// (or we could bind them and always take from here, maybe better?)
	
	/** Remember settings */
	static public Boolean rememberSettings;
	
	/** Network collection directory */
	static public File networkCollectionDir;
	
	/** Use precomputed network kernels if available */
	static public boolean usePrecomputedKernels;
	/** GWAS gene score file */
	static public File geneScoreFile;
	/** Output directory */
	static public File outputDir;
	/** Delete network kernels after completion */
	static public boolean deleteKernels;
	/** Number of permutations */
	static public int numPermutations;
	/** Exclude HLA genes */
	static public boolean excludeHlaGenes;
	/** Exclude X, Y chromosomes */
	static public boolean excludeAllosomes;


	// ============================================================================
	// PUBLIC

    /** Reset all settings to their default values */
    static public void setDefaults() {
    	
        try {
            // Reset
			prefs.clear();
	        // Load (will get the defaults)
	        loadSettings();
	        
		} catch (BackingStoreException e) {
			Magnum.log.error(e.toString());
		}
    }
    
    
	// ----------------------------------------------------------------------------

    /** Load settings from .magnum.txt file in user directory */
    static public void loadSettings() {

    	Magnum.log.println("\nLoading Magnum App settings...");      
        rememberSettings = prefs.getBoolean("rememberSettings", true);
    	if (!rememberSettings)
    		return;
    	
    	networkCollectionDir = getFilePreference("networkCollectionDir");
    	usePrecomputedKernels = prefs.getBoolean("usePrecomputedKernels", true);
    	geneScoreFile = getFilePreference("geneScoreFile");
    	outputDir = getFilePreference("outputDir");
    	deleteKernels = prefs.getBoolean("deleteKernels", true);
    	numPermutations = prefs.getInt("numPermutations", 10000);
    	excludeHlaGenes = prefs.getBoolean("excludeHlaGenes", true);
    	excludeAllosomes = prefs.getBoolean("excludeAllosomes", true);

//		try {
//	    	if (!settingsFile.exists()) {
//	    		Magnum.log.println("- Settings file was not saved");
//	    		return;
//	    	}
//	    	
//	    	// Load properties
//			Magnum.log.println("- Loading settings file: " + settingsFile.toString());
//			InputStream in = new FileInputStream(settingsFile);
//			set = new Properties();
//			set.load(new InputStreamReader(in));
//			
//			// Extract to instance variables
//			setParameterValues();
//			
//		} catch (Exception e) {
//			Magnum.log.warning(e.getMessage());
//			Magnum.log.println("- Failed to load settings file.");
//			Magnum.log.println("- Deleting (corrupted) settings file.");
//			if (!settingsFile.delete())
//				Magnum.log.warning("Failed to delete file: " + settingsFile.toString());
//		}
    }
    
    
	// ----------------------------------------------------------------------------

    /** Save settings to .magnum.txt file in user directory */
    static public void saveSettings() {
    	
    	Magnum.log.println("\nSaving configuration file...");    	
    	// Update settings
    	getCurrentSettings();
    	
        // Reset
        try {
			prefs.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
        
        // Remember settings
        prefs.putBoolean("rememberSettings", rememberSettings);
        if (!rememberSettings)
        	return;
        
        // Settings
        if (networkCollectionDir != null)
        	prefs.put("networkCollectionDir", networkCollectionDir.getPath());
        prefs.putBoolean("usePrecomputedKernels", usePrecomputedKernels);
        if (geneScoreFile != null)
        	prefs.put("geneScoreFile", geneScoreFile.getPath());
        if (outputDir != null)
        	prefs.put("outputDir", outputDir.getPath());
        prefs.putBoolean("deleteKernels", deleteKernels);
        prefs.putInt("numPermutations", numPermutations);
        prefs.putBoolean("excludeHlaGenes", excludeHlaGenes);
        prefs.putBoolean("excludeAllosomes", excludeAllosomes);

//    	// Update settings
//    	getCurrentSettings();
//    	
//    	// Save settings file    	
//    	FileExport out = new FileExport(settingsFile.getAbsolutePath());    	
//    	out.println(getHeader());  	
//    	out.println();
//    	out.println("##########################################################################");
//    	out.println("# APP SETTINGS");
//    	out.println();
//    	out.println("# Remember settings from last session");
//    	out.println("rememberSettings = " + rememberSettings);
//    	out.println();
//    	if (!rememberSettings) {
//    		out.println("# Remaining settings were not saved because rememberSettings=false ...");
//    		out.close();
//    		return;
//    	}
//    	out.println("# Network collection directory");
//    	out.println("networkCollectionDir = " + MagnumUtils.fileToString(networkCollectionDir));
//    	out.println();
//    	out.println("##########################################################################");
//    	out.println("# CONNECTIVITY ENRICHMENT");
//    	out.println();
//    	out.println("# Use precomputed network kernels if available");
//    	out.println("usePrecomputedKernels = " + usePrecomputedKernels);
//    	out.println("# GWAS gene score file");
//    	out.println("geneScoreFile = " + MagnumUtils.fileToString(geneScoreFile));
//    	out.println("# Output directory");
//    	out.println("outputDir = " + MagnumUtils.fileToString(outputDir));
//    	out.println("# Delete network kernels after completion");
//    	out.println("deleteKernels = " + deleteKernels);
//    	out.println("# Number of permutations");
//    	out.println("numPermutations = " + numPermutations);
//    	out.println("# Exclude HLA genes");
//    	out.println("excludeHlaGenes = " + excludeHlaGenes);
//    	out.println("# Exclude X, Y chromosomes");
//    	out.println("excludeAllosomes = " + excludeAllosomes);
//    	out.println();
//    	out.close();
}

    
	// ----------------------------------------------------------------------------



    
	// ============================================================================
	// PRIVATE
    
    /** Get the current settings from the App */
    static private void getCurrentSettings() {

    	rememberSettings = app.getPreferencesController().getRememberSettings();
    	
    	networkCollectionDir = app.getNetworkCollection().getNetworkDir();
    	//usePrecomputedKernels = app.getEnrichmentController().get
    }
    
    
	// ----------------------------------------------------------------------------

    /** Get a file from a string preference, return null if preference or file doesn't exist */
    static private File getFilePreference(String key) {

    	String filename = prefs.get(key, null);
    	if (filename == null)
    		return null;
    	
    	File file = new File(filename);
    	if (file.exists())
    		return file;
    	else
    		return null;
    }

    
	// ----------------------------------------------------------------------------

//    /** Extract params from properties */
//    static private void setParameterValues() {
//
//    	// Return if we are not supposed to remember (that's the only thing we do remember :)
//    	rememberSettings = getSettingBoolean("rememberSettings");
//    	if (!rememberSettings)
//    		return;
//    	
//    	networkCollectionDir = getFileSetting("networkCollectionDir");
//    	usePrecomputedKernels = getSettingBoolean("usePrecomputedKernels");
//    	geneScoreFile = getFileSetting("geneScoreFile");
//    	outputDir = getFileSetting("outputDir");
//    	deleteKernels = getSettingBoolean("deleteKernels");
//    	numPermutations = getSettingInt("numPermutations");
//    	excludeHlaGenes = getSettingBoolean("excludeHlaGenes");
//    	excludeAllosomes = getSettingBoolean("excludeAllosomes");
//    }
    
    
//	// ----------------------------------------------------------------------------
//    
//    /** Get header for settings file */
//    static private String getHeader() {
//    	
//    	String header = "";
//    	header = "##########################################################################\n"
//    			+ "# Magnum v1.0 settings file\n"
//    			+ "# \n"
//    			+ "# Note: this is a settings file for the MAGNUM APP, a different settings \n"
//    			+ "# file has to be used for the MAGNUM COMMAND-LINE TOOL.\n"
//    			+ "##########################################################################\n";
//    	return header;
//    }

}
