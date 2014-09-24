package at.bestsolution.e4.viewer.nls;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.services.nls.IMessageFactoryService;
import org.eclipse.e4.core.services.nls.Message;
import org.eclipse.e4.core.services.nls.Message.ReferenceType;
import org.eclipse.e4.core.services.translation.ResourceBundleProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import at.bestsolution.di.services.TranslationService;

public class CustomMessageFactory implements IMessageFactoryService {

	// Cache so when multiple instance use the same message class
	private Map<Object, Reference<Object>> SOFT_CACHE = Collections
			.synchronizedMap(new HashMap<Object, Reference<Object>>());

	private Map<Object, Reference<Object>> WEAK_CACHE = Collections
			.synchronizedMap(new HashMap<Object, Reference<Object>>());

	private int CLEANUPCOUNT = 0;

	private IMessageFactoryService defaultFactory;

	private TranslationService translationService;
	
	private IMessageFactoryService getDefaultFactory() {
		if( defaultFactory == null ) {
			try {
				BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
				Collection<ServiceReference<IMessageFactoryService>> refs = bundleContext.getServiceReferences(IMessageFactoryService.class, null);
				for( ServiceReference<IMessageFactoryService> r : refs ) {
					if( bundleContext.getService(r) != this ) {
						defaultFactory = bundleContext.getService(r);
						break;
					}
				}
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defaultFactory;
	}
	
	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}
	
	@Override
	public <M> M getMessageInstance(final Locale locale, final Class<M> messages,
			final ResourceBundleProvider provider) {
		String key = messages.getName() + "_" + locale; //$NON-NLS-1$

		final Message annotation = messages.getAnnotation(Message.class);
		Map<Object, Reference<Object>> cache = null;
		ReferenceType type = ReferenceType.NONE;

		if (++CLEANUPCOUNT > 1000) {
			Iterator<Entry<Object, Reference<Object>>> it = WEAK_CACHE.entrySet().iterator();
			while (it.hasNext()) {
				if (it.next().getValue().get() == null) {
					it.remove();
				}
			}

			it = SOFT_CACHE.entrySet().iterator();
			while (it.hasNext()) {
				if (it.next().getValue().get() == null) {
					it.remove();
				}
			}
			CLEANUPCOUNT = 0;
		}

		if (annotation == null || annotation.referenceType() == ReferenceType.SOFT) {
			cache = SOFT_CACHE;
			type = ReferenceType.SOFT;
		} else if (annotation.referenceType() == ReferenceType.WEAK) {
			cache = WEAK_CACHE;
			type = ReferenceType.WEAK;
		}

		if (cache != null && cache.containsKey(key)) {
			@SuppressWarnings("unchecked")
			Reference<M> ref = (Reference<M>) cache.get(key);
			M o = ref.get();
			if (o != null) {
				return o;
			}
			cache.remove(key);
		}

		M instance;

		if (System.getSecurityManager() == null) {
			instance = createInstance(locale, messages, annotation, provider);
		} else {
			instance = AccessController.doPrivileged(new PrivilegedAction<M>() {

				public M run() {
					return createInstance(locale, messages, annotation, provider);
				}

			});
		}

		if (cache != null) {
			if (type == ReferenceType.SOFT) {
				cache.put(key, new SoftReference<Object>(instance));
			} else if (type == ReferenceType.WEAK) {
				cache.put(key, new WeakReference<Object>(instance));
			}
		}

		return instance;
	}

	private <M> M createInstance(Locale locale, Class<M> messages, Message annotation,
			ResourceBundleProvider rbProvider) {

		M enMessages = getDefaultFactory().getMessageInstance(Locale.ENGLISH, messages, rbProvider);
		if( "en".equals(locale.getLanguage()) || "de".equals(locale.getLanguage()) ) {
			return enMessages;
		}
		
		M m = null;
		try {
			m = messages.newInstance();
			for( Field f : messages.getFields() ) {
				System.err.println("TRANSLATING: " + f.get(enMessages) + " => " + locale.getLanguage());
				f.set(m, translationService.translate(locale.getLanguage(), f.get(enMessages)+"")[0]);
			}
			
			// invoke the method annotated with @PostConstruct
			processPostConstruct(m, messages);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return m;
	}

	private static void processPostConstruct(Object messageObject, Class<?> messageClass) {
		if (messageObject != null) {
			Method[] methods = messageClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if (!method.isAnnotationPresent(PostConstruct.class)) {
					continue;
				} else {
					try {
						method.invoke(messageObject);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}