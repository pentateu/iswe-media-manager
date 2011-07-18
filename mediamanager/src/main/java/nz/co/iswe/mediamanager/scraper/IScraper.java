package nz.co.iswe.mediamanager.scraper;

import nz.co.iswe.mediamanager.media.IMediaDetail;

public interface IScraper {

	/**
	 * Method that scraps the media detail using the web site search feature.
	 * It will look at the search details to find the best match for the media details.
	 */
	void searchAndScrap();

	/**
	 * Search the media detail using the web site search feature and returns a SearchResult Object.
	 * @return
	 */
	SearchResult search();
	
	/**
	 * For a given SearchResult scrap the media details.
	 * @param searchResult
	 */
	void scrape(SearchResult searchResult);
	
	void scrapCandidates();
	
	boolean preferedScraperFor(IMediaDetail mediaDefinition);

	void setMediaDefinition(IMediaDetail mediaDefinition);
	
	void setScrapingStatusObserver(IScrapingStatusObserver observer);

	boolean canScrapeURL(String url);

	/**
	 * Create a SearchResult for a given URL.
	 * The scrape will validate the URL pattern to make sure it can scrape the search results
	 * @param url
	 * @param movie
	 * @return
	 */
	SearchResult createSearchResults(String url, MediaType movie);
}
