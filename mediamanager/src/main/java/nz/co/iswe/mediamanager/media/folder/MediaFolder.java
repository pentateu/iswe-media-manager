package nz.co.iswe.mediamanager.media.folder;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;

public class MediaFolder {

	protected MediaFolderContext mediaFolderContext = null;
	protected String folderPath = null;
	protected List<MediaFolderChangeListener> mediaFolderChangeListeners = new ArrayList<MediaFolderChangeListener>();
	
	MediaFolder(MediaFolderContext mediaFolderContext, String folderPath) throws MediaFileException {
		this.mediaFolderContext = mediaFolderContext;
		this.folderPath = folderPath;
		//validate folder path
		getFile();
	}
	
	public void moveTo(File newFolder) throws MediaFileException {
		boolean moved = getFile().renameTo(newFolder);
		if(moved){
			setNewFolder(newFolder);
			
			//notify the listeners
			MediaFolderChangeListener [] listenerArray = mediaFolderChangeListeners.toArray(new MediaFolderChangeListener[]{});
			for(MediaFolderChangeListener listener : listenerArray){
				listener.notifyMediaFolderMoved(this);
			}
		}
		else{
			throw new MediaFileException("Cannot move to folder" + newFolder.getPath());
		}
		
		
	}
	
	public void createNewExclusiveFolder(File newFolder) throws MediaFileException {
		if(! newFolder.exists()){
			newFolder.mkdirs();
		}
		else{
			throw new MediaFileException("Folder " + newFolder.getPath() + " already exists. Cannot move the media files.");
		}
		
		setNewFolder(newFolder);
		
		//notify the listeners
		MediaFolderChangeListener [] listenerArray = mediaFolderChangeListeners.toArray(new MediaFolderChangeListener[]{});
		for(MediaFolderChangeListener listener : listenerArray){
			listener.notifyNewMediaFolderCreated(this);
		}
	}
	
	protected void setNewFolder(File newFolder) throws MediaFileException {
		//validate the new folder
		validateFolder(newFolder);
		
		//remove the old reference form the pool
		mediaFolderContext.replace(folderPath, newFolder.getPath());
		
		//store the new folder path
		folderPath = newFolder.getPath();
	}

	public synchronized void notifyMediaFolderRenamed(File newFolder) throws MediaFileException {
		setNewFolder(newFolder);
		
		//notify the listeners
		MediaFolderChangeListener [] listenerArray = mediaFolderChangeListeners.toArray(new MediaFolderChangeListener[]{});
		for(MediaFolderChangeListener listener : listenerArray){
			listener.notifyMediaFolderRenamed(this);
		}
	}
	
	public void renameTo(File newFolder) throws MediaFileException {
		boolean renamed = false;
		try{
			File oldFolder = getFile();
			renamed = oldFolder.renameTo(newFolder);
		}
		finally{
			if(renamed){
				notifyMediaFolderRenamed(newFolder);
			}
		}
	}
	
	public boolean isExclusive(MediaDetail mediaFileDefinition) throws MediaFileException {
		
		// Use the scoring technique to determine if the
		// folder is specific for this media.. or if it is a Generic media
		// folder
		// with other media files
		double score = scoreFolder_01(mediaFileDefinition);
		
		if (score >= 50) {// close enought ;-)
			//mediaFolder = parentFolder;
			return true;
		}
		return false;
	}

	private double scoreFolder_01(MediaDetail mediaDetail) throws MediaFileException {
		
		File folder = getFile();
		
		//scoring using the filename and folder name
		double score = Util.compareAndScore(folder.getName(), mediaDetail.getFileName());
		
		if (score >= 50) {// close enought ;-)
			return score;
		}

		//Scoring based on the number of files inside the folder
		// get the number of files inside the parent folder that are also media
		// files
		File[] mediaFiles = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				try {
					return pathname.isFile() && 
							Util.isMediaValidFile(pathname) &&
							! Util.isSampleFile(pathname);
				} catch (MediaFileException e) {
					return false;
				}
			}
		});

		File[] subFolders = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		if (mediaFiles.length == 1) {
			score = score * 1.5;// add 50% to the score becaus only one media
								// file is found
		}

		if (subFolders.length == 0) {
			score = score * 1.5;// add 50% to the score because there are no
								// subfolders
		}

		return score;
	}



	public File getFile() throws MediaFileException {
		File folderFile = new File(folderPath);
		validateFolder(folderFile);
		return folderFile;
	}



	private void validateFolder(File folder) throws MediaFileException {
		if( ! folder.exists()){
			throw new MediaFileException("Media Folder does not exists: " + folder.getPath());
		}
		if( ! folder.isDirectory()){
			throw new MediaFileException("Media Folder is not a Directory: " + folder.getPath());
		}
	}



	public synchronized void addListener(MediaFolderChangeListener listener) {
		mediaFolderChangeListeners.add(listener);
	}


	public synchronized void removeListener(MediaFolderChangeListener listener) {
		mediaFolderChangeListeners.remove(listener);
	}

	@Override
	public int hashCode() {
		return this.folderPath.hashCode();
	}
	
	/*
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(MediaFolder.class.isAssignableFrom( obj.getClass() )){
			MediaFolder other = (MediaFolder)obj;		
			return other.folderPath.equals(this.folderPath);
		}
		else{
			return false;
		}
	}
	*/

}
