package nz.co.iswe.mediamanager.media.subtitles;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import nz.co.iswe.mediamanager.media.ImageInfoStatus;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileContext;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.testutility.TestSuitConfig;
import nz.co.iswe.mediamanager.testutility.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SubtitleContextTest extends TestUtil {

	private File testMediaFolder = null;
	
	@Before
	public void setup() throws IOException{
		
		TestSuitConfig.createNewJUnitMediaFolder();
		
		testMediaFolder = TestSuitConfig.getJUnitMediaFolder();
		
	}

	@After
	public void cleanup(){
		TestSuitConfig.deleteJUnitMediaFolder();
	}	
	
	@Test
	public void testSubIdxSubtitles() throws MediaFileException{
		//create a valid MediaFileDefinition
		File mediaFile = new File(testMediaFolder, "Limitless (2011)/Limitless (2011).mkv");
		MediaDetail mediaFileDefinition = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		//assert basic properties
		Assert.assertEquals("Title", "Limitless", mediaFileDefinition.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaFileDefinition.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.MEDIA_DETAILS_FOUND, mediaFileDefinition.getStatus() );
		Assert.assertNotNull("Media NFO", mediaFileDefinition.getMediaNFO() );
		Assert.assertNotNull("Media PosterImage", mediaFileDefinition.getPosterImage() );
		Assert.assertTrue("Media PosterImage", mediaFileDefinition.hasPoster());
		Assert.assertEquals("Media PosterImage Status", ImageInfoStatus.IN_FILE, mediaFileDefinition.getPosterImage().getImageStatus() );
		Assert.assertEquals("Media Type", MediaType.MOVIE, mediaFileDefinition.getMediaType() );
		
		//assert NFO
		Assert.assertEquals("MediaNFO Year", new Integer(2011), mediaFileDefinition.getMediaNFO().getYear() );
		Assert.assertEquals("MediaNFO Title", "Limitless", mediaFileDefinition.getMediaNFO().getTitle() );
		Assert.assertEquals("MediaNFO MediaType", MediaType.MOVIE, mediaFileDefinition.getMediaNFO().getMediaType() );
		Assert.assertNotNull("MediaNFO Thumb", mediaFileDefinition.getMediaNFO().getThumb() );
		Assert.assertEquals("MediaNFO Thumb", testMediaFolder.getPath() + File.separator + "Limitless (2011)" + File.separator + "cover.jpg", mediaFileDefinition.getMediaNFO().getThumb() );
		
		//assert subtitles
		Assert.assertTrue("Media has subtitles", mediaFileDefinition.hasSubtitles() );
		ISubtitle subtitle = mediaFileDefinition.getSubtitle();
		Assert.assertTrue("Media has sub/idx subtitles", subtitle instanceof SubIdxSubtitles );
		SubIdxSubtitles subIdxSubtitles = (SubIdxSubtitles)subtitle;
		File subFile = subIdxSubtitles.getSubFile();
		Assert.assertNotNull("Sub file",  subFile);
		Assert.assertTrue("Sub file is file", subFile.isFile() );
		File idxFile = subIdxSubtitles.getIdxFile();
		Assert.assertNotNull("Idx file",  idxFile);
		Assert.assertTrue("Idx file is file", idxFile.isFile() );
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaFileDefinition.isInExclusiveFolder() );
	}
	
}
