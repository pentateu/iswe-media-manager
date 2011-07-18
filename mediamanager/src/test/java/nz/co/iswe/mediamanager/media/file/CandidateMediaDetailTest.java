package nz.co.iswe.mediamanager.media.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.ImageInfoStatus;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.folder.MediaFolderContext;
import nz.co.iswe.mediamanager.media.nfo.DocumentaryFileNFO;
import nz.co.iswe.mediamanager.media.nfo.MovieFileNFO;
import nz.co.iswe.mediamanager.media.nfo.TVShowFileNFO;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.testutility.TestSuitConfig;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case
 * 
 * 1: Emulate the creation of Candidates - OK
 * 2: Emulate confirming a candidade - OK
 * 3: Test lookup candidates - OK
 * 4: Saving a cover image for a candidate - OK 
 */
public class CandidateMediaDetailTest  {

	private static File testMediaFolder = null;
	
	private static BufferedImage bufferedImage;
	
	@BeforeClass
	public static void setup() throws IOException{
		
		TestSuitConfig.createNewJUnitMediaFolder();
		
		testMediaFolder = TestSuitConfig.getJUnitMediaFolder();
		
		bufferedImage = ImageIO.read(new File(testMediaFolder, "images/image.jpg" ));
		
		MediaFileContext.clearInstance();
		MediaFolderContext.clearInstance();
	}

	@AfterClass
	public static void cleanup(){
		TestSuitConfig.deleteJUnitMediaFolder();
		MediaFileContext.clearInstance();
		MediaFolderContext.clearInstance();
	}	
	
	@Test
	public void testCreateMediaFileDefinition() throws MediaFileException{
		
		// ######################################################## //
		// ################          Setup         ################ //
		//create a valid MediaFileDefinition
		File mediaFile = new File(testMediaFolder, "1-3-3-8.com_twz-drive.angry.720p.mkv");
		MediaDetail mediaFileDefinition = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		
		// ======================================================== //
		// =================        Assert        ================= //
		
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.NO_MEDIA_DETAILS, mediaFileDefinition.getStatus() );
		
		Assert.assertNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertNull("Media Type", mediaFileDefinition.getMediaType() );
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
		
		File nfoFile = new File(testMediaFolder, "1-3-3-8.com_twz-drive.angry.720p.nfo");
		Assert.assertFalse("NFO does NOT exists", nfoFile.exists());
		
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image does NOT exists", imageFile.exists());
		// ====================================================================================== //
	}
	
	@Test
	public void testCreateSimpleCandidate() throws MediaFileException{
		// ######################################################## //
		// ################          Setup         ################ //
		File mediaFile = new File(testMediaFolder, "1-3-3-8.com_twz-drive.angry.720p.mkv");
		MediaDetail mediaFileDefinition = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		//create a candidate
		CandidateMediaDetail newCandidate = new CandidateMediaDetail(mediaFileDefinition);
		newCandidate.setMediaType(MediaType.MOVIE);
		newCandidate.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
		mediaFileDefinition.addCandidate(newCandidate);
		
		newCandidate.save();
		
		// ======================================================== //
		// =================        Assert        ================= //
		//media file
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 1, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.CANDIDATE_DETAILS_FOUND, mediaFileDefinition.getStatus() );
		
		Assert.assertNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertNull("Media Type", mediaFileDefinition.getMediaType() );
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
		
		//media files validation
		File nfoFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertFalse("NFO does NOT exists", nfoFile.exists());
		
		//media poster image
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image does NOT exists", imageFile.exists());
		
		//validate candidate
		CandidateMediaDetail candidate_0 = mediaFileDefinition.getCandidates().get(0);
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, candidate_0.getStatus() );
		Assert.assertNotNull("Media NFO", candidate_0.getMediaNFO() );
		Assert.assertNull("Media PosterImage", candidate_0.getPosterImage() );
		Assert.assertFalse("Media PosterImage", candidate_0.hasPoster());
		Assert.assertEquals("Media Type", MediaType.MOVIE, candidate_0.getMediaType() );
		
		//candidate NFO
		MovieFileNFO candidate_0_nfo = (MovieFileNFO)candidate_0.getMediaNFO();
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", candidate_0_nfo.getTitle() );
		Assert.assertNull("NFO Year", candidate_0_nfo.getYear());
		Assert.assertNull("NFO Thumb", candidate_0_nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.MOVIE, candidate_0_nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", candidate_0_nfo.getMovie().getFilenameandpath() );
		
		//candidate folder
		File candidate_0_folder = new File(testMediaFolder, "1-3-3-8_com_twz-drive_angry_720p_candidate_0");
		Assert.assertTrue("Candidate folder exists", candidate_0_folder.exists());
		Assert.assertTrue("Candidate folder is directory", candidate_0_folder.isDirectory());
		//candidate NFO
		File candidate_0_nfo_file = new File(candidate_0_folder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("Candidate NFO exists", candidate_0_nfo_file.exists());
		Assert.assertTrue("Candidate NFO is file", candidate_0_nfo_file.isFile());
		//candidate poster image
		File candidate_0_image = new File(candidate_0_folder, "cover.jpg");
		Assert.assertFalse("Candidate image does NOT exists", candidate_0_image.exists());
		// ====================================================================================== //
	}
	
	/**
	 * Simulates when the scraping of a candidate occurs and the details are saved
	 * @param mediaDetail 
	 * @throws MediaFileException
	 */
	@Test
	public void testLoadExistingCandidatesAndPopulateCandidateDetails() throws MediaFileException{
		// ######################################################## //
		// ################        Setup 01        ################ //
		File mediaFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.mkv");
		MediaDetail mediaFileDefinition = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//Media File
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 1, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.CANDIDATE_DETAILS_FOUND, mediaFileDefinition.getStatus() );
		
		Assert.assertNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertNull("Media Type", mediaFileDefinition.getMediaType() );
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
		
		//media files validation
		File nfoFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertFalse("NFO does NOT exists", nfoFile.exists());
		
		//media poster image
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image does NOT exists", imageFile.exists());
		// ======================================================== //
		
		
		// ######################################################## //
		// ################        Setup 02        ################ //
		//get the candidate reference
		CandidateMediaDetail candidate_0 = mediaFileDefinition.getCandidates().get(0);
		candidate_0.setTitle("Drive Angry");
		candidate_0.setMediaType(MediaType.MOVIE);
		ImageInfo imageInfo = ImageInfo.createImageWithURL("http://domain/image.jpg");
		candidate_0.setPosterImage(imageInfo);
		
		candidate_0.save();
		
		// ======================================================== //
		// =================      Assert 02       ================= //
		//validate candidate
		candidate_0 = mediaFileDefinition.getCandidates().get(0);
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, candidate_0.getStatus() );
		Assert.assertNotNull("Media NFO", candidate_0.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", candidate_0.getPosterImage() );
		Assert.assertTrue("Media PosterImage", candidate_0.hasPoster());
		Assert.assertEquals("Media PosterImage", ImageInfoStatus.REFERENCE_ONLY, candidate_0.getPosterImage().getImageStatus() );
		Assert.assertEquals("Media Type", MediaType.MOVIE, candidate_0.getMediaType() );
		
		//candidate NFO
		MovieFileNFO candidate_0_nfo = (MovieFileNFO)candidate_0.getMediaNFO();
		Assert.assertEquals("Title", "Drive Angry", candidate_0_nfo.getTitle() );
		Assert.assertNull("NFO Year", candidate_0_nfo.getYear());
		Assert.assertNull("NFO Thumb", candidate_0_nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.MOVIE, candidate_0_nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", candidate_0_nfo.getMovie().getFilenameandpath() );		
		
		//candidate folder
		File candidate_0_folder = new File(testMediaFolder, "1-3-3-8_com_twz-drive_angry_720p_candidate_0");
		Assert.assertTrue("Candidate folder exists", candidate_0_folder.exists());
		Assert.assertTrue("Candidate folder is directory", candidate_0_folder.isDirectory());
		//candidate NFO
		File candidate_0_nfo_file = new File(candidate_0_folder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("Candidate NFO exists", candidate_0_nfo_file.exists());
		Assert.assertTrue("Candidate NFO is file", candidate_0_nfo_file.isFile());
		//candidate poster image
		File candidate_0_image = new File(candidate_0_folder, "cover.jpg");
		Assert.assertFalse("Candidate image does NOT exists", candidate_0_image.exists());
		// ====================================================================================== //
		
		downloadCandidateImage(mediaFileDefinition, candidate_0);
	}

	private void downloadCandidateImage(MediaDetail mediaFileDefinition, CandidateMediaDetail candidate_0) throws MediaFileException {
		// ######################################################## //
		// ################        Setup 01        ################ //
		candidate_0.setYear(2011);
		
		ImageInfo imageInfo = candidate_0.getPosterImage();
		imageInfo.setBufferedImage(bufferedImage);
		
		candidate_0.getMediaNFO().setRating("0.1");
		candidate_0.getMediaNFO().setOutline("This movie is horrible...");
		
		candidate_0.save();
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//Media File
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 1, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.CANDIDATE_DETAILS_FOUND, mediaFileDefinition.getStatus() );
		
		Assert.assertNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertNull("Media Type", mediaFileDefinition.getMediaType() );
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
		
		//media files validation
		File nfoFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertFalse("NFO does NOT exists", nfoFile.exists());
		
		//media poster image
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image does NOT exists", imageFile.exists());
		
		//validate candidate
		candidate_0 = mediaFileDefinition.getCandidates().get(0);
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, candidate_0.getStatus() );
		Assert.assertNotNull("Media NFO", candidate_0.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", candidate_0.getPosterImage() );
		Assert.assertTrue("Media PosterImage", candidate_0.hasPoster());
		Assert.assertEquals("Media PosterImage", ImageInfoStatus.IN_FILE, candidate_0.getPosterImage().getImageStatus() );
		Assert.assertEquals("Media Type", MediaType.MOVIE, candidate_0.getMediaType() );
		
		//candidate NFO
		MovieFileNFO candidate_0_nfo = (MovieFileNFO)candidate_0.getMediaNFO();
		Assert.assertEquals("Title", "Drive Angry", candidate_0_nfo.getTitle() );
		Assert.assertEquals("NFO Year", new Integer(2011), candidate_0_nfo.getYear());
		Assert.assertEquals("NFO Thumb", testMediaFolder.getPath() + File.separator + "1-3-3-8_com_twz-drive_angry_720p_candidate_0" + File.separator + "cover.jpg", candidate_0_nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.MOVIE, candidate_0_nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", candidate_0_nfo.getMovie().getFilenameandpath() );		
		
		Assert.assertEquals("NFO Rating", "0.1", candidate_0_nfo.getRating());
		Assert.assertEquals("NFO Outline", "This movie is horrible...", candidate_0_nfo.getOutline());
		
		//candidate folder
		File candidate_0_folder = new File(testMediaFolder, "1-3-3-8_com_twz-drive_angry_720p_candidate_0");
		Assert.assertTrue("Candidate folder exists", candidate_0_folder.exists());
		Assert.assertTrue("Candidate folder is directory", candidate_0_folder.isDirectory());
		//candidate NFO
		File candidate_0_nfo_file = new File(candidate_0_folder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("Candidate NFO exists", candidate_0_nfo_file.exists());
		Assert.assertTrue("Candidate NFO is file", candidate_0_nfo_file.isFile());
		//candidate poster image
		File candidate_0_image = new File(candidate_0_folder, "cover.jpg");
		Assert.assertTrue("Candidate image does NOT exists", candidate_0_image.exists());
		Assert.assertTrue("Candidate image is file", candidate_0_image.isFile());
		// ====================================================================================== //
	}
	
	/**
	 * Simulates when the scraping of a candidate occurs and the details are saved
	 * @param mediaDetail 
	 * @throws MediaFileException
	 */
	@Test
	public void testLoadExistingCandidatesAndConfirmingToMediaFile() throws MediaFileException{
		// ######################################################## //
		// ################        Setup 01        ################ //
		File mediaFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.mkv");
		MediaDetail mediaFileDefinition = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		CandidateMediaDetail candidate_0 = mediaFileDefinition.getCandidates().get(0);
		
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//Media File
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 1, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.CANDIDATE_DETAILS_FOUND, mediaFileDefinition.getStatus() );
		
		Assert.assertNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertNull("Media Type", mediaFileDefinition.getMediaType() );
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
		
		//media files validation
		File nfoFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertFalse("NFO does NOT exists", nfoFile.exists());
		
		//media poster image
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image does NOT exists", imageFile.exists());
		
		//validate candidate
		candidate_0 = mediaFileDefinition.getCandidates().get(0);
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, candidate_0.getStatus() );
		Assert.assertNotNull("Media NFO", candidate_0.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", candidate_0.getPosterImage() );
		Assert.assertTrue("Media PosterImage", candidate_0.hasPoster());
		Assert.assertEquals("Media PosterImage", ImageInfoStatus.IN_FILE, candidate_0.getPosterImage().getImageStatus() );
		Assert.assertEquals("Media Type", MediaType.MOVIE, candidate_0.getMediaType() );
		
		//candidate NFO
		MovieFileNFO candidate_0_nfo = (MovieFileNFO)candidate_0.getMediaNFO();
		Assert.assertEquals("Title", "Drive Angry", candidate_0_nfo.getTitle() );
		Assert.assertEquals("NFO Year", new Integer(2011), candidate_0_nfo.getYear());
		Assert.assertEquals("NFO Thumb", testMediaFolder.getPath() + File.separator + "1-3-3-8_com_twz-drive_angry_720p_candidate_0" + File.separator + "cover.jpg", candidate_0_nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.MOVIE, candidate_0_nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", candidate_0_nfo.getMovie().getFilenameandpath() );		
		
		//candidate folder
		File candidate_0_folder = new File(testMediaFolder, "1-3-3-8_com_twz-drive_angry_720p_candidate_0");
		Assert.assertTrue("Candidate folder exists", candidate_0_folder.exists());
		Assert.assertTrue("Candidate folder is directory", candidate_0_folder.isDirectory());
		//candidate NFO
		File candidate_0_nfo_file = new File(candidate_0_folder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("Candidate NFO exists", candidate_0_nfo_file.exists());
		Assert.assertTrue("Candidate NFO is file", candidate_0_nfo_file.isFile());
		//candidate poster image
		File candidate_0_image = new File(candidate_0_folder, "cover.jpg");
		Assert.assertTrue("Candidate image does NOT exists", candidate_0_image.exists());
		Assert.assertTrue("Candidate image is file", candidate_0_image.isFile());
		// ====================================================================================== //
		
		
		createMoreCandidates(mediaFileDefinition);
		
		confirmCandidate(mediaFileDefinition);
	}

	private void createMoreCandidates(MediaDetail mediaFileDefinition) throws MediaFileException {
		// ######################################################## //
		// ################        Setup 01        ################ //
		//create more candidates
		CandidateMediaDetail candidate_1 = new CandidateMediaDetail(mediaFileDefinition);
		candidate_1.setMediaType(MediaType.DOCUMENTARY);
		candidate_1.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
		candidate_1.setTitle("Bio of Mike Tyson");
		mediaFileDefinition.addCandidate(candidate_1);
		
		candidate_1.save();
		candidate_1.setYear(1995);
		candidate_1.save();
		
		
		CandidateMediaDetail candidate_2 = new CandidateMediaDetail(mediaFileDefinition);
		candidate_2.setMediaType(MediaType.TV_SHOW);
		candidate_2.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
		candidate_2.setTitle("UFC 130");
		mediaFileDefinition.addCandidate(candidate_2);
		
		candidate_2.save();
		candidate_2.setYear(2010);
		candidate_2.save();
		
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//Media File
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 3, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.CANDIDATE_DETAILS_FOUND, mediaFileDefinition.getStatus() );
		
		Assert.assertNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertNull("Media Type", mediaFileDefinition.getMediaType() );
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
		
		//media files validation
		File nfoFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertFalse("NFO does NOT exists", nfoFile.exists());
		
		//media poster image
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image does NOT exists", imageFile.exists());
		
		//validate candidate 1
		candidate_1 = mediaFileDefinition.getCandidates().get(1);
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, candidate_1.getStatus() );
		Assert.assertNotNull("Media NFO", candidate_1.getMediaNFO() );
		Assert.assertNull("Media PosterImage", candidate_1.getPosterImage() );
		Assert.assertFalse("Media PosterImage", candidate_1.hasPoster());
		Assert.assertEquals("Media Type", MediaType.DOCUMENTARY, candidate_1.getMediaType() );
		
		//candidate NFO
		DocumentaryFileNFO candidate_1_nfo = (DocumentaryFileNFO)candidate_1.getMediaNFO();
		Assert.assertEquals("Title", "Bio of Mike Tyson", candidate_1_nfo.getTitle() );
		Assert.assertEquals("NFO Year", new Integer(1995), candidate_1_nfo.getYear());
		Assert.assertNull("NFO Thumb", candidate_1_nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.DOCUMENTARY, candidate_1_nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", candidate_1_nfo.getMovie().getFilenameandpath() );		
		
		//candidate folder
		File candidate_1_folder = new File(testMediaFolder, "1-3-3-8_com_twz-drive_angry_720p_candidate_1");
		Assert.assertTrue("Candidate folder exists", candidate_1_folder.exists());
		Assert.assertTrue("Candidate folder is directory", candidate_1_folder.isDirectory());
		//candidate NFO
		File candidate_1_nfo_file = new File(candidate_1_folder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("Candidate NFO exists", candidate_1_nfo_file.exists());
		Assert.assertTrue("Candidate NFO is file", candidate_1_nfo_file.isFile());
		//candidate poster image
		File candidate_1_image = new File(candidate_1_folder, "cover.jpg");
		Assert.assertFalse("Candidate image does NOT exists", candidate_1_image.exists());
		// ====================================================================================== //
		
		// ======================================================== //
		// =================      Assert 02       ================= //
		//validate candidate 2
		candidate_2 = mediaFileDefinition.getCandidates().get(2);
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, candidate_2.getStatus() );
		Assert.assertNotNull("Media NFO", candidate_2.getMediaNFO() );
		Assert.assertNull("Media PosterImage", candidate_2.getPosterImage() );
		Assert.assertFalse("Media PosterImage", candidate_2.hasPoster());
		Assert.assertEquals("Media Type", MediaType.TV_SHOW, candidate_2.getMediaType() );
		
		//candidate NFO
		TVShowFileNFO candidate_2_nfo = (TVShowFileNFO)candidate_2.getMediaNFO();
		Assert.assertEquals("Title", "UFC 130", candidate_2_nfo.getTitle() );
		Assert.assertEquals("NFO Year", new Integer(2010), candidate_2_nfo.getYear());
		Assert.assertNull("NFO Thumb", candidate_2_nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.TV_SHOW, candidate_2_nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", candidate_2_nfo.getMovie().getFilenameandpath() );		
		
		//candidate folder
		File candidate_2_folder = new File(testMediaFolder, "1-3-3-8_com_twz-drive_angry_720p_candidate_2");
		Assert.assertTrue("Candidate folder exists", candidate_2_folder.exists());
		Assert.assertTrue("Candidate folder is directory", candidate_2_folder.isDirectory());
		//candidate NFO
		File candidate_2_nfo_file = new File(candidate_2_folder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("Candidate NFO exists", candidate_2_nfo_file.exists());
		Assert.assertTrue("Candidate NFO is file", candidate_2_nfo_file.isFile());
		//candidate poster image
		File candidate_2_image = new File(candidate_2_folder, "cover.jpg");
		Assert.assertFalse("Candidate image does NOT exists", candidate_2_image.exists());
		// ====================================================================================== //
	}

	private void confirmCandidate(MediaDetail mediaDetail) throws MediaFileException {
		// ######################################################## //
		// ################        Setup 01        ################ //
		//Confirm the cadidate
		CandidateMediaDetail candidate_0 = mediaDetail.getCandidates().get(0);
		
		mediaDetail.confirmCandidate(candidate_0);
				
		// ======================================================== //
		// =================      Assert 01       ================= //
		//Media File
		Assert.assertEquals("Title", "Drive Angry", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, mediaDetail.getStatus() );
		Assert.assertNotNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertTrue("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertEquals("Media Type", MediaType.MOVIE, mediaDetail.getMediaType() );
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		//media files validation
		File mediaFile = new File(testMediaFolder, "Drive Angry (2011).mkv");
		Assert.assertFalse("MediaFile does exists", mediaFile.exists());
		
		testMediaFolder = new File(testMediaFolder, "Drive Angry (2011)");
		
		mediaFile = new File(testMediaFolder, "Drive Angry (2011).mkv");
		Assert.assertTrue("MediaFile does exists", mediaFile.exists());
		Assert.assertTrue("MediaFile is file", mediaFile.isFile());
		
		File nfoFile = new File(testMediaFolder, "Drive Angry (2011).nfo");
		Assert.assertTrue("NFO does exists", nfoFile.exists());
		Assert.assertTrue("NFO is file", nfoFile.isFile());
		
		//media poster image
		File imageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertTrue("Poster Image does exists", imageFile.exists());
		Assert.assertTrue("Poster Image is file", imageFile.isFile());
		
		//NFO
		MovieFileNFO nfo = (MovieFileNFO)mediaDetail.getMediaNFO();
		Assert.assertEquals("Title", "Drive Angry", nfo.getTitle() );
		Assert.assertEquals("NFO Year", new Integer(2011), nfo.getYear());
		Assert.assertEquals("NFO Thumb", testMediaFolder.getPath() + File.separator + "cover.jpg", nfo.getThumb());
		Assert.assertEquals("NFO Media Type", MediaType.MOVIE, nfo.getMediaType() );
		Assert.assertEquals("NFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "Drive Angry (2011).mkv", nfo.getFilenameAndPath());		
		
		Assert.assertEquals("NFO Rating", "0.1", nfo.getRating());
		Assert.assertEquals("NFO Outline", "This movie is horrible...", nfo.getOutline());
		
		Assert.assertEquals("Original File Name", "1-3-3-8.com_twz-drive.angry.720p.mkv", mediaDetail.getOriginalFileName());
		// ====================================================================================== //
	}
}
