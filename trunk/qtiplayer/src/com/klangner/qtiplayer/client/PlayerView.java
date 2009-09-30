package com.klangner.qtiplayer.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.klangner.qtiplayer.client.model.Assessment;
import com.klangner.qtiplayer.client.model.AssessmentItem;

public class PlayerView {

	/** Show this assessment */
	private Assessment			assessment;
	/** Body panel. AssessmentItem view will be shown there */
	private VerticalPanel 	bodyPanel;
	/** Assessment item feedback */
	private Label 					feedbackLabel;
	/** Check button */ 
	private Button					checkButton;
	/** Next button */ 
	private Button					nextButton;
	/** Finish button */ 
	private Button					finishButton;
	
	/**
	 * constructor
	 * @param assessment to show
	 */
	public PlayerView(Assessment assessment){
		this.assessment = assessment;
	}
	
	/**
	 * @return check button
	 */
	public Button getCheckButton(){
		return checkButton;
	}
	
	/**
	 * @return next button
	 */
	public Button getNextButton(){
		return nextButton;
	}
	
	/**
	 * @return finish button
	 */
	public Button getFinishButton(){
		return finishButton;
	}
	
	/**
	 * @return view with player
	 */
	public Widget getView(){
		VerticalPanel 	playerPanel = new VerticalPanel();
		Label						label;
		Label 					header = new Label();
		HorizontalPanel	footer = new HorizontalPanel();

		
		playerPanel.setStyleName("qp-player");
		header.setText(assessment.getTitle());
		header.setStyleName("qp-header");
		playerPanel.add(header);

		bodyPanel = new VerticalPanel();
		bodyPanel.setStyleName("qp-body");
		label = new Label("There are: " + assessment.getItemCount() + " items.");
		bodyPanel.add(label);
		playerPanel.add(bodyPanel);
		
		feedbackLabel = new Label();
		feedbackLabel.setStyleName("qp-feedback");
		playerPanel.add(feedbackLabel);

		footer.setStyleName("qp-footer");
		playerPanel.add(footer);

		checkButton = new Button("Check");
		checkButton.setStyleName("qp-check-button");
		footer.add(checkButton);
		nextButton = new Button("Next");
		nextButton.setStyleName("qp-next-button");
		footer.add(nextButton);
		
		finishButton = new Button("Finish");
		finishButton.setStyleName("qp-finish-button");
		footer.add(finishButton);
		
		return playerPanel;
	}
	
	/**
	 * Create view for given assessment item and show it in player
	 * @param index of assessment item
	 */
	public void showAssessmentItem(AssessmentItem assessmentItem){

		bodyPanel.clear();
		feedbackLabel.setText("");
		for(int i = 0; i < assessmentItem.getModuleCount(); i++){
			bodyPanel.add(assessmentItem.getModule(i).getView());
		}
	}
	
	public void showFeedback(String feedback){
		feedbackLabel.setText(feedback);
	}
	
	/**
	 * Show view with assessment score
	 * @param index of assessment item
	 */
	public void showResultPage(String message){

		bodyPanel.clear();
		showFeedback("");
		bodyPanel.add(new Label(message));
	}
	
}
