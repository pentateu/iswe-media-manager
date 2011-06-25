package nz.co.iswe.mediamanager.media.folder;

import nz.co.iswe.mediamanager.media.file.MediaFileException;

public interface MediaFolderChangeListener {

	void notifyMediaFolderRenamed(MediaFolder mediaFolder) throws MediaFileException;

	void notifyMediaFolderMoved(MediaFolder mediaFolder) throws MediaFileException;

	void notifyNewMediaFolderCreated(MediaFolder mediaFolder) throws MediaFileException;

}
