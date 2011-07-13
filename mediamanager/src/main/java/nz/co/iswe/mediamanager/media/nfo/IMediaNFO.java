package nz.co.iswe.mediamanager.media.nfo;

import java.io.File;

import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.MediaType;

public interface IMediaNFO {

	void save() throws MediaFileException;
	
	void setRating(String rating);
	String getRating();
	
	void setOutline(String outline);
	String getOutline();
	
	void setTitle(String title);
	String getTitle();

	void setYear(Integer year);
	Integer getYear();

	void setThumb(String path);
	String getThumb();
	
	void setFilenameAndPath(String path);
	String getFilenameAndPath();
	
	MediaType getMediaType();
	
	void setMediaDefinition(IMediaDetail mediaDefinition);

	void setOriginalFileName(String originalFileName);
	String getOriginalFileName();

	File getFile();
}
