package at.bestsolution.e4.viewer.nls;

import java.util.Optional;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import at.bestsolution.di.services.TranslationService;

public class MenuProcessor {
	@Execute
	void processorLanguageFlip(MApplication application, EModelService modelService, TranslationService translationService) {
		Optional<MMenu> mainMenu = application.getChildren().stream()
			.map(w -> w.getMainMenu()).findFirst();
		if( mainMenu.isPresent() ) {
			MMenu main = mainMenu.get();
			Optional<MMenuElement> languageMenu = main.getChildren().stream().filter(m -> "at.bestsolution.e4.viewer.app.menu.language".equals(m.getElementId())).findFirst();
			MMenu menu = null;
			if( languageMenu.isPresent() ) {
				menu = (MMenu) languageMenu.get();
				menu.getChildren().clear();
			} else {
				menu = modelService.createModelElement(MMenu.class);
				menu.setElementId("at.bestsolution.e4.viewer.app.menu.language");
				menu.setContributorURI("platform:/plugin/at.bestsolution.e4.viewer.nls");
				menu.setLabel("%menu.language");
				menu.setToBeRendered(true);
				main.getChildren().add(menu);
			}

			MCommand command = application.getCommands().stream().filter(c -> "at.bestsolution.e4.viewer.app.command.changelang".equals(c.getElementId())).findFirst().get();
			
			String[] langs = translationService.getLocales();
			
			
			MMenu langMenu = menu;
			
			for( int i = 0; i < langs.length; i++ ) {
				if( langs.length > 0 && i % 5 == 0 ) {
					menu = modelService.createModelElement(MMenu.class);
					menu.setLabel((i+1) + " - " + (i+5));
					langMenu.getChildren().add(menu);
				}
				
				MHandledMenuItem item = modelService.createModelElement(MHandledMenuItem.class);
				item.setToBeRendered(true);
				item.setLabel(langs[i]);
				MParameter p = modelService.createModelElement(MParameter.class);
				p.setName("parameter.changelanguage.language");
				p.setValue(langs[i]);
				item.getParameters().add(p);
				item.setCommand(command);
				menu.getChildren().add(item);
			}
		}
	}
}
