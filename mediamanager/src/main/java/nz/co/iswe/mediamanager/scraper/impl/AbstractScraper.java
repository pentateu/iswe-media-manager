package nz.co.iswe.mediamanager.scraper.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.IScraper;
import nz.co.iswe.mediamanager.scraper.IScrapingStatusObserver;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class AbstractScraper implements IScraper {

	private static Logger log = Logger.getLogger(AbstractScraper.class
			.getName());

	// Google Chrome user agent
	protected static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13";

	// TODO: [CONFIG]  -> Should be configurable
	protected double minimumScore = 80;

	// TODO: [CONFIG]  -> Should be a configuration and not fixed
	protected boolean preLoadCandidates = true;

	protected IMediaDetail mediaDetail;

	protected IScrapingStatusObserver observer;

	@Override
	public void setMediaDefinition(IMediaDetail mediaDetail) {
		this.mediaDetail = mediaDetail;
	}

	@Override
	public void setScrapingStatusObserver(IScrapingStatusObserver observer) {
		this.observer = observer;
	}

	protected void downloadPicture(final IMediaDetail mediaDetail, final ImageDownloadCallBack callback,
			final String url) {
		Thread pictureDownloadThread = new Thread("Picture Download Thread") {
			@Override
			public void run() {
				// download the picture async in parallel of the rest of the
				// processing
				try {
					downloadPictureImpl(callback, url);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Error downloading picture. MediaDetail: " + mediaDetail, e);
				}
			}
		};
		pictureDownloadThread.start();
	}

	protected synchronized void downloadPictureImpl(
			final ImageDownloadCallBack callback, final String url) throws MediaFileException {

		try {
			URL imgURL = new URL(url);
			BufferedImage bufferedImage = ImageIO.read(imgURL);
			callback.downloadComplete(bufferedImage);
		}
		catch (MalformedURLException e) {
			log.log(Level.WARNING, "Error Downloading Image URL: " + url, e);
			callback.errorDownloading(e);
		}
		catch (IOException e) {
			log.log(Level.WARNING, "Error Downloading Image URL: " + url, e);
			callback.errorDownloading(e);
		}
		
		/*
		 * String imgURL="http://nscraps.com/images/logo.png";
		 * 
		 * URL url = new URL(imgURL);
		 * 
		 * Image image = ImageIO.read(url);
		 * 
		 * BufferedImage cpimg=bufferImage(image);
		 * 
		 * File f1 = new File("test.png");
		 * 
		 * ImageIO.write(cpimg, "png", f1);
		 */
	}
/*
	protected BufferedImage createBufferedImage(Image image) {
		return createBufferedImage(image, BufferedImage.TYPE_INT_RGB);
	}

	protected BufferedImage createBufferedImage(Image image, int type) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), type);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(image, null, null);
		return bufferedImage;
	}
	*/

	protected Element getSingleElement(String url, Document doc,
			String elementQuery) {
		Elements elements = doc.select(elementQuery);

		if (elements.size() == 0) {
			return null;
		} else if (elements.size() == 1) {
			return elements.get(0);
		} else {
			// Log, since it should not find more than one tag
			log.warning("[Page format might have changed] A single element should be found, but more was found. url: "
					+ url + " element query: " + elementQuery);
			return null;
		}
	}
}
