package com.klangner.qtiplayer.client.widget;

import java.util.HashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.klangner.qtiplayer.client.util.IDomElementFactory;
import com.klangner.qtiplayer.client.util.XmlElement;
import com.klangner.qtiplayer.client.widget.inline.IOnChangeHandler;
import com.klangner.qtiplayer.client.widget.inline.SelectionWidget;

/**
 * Create Text which can contain gap and inline choice interactions.
 * 
 * @author klangner
 */
public class TextWidget extends Widget{
	
	/** response processing interface */
	private IResponse 	response;
	/** XML root */
	private XmlElement xmlRoot;
	/** All sub widgets */
	private HashMap<String, IOnChangeHandler>	children = new HashMap<String, IOnChangeHandler>();

	
	/**
	 * constructor 
	 * @param node
	 */
	public TextWidget(Element node, IResponse 	response){

		this.xmlRoot = new XmlElement(node);
		this.response = response;
		
		// Convert into text
		xmlRoot.setDomElementFactory(new IDomElementFactory(){
			public com.google.gwt.dom.client.Element createDomElement(Element xmlElement) {
				return createInlineChoice( xmlElement );
			}
			public boolean isSupportedElement(String tagName) {
				return (tagName.compareTo("inlineChoiceInteraction") == 0);
			}
		});
		
		setElement(xmlRoot.convertToHtml());
		setStyleName("qp-text-module");
		this.sinkEvents(Event.ONCHANGE);

	}

	/**
	 * Catch inner controls events.
	 * Since events are not fired for internal Widgets. All events should be handled in this function
	 */
	public void onBrowserEvent(Event event){
		super.onBrowserEvent(event);
		
		if(event.getTypeInt() == Event.ONCHANGE){
			com.google.gwt.dom.client.Element element = 
				com.google.gwt.dom.client.Element.as(event.getEventTarget());
			
			IOnChangeHandler	handler = children.get(element.getId());
			if(handler != null){
				handler.onChange();
			}
		}
	}

	/**
	 * Create inline choice 
	 * @param srcElement XML with content
	 * @param dstElement DOM element where this choice should be added
	 * @return Selection element
	 */
	private com.google.gwt.user.client.Element createInlineChoice(Element inlineChoiceElement){
		SelectionWidget	listBox = new SelectionWidget(inlineChoiceElement, response);
		String	id = Document.get().createUniqueId();

		// Add change handler
		listBox.getElement().setId(id);
		children.put(id, listBox);
		
		return listBox.getElement();
		
	}
	
}
