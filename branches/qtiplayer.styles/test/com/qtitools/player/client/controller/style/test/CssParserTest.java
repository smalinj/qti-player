package com.qtitools.player.client.controller.style.test;

import junit.framework.Assert;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.junit.client.GWTTestCase;
import com.qtitools.player.client.controller.data.StyleDataSourceManager;
import com.qtitools.player.client.controller.data.events.StyleDataLoaderEventListener;
import com.qtitools.player.client.util.js.JSOModel;

public class CssParserTest extends GWTTestCase {

	private String css1 = "customSelector { width: 100px; customProperty: abc; } .customClass { height: 100px; xyz: 200; }";
	private String css2 = "customSelector { width: 200px; customproperty: xyz; color: #666; } .customClass { width: 10px; } order { module-layout: vertical; }";
	
	@Override
	public String getModuleName() {
		return "com.qtitools.player.Player";
	}
	
	private static native String parseCSS() /*-{
		var css = "customSelector { width: 100px; customProperty: abc } .customClass { height: 100px; xyz: 200 }";
		var parser = new $wnd.CSSParser();
		var sheet = parser.parse(css, false, true);
		return sheet.cssText();
	}-*/;
	
	private static native JavaScriptObject getParser() /*-{
		return new $wnd.CSSParser();
	}-*/;

	public void testIfParserIsPresent() {
		String parsed = parseCSS();
		System.out.println(parsed);
		Assert.assertEquals("sample css was parsed", 0, parsed.indexOf("customSelector"));
	}

	public void testStyleDataSourceManager() {
		StyleDataSourceManager sdsm = new StyleDataSourceManager( new StyleDataLoaderEventListener() {
			@Override
			public void onStyleDataLoaded() {
				System.out.println("style data loaded");
			}
		});
		
		Document doc = XMLParser.parse("<orderInteraction responseIdentifier=\"RESPONSE\" shuffle=\"true\" />");
		Element e = doc.getDocumentElement();
		
		Assert.assertNotNull( sdsm );
		Assert.assertEquals( "no styles were added yet", 0, sdsm.getStyleProperties(e).keys().length() );


		doc = XMLParser.parse("<customSelector />");
		e = doc.getDocumentElement();

		sdsm.addAssessmentStyle(css1);
		sdsm.addItemStyle(0, css2);
		JSOModel styles = sdsm.getStyleProperties( e );
		JsArrayString keys = styles.keys();
		
		Assert.assertEquals("style has correct number of properties",3,keys.length());
		Assert.assertFalse("StyleDataSourceManager returns correct style values", styles.hasKey("nonExistingStyle") );
		Assert.assertTrue(styles.hasKey("customproperty") );
		Assert.assertFalse(styles.hasKey("customProperty") );
		
		Assert.assertEquals("styles added later override existing values", "xyz", styles.get("customproperty") );
	}
	
	public void testEmptyStyleManager() {
		StyleDataSourceManager sdsm = new StyleDataSourceManager( new StyleDataLoaderEventListener() {
			@Override
			public void onStyleDataLoaded() {
				System.out.println("style data loaded");
			}
		});
		
		Document doc = XMLParser.parse("<customSelector />");
		Element e = doc.getDocumentElement();
		
		JSOModel styles = sdsm.getStyleProperties(e);
		Assert.assertNotNull( "if styles are not initialized getStyleProperties still returns JSOModel", styles );
		Assert.assertEquals( "returned JSOModel is empty", 0, styles.keys().length());
	}
	
}
