package at.bestsolution.e4.viewer.ui.config;

import java.util.Collections;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.fx.core.di.ContextValue;
import org.eclipse.fx.ui.panes.GridLayoutPane;

@SuppressWarnings("restriction")
public class ConfigurationView {
	
	private ObjectProperty<Messages> currentMessages = new SimpleObjectProperty<>(this, "currentMessages");
	
	@Inject
	@ContextValue("perspectiveAnimation")
	Property<AnimationType> animationType;
	
	private Label perspectiveAnimation;
	private ComboBox<AnimationType> perspectiveAnimationView;
	
	@Inject
	private ECommandService commandService;
	
	@Inject
	private EHandlerService handlerService;
	
	@Inject
	public void updateAnimationType(@Optional @Named("perspectiveAnimation") AnimationType type) {
		System.err.println("TYPE: " + type);
	}
	
	@PostConstruct
	void init(BorderPane parent) {
		GridLayoutPane p = new GridLayoutPane();
		p.setNumColumns(2);
		
		{
			perspectiveAnimation = new Label(currentMessages.get().perspective_animation);
			p.getChildren().add(perspectiveAnimation);
			
			perspectiveAnimationView = new ComboBox<>();
			perspectiveAnimationView.setCellFactory((v) -> new AnimationTypeCell());
			perspectiveAnimationView.setButtonCell(new AnimationTypeCell());
			perspectiveAnimationView.setItems(FXCollections.observableArrayList(
					new AnimationType("fade", getAnimationLabel("fade")),
					new AnimationType("swipe", getAnimationLabel("swipe")),
					new AnimationType("fancy", getAnimationLabel("fancy"))
					));
			animationType.bind(perspectiveAnimationView.getSelectionModel().selectedItemProperty());
			perspectiveAnimationView.getSelectionModel().select(0);
			
			p.getChildren().add(perspectiveAnimationView);
		}
		currentMessages.addListener((o) -> updateUI());
		parent.setCenter(p);
		
		Button b = new Button("Close");
		b.setOnAction((e) -> {
			ParameterizedCommand command = commandService.createCommand("at.bestsolution.e4.viewer.app.command.switchperspective", Collections.singletonMap("parameter.switch.perspective", "at.bestsolution.e4.viewer.app.perspective.main"));
			handlerService.executeHandler(command);	
		});
		BorderPane.setAlignment(b, Pos.BASELINE_RIGHT);
		parent.setBottom(b);
	}
	
	private void updateUI() {
		perspectiveAnimation.setText(currentMessages.get().perspective_animation);
		for( AnimationType t : perspectiveAnimationView.getItems() ) {
			t.setLabel(getAnimationLabel(t.type));
		}
	}
	
	@Inject
	public void updateMessages(@Translation Messages messages) {
		currentMessages.set(messages);
	}
	
	static class AnimationTypeCell extends ListCell<AnimationType> {
		@Override
		protected void updateItem(AnimationType item, boolean empty) {
			if( item != null && ! empty ) {
				textProperty().bind(item.labelProperty());
			} else {
				textProperty().unbind();
				setText("");
			}
			super.updateItem(item, empty);
		}
	}
	
	private String getAnimationLabel(String type) {
		switch (type) {
		case AnimationType.TYPE_FADE:
			return currentMessages.get().animation_fade;
		case AnimationType.TYPE_FANCY:
			return currentMessages.get().animation_fancy;
		default:
			return currentMessages.get().animation_swipe;
		}
	}
	
	public static class AnimationType {
		public static final String TYPE_FADE = "fade";
		public static final String TYPE_SWIPE = "swipe";
		public static final String TYPE_FANCY = "fancy";

		public final String type;
		private StringProperty label = new SimpleStringProperty(this, "label");
		
		public AnimationType(String type, String label) {
			this.type = type;
			this.label.set(label);
		}
		
		public final StringProperty labelProperty() {
			return this.label;
		}

		public final java.lang.String getLabel() {
			return this.labelProperty().get();
		}

		public final void setLabel(final java.lang.String label) {
			this.labelProperty().set(label);
		}
	}
}
