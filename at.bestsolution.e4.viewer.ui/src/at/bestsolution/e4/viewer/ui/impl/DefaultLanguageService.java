package at.bestsolution.e4.viewer.ui.impl;

import java.util.Arrays;
import java.util.List;

import at.bestsolution.e4.viewer.ui.LanguageService;

public class DefaultLanguageService implements LanguageService {

	@Override
	public List<String> getLanguages() {
		return Arrays.asList("en","fr","de");
	}

}
