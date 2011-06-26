package nz.co.iswe.mediamanager.scraper.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.nfo.MovieFileNFO;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.scraper.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IMDBMovieScraper extends AbstractScraper {

	private static Logger log = Logger.getLogger(IMDBMovieScraper.class.getName());

	protected boolean skipDownloadPoster = false;
	
	protected Pattern imdbUrlPattern = Pattern.compile("(http://)?(.)?(" + Pattern.quote("imdb.com") + "|" + Pattern.quote("www.imdb.com") + "){1}(.+)");
	
	protected Pattern searchSubUrlPattern = Pattern.compile("^/find\\?s\\=\\w+\\&q\\=(.+)$");
	
	protected Pattern movieDetailSubUrlPattern = Pattern.compile("^/title/\\w{2}\\d+/$");
	
	
	@Override
	public void searchAndScrap() {
		// Search a movie on iMDB
		
		if(urlToScrape != null){
			//1: verify if this is search results page or a movie page
			
			//2: if it is a movie page -> scrap the content
			if(isMovieDetaiScreenlURL(urlToScrape)){
				scrape(urlToScrape);
			}
			
		}

		// TODO: Implement IMDBMovieScraper.searchAndScrap()

	}
	
	protected boolean isMovieDetaiScreenlURL(String url){
		Matcher matcher = imdbUrlPattern.matcher(url);
		if(matcher.find()){
			String parameters = matcher.group(4);
			matcher = movieDetailSubUrlPattern.matcher(parameters);
			if(matcher.matches()){
				return true;
			}
			
		}
		return false;
	}
	
	protected boolean isSearchScreenURL(String url){
		Matcher matcher = imdbUrlPattern.matcher(url);
		if(matcher.find()){
			String parameters = matcher.group(4);
			matcher = searchSubUrlPattern.matcher(parameters);
			if(matcher.matches()){
				return true;
			}
			
		}
		return false;
	}

	@Override
	public boolean preferedScraperFor(IMediaDetail mediaDefinition) {
		// Never the prefered scraper
		return false;
	}
	
	@Override
	public boolean canScrapeURL(String url) {
		Matcher matcher = imdbUrlPattern.matcher(url);
		
		if(matcher.find()){
			String domain = matcher.group(3);
			if("www.imdb.com".equals(domain) || "imdb.com".equals(domain)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public SearchResult search() {
		// TODO : Implement IMDBMovieScraper.search()
		return null;
	}

	protected void scrapPicture(final IMediaDetail mediaDetail, final String url) {
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();
			// img#primary-img
			Element imgTag = getSingleElement(url, doc, "img#primary-img");
			if (imgTag != null) {
				final String pictureURL = imgTag.attr("src");

				final ImageInfo imageInfo = ImageInfo.createImageWithURL(pictureURL);

				mediaDetail.setPosterImage(imageInfo);

				downloadPicture(mediaDetail, new ImageDownloadCallBack() {
					@Override
					public void errorDownloading(Throwable th) {
						log.warning("Image could not be downloaded. url: " + url);
					}

					@Override
					public void downloadComplete(BufferedImage image) {
						imageInfo.setBufferedImage(image);
					}
				}, pictureURL);
			}
			else {
				// Log Picture not found
				log.warning("Image tag not found. url: " + url);
			}
		}
		catch (IOException e) {
			log.log(Level.WARNING, "Error fetching Media Name: " + mediaDetail + "  URL: " + url, e);
		}
	}

	@Override
	public void scrape(SearchResult searchResult) {
		String url = searchResult.getURL();
		scrape(url);
	}

	private void scrape(String url) {
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();

			scrapeDocument(doc, url);

		}
		catch (IOException e) {
			log.log(Level.WARNING, "Error fetching Media: " + mediaDetail + "  URL: " + url, e);
			mediaDetail.setStatus(MediaStatus.ERROR);
		}
	}

	protected void scrapeDocument(Document doc, String url) {
		
		try {
			if(mediaDetail.getMediaType() == null){
				//TODO: IMplement the check for media type! .. use the genre for that
				mediaDetail.setMediaType(MediaType.MOVIE);
			}
			//validate MediaFile
			mediaDetail.ensureNFOExists();
			MovieFileNFO movieFileNFO = (MovieFileNFO)mediaDetail.getMediaNFO();
			if(movieFileNFO == null){
				log.warning("No scraping will be done! -> MediaNFO is null for MediaDetail: " + mediaDetail);
				return;
			}
			
			if (!skipDownloadPoster) {
				// Picture
				// td#img_primary > a[onclick*=new Image()]
				Element pictureAHref = getSingleElement(url, doc, "td#img_primary > a[onclick*=new Image()]");
				if (pictureAHref != null) {
					String pictureURL = "http://www.imdb.com" + pictureAHref.attr("href");
					scrapPicture(mediaDetail, pictureURL);
				}
				else {
					// Log Picture not found
					log.warning("Poster image tag not found. url: " + url);
				}
			}

			//Title
			String mediaTitle = null;
			// Title td#overview-top > h1.header
			Element titleH1 = getSingleElement(url, doc, "td#overview-top > h1.header");
			if (titleH1 != null) {
				mediaTitle = Util.trim( titleH1.ownText() );
			}
			else {
				// Log tag not found
				log.warning("Title H1 tag not found. url: " + url);
				// return since the most important info could not be found
				return;
			}
			mediaDetail.setTitle(mediaTitle);
			
			//Original Title
			Element spanOriginalTitle = getSingleElement(url, doc, "td#overview-top > h1.header > span.title-extra");
			String originalTitle = mediaTitle;
			if (spanOriginalTitle != null) {
				originalTitle = Util.trim( spanOriginalTitle.ownText() );
			}
			movieFileNFO.setOriginalTitle(originalTitle);
			
			//Sort Title
			movieFileNFO.setSortTitle(mediaTitle);
			
			Integer year = null;
			// Year
			// td#overview-top > span > a[href$=year]
			Element ahrefYear = getSingleElement(url, doc, "td#overview-top > h1.header > span > a[href*=year]");
			if (ahrefYear != null) {
				try {
					year = new Integer(ahrefYear.text());
				}
				catch (Exception exp) {
					log.warning("Year a[href] content is not an integer. content: " + ahrefYear.text() + " url: " + url);
				}
			}
			else {
				// Log tag not found
				log.fine("Year a[href] tag not found. url: " + url);
			}
			movieFileNFO.setYear(year);

			String rating = null;
			// rating
			// div > div.rating > span.rating
			Element ratingSpan = getSingleElement(url, doc, "div > div.rating > span.rating-rating");
			if (ratingSpan != null) {
				rating = ratingSpan.ownText();
			}
			else {
				// Log tag not found
				log.fine("Rating span tag not found. url: " + url);
			}
			movieFileNFO.setRating(rating);

			
			//top250
			Integer top250 = null;
			Element top250Strong = getSingleElement(url, doc, "div#main > div.article.highlighted > a > strong");
			if (top250Strong != null) {
				try {
					String number = top250Strong.text().substring(top250Strong.text().indexOf('#')+1);
					top250 = new Integer(number);
				}
				catch (Exception exp) {
					log.warning("could not find the integer inside the top250 Strong element. content: " + top250Strong.text() + " url: " + url);
				}
			}
			else {
				// Log tag not found
				log.fine("top250 strong tag not found. url: " + url);
			}
			movieFileNFO.setTop250(top250);
			
			//votes
			Integer votes = null;
			Element voteAnchor = getSingleElement(url, doc, "div.star-box > a[href=ratings]");
			if (voteAnchor != null) {
				try {
					String number = voteAnchor.text().replaceAll("votes", "").replaceAll(",", "").trim();
					votes = new Integer(number);
				}
				catch (Exception exp) {
					log.warning("could not find the integer inside the votes a element. content: " + voteAnchor.text() + " url: " + url);
				}
			}
			else {
				// Log tag not found
				log.fine("vote anchor tag not found. url: " + url);
			}
			movieFileNFO.setVotes(votes);
			
			//Outline
			String shortDescription = null;
			// td#overview-top > p:matchesOwn(\w+)
			Element overviewParagraph = getSingleElement(url, doc, "td#overview-top > p:matchesOwn(\\w+)");
			if (overviewParagraph != null) {
				shortDescription = Util.trim( overviewParagraph.ownText() );
			}
			else {
				// Log tag not found
				log.fine("Overview  tag not found. url: " + url);
			}
			movieFileNFO.setOutline(shortDescription);
			
			
			//Plot
			String plot = null;
			Element plotParagraph = getSingleElement(url, doc, "div#main > div.article > p:matchesOwn(\\w+)");
			if (plotParagraph != null) {
				plot = Util.trim( plotParagraph.ownText() );
			}
			else {
				// Log tag not found
				log.fine("plot Paragraph tag not found. url: " + url);
			}
			movieFileNFO.setPlot(plot);

			//tag line is not implemented...
			
			//runtime
			String runtime = "";
			Element divRuntime = getSingleElement(url, doc, "h4.inline:matchesOwn(Runtime:)");
			if(divRuntime != null){
				divRuntime = divRuntime.parent();
			}
			if (divRuntime != null) {
				runtime = Util.trim( divRuntime.ownText() );
			}
			else {
				// Log tag not found
				log.fine("runtime tag not found. url: " + url);
			}
			movieFileNFO.setRuntime(runtime);
			
			//ID
			String urlParts[] = url.split("/");
			String id = urlParts[urlParts.length-1];
			movieFileNFO.setId(id);
			
			//TODO: Improve the XBMC Movie Schema.. nodes like genre has its own type when it should be string
			//and Integer nodes are BidDecimal in java..
			//genre
			/*
			String genre = "";
			Element divGenres = getSingleElement(url, doc, "h4.inline:matchesOwn(Genres:)");
			if(divGenres != null){
				divGenres = divGenres.parent();
			}
			if (divGenres != null) {
				genre = Util.trim( divGenres.ownText() );
			}
			else {
				// Log tag not found
				log.fine("genre tag not found. url: " + url);
			}
			movieFileNFO.setGenre(genre);
			*/
			
			// TODO: .. more stuff to scrap form iMDB

			mediaDetail.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
			mediaDetail.save();
		}
		catch (MediaFileException e) {
			log.log(Level.WARNING, "Error saving media info Media: " + mediaDetail + "  URL: " + url, e);
			mediaDetail.setStatus(MediaStatus.ERROR);
		}
	}

	@Override
	public void scrapCandidates() {
		// TODO: Implement IMDBMovieScraper.scrapCandidates

	}

	public boolean isSkipDownloadPoster() {
		return skipDownloadPoster;
	}

	public void setSkipDownloadPoster(boolean skipDownloadPoster) {
		this.skipDownloadPoster = skipDownloadPoster;
	}
}
