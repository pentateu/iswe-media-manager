package nz.co.iswe.mediamanager.media.nfo;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.nfo.xml.movie.Movie;
import nz.co.iswe.mediamanager.scraper.MediaType;

public class TVShowFileNFO extends AbstractFileNFO  {

	private Movie movie;
	
	protected TVShowFileNFO(MediaDetail mediaFileDefinition, Movie movie) throws MediaFileException {
		super(mediaFileDefinition);
		this.movie = movie;
	}
	
	protected TVShowFileNFO(File file, Movie movie) throws MediaFileException {
		super(file);
		this.movie = movie;
	}

	@Override
	public void setRating(String rating) {
		movie.setRating(rating);
	}
	@Override
	public String getRating() {
		return movie.getRating();
	}

	@Override
	public void setOutline(String outline) {
		movie.setOutline(outline);
	}
	@Override
	public String getOutline() {
		return movie.getOutline();
	}

	@Override
	public void setTitle(String title) {
		movie.setTitle(title);
	}
	
	@Override
	public String getTitle() {
		return movie.getTitle();
	}

	@Override
	public void setYear(Integer year) {
		movie.setYear(new BigInteger(year.toString()));
	}

	@Override
	public void setThumb(String path) {
		movie.setThumb(path);
	}

	@Override
	public void setFilenameAndPath(String path) {
		movie.setFilenameandpath(path);
	}
	@Override
	public String getFilenameAndPath() {
		return movie.getFilenameandpath();
	}
	
	@Override
	protected void saveToXML(File file) {
		// TODO : TVShowFileNFO do the proper implementation
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Movie getMovie() {
		return movie;
	}

	@Override
	public MediaType getMediaType() {
		return MediaType.TV_SHOW;
	}
	
	@Override
	public String getThumb() {
		return movie.getThumb();
	}
	
	@Override
	public Integer getYear() {
		if(movie.getYear() != null){
			return movie.getYear().intValue();
		}
		return null;
	}
	
	protected StringBuffer buildToStringDescription(){
		StringBuffer strDescription = new StringBuffer("[MovieFileNFO ");
		
		strDescription.append("\n Title: ").append(movie.getTitle());
		
		strDescription.append("\n Rating: " + movie.getRating());
		
		strDescription.append("\n filenameandpath: " + movie.getFilenameandpath());
		
		strDescription.append("\n ID: " + movie.getId());
		
		strDescription.append("\n Outline: " + movie.getOutline());
		
		strDescription.append("\n Year: " + movie.getYear());
		
		return strDescription;
	}
	
	@Override
	public String toString() {
		StringBuffer strDescription = buildToStringDescription();
		
		//finish the string description construction
		strDescription.append("]");
		
		return strDescription.toString();
	}

	
	
}
