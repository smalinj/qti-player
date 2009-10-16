package com.klangner.qtiplayer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.klangner.qtiplayer.client.model.Assessment;
import com.klangner.qtiplayer.client.model.AssessmentItem;
import com.klangner.qtiplayer.client.model.IDocumentLoaded;
import com.klangner.qtiplayer.client.model.Result;
import com.klangner.qtiplayer.client.module.IActivity;

/**
 * Main class with player API
 * @author Krzysztof Langner
 */
public class Player {

  /** player node id */
  private String              id;
  /** Javascript object representing this java object */
  private JavaScriptObject    jsObject;
  /** Assessment played by this player*/
  private Assessment          assessment;
  /** Player view */
  private PlayerView          playerView;
  /** Current item index. 0 based */
  private int                 currentItemIndex;
  /** current item object */
  private AssessmentItem      currentItem = null;
  /** Item results */
  private Result[]      			results;
  
  
  /**
   * Event send when assessment is done
   * @param msg
   */
  private static native void onAssessmentFinished(
      JavaScriptObject player, int score, int max) /*-{
    
    if(typeof player.onAssessmentFinished == 'function') {
      var result = new Array();
      result.score = score;
      result.max = max;
      player.onAssessmentFinished(result);
    }
  }-*/;
  
    
  
  /**
   * constructor
   * @param id
   */
  public Player(String id){
  
    this.id = id;
    this.jsObject = JavaScriptObject.createFunction();
  }
  
  public JavaScriptObject getJavaScriptObject(){
    return jsObject;
  }
  
  /**
   * Load assessment file from server
   */
  public void loadAssessment(String url){
    
    assessment = new Assessment();
    assessment.load(GWT.getHostPageBaseURL() + url, new IDocumentLoaded(){

    public void finishedLoading() {
        onAssessmentLoaded();
      }
    });
  }

  /**
   * Show assessment item in body part of player
   * @param index
   */
  private void loadAssessmentItem(int index){
  	
  	if(index >= 0 && index < assessment.getItemCount()){
	    String  url = assessment.getItemRef(index);

	    // Unload last page
	    if(currentItem != null)
	    	onItemFinished();
	    
	    currentItem = new AssessmentItem();
	    currentItemIndex = index;
	    currentItem.load(url, new IDocumentLoaded(){
	
	      public void finishedLoading() {
	        onItemLoaded();
	      }
	    });
  	}
  }
  
  
  /**
   * Create user interface
   */
  private void onAssessmentLoaded() {
    
    RootPanel rootPanel = RootPanel.get(id); 
    Element element = rootPanel.getElement();
    Node node = element.getFirstChild();
    if(node != null)
      element.removeChild(node);
    
    results = new Result[assessment.getItemCount()];
    
    playerView = new PlayerView(assessment);
    rootPanel.add(playerView.getView());
    
    playerView.getCheckButton().addClickHandler(new ClickHandler(){
      public void onClick(ClickEvent event) {
        showItemResult();
      }
    });
    
    playerView.getResetButton().addClickHandler(new ClickHandler(){
      public void onClick(ClickEvent event) {
        resetActivities();
      }
    });
    
    playerView.getPrevButton().addClickHandler(new ClickHandler(){
      public void onClick(ClickEvent event) {
        loadAssessmentItem(currentItemIndex-1);
      }
    });
    
    playerView.getNextButton().addClickHandler(new ClickHandler(){
      public void onClick(ClickEvent event) {
        loadAssessmentItem(currentItemIndex+1);
      }
    });
    
    playerView.getFinishButton().addClickHandler(new ClickHandler(){
      public void onClick(ClickEvent event) {
        showAssessmentResult();
      }
    });


    // Switch to first item
    loadAssessmentItem(0);
  }
  
  
  /**
   * create view for assessment item
   */
  private void onItemLoaded(){
    
		playerView.getCheckButton().setVisible(true);
		playerView.getResetButton().setVisible(false);

    playerView.getNextButton().setEnabled(false);
    playerView.getPrevButton().setEnabled(false);
    playerView.getFinishButton().setEnabled(false);
    
    if(currentItemIndex+1 < assessment.getItemCount()){
      playerView.getNextButton().setEnabled(true);
    }else{
      playerView.getFinishButton().setEnabled(true);
    }
    
    if(currentItemIndex > 0){
      playerView.getPrevButton().setEnabled(true);
    }
    
    playerView.showPage(currentItem, currentItemIndex+1);
    
  }

  /**
   * Called when item is unloaded
   */
	private void onItemFinished() {
    results[currentItemIndex] = currentItem.getResult();
	}


	/**
	 * Create view for given assessment item and show it in player
	 * @param index of assessment item
	 */
	public void markAnswers(){

		for(int i = 0; i < currentItem.getModuleCount(); i++){
			if(currentItem.getModule(i) instanceof IActivity){
				IActivity module = (IActivity)currentItem.getModule(i);
				module.markAnswers();
			}
		}
	}
	
	/**
	 * Reset
	 */
	public void resetActivities(){

		results[currentItemIndex] = null;
		playerView.showFeedback("");
		currentItem.reset();
		for(int i = 0; i < currentItem.getModuleCount(); i++){
			if(currentItem.getModule(i) instanceof IActivity){
				IActivity module = (IActivity)currentItem.getModule(i);
				module.reset();
			}
		}

		playerView.getCheckButton().setVisible(true);
		playerView.getResetButton().setVisible(false);
		
	}
	
  
  /**
   * Show assessment item in body part of player
   * @param index
   */
  private void showAssessmentResult(){
    int score = 0;
    int max = 0;

    // Unload page to get score
    onItemFinished();
    for(Result result : results){
    	if(result != null){
	      score += result.getScore();
	      max += result.getMaxPoints();
    	}
    }
    
    playerView.showResultPage("Your score is: " + (int)((score * 100)/max) + "% " + score + " points.");
    onAssessmentFinished(jsObject, score, max);
  }
   
  /**
   * Check score
   * @param index
   */
  private void showItemResult(){

  	Result result = currentItem.getResult();
		playerView.getCheckButton().setVisible(false);
		playerView.getResetButton().setVisible(true);
    
    if(currentItemIndex+1 < assessment.getItemCount()){
      playerView.getNextButton().setVisible(true);
    }
    else{
      playerView.getFinishButton().setVisible(true);
    }

    markAnswers();
    String feedback = "Score: " + result.getScore() + " out of " + 
    		result.getMaxPoints() + " points";

    playerView.showFeedback(feedback);
  }

  
}
