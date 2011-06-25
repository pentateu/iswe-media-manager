package nz.co.iswe.mediamanager.media;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;

public interface MediaFileListener {

	void notifyChange(IMediaDetail mediaDefinition);

	void notifyMediaFileRenamed(MediaDetail mediaFile) throws MediaFileException;
}
