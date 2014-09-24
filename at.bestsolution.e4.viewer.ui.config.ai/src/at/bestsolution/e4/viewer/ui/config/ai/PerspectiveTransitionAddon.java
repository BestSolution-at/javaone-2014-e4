package at.bestsolution.e4.viewer.ui.config.ai;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.fx.ui.animation.pagetransition.animation.FadeAnimation;
import org.eclipse.fx.ui.animation.pagetransition.animation.PageChangeAnimation;
import org.eclipse.fx.ui.animation.pagetransition.animation.ZoomSlideAnimation;
import org.eclipse.fx.ui.workbench.renderers.base.services.PerspectiveTransitionService;
import org.eclipse.jdt.annotation.NonNull;

import at.bestsolution.e4.viewer.ui.config.ConfigurationView.AnimationType;

@SuppressWarnings("restriction")
public class PerspectiveTransitionAddon {
	@PostConstruct
	void init(IEclipseContext factory) {
		PerspectiveTransitionServiceImpl impl = ContextInjectionFactory.make(PerspectiveTransitionServiceImpl.class, factory);
		factory.set(PerspectiveTransitionService.class, impl);
	}
	
	public static class PerspectiveTransitionServiceImpl implements PerspectiveTransitionService<BorderPane, Node> {
		
		
		private AnimationType type; 

		private FadeAnimation fade = new FadeAnimation();
		private ZoomSlideAnimation zoomSlide = new ZoomSlideAnimation();
		private PageChangeAnimation pageChange = new PageChangeAnimation();
		
		@Inject
		public void updateAnimationType(@Optional @Named("perspectiveAnimation") AnimationType type) {
			this.type = type;
			System.err.println("TYPE: " + type);
		}
		
		@Override
		public AnimationDelegate<BorderPane, Node> getDelegate(
				MPerspective fromPerspective, MPerspective toPerspective) {
			return new AnimationDelegate<BorderPane, Node>() {
				
				@Override
				public void animate(@NonNull BorderPane container, @NonNull Node control,
						@NonNull Runnable finished) {
					if( type == null || AnimationType.TYPE_FADE.equals(type.type) ) {
						fade.animate(container, control, finished);
					} else if( AnimationType.TYPE_SWIPE.equals(type.type) ) {
						zoomSlide.animate(container, control, finished);
					} else {
						pageChange.animate(container, control, finished);
					}
				}
			};
		}
	}
}
