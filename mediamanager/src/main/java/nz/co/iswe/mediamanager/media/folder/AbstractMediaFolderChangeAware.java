package nz.co.iswe.mediamanager.media.folder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;

public abstract class AbstractMediaFolderChangeAware implements MediaFolderChangeListener {
	protected MediaFolder mediaFolder;
	
	protected Map<String, File> files = new HashMap<String, File>();
	
	public AbstractMediaFolderChangeAware(MediaFolder mediaFolder){
		this.mediaFolder = mediaFolder;
		this.mediaFolder.addListener(this);
	}

	protected void setFile(String key, File file) {
		files.remove(key);
		files.put(key, file);
	}
	
	protected File getFile(String key) {
		return files.get(key);
	}
	
	public File[] getFiles() {
		return files.values().toArray(new File[]{});
	}
	
	public void release() {
		this.mediaFolder.removeListener(this);
		this.mediaFolder = null;
		
		this.files.clear();
		this.files = null;
	}
	
	protected boolean renameTo(String key, String newFileName) throws MediaFileException {
		boolean renamed = false;
		File newFile = new File(mediaFolder.getFile(), newFileName);
		File file = getFile(key);
		if(file == null){
			throw new MediaFileException("Error trying to rename file. file null for key: " + key);
		}
		try{
			renamed = file.renameTo(newFile);
		}
		finally{
			if(renamed){
				setFile(key, newFile);
			}
			else{
				throw new MediaFileException("Error trying to rename file from: " + file.getPath() + " to: " + newFile.getPath());
			}
		}
		return renamed;
	}
	

	/**
	 * Delete the file (and any nested in case od a folder) from the file system
	 * 
	 * @param key
	 */
	protected boolean deleteFile(String key) {
		return deleteAll(files.get(key));
	}

	
	protected boolean deleteAll(File file) {
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
	
	protected boolean moveTo(String key, File newFile) throws MediaFileException {
		boolean moved = false;
		File file = getFile(key);
		if(file == null){
			return false;
		}
		try{
			moved = file.renameTo(newFile);
		}
		finally{
			if(moved){
				//remove the listener from the older mediaFolder
				mediaFolder.removeListener(this);
				
				setFile(key, newFile);
				
				//get a reference for the new media folder
				this.mediaFolder = MediaFolderContext.getInstance().getMediaFolder(newFile.getParent());
				mediaFolder.addListener(this);
			}
		}
		return moved;
	}
	
	public MediaFolder getMediaFolder() {
		return mediaFolder;
	}
	
	protected abstract String buildNewFileName(String key, MediaDetail mediaFile);

	@Override
	public void notifyMediaFolderRenamed(MediaFolder mediaFolder) throws MediaFileException {
		notifyMediaFolderMoved(mediaFolder);
	}

	@Override
	public void notifyMediaFolderMoved(MediaFolder mediaFolder) throws MediaFileException {
		this.mediaFolder = mediaFolder;
		for(String key : files.keySet()){
			File file = files.get(key);
			if(file != null){
				//move file to the new folder
				File newFile = new File(mediaFolder.getFile(), file.getName());
				if( ! file.exists() &&  newFile.exists() ){
					validateFile(newFile);
					files.put(key, newFile);
				}
				else if(file.getPath().equals( newFile.getPath()) ){
					validateFile(newFile);
					files.put(key, newFile);
				}
				else{
					throw new MediaFileException("Error trying get a reference of the '" + key + "' file from the new folder! " + newFile.getPath());
				}
			}
		}
	}

	@Override
	public void notifyNewMediaFolderCreated(MediaFolder mediaFolder) throws MediaFileException {
		this.mediaFolder = mediaFolder;
		
		for(String key : files.keySet()){
			File file = files.get(key);
			if(file != null){
				//move file to the new folder
				File newFile = new File(mediaFolder.getFile(), file.getName());
				boolean moved = file.renameTo(newFile);
				if(moved){
					validateFile(newFile);
					files.put(key, newFile);
				}
				else{
					throw new MediaFileException("Error trying to move/rename '" + key + "' file in the new folder! " + newFile.getPath());
				}
			}
		}
		mediaFolder.addListener(this);
	}

	protected void validateFile(File file) throws MediaFileException {
		if (!file.exists()) {
			throw new MediaFileException("File does not exist in the file system: " + file.getPath());
		}
		if (!file.isFile()) {
			throw new MediaFileException("File is not a valid file: " + file.getPath());
		}
		validateFileImpl(file);
	}

	protected abstract void validateFileImpl(File file) throws MediaFileException;
}
