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

import edu.mit.magnum.FileExport;
import edu.mit.magnum.Magnum;
import edu.mit.magnum.Settings;

/**
 * Class for settings
 */
final public class AppSettings extends Settings {


	// ============================================================================
	// STATIC

	/** The magnum app instance*/
	final static private MagnumApp app = MagnumApp.getInstance();
	/** MagnumApp version */
	final static public String magnumAppVersion = "Magnum v1.0";

	/** Download gene scores */
	final static public String geneScoresLink = "http://regulatorycircuits.org/data/GWAS%20gene%20scores%20v1.zip";
	/** Download PASCAL */
	final static public String pascalLink = "http://www2.unil.ch/cbg/index.php?title=Pascal";

	/** The configuration file with the settings (leave empty for default settings) */
	final static public File settingsFile = Paths.get(System.getProperty("user.home"), ".magnum.txt").toFile();	
	
	/** Remember settings */
	static public Boolean rememberSettings = true;
	
	/** Network collection directory */
	static public String networkCollectionDir;

	
	// ============================================================================
	// PUBLIC

    /** Load settings from .magnum.txt file in user directory */
    static public void loadSettings() {

    	Magnum.log.println("\nLoading Magnum App settings...");
		try {
	    	if (!settingsFile.exists()) {
	    		Magnum.log.println("- Settings file was not saved");
	    		return;
	    	}
	    	
	    	// Load properties
			Magnum.log.println("- Loading settings file: " + settingsFile.toString());
			InputStream in = new FileInputStream(settingsFile);
			set = new Properties();
			set.load(new InputStreamReader(in));
			
			// Extract to instance variables
			setParameterValues();
			
		} catch (Exception e) {
			Magnum.log.warning(e.getMessage());
			Magnum.log.println("- Failed to load settings file.");
			Magnum.log.println("- Deleting (corrupted) settings file.");
			if (!settingsFile.delete())
				Magnum.log.warning("Failed to delete file: " + settingsFile.toString());
		}
    }
    
    
	// ----------------------------------------------------------------------------

    /** Save settings to .magnum.txt file in user directory */
    static public void saveSettings() {
    	
    	// Save settings file
    	Magnum.log.println("\nSaving configuration file...");
    	// Update settings
    	getCurrentSettings();
    	FileExport out = new FileExport(settingsFile.getAbsolutePath());
    	
    	out.println(getHeader());  	
    	out.println();
    	out.println("##########################################################################");
    	out.println("# APP SETTINGS");
    	out.println();
    	out.println("# Remember settings from last session");
    	out.println("rememberSettings = " + rememberSettings.toString());
    	out.println();
    	out.println("# Network collection directory");
    	out.println("networkCollectionDir = " + networkCollectionDir);
    	out.println();
    	out.println("##########################################################################");
    	out.println("# CONNECTIVITY ENRICHMENT");
    	out.println();
    	out.println("# GWAS gene score file");
    	//out.println("geneScoreFile = " + geneScoreFile);
    	//out.println("# GWAS gene score file");
    	//out.println("geneScoreFile = " + geneScoreFile);

    	out.println();


    	out.close();
    }

    
	// ----------------------------------------------------------------------------



    
	// ============================================================================
	// PRIVATE
    
    /** Get the current settings from the App */
    static private void getCurrentSettings() {

    	rememberSettings = app.getPreferencesController().getRememberSettings();
    	
    	Path dir = app.getNetworkCollection().getNetworkDir();
    	networkCollectionDir = (dir == null ? "" : dir.toString());
    }
    
    
	// ----------------------------------------------------------------------------

    /** Extract params from properties */
    static private void setParameterValues() {

    	rememberSettings = getSettingBoolean("rememberSettings");
    	networkCollectionDir = getSetting("networkCollectionDir");
    }
    
    
	// ----------------------------------------------------------------------------
    
    /** Get header for settings file */
    static private String getHeader() {
    	
    	String header = "";
    	header = "##########################################################################\n"
    			+ "# Magnum v1.0 settings file\n"
    			+ "# \n"
    			+ "# Note: this is a settings file for the Magnum App, a different settings \n"
    			+ "# file is available for the command-line version.\n"
    			+ "##########################################################################\n";
    	return header;
    }

    
}
