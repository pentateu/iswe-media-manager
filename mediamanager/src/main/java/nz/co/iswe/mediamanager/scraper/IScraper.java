package nz.co.iswe.mediamanager.scraper;

import nz.co.iswe.mediamanager.media.IMediaDetail;

public interface IScraper {

	void searchAndScrap();

	SearchResult search();
	
	void scrape(SearchResult searchResult);
	
	void scrapCandidates();
	
	boolean preferedScraperFor(IMediaDetail mediaDefinition);

	void setMediaDefinition(IMediaDetail mediaDefinition);
	
	void setScrapingStatusObserver(IScrapingStatusObserver observer);

	
	
}
