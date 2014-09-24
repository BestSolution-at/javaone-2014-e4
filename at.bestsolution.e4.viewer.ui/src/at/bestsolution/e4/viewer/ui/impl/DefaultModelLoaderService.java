package at.bestsolution.e4.viewer.ui.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import at.bestsolution.e4.viewer.ui.ModelLoaderService;

public class DefaultModelLoaderService implements ModelLoaderService {

	@Override
	public Node loadModel(URL url) {
		Node rv = null;
		try {
			rv = FXMLLoader.load(url);
			if( rv != null ) {
				String js = loadJS(new URL(url.toExternalForm().replace(".fxml", ".js")));
				if( js != null ) {
					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("nashorn");
					@SuppressWarnings("unchecked")
					BiConsumer<Node,String> c = (BiConsumer<Node, String>) engine.eval(js);
					String[] split = url.getPath().split("/");
					String path = Arrays.stream(split).limit(split.length-1).collect(Collectors.joining("/"));
					c.accept(rv,url.getProtocol()+":"+path);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rv;
	}

	private String loadJS(URL url) {
		StringBuilder rv = new StringBuilder();
		try (
				InputStream stream = url.openStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(stream));) {
			String line;
			while( (line = r.readLine()) != null ) {
				rv.append(line + System.getProperty("line.separator"));
			}
		} catch (IOException e) {
		}
		
		return rv.length() == 0 ? null : rv.toString();
	}
}
