package nz.co.iswe.mediamanager.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;
import nz.co.iswe.mediamanager.media.folder.MediaFolderChangeListener;
import nz.co.iswe.mediamanager.media.folder.MediaFolderContext;

public class ImageInfo implements MediaFolderChangeListener {
	
	private static Logger log = Logger.getLogger(ImageInfo.class
			.getName());
	
	
	private MediaFolder mediaFolder;
	private BufferedImage bufferedImage;
	private String imageType;
	private File file;
	private String imageURL;
	
	private ImageInfoStatus imageStatus;
	
	private ImageInfo(){
		
	}
	
	public ImageInfo(File file) throws MediaFileException {
		//verifiy if it is a valida image file
		validateImageFile(file);
		
		this.mediaFolder = MediaFolderContext.getInstance().getMediaFolder(file.getParent());
		mediaFolder.addListener(this);
		
		this.file = file;
		this.imageType = Util.getFileExtension(file.getName());
		
		imageStatus = ImageInfoStatus.IN_FILE;
	}
	
	@Override
	public void notifyMediaFolderMoved(MediaFolder mediaFolder) throws MediaFileException {
		notifyMediaFolderRenamed(mediaFolder);
	}
	
	public void notifyNewMediaFolderCreated(MediaFolder mediaFolder) throws MediaFileException {
		this.mediaFolder = mediaFolder;
		if(file != null){
			//move file to the new folder
			File newFile = new File(mediaFolder.getFile(), file.getName());
			boolean moved = file.renameTo(newFile);
			if(moved){
				validateImageFile(newFile);
				file = newFile;
			}
			else{
				throw new MediaFileException("Error trying to move the NFO file to the new folder! " + newFile.getPath());
			}
		}
	}
	
	@Override
	public void notifyMediaFolderRenamed(MediaFolder mediaFolder) throws MediaFileException {
		this.mediaFolder = mediaFolder;
		if(file != null){
			//get the reference to the file in the new folder
			File newFile = new File(mediaFolder.getFile(), file.getName());
			if(file.getPath().equals(newFile.getPath())){
				//nothing changed
			}
			else if( ! file.exists() &&  newFile.exists() ){
				validateImageFile(newFile);
				file = newFile;
			}
			else{
				throw new MediaFileException("Error trying to get the reference to the image file in the new media folder! " + newFile.getPath());
			}
		}
	}

	private void validateImageFile(File file) throws MediaFileException {
		if (!file.exists()) {
			throw new MediaFileException("Image File does not exist in the file system: " + file.getPath());
		}
		if (!file.isFile()) {
			throw new MediaFileException("Image File is not a valid file: " + file.getPath());
		}
		if (!Util.isValidImageFile(file)){
			throw new MediaFileException("Image File is not a valid file: " + file.getPath());
		}
	}

	public static ImageInfo createImageWithURL(String imageURL){
		ImageInfo imageInfo = new ImageInfo();
		
		imageInfo.imageURL = imageURL;
		imageInfo.imageType = Util.getExtensionFromImageURL(imageURL);
		
		
		imageInfo.imageStatus = ImageInfoStatus.REFERENCE_ONLY;
		
		return imageInfo;
	}

	public void saveAs(MediaFolder mediaFolder, String fileName) throws MediaFileException {
		if (bufferedImage != null) {

			try {
				File newImageFile = new File(mediaFolder.getFile(), fileName + "." + imageType);

				ImageIO.write(bufferedImage, imageType, newImageFile);

				this.file = newImageFile;
				this.mediaFolder = mediaFolder;
				mediaFolder.addListener(this);
				
				imageStatus = ImageInfoStatus.IN_FILE;
				
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not save the Poster Image!", e);
				throw new MediaFileException("Could not save the Poster Image!", e);
			}
		}
	}

	public File getImageFile() throws MediaFileException {
		if(file == null){
			return null;
		}
		validateImageFile(file);
		return file;
	}

	public void setImageFile(File imageFile) {
		this.file = imageFile;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		if(bufferedImage != null){
			imageStatus = ImageInfoStatus.IN_MEMORY;
		}
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public boolean moveTo(File newFile) throws MediaFileException {
		boolean moved = false;
		try{
			moved = file.renameTo(newFile);
		}
		finally{
			if(moved){
				//remove the listener from the older mediaFolder
				mediaFolder.removeListener(this);
				
				this.file = newFile;
				
				//get a reference for the new media folder
				this.mediaFolder = MediaFolderContext.getInstance().getMediaFolder(newFile.getParent());
				mediaFolder.addListener(this);
			}
		}
		return moved;
	}
	
	public ImageInfoStatus getImageStatus() {
		return imageStatus;
	}
}
