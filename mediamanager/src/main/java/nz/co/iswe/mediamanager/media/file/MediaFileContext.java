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

	private List<MediaDetail> scrapingQueuee = new ArrayList<MediaDetail>();

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

	public MediaDetail addToScrapingQueue(MediaDetail mediaFileDefinition) {
		if (!scrapingQueuee.contains(mediaFileDefinition)) {
			scrapingQueuee.add(mediaFileDefinition);
		}
		return mediaFileDefinition;
	}

	public Collection<MediaDetail> getAll() {
		return pathToMediaDetail.values();
	}

	private MediaDetail getFromPool(String fileName) {
		return pathToMediaDetail.get(fileName);
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
					MediaDetail mediaFileDefinition = scrapingQueuee.remove(0);

					try {
						scraperContext.setScrapingStatusObserver(observer);

						log.fine(" ### START - Scraping ### Media File: " + mediaFileDefinition);

						scraperContext.scrape(mediaFileDefinition);

						long total = System.currentTimeMillis() - start;

						log.fine(" ### END - Scraping ### Total time to scrape media. miliseconds: " + total
								+ " Media File: " + mediaFileDefinition);
					} catch (Exception e) {
						log.log(Level.SEVERE, "Error Scraping Media File: " + mediaFileDefinition, e);

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

	};
}
