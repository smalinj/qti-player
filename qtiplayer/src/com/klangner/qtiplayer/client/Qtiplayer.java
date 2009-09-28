package com.klangner.qtiplayer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.klangner.qtiplayer.client.model.Assessment;
import com.klangner.qtiplayer.client.model.AssessmentItem;
import com.klangner.qtiplayer.client.model.IDocumentLoaded;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Qtiplayer implements EntryPoint {

	private Assessment			assessment;
	private int							currentItemIndex;
	AssessmentItem					currentItem;
	private VerticalPanel 	body;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Element element = RootPanel.get("player").getElement();
		Node node = element.getFirstChild();
		element.removeChild(node);
		
		loadAssessment(node.getNodeValue());
	}
	
	/**
	 * Create user interface
	 */
	private void createViews() {
		VerticalPanel playerPanel = new VerticalPanel();
		Label					label;
		Label 				header = new Label();
		Label 	feedback = new Label();
		Label 	footer = new Label();
		

		playerPanel.setStyleName("qp-player");
		header.setText(assessment.getTitle());
		header.setStyleName("qp-header");
		playerPanel.add(header);

		body = new VerticalPanel();
		body.setStyleName("qp-body");
		label = new Label("There are: " + assessment.getItemCount() + " items.");
		body.add(label);
		playerPanel.add(body);
		
		feedback.setText("feeback");
		feedback.setStyleName("qp-feedback");
		playerPanel.add(feedback);
		
		footer.setText("Footer");
		footer.setStyleName("qp-footer");
		playerPanel.add(footer);

		RootPanel.get("player").add(playerPanel);
	}
	
	/**
	 * create view for assessment item
	 */
	private void createItemViews(){
		
		Button nextButton = new Button();
		
		if(currentItemIndex+1 < assessment.getItemCount()){
			nextButton.setText("Next");
			nextButton.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					showItem(currentItemIndex+1);
				}
			});
		}
		else{
			nextButton.setText("Finish");
		}
		
		body.clear();
		body.add(new Label(currentItem.getTitle()));
		body.add(nextButton);
	}
	
	/**
	 * Load assessment file from server
	 */
	private void loadAssessment(String url){
		
		assessment = new Assessment();
		assessment.load(url, new IDocumentLoaded(){

			public void finishedLoading() {
        createViews();
        showItem(0);
			}
		});
	}
	
	
	/**
	 * Show assessment item in body part of player
	 * @param index
	 */
	private void showItem(int index){
		String 					url = assessment.getItemRef(index);

		currentItem = new AssessmentItem();
		currentItemIndex = index;

		currentItem.load(url, new IDocumentLoaded(){

			public void finishedLoading() {
				createItemViews();
			}
		});

	}
	 
}