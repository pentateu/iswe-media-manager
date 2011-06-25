package nz.co.iswe.mediamanager.scraper.impl;

import java.awt.image.BufferedImage;

import nz.co.iswe.mediamanager.media.file.MediaFileException;

public interface ImageDownloadCallBack {

	void downloadComplete(BufferedImage image) throws MediaFileException;

	void errorDownloading(Throwable th);

}
