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

import javafx.util.StringConverter;

/**
 * Used for binding text fields to files
 */
public class FileStringConverter extends StringConverter<File> {

	/** Set flag to return only the filename instead of the full path */
	private boolean returnFilename;
	
	// ============================================================================
	// PUBLIC METHODS

	public FileStringConverter() {
		this.returnFilename = false;
	}

	public FileStringConverter(boolean returnFilename) {
		this.returnFilename = returnFilename;
	}
	
	// ----------------------------------------------------------------------------

	/** Return null if file=null or does not exist */
	@Override
	public String toString(File file) {
		
		if (file == null || !file.exists())
			return null;
		else if (!returnFilename)
			return file.getPath();
		else
			return file.getName();
	}

	
	// ----------------------------------------------------------------------------

	/** Return null if filename=null or the file does not exist */
	@Override
	public File fromString(String filename) {
    	
		if (filename == null)
    		return null;
    	
    	File file = new File(filename);
    	if (file.exists())
    		return file;
    	else
    		return null;
	}
}
