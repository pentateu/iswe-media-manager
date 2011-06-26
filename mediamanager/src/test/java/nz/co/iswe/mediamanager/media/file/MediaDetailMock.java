package nz.co.iswe.mediamanager.media.file;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.folder.MediaFolderMock;

public class MediaDetailMock extends MediaDetail {

	public MediaDetailMock( ) throws MediaFileException {
		super(new MediaFolderMock());
	}

}
