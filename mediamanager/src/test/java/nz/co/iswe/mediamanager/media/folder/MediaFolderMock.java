package nz.co.iswe.mediamanager.media.folder;

import java.io.File;

import nz.co.iswe.mediamanager.media.file.MediaFileException;

public class MediaFolderMock extends MediaFolder {

	public MediaFolderMock() throws MediaFileException {
		super(null, null);
	}
	
	@Override
	public File getFile() throws MediaFileException {
		return null;
	}

}
