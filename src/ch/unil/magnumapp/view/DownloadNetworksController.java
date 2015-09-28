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

import ch.unil.magnumapp.AppSettings;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Controller for "Other networks" pane 
 */
public class DownloadNetworksController extends ViewController {

	@FXML
	private VBox downloadNetworksVBox;
	
	
	// ============================================================================
	// HANDLES

    @FXML
    private void handleNetworkCompendiumLink() {
    	openWebpage(AppSettings.networkCompendiumLink);
    }

    @FXML
    private void handleIndividualNetworksLink() {
    	openWebpage(AppSettings.individualNetworksLink);
    }

	
	// ============================================================================
	// SETTERS AND GETTERS

	public VBox getDownloadNetworksVBox() {
		return downloadNetworksVBox;
	}

}
