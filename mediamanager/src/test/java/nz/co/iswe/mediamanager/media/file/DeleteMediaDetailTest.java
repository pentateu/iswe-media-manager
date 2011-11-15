package nz.co.iswe.mediamanager.media.file;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.folder.MediaFolderContext;
import nz.co.iswe.mediamanager.testutility.TestSuitConfig;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DeleteMediaDetailTest {

	private static File testMediaFolder = null;
	
	@BeforeClass
	public static void setup() throws IOException{
		
		TestSuitConfig.createNewJUnitMediaFolder();
		
		testMediaFolder = TestSuitConfig.getJUnitMediaFolder();
		
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
	public void testMediaWithSampleFile() throws MediaFileException{
		
		// ######################################################## //
		// ##### Setup 1 ##### //
		//create a valid mediaDetail
		File mediaFile = new File(testMediaFolder, "flhd-withlove720p.1500/1-3-3-8.com_flhd-withlove720p.todelete.mkv");
		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(mediaFile);
		
		
		// ##### Assert 01 ##### //
		Assert.assertEquals("Title", "1-3-3-8.com_flhd-withlove720p.todelete", mediaDetail.getTitle() );
		Assert.assertEquals("List of Candidates", 0, mediaDetail.getCandidates().size() );
		Assert.assertEquals("MediaDetailStatus", MediaStatus.NO_MEDIA_DETAILS, mediaDetail.getStatus() );
		Assert.assertNull("Media NFO", mediaDetail.getMediaNFO() );
		Assert.assertNull("Media PosterImage", mediaDetail.getPosterImage() );
		Assert.assertFalse("Media PosterImage", mediaDetail.hasPoster());
		Assert.assertNull("Media Type", mediaDetail.getMediaType() );
		Assert.assertFalse("Media hasSiblings", mediaDetail.isMultiPart());
		
		//media folder exclusive
		Assert.assertTrue("Media folder exclusive", mediaDetail.isInExclusiveFolder() );
		
		Assert.assertTrue("Media file exists", mediaFile.exists());
		
		mediaDetail.delete();
		
		Assert.assertFalse("Media file exists", mediaFile.exists());
		
		File otherFile = new File(testMediaFolder, "flhd-withlove720p.1500/1-3-3-8.com_flhd-withlove720p.mkv");
		Assert.assertTrue("Other file still exists", otherFile.exists());
	}
}
