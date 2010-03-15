/*
  The MIT License
  
  Copyright (c) 2009 Krzysztof Langner
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/
package com.qtitools.player.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.qtitools.player.client.control.DeliveryEngine;
import com.qtitools.player.client.control.DeliveryEngineEventListener;
import com.qtitools.player.client.control.IAssessmentSessionReport;
import com.qtitools.player.client.control.Result;
import com.qtitools.player.client.view.PlayerWidget;
/**
 * Main class with player API
 * @author Krzysztof Langner
 */
public class Player implements DeliveryEngineEventListener, EntryPointEventListener {

  /** player node id */
  private String              id;
  /** JavaScript object representing this java object */
  private JavaScriptObject    jsObject;
  /** Player view */
  private PlayerWidget          playerView;
  /** Delivery engine do manage the assessment content */
  public DeliveryEngine deliveryEngine;
  
  private JavaScriptResult    testResult;  
  
    
  
  /**
   * constructor
   * @param id
   */
  public Player(String id){
  
    this.id = id;
    this.jsObject = JavaScriptObject.createFunction();
    testResult = new JavaScriptResult(0, 0);
    
    deliveryEngine = new DeliveryEngine(this);
  }

  public void loadAssessment(String url){
	  deliveryEngine.loadAssessment(url);
  }
  
  /**
   * @return js object representing this player
   */
  public JavaScriptObject getJavaScriptObject(){
    return jsObject;
  }

  
  /**
   * Return interface to get test result
   */
  public JavaScriptResult getResult() {

    return testResult;
  }
  
  /**
   * Return interface to get assessment session time
   */
  public JavaScriptObject getAssessmentSessionReport() {

	  IAssessmentSessionReport report = deliveryEngine.report();
	  
	  int assessmentSessionTime = report.getAssessmentSessionTime();
	  int score = (int)(deliveryEngine.getAssessmentResult().getScore() - deliveryEngine.getAssessmentResult().getMinPoints());
	  int max = (int)(deliveryEngine.getAssessmentResult().getMaxPoints() - deliveryEngine.getAssessmentResult().getMinPoints());
	  int itemsCount = report.getAssessmentItemsCount();
	  int itemsVisited = report.getAssessmentItemsVisitedCount();
	  boolean passed = report.getAssessmentMasteryPassed();
	  
	  String lessonStatus = "INCOMPLETE";
	  if (itemsVisited == itemsCount){
		  if (max == 0)
			  lessonStatus = "COMPLETED";
		  else if (passed)
			  lessonStatus = "PASSED";
		  else
			  lessonStatus = "FAILED";
	  }
		  
		  
	  
	  JavaScriptObject obj = JavaScriptObject.createObject();
	  
	  initAssessmentSessionReportJS(obj, assessmentSessionTime, score, max, lessonStatus);
	  
	  return obj;
  }
  
  private native static void initAssessmentSessionReportJS(JavaScriptObject obj, int time, int score, int scoreMax, String lessonStatus) /*-{
	  obj.getTime = function(){
		  return time;
	  }
	  obj.getScore = function(){
		  return score;
	  }
	  obj.getScoreMax = function(){
		  return scoreMax;
	  }
	  obj.getLessonStatus = function(){
		  return lessonStatus;
	  }
  }-*/;

  public void setMasteryScore(int mastery){
	  deliveryEngine.setMasteryScore(mastery);
  }
  
  /**
   * Return interface to get assessment session state
   */
  public String getState() {
	  
	  String stateString = deliveryEngine.getState().toString();
	  return stateString;
  }
  
  /**
   * Return interface to get assessment session time
   */
  public void setState(String obj) {
	  try {
		  JSONArray statesArr = (JSONArray)JSONParser.parse(obj);
		  deliveryEngine.setState(statesArr);
	  } catch (Exception e) {
	}
  }
  
  
  	public JavaScriptObject getEngineMode(){
  	  JavaScriptObject obj = JavaScriptObject.createObject();
  	  String es = deliveryEngine.getEngineMode();
  	initEngineModeJS(obj, es);
  	  return obj;
  	}
  	
    private native static void initEngineModeJS(JavaScriptObject obj, String state) /*-{
		obj.toString = function(){
			return state;
		}
	}-*/;



  
  /**
   * Create user interface
   */
  private void createUserInterface() {

	RootPanel rootPanel = RootPanel.get(id); 
	try {
		Element element = rootPanel.getElement();
		Node node = element.getFirstChild();
		if(node != null)
			element.removeChild(node);
    
    	playerView = new PlayerWidget(deliveryEngine.assessment);
    	rootPanel.add(playerView);
    
	    playerView.getCheckButton().addClickHandler(new ClickHandler(){
	      public void onClick(ClickEvent event) {
	    	  onNavigateFinishItem();
	      }
	    });
	    
	    playerView.getResetButton().addClickHandler(new ClickHandler(){
	      public void onClick(ClickEvent event) {
	        onNavigateResetItem();
	        playerView.getCheckButton().setVisible(true);
		  	playerView.getResetButton().setVisible(false);
	      }
	    });
	    
	    playerView.getPrevButton().addClickHandler(new ClickHandler(){
	      public void onClick(ClickEvent event) {
	        onNavigatePreviousItem();
	      }
	    });
	    
	    playerView.getNextButton().addClickHandler(new ClickHandler(){
	      public void onClick(ClickEvent event) {
	    	  onNavigateNextItem();
	      }
	    });
	    
	    playerView.getFinishButton().addClickHandler(new ClickHandler(){
	      public void onClick(ClickEvent event) {
	    	onNavigateFinishAssessment();
	      }
	    });

    } catch (Exception e) {
    	Label l = new Label();
    	l.setText("Could not create view.");
    	rootPanel.add(l);
	}
  }
  
  
  /**
   * create view for assessment item
   */
  private void createAssessmentItemView(){
    
	playerView.getCheckButton().setVisible(true);
	playerView.getResetButton().setVisible(false);

    playerView.getNextButton().setEnabled(false);
    playerView.getPrevButton().setEnabled(false);
    playerView.getFinishButton().setEnabled(false);
    
    if(!deliveryEngine.isLastAssessmentItem()){
      playerView.getNextButton().setEnabled(true);
    }else{
      playerView.getFinishButton().setEnabled(true);
    }
    
    if(!deliveryEngine.isFirstAssessmentItem()){
      playerView.getPrevButton().setEnabled(true);
    }
    
    playerView.showPage(deliveryEngine.assessment, deliveryEngine.currentAssessmentItem, deliveryEngine.getCurrentAssessmentItemIndex()+1);
    
  }

  /**
   * Reset
   */
  public void resetActivities(){

	  deliveryEngine.reset();

	  playerView.showFeedback("");

	  playerView.getCheckButton().setVisible(true);
	  playerView.getResetButton().setVisible(false);

  }

  
  /**
   * Show assessment item in body part of player
   * @param index
   */
  private void showAssessmentResult(){

    deliveryEngine.endItemSession();
    
    Result assessmentResult = deliveryEngine.getAssessmentResult(); 
    
    int score = (int)assessmentResult.getScore();
    int max = (int)assessmentResult.getMaxPoints();
    
    testResult = new JavaScriptResult(score, max);
    playerView.showResultPage("Your score is: " + (int)((score * 100)/max) + "% " + score + " points.");
    
  }
   
  /**
   * Check score
   * @param index
   */
  private void showItemResult(){

	  
	  Result result = deliveryEngine.getAssessmentItemResult();
	  playerView.getCheckButton().setVisible(false);
	  playerView.getResetButton().setVisible(true);

	  if(!deliveryEngine.isLastAssessmentItem()){
		  playerView.getNextButton().setVisible(true);
	  } else {
		  playerView.getFinishButton().setVisible(true);
	  }

	  deliveryEngine.markAnswers();
	  String feedback = "Score: " + result.getScore() + " out of " + 
	  result.getMaxPoints() + " points";

	  playerView.showFeedback(feedback);
  }

private void resetItem(){
	deliveryEngine.reset();
	playerView.showFeedback("");
}


@Override
public void onAssessmentSessionBegin() {
	
	createUserInterface();
	
	onAssessmentSessionBeginJS(jsObject);
		
}

private static native void onAssessmentSessionBeginJS(JavaScriptObject player) /*-{
	if(typeof player.onAssessmentSessionBegin == 'function') {
		player.onAssessmentSessionBegin();
}
}-*/;


@Override
public void onAssessmentSessionFinished() {
	showAssessmentResult();
    onAssessmentSessionFinishedJS(jsObject);
}

private static native void onAssessmentSessionFinishedJS(JavaScriptObject player) /*-{
  	if(typeof player.onAssessmentSessionFinished == 'function') {
		player.onAssessmentSessionFinished();
	}
}-*/;

@Override
public void onItemSessionBegin(int currentAssessmentItemIndex) {
	
	createAssessmentItemView();
	
	onItemSessionBeginJS(jsObject);
}

private static native void onItemSessionBeginJS(JavaScriptObject player) /*-{
	if(typeof player.onItemSessionBegin == 'function') {
		player.onItemSessionBegin();
	}
}-*/;

@Override
public void onItemSessionFinished(int currentAssessmentItemIndex) {
	onItemSessionFinishedJS(jsObject);
	
}

private static native void onItemSessionFinishedJS(JavaScriptObject player) /*-{
	if(typeof player.onItemSessionFinished == 'function') {
		player.onItemSessionFinished();
	}
}-*/;

@Override
public void onAssessmentItemLoadingError(String errorMessage) {
	playerView.getCheckButton().setVisible(false);
	playerView.showError(errorMessage);
	
}

@Override
public void onAssessmentLoadingError(String errorMessage) {

	Label	errorLabel = new Label(errorMessage);
	errorLabel.setStyleName("qp-error");
	RootPanel.get(id).add(errorLabel);
	
}

@Override
public void onNavigateFinishAssessment() {
	if (deliveryEngine.isNavigationPossible()){
		deliveryEngine.endAssessmentSession();
	}
}


@Override
public void onNavigateFinishItem() {
	if (deliveryEngine.isNavigationPossible()){
		deliveryEngine.endItemSession();
		showItemResult();
	}
}

@Override
public void onNavigateNextItem() {
	if (deliveryEngine.isNavigationPossible()){
		deliveryEngine.nextAssessmentItem();
	}
	
}

@Override
public void onNavigatePreviousItem() {
	if (deliveryEngine.isNavigationPossible()){
		deliveryEngine.previousAssessmentItem();
	}
	
}

@Override
public void onNavigateResetAssessment() {
	if (deliveryEngine.isNavigationPossible()){
		deliveryEngine.reset();
	}
}

@Override
public void onNavigateResetItem() {
	if (deliveryEngine.isNavigationPossible()){
		resetItem();
	}
}

}