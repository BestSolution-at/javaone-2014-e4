package at.bestsolution.e4.viewer.app;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.services.theme.ThemeManager;

public class ThemeSwitch {
	@Execute
	public void switchTheme(@Named("parameter.theme.switch") String name, ThemeManager themeManager) {
		themeManager.setCurrentThemeId(name);
	}
}
