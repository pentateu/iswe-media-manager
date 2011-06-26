package nz.co.iswe.mediamanager.scraper.impl;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileContext;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.nfo.MovieFileNFO;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.testutility.TestSuitConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IMDBMovieScraperTest {

	static File tempMediaFolder;
	static File imdbSamplesFolder;

	@BeforeClass
	public static void setupClass() throws IOException {
		TestSuitConfig.createNewJUnitMediaFolder();
		tempMediaFolder = TestSuitConfig.getJUnitMediaFolder();

		imdbSamplesFolder = TestSuitConfig.getIMDBSamplesFolder();
	}

	@AfterClass
	public static void tearDownClass() {
		TestSuitConfig.deleteJUnitMediaFolder();
	}

	@Test
	public void testIsMovieDetailScreenURL(){
		IMDBMovieScraper scraper = new IMDBMovieScraper();
		
		Assert.assertTrue( scraper.isMovieDetaiScreenlURL("http://www.imdb.com/title/tt0120737/") );
		
		Assert.assertTrue( scraper.isMovieDetaiScreenlURL("http://www.imdb.com/title/tt1555149/") );

		Assert.assertFalse( scraper.isMovieDetaiScreenlURL("http://www.imdb.com/title/tt1555149/taglines") );

		Assert.assertFalse( scraper.isMovieDetaiScreenlURL("http://www.imdb.com/find?s=all&q=Elite+Squad") );

	}
	
	@Test
	public void testIsSearchScreenURL(){
		IMDBMovieScraper scraper = new IMDBMovieScraper();
		
		Assert.assertTrue( scraper.isSearchScreenURL("http://www.imdb.com/find?s=all&q=Elite+Squad") );

		Assert.assertTrue( scraper.isSearchScreenURL("http://www.imdb.com/find?s=all&q=The+Lord+of+the+Rings") );
		
		Assert.assertTrue( scraper.isSearchScreenURL("http://www.imdb.com/find?s=tt&q=Hangover") );
		
		Assert.assertFalse( scraper.isSearchScreenURL("http://www.imdb.com/title/tt0120737/") );
		
		Assert.assertFalse( scraper.isSearchScreenURL("http://www.imdb.com/title/tt1555149/") );

		Assert.assertFalse( scraper.isSearchScreenURL("http://www.imdb.com/title/tt1555149/taglines") );

		

	}
	
	@Test
	public void testScrapeDocument() throws IOException, MediaFileException {

		// Setup
		File movieFolder = new File(tempMediaFolder, "The Lord of the Rings - The Fellowship of the Ring");
		File movieFile = new File(movieFolder, "The Lord of the Rings - The Fellowship of the Ring.mkv");

		File htmlFile = new File(imdbSamplesFolder, "imdb-LordOfTheRings.html");

		Document doc = Jsoup.parse(htmlFile, null);

		IMDBMovieScraper scraper = new IMDBMovieScraper();
		scraper.setSkipDownloadPoster(true);

		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(movieFile);
		scraper.setMediaDefinition(mediaDetail);
		mediaDetail.setMediaType(MediaType.MOVIE);

		// Execute method to be tested
		scraper.scrapeDocument(doc, "http://www.imdb.com/title/tt0120737/");

		MovieFileNFO movieFileNFO = (MovieFileNFO) mediaDetail.getMediaNFO();

		Assert.assertEquals("Title", "The Lord of the Rings: The Fellowship of the Ring", mediaDetail.getTitle());

		Assert.assertEquals("Original Title", "The Lord of the Rings: The Fellowship of the Ring",
				movieFileNFO.getOriginalTitle());

		Assert.assertEquals("Sort Title", "The Lord of the Rings: The Fellowship of the Ring",
				movieFileNFO.getSortTitle());

		Assert.assertEquals("Year", new Integer(2001), movieFileNFO.getYear());

		Assert.assertEquals("Rating", "8.8", movieFileNFO.getRating());

		Assert.assertEquals("top250", new Integer(17), movieFileNFO.getTop250());

		Assert.assertEquals("Votes", new Integer(442909), movieFileNFO.getVotes());

		Assert.assertEquals(
				"Outline",
				"In a small village in the Shire a young Hobbit named Frodo has been entrusted with an ancient Ring. Now he must embark on an Epic quest to the Cracks of Doom in order to destroy it.",
				movieFileNFO.getOutline());

		Assert.assertEquals(
				"Plot",
				"An ancient Ring thought lost for centuries has been found, and through a strange twist in fate has been given to a small Hobbit named Frodo. When Gandalf discovers the Ring is in fact the One Ring of the Dark Lord Sauron, Frodo must make an epic quest to the Cracks of Doom in order to destroy it! However he does not go alone. He is joined by Gandalf, Legolas the elf, Gimli the Dwarf, Aragorn, Boromir and his three Hobbit friends Merry, Pippin and Samwise. Through mountains, snow, darkness, forests, rivers and plains, facing evil and danger at every corner the Fellowship of the Ring must go. Their quest to destroy the One Ring is the only hope for the end of the Dark Lords reign!",
				movieFileNFO.getPlot());

		Assert.assertEquals("Runtime",
				"178 min  208 min (special extended edition)  228 min (Special Extended Blu-Ray Edition)",
				movieFileNFO.getRuntime());

		Assert.assertEquals("ID", "tt0120737", movieFileNFO.getId());

	}

	@Test
	public void testScrapeDocument_2() throws IOException, MediaFileException {

		// Setup
		File movieFolder = new File(tempMediaFolder, "Elite Squad 2");
		File movieFile = new File(movieFolder, "Elite Squad 2.mkv");

		File htmlFile = new File(imdbSamplesFolder, "imdb-EliteSquad2.html");

		Document doc = Jsoup.parse(htmlFile, null);

		IMDBMovieScraper scraper = new IMDBMovieScraper();
		scraper.setSkipDownloadPoster(true);

		MediaDetail mediaDetail = MediaFileContext.getInstance().getMediaFile(movieFile);
		scraper.setMediaDefinition(mediaDetail);
		mediaDetail.setMediaType(MediaType.MOVIE);

		// Execute method to be tested
		scraper.scrapeDocument(doc, "http://www.imdb.com/title/tt1555149/");

		MovieFileNFO movieFileNFO = (MovieFileNFO) mediaDetail.getMediaNFO();

		Assert.assertEquals("Title", "Elite Squad 2", mediaDetail.getTitle());

		Assert.assertEquals("Original Title", "Tropa de Elite 2 - O Inimigo Agora É Outro",
				movieFileNFO.getOriginalTitle());

		Assert.assertEquals("Sort Title", "Elite Squad 2", movieFileNFO.getSortTitle());

		Assert.assertEquals("Year", new Integer(2010), movieFileNFO.getYear());

		Assert.assertEquals("Rating", "8.4", movieFileNFO.getRating());

		Assert.assertNull("top250", movieFileNFO.getTop250());

		Assert.assertEquals("Votes", new Integer(8343), movieFileNFO.getVotes());

		Assert.assertEquals(
				"Outline",
				"After a bloody invasion of the BOPE in the High-Security Penitentiary Bangu 1 in Rio de Janeiro to control a rebellion of interns... »",
				movieFileNFO.getOutline());

		Assert.assertEquals(
				"Plot",
				"After a bloody invasion of the BOPE in the High-Security Penitentiary Bangu 1 in Rio de Janeiro to control a rebellion of interns, the Lieutenant-Colonel Roberto Nascimento and the second in command Captain André Matias are accused by the Human Right Aids member Diogo Fraga of execution of prisoners. Matias is transferred to the corrupted Military Police and Nascimento is exonerated from the BOPE by the Governor. However, due to the increasing popularity of Nascimento, the Governor invites him to team-up with the intelligence area of the Secretary of Security. Along the years, Fraga, who is married with Nascimento's former wife, is elected State Representative and Nascimento's son Rafael has issues with his biological father. Meanwhile Nascimento and the BOPE expel the drug dealers from several slums but another enemy arises: the militia led by Major Rocha and supported by the Governor, the Secretary of Security and politicians interested in votes...",
				movieFileNFO.getPlot());

		Assert.assertEquals("Runtime", "115 min", movieFileNFO.getRuntime());

		Assert.assertEquals("ID", "tt1555149", movieFileNFO.getId());

	}

}
