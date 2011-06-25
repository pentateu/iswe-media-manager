package nz.co.iswe.mediamanager.media.nfo;

import java.io.File;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.file.AbstractMediaFileChangeAware;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.folder.MediaFolderContext;

public abstract class AbstractFileNFO extends AbstractMediaFileChangeAware  implements IMediaNFO {
	
	private static final String NFO = "nfo";
	//protected MediaFolder mediaFolder;
	protected MediaDetail mediaDetail;
	//protected File file;
	
	public AbstractFileNFO(MediaDetail mediaDetail) throws MediaFileException {
		super(mediaDetail.getMediaFolder());
		this.mediaDetail = mediaDetail;
	}
	
	@Override
	public void setMediaDefinition(IMediaDetail mediaDefinition) {
		this.mediaDetail = (MediaDetail)mediaDefinition;
	}
	
	public AbstractFileNFO(File file) throws MediaFileException {
		super(MediaFolderContext.getInstance().getMediaFolder(file.getParent()));
		validateFile(file);
		setFile(NFO, file);
	}

	@Override
	protected void validateFileImpl(File file) throws MediaFileException {
		String extension = Util.getFileExtension(file.getName());
		if( ! NFO.equalsIgnoreCase(extension)){
			throw new MediaFileException("NFO File is not a .nfo file. File Path: " + file.getPath());
		}
	}
	
	public File getFile() {
		return getFile(NFO);
	}

	public void setFile(File file) throws MediaFileException {
		validateFile(file);
		setFile(NFO, file);
	}

	@Override
	public void save() throws MediaFileException {
		
		//validate before saving
		validate();
		
		if(getFile() == null){
			String nfoFileName = buildNewFileName(null, mediaDetail);
			File newNFOFile = new File(mediaFolder.getFile(), nfoFileName);
			
			saveToXML(newNFOFile);
			
			setFile(NFO, newNFOFile);
		}
		else{
			saveToXML(getFile());
		}
	}
	
	@Override
	protected String buildNewFileName(String key, MediaDetail mediaFile) {
		return mediaFile.getFileName() + "." + NFO;
	}
	
	private void validate() throws MediaFileException {
		if(mediaFolder == null){
			throw new MediaFileException("mediaFolder is null!");
		}
		
		if(mediaDetail == null && getFile() == null){
			throw new MediaFileException("mediaFileDefinition and file is null. At least one of the two properties must be set!");
		}
	}

	protected abstract void saveToXML(File newNFOFile);

	
	public MediaDetail getMediaFileDefinition() {
		return mediaDetail;
	}
	
}