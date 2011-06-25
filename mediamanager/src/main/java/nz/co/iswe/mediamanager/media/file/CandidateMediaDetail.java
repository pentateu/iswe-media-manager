package nz.co.iswe.mediamanager.media.file;

import java.io.File;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.MediaFileListener;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;
import nz.co.iswe.mediamanager.media.folder.MediaFolderContext;
import nz.co.iswe.mediamanager.media.folder.NestedMediaFolder;
import nz.co.iswe.mediamanager.media.nfo.IMediaNFO;


public class CandidateMediaDetail extends MediaDetail implements MediaFileListener {
	
	private MediaDetail parentMediaDetail;
	
	private int candidateId = 0;
	
	public CandidateMediaDetail(IMediaDetail parentMediaDetail) throws MediaFileException {
		super(buildCandidateMediaFolder((MediaDetail)parentMediaDetail));
		if( ! (parentMediaDetail instanceof MediaDetail)){
			throw new MediaFileException("Candidate media definition can only be created for a MediaFileDefinition parent!");
		}
		this.parentMediaDetail = (MediaDetail)parentMediaDetail;
		this.candidateId = this.parentMediaDetail.candidateCount++;
		this.parentMediaDetail.addListener(this);
		copy(this.parentMediaDetail);
	}

	private static MediaFolder buildCandidateMediaFolder(MediaDetail parentMediaDefinition) throws MediaFileException {
		int candidateId = parentMediaDefinition.candidateCount;
		String candidateFolderPrefix = Util.getCandidateFolderPrefix(parentMediaDefinition.getFileName());
		String folderName = candidateFolderPrefix + candidateId;
		File newCandidateFolder = new File(parentMediaDefinition.getMediaFolder().getFile(), folderName);
		if( ! newCandidateFolder.exists()){
			newCandidateFolder.mkdirs();
		}
		return MediaFolderContext.getInstance().getNestedMediaFolder(parentMediaDefinition.getMediaFolder(), folderName);
	}

	public void discard(){
		//remove the listeners
		parentMediaDetail.removeListener(this);
		mediaFolder.removeListener(this);
		((NestedMediaFolder)mediaFolder).discard();
	}
	
	@Override
	public void notifyMediaFileRenamed(MediaDetail mediaFile) throws MediaFileException {
		copy(parentMediaDetail);
		
		String candidateFolderPrefix = Util.getCandidateFolderPrefix(parentMediaDetail.getFileName());
		File newCandidateFolder = new File(parentMediaDetail.getMediaFolder().getFile(), candidateFolderPrefix + candidateId);
		
		if( ! newCandidateFolder.getPath().equals(mediaFolder.getFile().getPath())){
			mediaFolder.moveTo(newCandidateFolder);
		}
	}
	
	@Override
	public void notifyChange(IMediaDetail mediaDefinition) {
		//ignore
	}
	
	public CandidateMediaDetail(MediaDetail mediaFileDefinition, IMediaNFO mediaNFO) throws MediaFileException {
		this(mediaFileDefinition);
		this.mediaNFO = mediaNFO;
		
		if (mediaNFO != null) {
			this.mediaType = mediaNFO.getMediaType();

			this.title = mediaNFO.getTitle();
			
			// Load image
			if (mediaNFO.getThumb() != null) {
				try{
					posterImage = new ImageInfo(new File(mediaNFO.getThumb()));
				}
				catch(MediaFileException e){
					//try find the image localy
					tryLoadPosterImage();
					if(posterImage != null){
						mediaNFO.setThumb(posterImage.getImageFile().getPath());
					}
				}
			}
			
			this.status = MediaStatus.MEDIA_DETAILS_FOUND;
		}
	}
	

	@Override
	public void save() throws MediaFileException {
		//1 : save the parent to make sure file location and folder exists
		parentMediaDetail.save();
		
		if (posterImage != null) {
			// 3 save the poster image
			posterImage.saveAs(mediaFolder, "cover");
		}
		
		//3 save the NFO
		saveNFO();
	}
	
	@Override
	protected void saveNFO() throws MediaFileException {

		ensureNFOExists();

		if (mediaNFO != null) {
			// Populate the movie info
			mediaNFO.setTitle(this.getTitle());

			if (posterImage != null && posterImage.getImageFile() != null) {
				// Poster
				mediaNFO.setThumb(posterImage.getImageFile().getPath());
			}

			File file = parentMediaDetail.getMainFile();
			mediaNFO.setFilenameAndPath(file.getPath());
			
			mediaNFO.save();
		}

	}
	
	public String createNFOName() {
		return getFileName() + "." + NFO;
	}

	
	@Override
	public void notifyMediaFolderMoved(MediaFolder mediaFolder) throws MediaFileException {
		//do nothing
	}

	/**
	 * Confirm the candidate by changing the media file name and saving the proper
	 * NFO
	 * @throws MediaFileException 
	 */
	public void confirmCandidate() throws MediaFileException {
		//rename media file
		parentMediaDetail.confirmCandidate(this);
	}

	public MediaDetail getParentMediaDefinition() {
		return parentMediaDetail;
	}

	public void setMediaNFO(IMediaNFO newMediaNFO) {
		this.mediaNFO = newMediaNFO;
	}

}
