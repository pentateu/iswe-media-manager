package nz.co.iswe.mediamanager.media;

import java.util.List;

import nz.co.iswe.mediamanager.media.file.CandidateMediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;
import nz.co.iswe.mediamanager.media.nfo.IMediaNFO;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.scraper.SearchResult;


public interface IMediaDetail {

	//String getSimpleName();

	void setStatus(MediaStatus status);
	
	MediaStatus getStatus();
	
	void setCandidateUrls(List<SearchResult> candidateURLs);

	void addCandidate(
			CandidateMediaDetail candidateMediaDefinition);

	void setBlogPostURL(String url);

	void save() throws MediaFileException;

	void setPosterImage(ImageInfo imageInfo);

	boolean hasPoster();

	void setTitle(String title);
	String getTitle();
	

	void setMediaType(MediaType mediaType);
	MediaType getMediaType();
	
	IMediaNFO getMediaNFO();

	void ensureNFOExists() throws MediaFileException;

	MediaFolder getMediaFolder();

	String getFileName();

	void setYear(Integer year);
	Integer getYear();
	
}
