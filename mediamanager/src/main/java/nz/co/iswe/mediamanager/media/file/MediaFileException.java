package nz.co.iswe.mediamanager.media.file;


@SuppressWarnings("serial")
public class MediaFileException extends Exception {

	public MediaFileException(String message) {
		super(message);
	}

	public MediaFileException(String message, Exception e) {
		super(message, e);
	}

}
