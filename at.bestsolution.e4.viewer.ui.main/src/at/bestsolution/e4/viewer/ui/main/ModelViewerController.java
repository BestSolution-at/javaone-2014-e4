package at.bestsolution.e4.viewer.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.fx.core.log.Log;
import org.eclipse.fx.core.log.Logger;
import org.eclipse.fx.ui.controls.sceneviewer.Viewer3d;

import at.bestsolution.e4.viewer.ui.ModelLoaderService;
import javafx.scene.control.Button;

public class ModelViewerController implements Initializable {
	@FXML Button button_zoomIn;
	@FXML Button button_zoomOut;
	@FXML Button button_zoomReset;
	@FXML ToggleButton button_rotate;
	@FXML Viewer3d viewer_3d;

	@Inject
	@Log
	Logger logger;
	
	@Inject
	ModelLoaderService service;
	
	private final ObjectProperty<URL> currentModel = new SimpleObjectProperty<URL>(this, "currentModel");
	private final ObjectProperty<Messages> currentMessages = new SimpleObjectProperty<Messages>(this,"currentMessages");

	public void initialize(URL location, ResourceBundle resources) {
		viewer_3d.contentRotateProperty().bindBidirectional(button_rotate.selectedProperty());
		currentModel.addListener((o) -> updateViewer());
		currentMessages.addListener((o) -> updateMessages());
		updateViewer();
		updateMessages();
	}
	
	private void updateViewer() {
		Node n = null;
		URL url = currentModel.get();
		if( url != null ) {
			n = service.loadModel(url);
		}
		viewer_3d.setContent(n);
	}
	
	private void updateMessages() {
		button_zoomIn.setTooltip(new Tooltip(currentMessages.get().tooltip_zoomIn));
		button_zoomOut.setTooltip(new Tooltip(currentMessages.get().tooltip_zoomOut));
		button_zoomReset.setTooltip(new Tooltip(currentMessages.get().tooltip_zoomReset));
		button_rotate.setTooltip(new Tooltip(currentMessages.get().tooltip_rotate));
	}
	
	@FXML public void zoomIn() {
		
	}

	@FXML public void zoomOut() {
		
	}
	
	@FXML public void zoomRest() {
		
	}

	@Inject
	public void updateModelResource(@Optional @Named("modelResource") URL url) {
		currentModel.set(url);
	}
	
	@Inject
	public void updateMessages( @Translation Messages m ) {
		currentMessages.set(m);
	}

}
