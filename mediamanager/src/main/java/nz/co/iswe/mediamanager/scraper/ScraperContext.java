package nz.co.iswe.mediamanager.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.scraper.impl.IMDBMovieScraper;
import nz.co.iswe.mediamanager.scraper.impl.OneDDLScraper;


public class ScraperContext {

	private static Logger log = Logger.getLogger(ScraperContext.class.getName());
	
	private static ScraperContext instance = null;
	
	public static ScraperContext getInstance() {
		if(instance == null){
			instance = new ScraperContext();
		}
		return instance;
	}

	private IScrapingStatusObserver observer;
	
	private List<Class<? extends IScraper>> registeredScrapers;
	
	private List<Class<? extends IScraper>> defaultScrapers;
	
	public ScraperContext(){
		//load all the scrapers
		registeredScrapers = new ArrayList<Class<? extends IScraper>>();
		defaultScrapers = new ArrayList<Class<? extends IScraper>>();
		
		//TODO: Load this from an XML configuration file
		registeredScrapers.add(OneDDLScraper.class);
		registeredScrapers.add(IMDBMovieScraper.class);
		
		defaultScrapers.add(OneDDLScraper.class);
		defaultScrapers.add(IMDBMovieScraper.class);
	}
	
	public void setScrapingStatusObserver(IScrapingStatusObserver observer) {
		this.observer = observer;
	}

	public void scrape(MediaDetail mediaDetail, IScraper scraper) {
		
		boolean found = false;
		
		if(scraper != null){
			found = scrapeImpl(mediaDetail, scraper);
		}
	
		if(!found){
			//2: Locate the best scraper
			scraper = getBestScraper(mediaDetail);
			if(scraper != null){
				found = scrapeImpl(mediaDetail, scraper);
			}
		}
		
		if(!found){

			//No 'best scraper found'
			//use the default scrapers
			for(Class<? extends IScraper> scraperClass : defaultScrapers){
				scraper = newInstance(scraperClass);
				
				observer.notifyStepProgress();
				scraper.setMediaDefinition(mediaDetail);
				scraper.setScrapingStatusObserver(observer);
				
				//scrape the info for the provided media file
				scraper.searchAndScrap();
				
				if(MediaStatus.MEDIA_DETAILS_FOUND.equals( mediaDetail.getStatus()) ){
					//if the media detail is found, stop from trying others scrapers
					break;
				}
				
			}
		}
		
		
		if( ! ( MediaStatus.MEDIA_DETAILS_FOUND.equals( mediaDetail.getStatus()) || 
				MediaStatus.CANDIDATE_DETAILS_FOUND.equals( mediaDetail.getStatus()) ||
				MediaStatus.CANDIDATE_LIST_FOUND.equals( mediaDetail.getStatus()) 
			   ) ){
			
			mediaDetail.setStatus(MediaStatus.MEDIA_DETAILS_NOT_FOUND);
		
		}
		
		
	}
	
	private boolean scrapeImpl(MediaDetail mediaDetail, IScraper scraper) {
		observer.notifyStepProgress();
		
		scraper.setMediaDefinition(mediaDetail);
		scraper.setScrapingStatusObserver(observer);
		
		//scrape the info for the provided media file
		scraper.searchAndScrap();
		
		if(MediaStatus.MEDIA_DETAILS_FOUND.equals( mediaDetail.getStatus()) ){
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends IScraper> T getScraper(Class<T> clazz) {
		if(clazz == null){
			return null;
		}
		for(Class<? extends IScraper> scraperClass : registeredScrapers){
			if(clazz.equals(scraperClass)){
				return (T)newInstance(scraperClass);
			}
		}
		return null;
	}
	
	public IScraper getBestScraper(MediaDetail mediaFileDefinition) {
		for(Class<? extends IScraper> scraperClass : registeredScrapers){
			IScraper scraper = newInstance(scraperClass);
			if(scraper != null && scraper.preferedScraperFor(mediaFileDefinition)){
				return scraper;
			}
		}
		return null;
	}
	
	public IScraper getScraperByURL(String url) {
		for(Class<? extends IScraper> scraperClass : registeredScrapers){
			IScraper scraper = newInstance(scraperClass);
			if(scraper != null && scraper.canScrapeURL(url)){
				return scraper;
			}
		}
		return null;
	}

	private IScraper newInstance(Class<? extends IScraper> scraperClass) {
		if(scraperClass == null){
			return null;
		}
		try {
			return scraperClass.newInstance();
		} catch (InstantiationException e) {
			log.log(Level.WARNING, "Error creating new instance of class: " + scraperClass.getName() , e);
			return null;
		} catch (IllegalAccessException e) {
			log.log(Level.WARNING, "Error creating new instance of class: " + scraperClass.getName() , e);
			return null;
		}
	}

	

	


}
