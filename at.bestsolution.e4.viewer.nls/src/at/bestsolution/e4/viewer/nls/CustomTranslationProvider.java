package at.bestsolution.e4.viewer.nls;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.e4.core.services.translation.ResourceBundleProvider;
import org.eclipse.osgi.service.localization.BundleLocalization;
import org.osgi.framework.Bundle;

import at.bestsolution.di.services.TranslationService;

public class CustomTranslationProvider implements ResourceBundleProvider {
	private BundleLocalization localization;
	private TranslationService translationService;
	
	private Map<String, ResourceBundle> bundleCache = new HashMap<>();
	
	@Override
	public ResourceBundle getResourceBundle(Bundle bundle, String locale) {
		if( "de".equals(locale) || "en".equals(locale) ) {
			if (localization != null)
				return localization.getLocalization(bundle, locale);	
		}
		
		ResourceBundle b = bundleCache.get(bundle+locale);
		if( b == null ) {
			b = new CustomResourceBundle(bundle,locale);
			bundleCache.put(bundle+locale, b);
		}
		return b;
	}
	
	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}

	public void setBundleLocalization(BundleLocalization localization) {
		this.localization = localization;
	}

	class CustomResourceBundle extends ResourceBundle {
		private final Bundle bundle;
		private final String locale;
		private Map<String, String> cache = new HashMap<>();
		
		public CustomResourceBundle(final Bundle bundle, final String locale) {
			this.bundle = bundle;
			this.locale = locale;
		}
		
		@Override
		public boolean containsKey(String key) {
			return true;
		}
		
		@Override
		protected Object handleGetObject(String key) {
			String rv = cache.get(key);
			if( rv == null ) {
				// Treat the key as the term (ensured by CustomMessageFactory)
				String term = key;
				
				ResourceBundle rs = getResourceBundle(bundle,"en");
				if( rs != null ) {
					term = rs.getString(key);
				}
				
				System.err.println("TRANSLATING: " + term);
				
				String[] data = translationService.translate(locale, term);
				if( data.length > 0 ) {
					rv = data[0];
					System.err.println("NEW VALUE: " + rv);
					cache.put(key, rv);
				}
			}
			return rv;
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.emptyEnumeration();
		}
		
	}
}
