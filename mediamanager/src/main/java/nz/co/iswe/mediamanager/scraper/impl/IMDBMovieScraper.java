package nz.co.iswe.mediamanager.scraper.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IMDBMovieScraper extends AbstractScraper {

	private static Logger log = Logger.getLogger(IMDBMovieScraper.class.getName());

	private boolean skipDownloadPoster = false;

	@Override
	public void searchAndScrap() {
		// Search a movie on iMDB

		// TODO: Implement IMDBMovieScraper.searchAndScrap()

	}

	@Override
	public boolean preferedScraperFor(IMediaDetail mediaDefinition) {
		// Never the prefered scraper
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
			} else {
				// Log Picture not found
				log.warning("Image tag not found. url: " + url);
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "Error fetching Media Name: " + mediaDetail + "  URL: " + url, e);
		}
	}

	@Override
	public void scrape(SearchResult searchResult) {
		String url = searchResult.getURL();
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();

			if ( ! skipDownloadPoster ) {
				// Picture
				// td#img_primary > a[onclick*=new Image()]
				Element pictureAHref = getSingleElement(url, doc, "td#img_primary > a[onclick*=new Image()]");
				if (pictureAHref != null) {
					String pictureURL = "http://www.imdb.com" + pictureAHref.attr("href");
					scrapPicture(mediaDefinition, pictureURL);
				} else {
					// Log Picture not found
					log.warning("Poster image tag not found. url: " + url);
				}
			}

			String mediaTitle = null;
			// Title td#overview-top > h1.header
			Element titleH1 = getSingleElement(url, doc, "td#overview-top > h1.header");
			if (titleH1 != null) {
				mediaTitle = titleH1.ownText();
			} else {
				// Log Picture not found
				log.warning("Title H1 tag not found. url: " + url);
				// return since the most important info could not be found
				return;
			}
			
			mediaDefinition.setTitle(mediaTitle);
			
			Integer year = null;
			// Year
			// td#overview-top > span > a[href$=year]
			Element ahrefYear = getSingleElement(url, doc, "td#overview-top > h1.header > span > a[href*=year]");
			if (ahrefYear != null) {
				try{
					year = new Integer(ahrefYear.text());
				}
				catch(Exception exp){
					log.warning("Year a[href] content is not an integer. content: " + ahrefYear.text() + " url: " + url);
				}
			} else {
				// Log Picture not found
				log.fine("Year a[href] tag not found. url: " + url);
			}
			mediaDefinition.ensureNFOExists();
			mediaDefinition.getMediaNFO().setYear(year);

			String rating = null;
			// rating
			// div > div.rating > span.rating
			Element ratingSpan = getSingleElement(url, doc, "div > div.rating > span.rating-rating");
			if (ratingSpan != null) {
				rating = ratingSpan.ownText();
			} else {
				// Log Picture not found
				log.fine("Rating span tag not found. url: " + url);
			}
			mediaDefinition.getMediaNFO().setRating(rating);

			String shortDescription = null;
			// td#overview-top > p:matchesOwn(\w+)
			Element shortDescriptionParagraph = getSingleElement(url, doc, "td#overview-top > p:matchesOwn(\\w+)");
			if (shortDescriptionParagraph != null) {
				shortDescription = shortDescriptionParagraph.text();
			} else {
				// Log Picture not found
				log.fine("Rating span tag not found. url: " + url);
			}
			mediaDefinition.getMediaNFO().setOutline(shortDescription);

			// TODO: .. more stuff to scrap form iMDB
			
			
			
			mediaDefinition.save();

		} catch (IOException e) {
			log.log(Level.WARNING, "Error fetching Media: " + mediaDefinition + "  URL: " + searchResult.getURL(), e);
			mediaDefinition.setStatus(MediaStatus.ERROR);
		} catch (MediaFileException e) {
			log.log(Level.WARNING, "Error saving media info Media: " + mediaDefinition + "  URL: " + searchResult.getURL(), e);
			mediaDefinition.setStatus(MediaStatus.ERROR);
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
