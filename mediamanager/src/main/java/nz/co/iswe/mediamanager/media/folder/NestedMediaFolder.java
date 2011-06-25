package nz.co.iswe.mediamanager.media.folder;

import java.io.File;

import nz.co.iswe.mediamanager.media.file.MediaFileException;

public class NestedMediaFolder extends MediaFolder implements MediaFolderChangeListener {

	protected MediaFolder parentMediaFolder;
	
	NestedMediaFolder(MediaFolderContext mediaFolderContext, MediaFolder parentMediaFolder, String folderPath) throws MediaFileException {
		super(mediaFolderContext, folderPath);
		this.parentMediaFolder = parentMediaFolder;
		this.parentMediaFolder.addListener(this);
	}

	@Override
	public void notifyNewMediaFolderCreated(MediaFolder parentMediaFolder) throws MediaFileException {
		File oldFolder = new File(folderPath);
		//move the candidate media folder
		File newFolder = new File(parentMediaFolder.getFile(), oldFolder.getName());
		moveTo(newFolder);
	}
	
	@Override
	public void notifyMediaFolderRenamed(MediaFolder parentMediaFolder) throws MediaFileException {
		File oldFolder = new File(folderPath);
		//update the reference since the parent media folder renamed
		File newFolder = new File(parentMediaFolder.getFile(), oldFolder.getName());
		setNewFolder(newFolder);
	}
	
	@Override
	public void notifyMediaFolderMoved(MediaFolder parentMediaFolder) throws MediaFileException {
		notifyMediaFolderRenamed(parentMediaFolder);
	}

	public void discard() {
		parentMediaFolder.removeListener(this);
	}

}
