package nz.co.iswe.mediamanager;

import java.io.File;

import junit.framework.Assert;
import nz.co.iswe.mediamanager.testutility.TestSuitConfig;

import org.junit.Test;


public class UtilTest {

	private static final String TEST_MEDIA_TEMPLATE_FOLDER = "test.media.template.folder";

	/**
	 * Examples:
	 *  - 22.Bullets.2010 - cd2.avi : It will return result[0] = "cd" and result[1] = "2"
	 *  - Certified Copy (2010) - CD1.avi : It will return result[0] = "CD" and result[1] = "1"  
	 *  - Toy Story 3 - Part 1.avi : It will return result[0] = "Part " and result[1] = "1"  
	 *  - Toy Story 3 - Part 2.avi : It will return result[0] = "Part " and result[1] = "2"  
	 *  - transformers.2.part1.avi : It will return result[0] = "part " and result[1] = "1"  
	 *  - transformers.2.part2.avi : It will return result[0] = "part " and result[1] = "2" 
	 */
	@Test
	public void testMultPartSufix(){
		String filename = "Harry Potter and the Deathly Hallows: Part";
		String[] result = Util.getMultiPartSufix(filename);
		Assert.assertNull("Not a mult-part filename", result);
		
		filename = "Harry Potter and the Deathly Hallows 1.mkv";
		result = Util.getMultiPartSufix(filename);
		Assert.assertNull("Not a mult-part filename", result);
		
		filename = "Harry.Potter.and.the.Deathly.Hallows_1.mkv";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("Harry.Potter.and.the.Deathly.Hallows", result[0]);
		Assert.assertEquals("_", result[1]);
		Assert.assertEquals("1", result[2]);
		
		filename = "Harry Potter and the Deathly Hallows: Part 1.mkv";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("Harry Potter and the Deathly Hallows: ", result[0]);
		Assert.assertEquals("Part ", result[1]);
		Assert.assertEquals("1", result[2]);
	
		filename = "22.Bullets.2010 - cd1.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("22.Bullets.2010 - ", result[0]);
		Assert.assertEquals("cd", result[1]);
		Assert.assertEquals("1", result[2]);
		
		filename = "22.Bullets.2010 - cd2.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("22.Bullets.2010 - ", result[0]);
		Assert.assertEquals("cd", result[1]);
		Assert.assertEquals("2", result[2]);
		
		filename = "Certified Copy (2010) - CD1.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("Certified Copy (2010) - ", result[0]);
		Assert.assertEquals("CD", result[1]);
		Assert.assertEquals("1", result[2]);
		
		filename = "Certified Copy (2010) - CD2.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("Certified Copy (2010) - ", result[0]);
		Assert.assertEquals("CD", result[1]);
		Assert.assertEquals("2", result[2]);
		
		filename = "Toy Story 3 - Part 1.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("Toy Story 3 - ", result[0]);
		Assert.assertEquals("Part ", result[1]);
		Assert.assertEquals("1", result[2]);
		
		filename = "Toy Story 3 - Part 2.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("Toy Story 3 - ", result[0]);
		Assert.assertEquals("Part ", result[1]);
		Assert.assertEquals("2", result[2]);
		
		filename = "transformers.2.part1.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("transformers.2.", result[0]);
		Assert.assertEquals("part", result[1]);
		Assert.assertEquals("1", result[2]);
	
		filename = "transformers.2.part2.avi";
		result = Util.getMultiPartSufix(filename);
		Assert.assertEquals("transformers.2.", result[0]);
		Assert.assertEquals("part", result[1]);
		Assert.assertEquals("2", result[2]);
	}
	
	@Test
	public void testNormalizeFileName(){
		
		String filename = "Harry Potter and the Deathly Hallows: Part 1.mkv";
		String result = Util.normalizeFileName(filename);
		
		Assert.assertEquals("Harry Potter and the Deathly Hallows - Part 1.mkv", result);
		
		
		filename = "Harry Potter and the Deathly Hallows / Part 1.mkv";
		result = Util.normalizeFileName(filename);
		
		Assert.assertEquals("Harry Potter and the Deathly Hallows - Part 1.mkv", result);
		
		filename = "Harry Potter and the Deathly Hallows \\ Part 1.mkv";
		result = Util.normalizeFileName(filename);
		
		Assert.assertEquals("Harry Potter and the Deathly Hallows - Part 1.mkv", result);
		
		
		filename = "Harry.Potter.and.the.Deathly.Hallows: Part 1.mkv";
		result = Util.normalizeFileName(filename);
		
		Assert.assertEquals("Harry Potter and the Deathly Hallows - Part 1.mkv", result);
		
		filename = "Harry.Potter.and.the.Deathly.Hallows ;  Part_1.mkv";
		result = Util.normalizeFileName(filename);
		
		Assert.assertEquals("Harry Potter and the Deathly Hallows - Part 1.mkv", result);
		
	}
	
	@Test
	public void testIsValidImageFile(){
		
		//Invalids
		File invalidImageFile = new File("file does not exists");
		boolean result = Util.isValidImageFile(invalidImageFile);
		Assert.assertFalse("Invalid image file", result);
		
		invalidImageFile = new File(TestSuitConfig.getTestProperties().getProperty(TEST_MEDIA_TEMPLATE_FOLDER));
		result = Util.isValidImageFile(invalidImageFile);
		Assert.assertFalse("Invalid image file", result);
		
		invalidImageFile = new File(TestSuitConfig.getTestProperties().getProperty(TEST_MEDIA_TEMPLATE_FOLDER) + "/It's Kind of a Funny Story (2010)/It's Kind of a Funny Story (2010).mkv");
		result = Util.isValidImageFile(invalidImageFile);
		Assert.assertFalse("Invalid image file", result);
		
		//valid
		File validImageFile = new File(TestSuitConfig.getTestProperties().getProperty(TEST_MEDIA_TEMPLATE_FOLDER) + "/It's Kind of a Funny Story (2010)/cover.jpg");
		result = Util.isValidImageFile(validImageFile);
		Assert.assertTrue("Valid image file", result);
	}
	
	@Test
	public void testRemoveDoubleSpaces(){
		
		String value = "Texto  com dois     ou mais espacos";
		
		String result = Util.removeDoubleSpaces(value);
		
		Assert.assertEquals("removeDoubleSpaces", "Texto com dois ou mais espacos", result);
		
	}
	
	@Test
	public void testGetCandidateFolderPrefix(){
		
		String fileName = "The Tourist (2011).mkv";
		
		String result = Util.getCandidateFolderPrefix(fileName);
		
		Assert.assertEquals("getCandidateFolderPrefix", "The_Tourist_candidate_", result);
	}
	
}
