package nz.co.iswe.mediamanager.media.file;

import java.io.File;

import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.MediaFileListener;
import nz.co.iswe.mediamanager.media.folder.AbstractMediaFolderChangeAware;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;

/**
 * Abstract class that represents a colloection of files that aware (listining to)
 * of changes in a MediaFile and are renamed accordinly to the MediaFile.
 * 
 * @author Rafael Almeida
 *
 */
public abstract class AbstractMediaFileChangeAware extends AbstractMediaFolderChangeAware implements MediaFileListener {

	public AbstractMediaFileChangeAware(MediaFolder mediaFolder) {
		super(mediaFolder);
	}

	
	@Override
	public void notifyChange(IMediaDetail mediaDefinition) {
		// ignore
	}
	
	@Override
	public void notifyMediaFileRenamed(MediaDetail mediaFile) throws MediaFileException {
		String[] keys = files.keySet().toArray(new String[]{});
		for(String key : keys){
			File file = files.get(key);
			if(file != null){
				String newFileName = buildNewFileName(key, mediaFile);
				File newFile = new File(mediaFolder.getFile(), newFileName);
				moveTo(key, newFile);
			}
		}
	}
	
}
