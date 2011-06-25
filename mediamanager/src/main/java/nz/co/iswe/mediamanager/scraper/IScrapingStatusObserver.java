package nz.co.iswe.mediamanager.scraper;

public interface IScrapingStatusObserver {

	void notifyErrorOccurred(String message, Throwable throwable);
	
	void notifyScrapingFinished();
	
	void notifyNewStep();
	
	void notifyStepProgress();

	void notifyStepFinished();
	
	
	void notifyScrapingStarted();
	
	/*
	void setMinimum(Integer value);
	void setMaximum(Integer value);
	void setCurrentValue(Integer value);
	 */
	
	
}
