package nz.co.iswe.mediamanager.media.subtitles;

import java.io.File;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.file.AbstractMediaFileChangeAware;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;

public class SubIdxSubtitles extends AbstractMediaFileChangeAware implements ISubtitle {

	private static final String IDX = "idx";
	private static final String SUB = "sub";

	public SubIdxSubtitles(MediaFolder mediaFolder) {
		super(mediaFolder);
	}

	@Override
	protected void validateFileImpl(File file) throws MediaFileException {
		if (!Util.isValidSubtitleFile(file)) {
			throw new MediaFileException("Subtitle File is not a .nfo file. File Path: " + file.getPath());
		}
	}

	public void setSubFile(File item) {
		setFile(SUB, item);
	}

	public File getSubFile() {
		return getFile(SUB);
	}

	public void setIdxFile(File item) {
		setFile(IDX, item);
	}

	public File getIdxFile() {
		return getFile(IDX);
	}

	@Override
	protected String buildNewFileName(String key, MediaDetail mediaFile) {
		return mediaFile.getFileName() + "." + key;
	}

}
