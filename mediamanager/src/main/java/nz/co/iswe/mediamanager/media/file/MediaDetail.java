package nz.co.iswe.mediamanager.media.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.ImageInfoStatus;
import nz.co.iswe.mediamanager.media.MediaFileListener;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.folder.AbstractMediaFolderChangeAware;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;
import nz.co.iswe.mediamanager.media.folder.MediaFolderChangeListener;
import nz.co.iswe.mediamanager.media.folder.MediaFolderContext;
import nz.co.iswe.mediamanager.media.nfo.AbstractFileNFO;
import nz.co.iswe.mediamanager.media.nfo.IMediaNFO;
import nz.co.iswe.mediamanager.media.nfo.MediaNFOFactory;
import nz.co.iswe.mediamanager.media.subtitles.ISubtitle;
import nz.co.iswe.mediamanager.media.subtitles.SubtitleContext;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.scraper.SearchResult;

import org.apache.commons.io.FileUtils;

public class MediaDetail extends AbstractMediaFolderChangeAware implements IMediaDetail {

	private static final String PART = "Part";
	private static final String MEDIA_FILE = "mediaFile";
	private static Logger log = Logger.getLogger(MediaDetail.class.getName());
	protected static final String NFO = "nfo";

	protected ImageInfo posterImage;

	protected int candidateCount = 0;

	protected int multiPartCount = 0;

	protected ISubtitle subtitle;

	protected MediaType mediaType;

	protected IMediaNFO mediaNFO;

	protected MediaStatus status = MediaStatus.NO_MEDIA_DETAILS;// default

	protected List<SearchResult> candidateURLs = new ArrayList<SearchResult>();

	protected List<CandidateMediaDetail> candidateMediaDetails = new ArrayList<CandidateMediaDetail>();

	protected String blogPostURL;

	protected String title;
	
	protected Integer year;
	
	protected String originalFileName;

	protected boolean multiPart;

	protected List<MediaFileListener> listeners = new ArrayList<MediaFileListener>();
	
	MediaDetail(MediaFileMetadata metadata) throws MediaFileException {
		super(MediaFolderContext.getInstance().getMediaFolder(metadata.getFile().getParent()));
		// load files
		if (metadata.isMultPart()) {
			//add multi part files
			for (MediaFileMetadata item : metadata.getMultiPartList()) {
				setFile(PART + item.getMultiPartNumber(), item.getFile());
				multiPartCount++;
			}
		} else {
			setFile(MEDIA_FILE, metadata.getFile());
		}
		this.multiPart = metadata.isMultPart();
	}

	MediaDetail(MediaFolder mediaFolder) {
		super(mediaFolder);
	}

	public void init() throws MediaFileException {
		// look for existing media information in the same folder
		lookUpNFO();

		if (mediaNFO != null) {
			this.status = MediaStatus.MEDIA_DETAILS_FOUND;
			this.mediaType = mediaNFO.getMediaType();
			this.title = mediaNFO.getTitle();
			this.year = mediaNFO.getYear();
			this.originalFileName = mediaNFO.getOriginalFileName();
			
			// Load image
			if (mediaNFO.getThumb() != null) {
				try {
					posterImage = new ImageInfo(new File(mediaNFO.getThumb()));
				} catch (MediaFileException e) {
					tryLoadPosterImage();
					if (posterImage != null) {
						mediaNFO.setThumb(posterImage.getImageFile().getPath());
					}
				}
			}
		} else {
			// create the title based on the filename
			this.title = buildTitle();
			this.year = getYearFromFileName();
			tryLoadPosterImage();
			this.status = MediaStatus.NO_MEDIA_DETAILS;
		}

		lookUpCandidates();

		lookUpSubtitles();
	}
	
	protected Integer getYearFromFileName(){
		// get the filename form the first file
		String fileName = getFileName();
		Integer tempYear = Util.getYearFromTitle(fileName);
		return tempYear;
	}

	protected String buildTitle() {
		// get the filename form the first file
		String fileName = getFileName();
		String result = null;
		result = Util.removeYearFromTitle(fileName);
		return result;
	}

	protected void tryLoadPosterImage() throws MediaFileException {
		// check if the media folder is exclusive to this media file
		if (isInExclusiveFolder()) {
			// verify if there is a cover file inside the media folder and load
			// as the poster image
			File[] files = mediaFolder.getFile().listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					String simpleFileName = Util.getFileSimpleName(file.getName());
					// if the file name is cover.* and the is a valid image file
					return "cover".equalsIgnoreCase(simpleFileName) && Util.isValidImageFile(file);
				}
			});
			if (files.length > 0) {
				posterImage = new ImageInfo(files[0]);
			}
		}
	}

	private void lookUpSubtitles() throws MediaFileException {
		this.subtitle = SubtitleContext.getInstance().lookUp(this);
	}

	private void lookUpCandidates() throws MediaFileException {
		String candidateFolderPrefix = Util.getCandidateFolderPrefix(getFileName());
		final String matchRegex = candidateFolderPrefix + "\\d{1,2}";
		File[] candidateFolders = mediaFolder.getFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().matches(matchRegex);
			}
		});

		if (candidateFolders != null && candidateFolders.length > 0) {
			for (File folder : candidateFolders) {

				// look for NFO files inside the candidate folder
				File[] nfoFiles = folder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isFile() && NFO.equals(Util.getFileExtension(pathname.getName()));
					}
				});

				if (nfoFiles != null && nfoFiles.length > 0) {
					IMediaNFO mediaNFO = MediaNFOFactory.getInstance().loadFromFile(nfoFiles[0]);
					if (mediaNFO != null) {
						CandidateMediaDetail candidateMediaDefinition = new CandidateMediaDetail(this, mediaNFO);
						addCandidate(candidateMediaDefinition);

						// set the status of the media info
						this.setStatus(MediaStatus.CANDIDATE_DETAILS_FOUND);
					}
				}

			}
		}

	}

	/**
	 * Search for an NFO file that matches this Media file
	 * 
	 * @throws MediaFileException
	 */
	protected void lookUpNFO() throws MediaFileException {

		if (mediaNFO != null) {
			return;
		}

		// 1: Look for a NFO file with the same name as the media file on the
		// same folder
		File[] results = mediaFolder.getFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				try {
					// NFO file name
					String nfoFileName = getFileName() + "." + NFO;
					return nfoFileName.equalsIgnoreCase(fileName);
				} catch (Exception e) {
					return false;
				}
			}
		});
		if (results != null && results.length > 0) {
			mediaNFO = MediaNFOFactory.getInstance().loadFromFile(results[0]);
		}

		if (mediaNFO == null) {
			log.fine("MediaNFO not found. MediaDetail: " + this);
		}
	}

	@Override
	public synchronized void save() throws MediaFileException {

		boolean isInExclusiveFolder = isInExclusiveFolder();
		boolean renamed = false;

		if (isMultiPart()) {
			String[] keys = files.keySet().toArray(new String[]{});
			for (String fileKey : keys) {
				File file = files.get(fileKey);
				String[] parts = Util.getMultiPartSufix(file.getName());
				String newFileName = buildNewFileName(file, parts[2]);
				// check wheather the file name will need to change
				if (!file.getName().equals(newFileName)) {
					renamed = renameTo(fileKey, newFileName);
				}
			}

		} 
		else {
			File file = getFile(MEDIA_FILE);
			String newFileName = buildNewFileName(file, null);
			// check wheather the file name will need to change
			if (!file.getName().equals(newFileName)) {
				renamed = renameTo(MEDIA_FILE, newFileName);
			}
		}
		
		if (renamed) { //if the file names were changed
			fireNotifyMediaFileRenamed();

			if (isInExclusiveFolder) {
				// rename the folder to match the mediafilename
				File newFolder = new File(mediaFolder.getFile().getParentFile(), buildExclusiveFolderName());
				mediaFolder.renameTo(newFolder);
			}
		}

		// save the poster image only if the media folder is exclusive to this
		// media file
		if (isInExclusiveFolder) {
			if (posterImage != null && ImageInfoStatus.IN_MEMORY.equals(posterImage.getImageStatus())) {
				// 3 save the poster image
				posterImage.saveAs(mediaFolder, "cover");
			}
		}

		// 4 save the NFO file
		saveNFO();
		notifyChange();
	}

	public boolean isInExclusiveFolder() throws MediaFileException {
		return mediaFolder.isExclusive(this);
	}

	@Override
	protected String buildNewFileName(String key, MediaDetail mediaFile) {
		//do nothing
		return null;
	}

	protected String buildNewFileName(File file, String partNumber) {
		String newFileName = getTitle();
		String extension = Util.getFileExtension(file.getName());
		if (newFileName != null) {
			if (getYear() != null && getYear() > 0) {
				newFileName = newFileName + " " + "(" + getYear() + ")";
			} 
			
			if(partNumber !=  null){
				//add the part sufix
				newFileName = newFileName + " - Part" + partNumber + "." + extension;
			}
			else{
				newFileName = newFileName + "." + extension;
			}
			
		} else {
			newFileName = file.getName();
		}
		newFileName = Util.normalizeFileName(newFileName);
		return newFileName;
	}

	@Override
	public void setPosterImage(ImageInfo imageInfo) {
		this.posterImage = imageInfo;
	}

	@Override
	public boolean hasPoster() {
		return posterImage != null;
	}

	protected void saveNFO() throws MediaFileException {

		ensureNFOExists();

		if (mediaNFO != null) {
			// Populate the movie info
			mediaNFO.setTitle(this.getTitle());
			if(this.year != null){
				mediaNFO.setYear(this.year);
				this.year = null;
			}

			if (posterImage != null && posterImage.getImageFile() != null) {
				// Poster
				mediaNFO.setThumb(posterImage.getImageFile().getPath());
			}

			File file = getMainFile();
			mediaNFO.setFilenameAndPath(file.getPath());

			//
			if(originalFileName != null){
				mediaNFO.setOriginalFileName(originalFileName);
			}
			
			mediaNFO.save();
		}

	}

	@Override
	public void ensureNFOExists() throws MediaFileException {

		// look for the NFO file in the media folder
		lookUpNFO();

		if (mediaNFO == null && mediaType != null) {

			if (MediaType.MOVIE.equals(mediaType)) {
				// Create a new NFO
				mediaNFO = MediaNFOFactory.getInstance().createMovieNFO(this);
			} else if (MediaType.DOCUMENTARY.equals(mediaType)) {
				// Create a new NFO
				mediaNFO = MediaNFOFactory.getInstance().createDocumentaryNFO(this);
			} else if (MediaType.TV_SHOW.equals(mediaType)) {
				// Create a new NFO
				mediaNFO = MediaNFOFactory.getInstance().createTVShowNFO(this);
			} else {
				throw new MediaFileException("Invalid Media Type");
			}

			this.addListener((MediaFileListener) mediaNFO);
		}
	}

	@Override
	public void notifyNewMediaFolderCreated(MediaFolder mediaFolder) throws MediaFileException {
		//move all files to the new folder
		String[] keys = files.keySet().toArray(new String[]{});
		for (String fileKey : keys) {
			File file = files.get(fileKey);
			moveToNewMediaFolder(fileKey, file);
		}
		
		this.mediaFolder = mediaFolder;
		if (mediaNFO != null) {
			File file = getMainFile();
			mediaNFO.setFilenameAndPath(file.getPath());
		}
		mediaFolder.addListener(this);
		//fireNotifyMediaFilePathChanged();
	}
/*
	private void fireNotifyMediaFilePathChanged() {
		MediaFileListener[] listenerArray = listeners.toArray(new MediaFileListener[] {});
		if (listenerArray.length > 0) {
			for (MediaFileListener listener : listenerArray) {
				listener.notifyMediaFilePathChanged(this);
			}
		}
	}
	*/

	private void moveToNewMediaFolder(String fileKey, File file) throws MediaFileException {
		// move file to the new folder
		File newFile = new File(mediaFolder.getFile(), file.getName());
		boolean moved = file.renameTo(newFile);
		if (moved && Util.isMediaValidFile(newFile)) {
			setFile(fileKey, newFile);
		} 
		else {
			throw new MediaFileException("Error trying to move Media File to the new folder! " + newFile.getPath());
		}
	}

	@Override
	public void notifyMediaFolderRenamed(MediaFolder mediaFolder) throws MediaFileException {
		mediaFolderChanged(mediaFolder);
	}

	@Override
	protected void validateFileImpl(File file) throws MediaFileException {
		Util.isMediaValidFile(file);
	}

	private void mediaFolderChanged(MediaFolder mediaFolder) throws MediaFileException {
		//move all files to the new folder
		String[] keys = files.keySet().toArray(new String[]{});
		for (String fileKey : keys) {
			File file = files.get(fileKey);
			validateFolderChange(mediaFolder, fileKey, file);
		}
		this.mediaFolder = mediaFolder;
		if (mediaNFO != null) {
			File file = getMainFile();
			mediaNFO.setFilenameAndPath(file.getPath());
		}
		//fireNotifyMediaFilePathChanged();
	}

	public File getMainFile() {
		File file = null;
		if(isMultiPart()){
			file = getFile(PART + "1");
		}
		else{
			file = getFile(MEDIA_FILE);
		}
		return file;
	}

	private void validateFolderChange(MediaFolder mediaFolder, String fileKey, File file) throws MediaFileException {
		// get a reference to the file in the new folder and validate
		File newFile = new File(mediaFolder.getFile(), file.getName());
		if(file.getPath().equals(newFile.getPath())){
			//no change to the file
		}
		else if (!file.exists() && newFile.exists() && Util.isMediaValidFile(newFile)) {
			setFile(fileKey, newFile);
		} else {
			throw new MediaFileException(
					"Error trying to get the reference to the Media File in the new media folder! " + newFile.getPath());
		}
	}

	@Override
	public void notifyMediaFolderMoved(MediaFolder mediaFolder) throws MediaFileException {
		mediaFolderChanged(mediaFolder);
	}

	protected void fireNotifyMediaFileRenamed() throws MediaFileException {
		MediaFileListener[] listenerArray = listeners.toArray(new MediaFileListener[] {});
		if (listenerArray.length > 0) {
			for (MediaFileListener listener : listenerArray) {
				listener.notifyMediaFileRenamed(this);
			}
		}
	}

	public ImageInfo getPosterImage() {
		return posterImage;
	}

	public List<CandidateMediaDetail> getCandidates() {
		return candidateMediaDetails;
	}

	/**
	 * Confirm the candidate by changing the media file name and saving the
	 * proper NFO
	 * 
	 * @throws MediaFileException
	 */
	public void confirmCandidate(CandidateMediaDetail candidateMediaDefinition) throws MediaFileException {
		log.info("Confirming candidate: " + candidateMediaDefinition + " \nOn top of: " + this);

		boolean isInExclusiveFolder = isInExclusiveFolder();
		
		// get details
		this.title = candidateMediaDefinition.getTitle();
		this.year = candidateMediaDefinition.getYear();
		this.mediaType = candidateMediaDefinition.getMediaType();
		this.status = MediaStatus.MEDIA_DETAILS_FOUND;

		// move candidate image
		if (candidateMediaDefinition.getPosterImage() != null) {
			File candidateImage = candidateMediaDefinition.getPosterImage().getImageFile();
			File newPosterImageFile = new File(mediaFolder.getFile(), candidateImage.getName());

			try {
				FileUtils.copyFile(candidateImage, newPosterImageFile);
				this.posterImage = new ImageInfo(newPosterImageFile);
			} catch (IOException e) {
				log.severe("Error trying to copy candidate posterImage file: " + candidateImage.getPath() + " to: "
						+ newPosterImageFile.getPath());
				throw new MediaFileException("Error trying to copy candidate posterImage file: "
						+ candidateImage.getPath() + " to: " + newPosterImageFile.getPath());
			}
		}

		// Move the NFO
		AbstractFileNFO candidateNFO = (AbstractFileNFO) candidateMediaDefinition.getMediaNFO();

		String newFileName = buildNewFileName(getMainFile(), null);

		// Set the NFO file path to the new folder
		String nfoFileName = Util.getFileSimpleName(newFileName) + "." + NFO;
		File newNFOFile = new File(mediaFolder.getFile(), nfoFileName);

		try {
			File candidateNFOFile = candidateNFO.getFile();

			log.info("Copying file: " + candidateNFOFile.getPath() + " to: " + newNFOFile.getPath());

			FileUtils.copyFile(candidateNFOFile, newNFOFile);
			// lookUpNFO();
			mediaNFO = MediaNFOFactory.getInstance().loadFromFile(newNFOFile);
			mediaNFO.setMediaDefinition(this);
		} catch (IOException e) {
			log.warning("Error trying to copy candidate NFO file: " + candidateNFO.getFile().getPath() + " to: "
					+ newNFOFile.getPath());
			throw new MediaFileException("Error trying to copy candidate NFO file: " + candidateNFO.getFile().getPath()
					+ " to: " + newNFOFile.getPath());
		}

		clearCandidates();

		boolean renamed = false;
		// rename file
		if (isMultiPart()) {
			String[] fileNames = files.keySet().toArray(new String[]{});
			for (String fileKey : fileNames) {
				File file = files.get(fileKey);
				String[] parts = Util.getMultiPartSufix(file.getName());
				newFileName = buildNewFileName(file, parts[2]);
				// check wheather the file name will need to change
				if (!file.getName().equals(newFileName)) {
					renamed = renameTo(fileKey, newFileName);
				}
			}

		} 
		else {
			File file = getFile(MEDIA_FILE);
			newFileName = buildNewFileName(file, null);
			// check wheather the file name will need to change
			if (!file.getName().equals(newFileName)) {
				renamed = renameTo(MEDIA_FILE, newFileName);
			}
		}
		
		if (renamed) { //if the file names were changed
			fireNotifyMediaFileRenamed();

			if (isInExclusiveFolder) {
				// rename the folder to match the mediafilename
				File newFolder = new File(mediaFolder.getFile().getParentFile(), buildExclusiveFolderName());
				mediaFolder.renameTo(newFolder);
			}
		}

		saveNFO();

		notifyChange();
		
		if ( ! isInExclusiveFolder) {
			moveToExclusiveFolder();
		}
	}
	
	@Override
	protected boolean renameTo(String key, String newFileName) throws MediaFileException {
		//store the previous filename
		if(originalFileName == null){
			originalFileName = getMainFile().getName();
		}
		
		return super.renameTo(key, newFileName);
	}

	private String buildExclusiveFolderName() {
		return getFileName();
	}

	private void clearCandidates() throws MediaFileException {

		for (CandidateMediaDetail candidateMediaDefinition : candidateMediaDetails) {
			candidateMediaDefinition.discard();
		}

		candidateMediaDetails.clear();
		candidateCount = 0;

		String candidateFolderPrefix = Util.getCandidateFolderPrefix(getFileName());
		final String matchRegex = candidateFolderPrefix + "\\d{1,2}";
		// delete candidate folders
		File[] candidateFolders = mediaFolder.getFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().matches(matchRegex);
			}
		});
		for (File candidateFolder : candidateFolders) {
			boolean deleted = deleteAll(candidateFolder);
			if (!deleted) {
				log.warning("Could not delete candidate folder: " + candidateFolder.getPath() + " mediaFile: " + this);
			}
		}
	}

	private boolean deleteAll(File file) {
		if (!file.exists()) {
			return true;
		}

		File[] files = file.listFiles();
		if (file.isDirectory() && files.length > 0) {
			boolean result = true;
			for (File item : files) {
				boolean deleted = deleteAll(item);
				if (!deleted) {
					result = false;
				}
			}
			file.delete();
			return result;
		} else if (file.isDirectory() && files.length == 0) {
			return file.delete();
		} else if (file.isFile()) {
			return file.delete();
		}
		return false;
	}

	
	/**
	 * Returns the File name without extension
	 * @return
	 */
	public String getFileName() {
		File file = getMainFile();
		String fileName = file.getName();
		if (isMultiPart()) {
			String[] parts = Util.getMultiPartSufix(fileName);
			fileName = parts[0];
			fileName = Util.normalizeFileName( fileName );
		}
		else{
			fileName = Util.getFileSimpleName(fileName);
		}
		return fileName;
	}

	public MediaFolder getMediaFolder() {
		return mediaFolder;
	}

	public synchronized void moveToExclusiveFolder() throws MediaFileException {
		File newFolder = new File(mediaFolder.getFile(), buildExclusiveFolderName());
		if(! newFolder.exists()){
			newFolder.mkdirs();
		}
		else{
			throw new MediaFileException("Folder " + newFolder.getPath() + " already exists. Cannot move the media files.");
		}
		
		//remove previous file listener from this folder
		mediaFolder.removeListener(this);
		
		//NFO
		MediaFolderChangeListener nfo = (AbstractFileNFO)getMediaNFO();
		if(nfo != null){
			mediaFolder.removeListener(nfo);
		}
		
		//Image
		MediaFolderChangeListener imageInfo = getPosterImage();
		if(imageInfo != null){
			mediaFolder.removeListener(imageInfo);
		}
		
		//Subtitle
		MediaFolderChangeListener subtitle = (MediaFolderChangeListener)getSubtitle();
		if(subtitle != null){
			mediaFolder.removeListener(subtitle);
		}
		
		//grab a reference to the new mediaFolder
		mediaFolder = MediaFolderContext.getInstance().getMediaFolder(newFolder.getPath());
		
		//move files to the new folder
		notifyNewMediaFolderCreated(mediaFolder);
		
		if(nfo != null){
			nfo.notifyNewMediaFolderCreated(mediaFolder);
		}
		
		if(imageInfo != null){
			imageInfo.notifyNewMediaFolderCreated(mediaFolder);
		}
		
		if(subtitle != null){
			subtitle.notifyNewMediaFolderCreated(mediaFolder);
		}
		
		saveNFO();//make sure update the poster file path
	}

	public boolean isMultiPart() {
		return multiPart;
	}

	public boolean hasSubtitles() {
		return subtitle != null;
	}

	public ISubtitle getSubtitle() {
		return subtitle;
	}

	@Override
	public int hashCode() {
		return getFileName().hashCode() + this.mediaFolder.hashCode();
	}

	public IMediaNFO getMediaNFO() {
		return mediaNFO;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
		notifyChange();
	}

	@Override
	public void setStatus(MediaStatus mediaDetailStatus) {
		this.status = mediaDetailStatus;
		notifyChange();
	}

	@Override
	public MediaStatus getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		notifyChange();
	}
	
	public Integer getYear() {
		if(getMediaNFO() != null){
			this.year = null;
			return getMediaNFO().getYear();
		}
		return year;
	}
	public void setYear(Integer year) {
		if(getMediaNFO() != null){
			getMediaNFO().setYear(year);
			this.year = null;
		}
		else{
			this.year = year;
		}
		notifyChange();
	}

	public void setCandidateUrls(List<SearchResult> candidateURLs) {
		this.candidateURLs = candidateURLs;
	}

	@Override
	public void addCandidate(CandidateMediaDetail candidateMediaDefinition) {
		candidateMediaDetails.add(candidateMediaDefinition);
		setStatus(MediaStatus.CANDIDATE_DETAILS_FOUND);
	}

	@Override
	public void setBlogPostURL(String blogPostURL) {
		this.blogPostURL = blogPostURL;
	}

	protected StringBuffer buildToStringDescription() {
		StringBuffer strDescription = new StringBuffer("[MediaDefinition ");

		strDescription.append(" Title: ").append(title);
		
		strDescription.append(" Year: ").append(year);

		strDescription.append(" Status: ").append(status);

		if (mediaNFO != null) {
			strDescription.append(" Media NFO: ").append(mediaNFO);
		} else {
			strDescription.append(" Media NFO: null");
		}

		strDescription.append(" BlogPostURL: ").append(blogPostURL);

		return strDescription;
	}

	protected void notifyChange() {
		if (listeners.size() > 0) {
			for (MediaFileListener listener : listeners) {
				listener.notifyChange(this);
			}
		}
	}

	public void addListener(MediaFileListener listener) {
		if( ! listeners.contains(listener)){
			listeners.add(listener);
		}
	}

	public void removeListener(MediaFileListener listener) {
		listeners.remove(listener);
	}

	protected void copy(MediaDetail from) {
		this.title 			= from.title;
		this.year			= from.year;
		this.files 			= new HashMap<String, File>(from.files);
		this.multiPart 		= from.multiPart;
		this.multiPartCount = from.multiPartCount;
	}
	
	@Override
	public String toString() {
		StringBuffer strDescription = buildToStringDescription();

		strDescription.append("\nFiles: [").append(files).append("]");

		// finish the string description construction
		strDescription.append("]");

		return strDescription.toString();
	}
	
	public void release() {
		log.info("*** Releasing MediaDetails: " + this);
		super.release();
		listeners.clear();
	}

	public String getBlogPostURL() {
		return blogPostURL;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}
}
