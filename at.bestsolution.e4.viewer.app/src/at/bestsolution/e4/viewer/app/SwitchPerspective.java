package at.bestsolution.e4.viewer.app;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SwitchPerspective {
	@Execute
	public void openPerspective(@Named("parameter.switch.perspective") String perspectiveId, EPartService service, EModelService modelService, MApplication application) {
		MPerspective perspective = (MPerspective) modelService.find(perspectiveId, application);
		service.switchPerspective(perspective);
	}
}
