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
import nz.co.iswe.mediamanager.media.nfo.MovieFileNFO;
import nz.co.iswe.mediamanager.media.subtitles.ISubtitle;
import nz.co.iswe.mediamanager.media.subtitles.SubIdxSubtitles;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.testutility.TestSuitConfig;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MediaDetailTest {

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
	public void testmediaDetailMediaFolderNotExclusive() throws MediaFileException{
		
		// ######################################################## //
		// ##### Setup 1 ##### //
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "1-3-3-8.com_twz-drive.angry.720p.mkv");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		
		// ##### Assert 01 ##### //
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.NO_MEDIA_DETAILS, mediaDetail.getStatus() );
		Assert.assertNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertNull("Media Type", mediaDetail.getMediaType() );
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		//assert subtitles
		Assert.assertTrue("Media has subtitles", mediaDetail.hasSubtitles() );
		ISubtitle subtitle = mediaDetail.getSubtitle();
		Assert.assertTrue("Media has sub/idx subtitles", subtitle instanceof SubIdxSubtitles );
		SubIdxSubtitles subIdxSubtitles = (SubIdxSubtitles)subtitle;
		File subFile = subIdxSubtitles.getSubFile();
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		Assert.assertEquals("Sub file Name", "1-3-3-8.com_twz-drive.angry.720p.sub", subFile.getName() );
		File idxFile = subIdxSubtitles.getIdxFile();
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		Assert.assertEquals("Idx file Name", "1-3-3-8.com_twz-drive.angry.720p.idx", idxFile.getName() );
		
		File nfoFile = new File(testMediaFolder, "1-3-3-8.com_twz-drive.angry.720p.nfo");
		Assert.assertFalse("NFO does not exists", nfoFile.exists());
		// ====================================================================================== //
		
		// ######################################################## //
		// ##### Setup 2 ##### //
		//Change the media details and save (to mimic the scrapers)
		mediaDetail.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
		mediaDetail.save();
		
		// ##### Assert 02 ##### //
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, mediaDetail.getStatus() );
		
		//title has not changed yet
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", mediaDetail.getTitle() );
		//NFO continues null, because not Media Type is being specified
		Assert.assertNull("Media NFO", mediaDetail.getMediaNFO() );
		
		nfoFile = new File(testMediaFolder, "1-3-3-8.com_twz-drive.angry.720p.nfo");
		Assert.assertFalse("NFO does not exists", nfoFile.exists());
		// ====================================================================================== //
		
		// ######################################################## //
		// ##### Setup 3 ##### //
		//set Media type
		mediaDetail.setMediaType(MediaType.MOVIE);
		mediaDetail.save();
		
		// ##### Assert 03 ##### //
		//a new NFO file has been created
		Assert.assertNotNull("Media NFO", mediaDetail.getMediaNFO() );
		MovieFileNFO movieFileNFO = (MovieFileNFO)mediaDetail.getMediaNFO();
		
		//assert NFO
		Assert.assertEquals("Title", "1-3-3-8.com_twz-drive.angry.720p", movieFileNFO.getTitle() );
		Assert.assertNull("MediaNFO Year", movieFileNFO.getYear() );
		Assert.assertEquals("MediaNFO MediaType", MediaType.MOVIE, movieFileNFO.getMediaType() );
		Assert.assertNull("MediaNFO Thumb", movieFileNFO.getThumb() );
		Assert.assertEquals("MediaNFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "1-3-3-8 com twz-drive angry 720p.mkv", movieFileNFO.getMovie().getFilenameandpath() );
		
		nfoFile = new File(testMediaFolder, "1-3-3-8 com twz-drive angry 720p.nfo");
		Assert.assertTrue("NFO does exists", nfoFile.exists());
		// ====================================================================================== //
		
		
		// ######################################################## //
		// ##### Setup 4 ##### //
		ImageInfo imageInfo = ImageInfo.createImageWithURL("http://domain/image.jpg");
		//set the media details title
		mediaDetail.setTitle("Drive Angry");
		mediaDetail.setPosterImage(imageInfo);
		mediaDetail.setYear(2011);
		movieFileNFO.setRating("0.1");
		movieFileNFO.setOutline("Short text description...");
		mediaDetail.save();
		
		// ##### Assert 04 ##### //
		//check if the file has being physicaly renamed
		Assert.assertFalse("Old Media file should not exist", mediaFile.exists());
		
		mediaFile = new File(testMediaFolder, "Drive Angry (2011).mkv");
		
		Assert.assertTrue("New Media file must exist", mediaFile.exists());
		
		//assert new media details
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, mediaDetail.getStatus() );
		
		//title has not changed yet
		Assert.assertEquals("Title", "Drive Angry", mediaDetail.getTitle() );
		
		//filename has changed
		Assert.assertEquals("Filename", "Drive Angry (2011)", mediaDetail.getFileName() );
		
		Assert.assertEquals("MediaNFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "Drive Angry (2011).mkv", movieFileNFO.getMovie().getFilenameandpath() );		
		
		//poster image is a reference only...
		Assert.assertNotNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertEquals("Media PosterImage", ImageInfoStatus.REFERENCE_ONLY, mediaDetail.getPosterImage().getImageStatus() );
		
		File posterImageFile = new File(testMediaFolder, "cover.jpg");
		Assert.assertFalse("Poster Image file should NOT exist", posterImageFile.exists());
		
		//a new NFO file has been created
		Assert.assertNotNull("Media NFO", mediaDetail.getMediaNFO() );
		//assert NFO
		Assert.assertEquals("MediaNFO Year", new Integer(2011), mediaDetail.getMediaNFO().getYear() );
		Assert.assertEquals("MediaNFO Title", "Drive Angry", mediaDetail.getMediaNFO().getTitle() );
		Assert.assertEquals("MediaNFO MediaType", MediaType.MOVIE, mediaDetail.getMediaNFO().getMediaType() );
		Assert.assertNull("MediaNFO Thumb", mediaDetail.getMediaNFO().getThumb() );
		
		//assert subtitles
		Assert.assertTrue("Media has subtitles", mediaDetail.hasSubtitles() );
		subtitle = mediaDetail.getSubtitle();
		Assert.assertTrue("Media has sub/idx subtitles", subtitle instanceof SubIdxSubtitles );
		subIdxSubtitles = (SubIdxSubtitles)subtitle;
		subFile = subIdxSubtitles.getSubFile();
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		Assert.assertEquals("Sub file Name", "Drive Angry (2011).sub", subFile.getName() );
		idxFile = subIdxSubtitles.getIdxFile();
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		Assert.assertEquals("Idx file Name", "Drive Angry (2011).idx", idxFile.getName() );
		
		
		nfoFile = new File(testMediaFolder, "Drive Angry (2011).nfo");
		Assert.assertTrue("NFO does exists", nfoFile.exists());
		// ====================================================================================== //
		
		
		// ######################################################## //
		// ##### Setup 5 ##### //
		//Move to a exclusive folder
		mediaDetail.moveToExclusiveFolder();
		
		// ##### Assert 05 ##### //
		Assert.assertEquals("Filename", "Drive Angry (2011)", mediaDetail.getFileName() );
		Assert.assertEquals("MediaNFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "Drive Angry (2011)" + File.separator + "Drive Angry (2011).mkv", movieFileNFO.getMovie().getFilenameandpath() );		
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		
		//check if the file has being physicaly renamed
		Assert.assertFalse("Old Media file should not exist", mediaFile.exists());
		
		File newMediaFolder = new File(testMediaFolder, "Drive Angry (2011)");
		mediaFile = new File(newMediaFolder, "Drive Angry (2011).mkv");
		Assert.assertTrue("New Media file must exist", mediaFile.exists());
		
		Assert.assertNull("MediaNFO Thumb", mediaDetail.getMediaNFO().getThumb() );
		
		nfoFile = new File(newMediaFolder, "Drive Angry (2011).nfo");
		Assert.assertTrue("NFO does exists", nfoFile.exists());
		
		
		//assert subtitles
		Assert.assertTrue("Media has subtitles", mediaDetail.hasSubtitles() );
		subtitle = mediaDetail.getSubtitle();
		Assert.assertTrue("Media has sub/idx subtitles", subtitle instanceof SubIdxSubtitles );
		subIdxSubtitles = (SubIdxSubtitles)subtitle;
		subFile = subIdxSubtitles.getSubFile();
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		Assert.assertEquals("Sub file Name", "Drive Angry (2011).sub", subFile.getName() );
		idxFile = subIdxSubtitles.getIdxFile();
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		Assert.assertEquals("Idx file Name", "Drive Angry (2011).idx", idxFile.getName() );
		
		subFile = new File(newMediaFolder, "Drive Angry (2011).sub");
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		
		idxFile = new File(newMediaFolder, "Drive Angry (2011).idx");
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		
		// ====================================================================================== //
		
		// ######################################################## //
		// ##### Setup 6 ##### //
		//image download completed, set
		imageInfo.setBufferedImage(bufferedImage);
		mediaDetail.save();
		
		// ##### Assert 06 ##### //
		//poster image is saved as a file
		Assert.assertNotNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertEquals("Media PosterImage", ImageInfoStatus.IN_FILE, mediaDetail.getPosterImage().getImageStatus() );

		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		
		posterImageFile = new File(newMediaFolder, "cover.jpg");
		Assert.assertTrue("Poster Image file must exist", posterImageFile.exists());
		
		Assert.assertEquals("MediaNFO Thumb", testMediaFolder.getPath() + File.separator + "Drive Angry (2011)" + File.separator + "cover.jpg", mediaDetail.getMediaNFO().getThumb() );
		
		nfoFile = new File(newMediaFolder, "Drive Angry (2011).nfo");
		Assert.assertTrue("NFO does exists", nfoFile.exists());
		// ====================================================================================== //
		
		// ######################################################## //
		// ##### Setup 7 ##### // Change year and will affect media file name and folder name, should also move all files to the new folder
		//change year
		movieFileNFO = (MovieFileNFO)mediaDetail.getMediaNFO();
		mediaDetail.setYear(2010);
		mediaDetail.save();
		
		// ##### Assert 07 ##### //
		Assert.assertEquals("Filename", "Drive Angry (2010)", mediaDetail.getFileName() );
		Assert.assertEquals("MediaNFO FilenameAndPath", testMediaFolder.getPath() + File.separator + "Drive Angry (2010)" + File.separator + "Drive Angry (2010).mkv", movieFileNFO.getMovie().getFilenameandpath() );		
		
		Assert.assertEquals("MediaNFO Year", new Integer(2010), mediaDetail.getMediaNFO().getYear() );
		Assert.assertEquals("MediaNFO Title", "Drive Angry", mediaDetail.getMediaNFO().getTitle() );
		
		//check if the file has being physicaly renamed
		Assert.assertFalse("Old Media file should not exist", mediaFile.exists());
		
		newMediaFolder = new File(testMediaFolder, "Drive Angry (2010)");
		mediaFile = new File(newMediaFolder, "Drive Angry (2010).mkv");
		Assert.assertTrue("New Media file must exist", mediaFile.exists());
		
		//poster image is saved as a file
		Assert.assertNotNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertEquals("Media PosterImage", ImageInfoStatus.IN_FILE, mediaDetail.getPosterImage().getImageStatus() );
		
		posterImageFile = new File(newMediaFolder, "cover.jpg");
		Assert.assertTrue("Poster Image file must exist", posterImageFile.exists());
		
		Assert.assertEquals("MediaNFO Thumb", testMediaFolder.getPath() + File.separator + "Drive Angry (2010)" + File.separator + "cover.jpg", mediaDetail.getMediaNFO().getThumb() );
		
		Assert.assertFalse("Old NFO does NOT exists", nfoFile.exists());
		nfoFile = new File(newMediaFolder, "Drive Angry (2010).nfo");
		Assert.assertTrue("NFO does exists", nfoFile.exists());
		
		//assert subtitles
		Assert.assertFalse("Old Sub file should NOT exist", subFile.exists() );
		Assert.assertFalse("Old Idx file should NOT exist", idxFile.exists() );
		
		Assert.assertTrue("Media has subtitles", mediaDetail.hasSubtitles() );
		subtitle = mediaDetail.getSubtitle();
		Assert.assertTrue("Media has sub/idx subtitles", subtitle instanceof SubIdxSubtitles );
		subIdxSubtitles = (SubIdxSubtitles)subtitle;
		subFile = subIdxSubtitles.getSubFile();
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		Assert.assertEquals("Sub file Name", "Drive Angry (2010).sub", subFile.getName() );
		idxFile = subIdxSubtitles.getIdxFile();
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		Assert.assertEquals("Idx file Name", "Drive Angry (2010).idx", idxFile.getName() );
		
		subFile = new File(newMediaFolder, "Drive Angry (2010).sub");
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		
		idxFile = new File(newMediaFolder, "Drive Angry (2010).idx");
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		
		Assert.assertEquals("Original File Name", "1-3-3-8.com_twz-drive.angry.720p.mkv", mediaDetail.getOriginalFileName());
		// ====================================================================================== //
		
	}
	
	@Test
	public void testmediaDetailNoMediaDetails() throws MediaFileException{
		
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "Elephant.White.2011.720p.BluRay.x264-Japhson/OneDDL.com-japhson-elephantwhite-720p.mkv");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		//assert basic properties
		Assert.assertEquals("Title", "OneDDL.com-japhson-elephantwhite-720p", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.NO_MEDIA_DETAILS, mediaDetail.getStatus() );
		Assert.assertNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertNull("Media Type", mediaDetail.getMediaType() );
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
	}
	
	@Test
	public void testmediaDetailWithoutNFOWithPoster() throws MediaFileException{
		
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "It's Kind of a Funny Story (2010)/It's Kind of a Funny Story (2010).mkv");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		//assert basic properties
		Assert.assertEquals("Title", "It's Kind of a Funny Story", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.NO_MEDIA_DETAILS, mediaDetail.getStatus() );
		Assert.assertNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertTrue("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertNull("Media Type", mediaDetail.getMediaType() );
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
	
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
	}
	
	@Test
	public void testmediaDetailWithNFO() throws MediaFileException{
		
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "Limitless (2011)/Limitless (2011).mkv");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		//assert basic properties
		Assert.assertEquals("Title", "Limitless", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, mediaDetail.getStatus() );
		Assert.assertNotNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertTrue("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertEquals("Media PosterImage Status", ImageInfoStatus.IN_FILE, mediaDetail.getPosterImage().getImageStatus() );
		Assert.assertEquals("Media Type", MediaType.MOVIE, mediaDetail.getMediaType() );
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		//assert NFO
		Assert.assertEquals("MediaNFO Year", new Integer(2011), mediaDetail.getMediaNFO().getYear() );
		Assert.assertEquals("MediaNFO Title", "Limitless", mediaDetail.getMediaNFO().getTitle() );
		Assert.assertEquals("MediaNFO MediaType", MediaType.MOVIE, mediaDetail.getMediaNFO().getMediaType() );
		
		Assert.assertNotNull("MediaNFO Thumb", mediaDetail.getMediaNFO().getThumb() );
		Assert.assertEquals("MediaNFO Thumb", testMediaFolder.getPath() + File.separator + "Limitless (2011)" + File.separator + "cover.jpg", mediaDetail.getMediaNFO().getThumb() );
		
		
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
	}
	
	@Test
	public void testFileNameNormalized() throws MediaFileException{
		// ######################################################## //
		// ################        Setup 01        ################ //
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "twiz-hp7-720p-rpk/1-3-3-8.com_twiz-hp7part1-720p-rpk.mkv");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "1-3-3-8.com_twiz-hp7part1-720p-rpk", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.NO_MEDIA_DETAILS, mediaDetail.getStatus() );
		Assert.assertNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		// ######################################################## //
		// ################        Setup 02        ################ //
		//Change the title
		mediaDetail.setTitle("Harry Potter and the Deathly Hallows: Part 1");
		mediaDetail.setMediaType(MediaType.MOVIE);
		mediaDetail.save();
		mediaDetail.setYear(2010);
		mediaDetail.save();
		
		// ======================================================== //
		// =================      Assert 02       ================= //
		Assert.assertEquals("Title", "Harry Potter and the Deathly Hallows: Part 1", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("Media File", "Harry Potter and the Deathly Hallows - Part 1 (2010).mkv", mediaDetail.getFiles()[0].getName() );
		
		Assert.assertNotNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNotNull("Media NFO", mediaDetail.getMediaNFO() );
		File nfoFile = new File(testMediaFolder, "Harry Potter and the Deathly Hallows - Part 1 (2010)/Harry Potter and the Deathly Hallows - Part 1 (2010).nfo");
		Assert.assertTrue("NFO Folder Exists", nfoFile.exists() );
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
	}
	
	@Test
	public void testMultiPartLookup_01() throws MediaFileException{
		// ######################################################## //
		// ################        Setup 01        ################ //
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "22.Bullets.2010/22.Bullets.2010 - cd1.avi");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "22 Bullets 2010", mediaDetail.getTitle() );
		Assert.assertTrue("Media isMultiPart", mediaDetail.isMultiPart());
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		// ######################################################## //
		// ################        Setup 02        ################ //
		//Change title
		mediaDetail.setMediaType(MediaType.MOVIE);
		mediaDetail.save();
		
		mediaDetail.setTitle("22 Bullets");
		mediaDetail.save();
		
		// ======================================================== //
		// =================      Assert 02       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "22 Bullets", mediaDetail.getTitle() );
		Assert.assertTrue("Media isMultiPart", mediaDetail.isMultiPart());
		
		File part1 = new File(testMediaFolder, "22 Bullets/22 Bullets - Part1.avi");
		Assert.assertTrue("File part renamed", part1.exists());
		
		File part2 = new File(testMediaFolder, "22 Bullets/22 Bullets - Part2.avi");
		Assert.assertTrue("File part renamed", part2.exists());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		
		// ######################################################## //
		// ################        Setup 03        ################ //
		//create a valid mediaDetail
		mediaDetail.setYear(2010);
		mediaDetail.save();
		
		// ======================================================== //
		// =================      Assert 03       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "22 Bullets", mediaDetail.getTitle() );
		Assert.assertTrue("Media isMultiPart", mediaDetail.isMultiPart());
		
		part1 = new File(testMediaFolder, "22 Bullets (2010)/22 Bullets (2010) - Part1.avi");
		Assert.assertTrue("File part renamed", part1.exists());
		
		part2 = new File(testMediaFolder, "22 Bullets (2010)/22 Bullets (2010) - Part2.avi");
		Assert.assertTrue("File part renamed", part2.exists());
		
		//NFO file
		File nfoFile = new File(testMediaFolder, "22 Bullets (2010)/22 Bullets (2010).nfo");
		Assert.assertTrue("NFO File", nfoFile.exists());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
	}
	
	@Test
	public void testMultiPartLookup_02() throws MediaFileException{
		// ######################################################## //
		// ################        Setup 01        ################ //
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "Certified Copy (2010) - CD2.avi");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		// ======================================================== //
		// =================      Assert 01       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "Certified Copy", mediaDetail.getTitle() );
		Assert.assertEquals("Year", new Integer(2010), mediaDetail.getYear());
		Assert.assertTrue("Media isMultiPart", mediaDetail.isMultiPart());
		//media folder exclusive
		Assert.assertFalse("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		// ######################################################## //
		// ################        Setup 02        ################ //
		//Set media type and poster image
		mediaDetail.setMediaType(MediaType.MOVIE);
		ImageInfo imageInfo = ImageInfo.createImageWithURL("http://teste/image.jpg");
		imageInfo.setBufferedImage(bufferedImage);
		mediaDetail.setPosterImage(imageInfo);
		mediaDetail.moveToExclusiveFolder();
		mediaDetail.save();
		
		// ======================================================== //
		// =================      Assert 02       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "Certified Copy", mediaDetail.getTitle() );
		Assert.assertTrue("Media isMultiPart", mediaDetail.isMultiPart());
		
		File part1 = new File(testMediaFolder, "Certified Copy (2010)/Certified Copy (2010) - Part1.avi");
		Assert.assertTrue("File part renamed", part1.exists());
		
		File part2 = new File(testMediaFolder, "Certified Copy (2010)/Certified Copy (2010) - Part2.avi");
		Assert.assertTrue("File part renamed", part2.exists());
		
		File nfoFile = new File(testMediaFolder, "Certified Copy (2010)/Certified Copy (2010).nfo");
		Assert.assertTrue("NFO file exists", nfoFile.exists());
		
		File coverFile = new File(testMediaFolder, "Certified Copy (2010)/cover.jpg");
		Assert.assertTrue("Cover file exists", coverFile.exists());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		
		// ######################################################## //
		// ################        Setup 03        ################ //
		//create a valid mediaDetail
		mediaDetail.setYear(2011);
		mediaDetail.save();
		
		// ======================================================== //
		// =================      Assert 03       ================= //
		//assert basic properties
		Assert.assertEquals("Title", "Certified Copy", mediaDetail.getTitle() );
		Assert.assertTrue("Media isMultiPart", mediaDetail.isMultiPart());
		
		Assert.assertFalse("File renamed", part1.exists());
		part1 = new File(testMediaFolder, "Certified Copy (2011)/Certified Copy (2011) - Part1.avi");
		Assert.assertTrue("File part renamed", part1.exists());
		
		Assert.assertFalse("File renamed", part2.exists());
		part2 = new File(testMediaFolder, "Certified Copy (2011)/Certified Copy (2011) - Part2.avi");
		Assert.assertTrue("File part renamed", part2.exists());
		
		Assert.assertFalse("File renamed", nfoFile.exists());
		nfoFile = new File(testMediaFolder, "Certified Copy (2011)/Certified Copy (2011).nfo");
		Assert.assertTrue("NFO file exists", nfoFile.exists());
		
		Assert.assertFalse("File renamed", coverFile.exists());
		coverFile = new File(testMediaFolder, "Certified Copy (2011)/cover.jpg");
		Assert.assertTrue("Cover file exists", coverFile.exists());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
	}
	
	@Test
	public void testInvalidmediaDetail(){
		
		//create an invalid mediaDetail
		File mediaFile = new File("file that does not exist");
		try {
			MediaFileMetadata fileMetadata = new MediaFileMetadata(mediaFile, true);
			MediaDetail mediaDetail = new MediaDetail(fileMetadata);
			mediaDetail.init();
			Assert.fail("Invalid media file, should generate an exception");
		} catch (MediaFileException e) {
			Assert.assertEquals(e.getMessage(), "Media File does not exist in the file system: " + mediaFile.getPath());
		}
		
		//create an invalid mediaDetail
		try {
			MediaFileMetadata fileMetadata = new MediaFileMetadata(testMediaFolder, true);
			MediaDetail mediaDetail = new MediaDetail(fileMetadata);
			mediaDetail.init();
			Assert.fail("Invalid media file, should generate an exception");
		} catch (MediaFileException e) {
			Assert.assertEquals(e.getMessage(), "Media File is not a valid file: " + testMediaFolder.getPath());
		}
		
	}
}
