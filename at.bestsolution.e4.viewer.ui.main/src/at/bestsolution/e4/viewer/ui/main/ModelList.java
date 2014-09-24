package at.bestsolution.e4.viewer.ui.main;

import java.net.URL;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.fx.core.di.ContextValue;

import at.bestsolution.e4.viewer.ui.ServiceConstants;

public class ModelList {
	private final ObservableList<URL> modelList = FXCollections.observableArrayList();
	
	@Inject
	@ContextValue("modelResource")
	Property<URL> contextValue;
	
	@PostConstruct
	void init(BorderPane parent) {
		ListView<URL> list = new ListView<>();
		list.setCellFactory(this::listfactory);
		list.setItems(modelList);
		parent.setCenter(list);
		contextValue.bind(list.getSelectionModel().selectedItemProperty());
	}
	
	@Inject
	@Optional
	void modelOpened(@UIEventTopic(ServiceConstants.TOPIC_MODEL_FILE_OPENED) URL modelURL) {
		modelList.add(modelURL);
	}
	
	public ListCell<URL> listfactory(ListView<URL> param) {
		return new ListCell<URL>() {
			@Override
			protected void updateItem(URL item, boolean empty) {
				if( item != null && ! empty) {
					int idx = item.getPath().lastIndexOf('/');
					setText(item.getPath().substring(idx+1));
				} else {
					setText("");
				}
				super.updateItem(item, empty);
				
			}
		};
	}
}
