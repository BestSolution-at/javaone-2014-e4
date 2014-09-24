package at.bestsolution.e4.viewer.ui.handler;

import java.io.File;
import java.net.MalformedURLException;

import javax.inject.Inject;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.fx.core.log.Log;
import org.eclipse.fx.core.log.Logger;

import at.bestsolution.e4.viewer.ui.ServiceConstants;

public class OpenModelHandler {
	@Inject
	@Log
	Logger logger;
	
	@Execute
	public void openModel(IEventBroker eventBroker, Stage stage) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Model File");
		chooser.getExtensionFilters().add(new ExtensionFilter("3d FXML", "*.fxml"));
		File file = chooser.showOpenDialog(stage);
		if( file != null ) {
			try {
				eventBroker.post(ServiceConstants.TOPIC_MODEL_FILE_OPENED, file.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error("Unable to open file", e);
			}
		}
	}
}
