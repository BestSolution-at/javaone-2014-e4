package at.bestsolution.e4.viewer.nls;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;

public class FlipLanguage {
	@Execute
	public void flip(@Named("parameter.changelanguage.language") String newLanguage, ILocaleChangeService lc) {
		lc.changeApplicationLocale(newLanguage);
	}
}
