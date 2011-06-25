package nz.co.iswe.mediamanager.media.folder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import nz.co.iswe.mediamanager.media.file.MediaFileException;

public class MediaFolderContext {

	private static MediaFolderContext instance = null;
	
	public static MediaFolderContext getInstance(){
		if(instance == null){
			instance = new MediaFolderContext();
		}
		return instance;
	}
	
	// ### Instance Members ### //
	private Map<String, MediaFolder> mediaFolderPool = new HashMap<String, MediaFolder>();
	//private Map<String, NestedMediaFolder> nestedMediaFolderPool = new HashMap<String, NestedMediaFolder>();
	
	private MediaFolderContext(){}

	public MediaFolder getMediaFolder(String folderPath) throws MediaFileException {
		MediaFolder mediaFolder = mediaFolderPool.get(folderPath);
		if(mediaFolder == null){
			mediaFolder = new MediaFolder(this, folderPath);
			mediaFolderPool.put(folderPath, mediaFolder);
		}
		
		return mediaFolder;
	}

	public void replace(String oldFolderPath, String newFolderPath) {
		MediaFolder mediaFolder = mediaFolderPool.remove(oldFolderPath);
		if(mediaFolder != null){
			mediaFolderPool.put(newFolderPath, mediaFolder);
		}
	}

	public static void clearInstance() {
		instance = null;
	}

	public MediaFolder getNestedMediaFolder(MediaFolder parentMediaFolder, String folderName) throws MediaFileException {
		String folderPath = parentMediaFolder.getFile().getPath() + File.separator + folderName;
		NestedMediaFolder nestedMediaFolder = (NestedMediaFolder)mediaFolderPool.get(folderPath);
		if(nestedMediaFolder == null){
			nestedMediaFolder = new NestedMediaFolder(this, parentMediaFolder, folderPath);
			mediaFolderPool.put(folderPath, nestedMediaFolder);
			
		}
		return nestedMediaFolder;
	}
}
