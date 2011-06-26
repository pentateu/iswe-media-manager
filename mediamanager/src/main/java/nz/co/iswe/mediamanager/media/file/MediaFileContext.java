package nz.co.iswe.mediamanager.media.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.MediaFileListener;
import nz.co.iswe.mediamanager.scraper.IScraper;
import nz.co.iswe.mediamanager.scraper.IScrapingStatusObserver;
import nz.co.iswe.mediamanager.scraper.ScraperContext;

public class MediaFileContext {

	private static Logger log = Logger.getLogger(MediaFileContext.class.getName());

	private static MediaFileContext instance;

	public static MediaFileContext getInstance() {
		if (instance == null) {
			instance = new MediaFileContext();
		}
		return instance;
	}

	private Map<String, MediaDetail> pathToMediaDetail = new HashMap<String, MediaDetail>();

	private List<ScrapingQueueItem> scrapingQueuee = new ArrayList<ScrapingQueueItem>();

	private boolean stopScraping = false;

	public MediaDetail getMediaFile(File item) throws MediaFileException {

		MediaFileMetadata metadata = new MediaFileMetadata(item, true);

		if (metadata.isValidMediaFile() && // valid media file
				!metadata.isSample()) { // is not a sample file

			// create a mediaDetail instance to load the additional metadata
			MediaDetail mediaDetail = new MediaDetail(metadata);

			// check if there is a reference form the pool
			MediaDetail fromPool = getFromPool(mediaDetail.getFileName());

			if (fromPool == null) {

				mediaDetail.addListener(new MediaFileListenerImpl(mediaDetail.getFileName()));

				addToPool(mediaDetail);
				mediaDetail.init();
				return mediaDetail;
			} else {
				mediaDetail.release();
				// return the reference form the pool
				return fromPool;
			}

		}
		// not valid or a sample file
		return null;
	}

	private void addToPool(MediaDetail mediaFileDefinition) {
		pathToMediaDetail.put(mediaFileDefinition.getFileName(), mediaFileDefinition);
	}

	public void addToScrapingQueue(MediaDetail mediaDetail, IScraper scraper) {
		ScrapingQueueItem item = new ScrapingQueueItem(mediaDetail, scraper);
		if (!scrapingQueuee.contains(item)) {
			scrapingQueuee.add(item);
		}
	};
	
	public void addToScrapingQueue(MediaDetail mediaDetail) {
		ScrapingQueueItem item = new ScrapingQueueItem(mediaDetail);
		if (!scrapingQueuee.contains(item)) {
			scrapingQueuee.add(item);
		}
	}

	public Collection<MediaDetail> getAll() {
		return pathToMediaDetail.values();
	}

	private MediaDetail getFromPool(String fileName) {
		return pathToMediaDetail.get(fileName);
	}
	
	public void scrap(final IScrapingStatusObserver observer, final IScraper scraper, final MediaDetail mediaDetail) {
		stopScraping = false;

		if (observer == null || scraper == null || mediaDetail == null) {
			log.warning("Invalid arguments! Exiting the method.");
			return;
		}
		
		final Thread scrapingThread = new Thread() {
			public void run() {

				observer.notifyScrapingStarted();
				
				ScraperContext scraperContext = ScraperContext.getInstance();
				
				// Notify a new file is being processed
				observer.notifyNewStep();

				long start = System.currentTimeMillis();
					
				try {
					scraperContext.setScrapingStatusObserver(observer);

					observer.notifyNewStep();
					
					log.fine(" ### START - Scraping ### Media Detail: " + mediaDetail);

					scraperContext.scrape(mediaDetail, scraper);

					long total = System.currentTimeMillis() - start;

					log.fine(" ### END - Scraping ### Total time to scrape media. miliseconds: " + total + " Media File: " + mediaDetail);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Error Scraping Media File: " + mediaDetail, e);
					observer.notifyErrorOccurred("Error Scraping Media File: " + mediaDetail, e);
				}
				observer.notifyStepFinished();

				observer.notifyScrapingFinished();
			}
		};
		scrapingThread.start();
	}

	public void startScrap(final IScrapingStatusObserver observer) {
		stopScraping = false;

		log.info("Start scraping queue size: " + scrapingQueuee.size());

		if (scrapingQueuee.size() == 0) {
			// nothing to scrap
			observer.notifyScrapingFinished();
			return;
		}

		final Thread scrapingThread = new Thread() {
			public void run() {

				observer.notifyScrapingStarted();
				
				ScraperContext scraperContext = ScraperContext.getInstance();
				while (!stopScraping && scrapingQueuee.size() > 0) {
					// Notify a new file is being processed
					observer.notifyNewStep();

					long start = System.currentTimeMillis();
					ScrapingQueueItem queueItem = scrapingQueuee.remove(0);
					IScraper scraper = queueItem.getScraper();
					MediaDetail mediaDetail = queueItem.getMediaDetail();

					try {
						scraperContext.setScrapingStatusObserver(observer);

						log.fine(" ### START - Scraping ### Media Detail: " + mediaDetail);

						scraperContext.scrape(mediaDetail, scraper);

						long total = System.currentTimeMillis() - start;

						log.fine(" ### END - Scraping ### Total time to scrape media. miliseconds: " + total + " Media File: " + mediaDetail);
					} catch (Exception e) {
						log.log(Level.SEVERE, "Error Scraping Media File: " + mediaDetail, e);
						observer.notifyErrorOccurred("Error Scraping Media File: " + mediaDetail, e);
					}
					observer.notifyStepFinished();
				}

				observer.notifyScrapingFinished();
			}
		};

		scrapingThread.start();

	}

	public static void clearInstance() {
		instance = null;
	}

	class ScrapingQueueItem {
		MediaDetail mediaDetail;
		IScraper scraper;
		public ScrapingQueueItem(MediaDetail mediaDetail) {
			this.mediaDetail = mediaDetail;
		}
		public ScrapingQueueItem(MediaDetail mediaDetail, IScraper scraper) {
			this.mediaDetail = mediaDetail;
			this.scraper = scraper;
		}
		public MediaDetail getMediaDetail() {
			return mediaDetail;
		}
		public IScraper getScraper() {
			return scraper;
		}
		@Override
		public int hashCode() {
			return mediaDetail.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if( ! (obj instanceof ScrapingQueueItem)){
				return false;
			}
			ScrapingQueueItem other = (ScrapingQueueItem)obj;
			return other.mediaDetail.equals(this.mediaDetail);
		}
	}
	
	class MediaFileListenerImpl implements MediaFileListener {

		String fileName;

		MediaFileListenerImpl(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void notifyMediaFileRenamed(MediaDetail mediaDetail) throws MediaFileException {
			// update the reference

			// remove using the old path
			pathToMediaDetail.remove(fileName);

			// add with the new path
			fileName = mediaDetail.getFileName();
			pathToMediaDetail.put(fileName, mediaDetail);
		}

		@Override
		public void notifyChange(IMediaDetail mediaDefinition) {
			// ignore
		}

	}

	

	
}
