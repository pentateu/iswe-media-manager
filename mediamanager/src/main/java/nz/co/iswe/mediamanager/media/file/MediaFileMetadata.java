package nz.co.iswe.mediamanager.media.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import nz.co.iswe.mediamanager.Util;


public class MediaFileMetadata {
	
	protected boolean multPart;
	protected boolean sample;
	protected boolean validMediaFile;
	
	protected List<MediaFileMetadata> multiPartList = new ArrayList<MediaFileMetadata>();
	
	protected File file;
	private Integer multiPartNumber;
	
	MediaFileMetadata(File file, boolean lookForSiblings) throws MediaFileException {
		this.file = file;
		
		validMediaFile = Util.isMediaValidFile(file);
		
		sample = Util.isSampleFile(file);
		
		if(lookForSiblings){
			lookUpMultiPartSiblings();
		}
		
		String[] multiPartSufix = Util.getMultiPartSufix(file.getName());
		if(multiPartSufix != null){
			String number = multiPartSufix[2];
			multiPartNumber = new Integer(number);
		}
	}
	
	

	/**
	 * Method that look for other mult-part sibling files within the same folder.
	 * 
	 * Example:  
	 * 22.Bullets.2010 - cd1.avi
	 * 22.Bullets.2010 - cd2.avi
	 * 
	 * @throws MediaFileException
	 */
	private void lookUpMultiPartSiblings() throws MediaFileException {
		//look for number sufix
		String[] multiPartSufix = Util.getMultiPartSufix(file.getName());
		if(multiPartSufix != null){
			final String filenameWithoutSufix =  multiPartSufix[0];
			final String sufix = multiPartSufix[1];
			final String number = multiPartSufix[2];
			final String ext = Util.getFileExtension(file.getName());
			
			//look for siblings mediaFiles that have the same multiPartSufix
			File[] siblingFiles = file.getParentFile().listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String[] siblingMultiPartSufix = Util.getMultiPartSufix(pathname.getName());
					if(siblingMultiPartSufix != null){
						String siblingFilenameWithoutSufix =  siblingMultiPartSufix[0];
						String siblingSufix = siblingMultiPartSufix[1];
						String siblingNumber = siblingMultiPartSufix[2];
						String siblingExt = Util.getFileExtension(pathname.getName());
						
						return 	( ! number.equals(siblingNumber)) && /*number must be diferent*/
								( sufix.equals(siblingSufix) ) && /*sufix must be the same*/
								( filenameWithoutSufix.equals(siblingFilenameWithoutSufix) ) && /*file name must be the same*/
								( ext.equals( siblingExt )) /*extension must be the same*/;
					}
					else{
						return false;
					}
				}
			});
			if(siblingFiles.length > 0){
				multPart = true;
				multiPartList.add(this);
				//get the media files for the sibling files found
				for(File siblingFile : siblingFiles){
					//addMultiPartSibling(number, siblingFile);
					multiPartList.add(new MediaFileMetadata(siblingFile, false));
				}
			}
		}
	}

	File getFile() {
		return file;
	}
	
	public boolean isMultPart() {
		return multPart;
	}
	
	public boolean isSample() {
		return sample;
	}
	
	public boolean isValidMediaFile() {
		return validMediaFile;
	}

	public List<MediaFileMetadata> getMultiPartList() {
		return multiPartList;
	}

	public Integer getMultiPartNumber() {
		return multiPartNumber;
	}
}
