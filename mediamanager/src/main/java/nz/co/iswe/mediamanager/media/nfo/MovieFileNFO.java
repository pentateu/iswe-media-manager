package nz.co.iswe.mediamanager.media.nfo;

import java.io.File;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.nfo.xml.movie.Movie;
import nz.co.iswe.mediamanager.media.nfo.xml.movie.ObjectFactory;
import nz.co.iswe.mediamanager.scraper.MediaType;

public class MovieFileNFO extends AbstractFileNFO  {

	private Movie movie;
	
	protected MovieFileNFO(MediaDetail mediaFileDefinition, Movie movie) throws MediaFileException {
		super(mediaFileDefinition);
		this.movie = movie;
	}
	
	protected MovieFileNFO(File file, Movie movie) throws MediaFileException {
		super(file);
		this.movie = movie;
	}
	
	protected StringBuffer buildToStringDescription(){
		StringBuffer strDescription = new StringBuffer("[MovieFileNFO ");
		
		strDescription.append("Title: ").append(movie.getTitle());
		
		strDescription.append(" Rating: " + movie.getRating());
		
		
		strDescription.append(" ID: " + movie.getId());
		
		
		strDescription.append(" Year: " + movie.getYear());
		
		strDescription.append("\nFilenameandpath: " + movie.getFilenameandpath());
		
		strDescription.append("\nOutline: " + movie.getOutline());
		
		
		return strDescription;
	}
	
	@Override
	public String toString() {
		StringBuffer strDescription = buildToStringDescription();
		
		//finish the string description construction
		strDescription.append("]");
		
		return strDescription.toString();
	}	

	@Override
	public void setOriginalFileName(String originalFileName) {
		if(movie.getMediaManager() == null){
			movie.setMediaManager(new ObjectFactory().createMediaManager());
		}
		movie.getMediaManager().setOriginalFileName(originalFileName);
	}
	@Override
	public String getOriginalFileName() {
		if(movie.getMediaManager() != null){
			return movie.getMediaManager().getOriginalFileName();
		}
		else{
			return null;
		}
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
		if(year == null){
			return;
		}
		movie.setYear( year );
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
		MediaNFOFactory.getInstance().saveToXMLFile(this, file);
	}
	
	public Movie getMovie() {
		return movie;
	}

	@Override
	public MediaType getMediaType() {
		return MediaType.MOVIE;
	}
	
	@Override
	public String getThumb() {
		return movie.getThumb();
	}

	public void setOriginalTitle(String title) {
		movie.setOriginaltitle(title);
	}
	
	public void setSortTitle(String title) {
		movie.setSorttitle(title);
	}
	
	public void setVotes(Integer value) {
		if(value == null){
			return;
		}
		movie.setVotes( value );
	}
	
	public void setTop250(Integer value) {
		if(value == null){
			return;
		}
		movie.setTop250( value );
	}
	
	@Override
	public Integer getYear() {
		if(movie.getYear() > 0){
			return movie.getYear();
		}
		return null;
	}
	
	public void setPlot(String plot) {
		movie.setPlot(plot);
	}
	
	public void setRuntime(String value) {
		movie.setRuntime(value);
	}
	public String getRuntime(){
		return movie.getRuntime();
	}
	
	public String getOriginalTitle() {
		return movie.getOriginaltitle();
	}

	public String getSortTitle() {
		return movie.getSorttitle();
	}

	public Integer getTop250() {
		if(movie.getTop250() > 0){
			return movie.getTop250();
		}
		return null;
	}

	public Integer getVotes() {
		if(movie.getVotes() > 0){
			return movie.getVotes();
		}
		return null;
	}

	public String getPlot() {
		return movie.getPlot();
	}

	public void setId(String id) {
		movie.setId(id);
	}	
	public String getId(){
		return movie.getId();
	}

	
}
