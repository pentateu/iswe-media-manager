package nz.co.iswe.mediamanager.scraper.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.IMediaDetail;
import nz.co.iswe.mediamanager.media.ImageInfo;
import nz.co.iswe.mediamanager.media.MediaStatus;
import nz.co.iswe.mediamanager.media.file.CandidateMediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.scraper.MediaType;
import nz.co.iswe.mediamanager.scraper.ScraperContext;
import nz.co.iswe.mediamanager.scraper.SearchResult;
import nz.co.iswe.mediamanager.text.NormalizerFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OneDDLScraper extends AbstractScraper {

	private static Logger log = Logger.getLogger(OneDDLScraper.class.getName());
	
	//TODO: Put this info inside a database so the system can learn overtime and not be constrained to these values
	
	//File names examples
	//1-3-3-8.com_The.Tourist.2010.720p.BluRay.x264-NODLABS
	//OneDDL.com-refined-ikoafs-720p
	
	//Patterns used to check whether this scraper
	//is the preferred scraper :-)
	private String [] matchFileNamePatterns = { //OneDDL common file name structures
			"OneDDL[\\.]com.{2,}",
			"1-3-3-8[\\.]com.{2,}"};
	
	
	//Example: of Post titles
	//Battle.Los.Angeles.2011.720p.BluRay.x264-BLA
	//Battle.Los.Angeles.R5.LiNE.DVDR-FiCODVDR
	//Battle.Los.Angeles.R5.LiNE.XViD-FOAM
	//Battle.Los.Angeles.2011.R5.XViD-IMAGiNE
	//Battle.of.Los.Angeles.2011.DVDRiP.XViD-TASTE
	//Battle.Los.Angeles.2011.TS.XViD–IMAGiNE
	//Battle.Los.Angeles.2011.CAM.XVID.READNFO-LKRG
	//Battle.Los.Angeles-SKIDROW
	
	//Patterns used to clean-up the post title by removing 
	//all the text sequences that does not make up the name of the movie
	private String [] cleanUpPostTitlePatterns = {
			"720p",
			"BluRay",
			"x264",
			"NODLABS",
			"BLA",
			"R5",
			"LiNE",
			"XViD",
			"DVDR",
			"FiCODVDR",
			"FOAM",
			"IMAGiNE",
			"TASTE",
			"CAM",
			"XVID",
			"READNFO",
			"LKRG",
			"SKIDROW",
			"HDTV",
			"XviD",
			"OTV",
			"TWiZTED",
			"REFiNED"};
	
	
	 
	private List<SearchResult> candidatePosts = new ArrayList<SearchResult>();
	
	@Override
	public void searchAndScrap() {
		
		SearchResult searchResult = search();
		
		if(searchResult != null){
			log.fine("Search succesfull! url: " + searchResult.getURL());
			//get the detailed information about the media
			mediaDetail.setMediaType(searchResult.getMediaType());
			mediaDetail.setBlogPostURL(searchResult.getURL());
			
			try {
				scrapeMediaDetails(mediaDetail, searchResult);
			} catch (MediaFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			log.fine("Search NOT succesfull! Number of candidates found: " + candidatePosts.size());
			//check if any candidates has been found
			if(candidatePosts.size() > 0){
				//save the candidates for the user to review
				mediaDetail.setStatus(MediaStatus.CANDIDATE_LIST_FOUND);
				
				if(preLoadCandidates){
					//scrap the candidate details
					scrapCandidates();
					mediaDetail.setStatus(MediaStatus.CANDIDATE_DETAILS_FOUND);
				}
				else{
					mediaDetail.setCandidateUrls(candidatePosts);
				}
				
				observer.notifyStepProgress();
			}
		}
		
	}

	public SearchResult search() {
		//get the movie name
		String mediaName = mediaDetail.getTitle();
		
		log.fine("Search for media : " + mediaName);
		
		//1: try a first an exact search using the filename
		String query = Util.buildURLQuery(mediaName);
		SearchResult searchResult = search(query, mediaDetail.getTitle(), false);
		
		observer.notifyStepProgress();
		
		if(searchResult == null){
		
			//2: try to remove the filename prefix
			String movieName = NormalizerFactory.getInstance().getFileNameNormalizer().cleanUp(mediaName);
			query = Util.buildURLQuery(movieName);
			searchResult = search(query, movieName, false);
		}
		
		observer.notifyStepProgress();
		
		if(searchResult == null){
			//3: try the movie name normalised and the post titles also normalized
			String movieName = NormalizerFactory.getInstance().getFileNameNormalizer().normalize(mediaName);
			query = Util.buildURLQuery(movieName);
			searchResult = search(query, movieName, true);
		}
		
		observer.notifyStepProgress();
		
		return searchResult;
	}

	/**
	 * Scrape the details of the candidade links found. So the user can review and confirm
	 * later off-line.
	 */
	public void scrapCandidates()  {
		/**
		 * For each candidate URL create an instance of CandidateMediaDefinition
		 */
		for(SearchResult searchResult : candidatePosts){
			try{
				//create media definition
				CandidateMediaDetail candidateMediaDefinition = new CandidateMediaDetail(mediaDetail);
				candidateMediaDefinition.setMediaType(searchResult.getMediaType());
				scrapeMediaDetails(candidateMediaDefinition, searchResult);
				candidateMediaDefinition.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
				
				mediaDetail.addCandidate(candidateMediaDefinition);
			}
			catch(MediaFileException e){
				e.printStackTrace();
			}
		}
	}

	public void scrape(SearchResult searchResult) {
		try {
			scrapeMediaDetails(mediaDetail, searchResult);
		} catch (MediaFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void scrapeMediaDetails(final IMediaDetail mediaDetail, final SearchResult searchResult) throws MediaFileException {
		//
		try {
			Document doc = Jsoup.connect(searchResult.getURL())
				.userAgent(USER_AGENT)
				.timeout(5000)
				.get();
			
			//verify if the query has returned an error
			Elements pageNotFound = doc.select("div.postarea > h3:containsOwn(Page not Found)");
			
			if(pageNotFound.size() > 0){
				//query did not return any result
				mediaDetail.setStatus(MediaStatus.MEDIA_DETAILS_NOT_FOUND);
				return;
			}
			else{			
				//scrap the media details
				mediaDetail.setStatus(MediaStatus.MEDIA_DETAILS_FOUND);
				
				//1: Get the NFO link
				Element nfoLink = getSingleElement(searchResult.getURL(), doc, "div.postarea > p[align=center] > a:containsOwn(NFO)");
				if(nfoLink != null){
					scrapeNFOFileDetails(mediaDetail, nfoLink.attr("href"));
				}
				else{
					//Log NFO not found
					log.fine("NFO link not found. url: " + searchResult.getURL());
				}
				
				//2: Get the Media Poster picture
				//Save the picture
				Element pictureImg = getSingleElement(searchResult.getURL(), doc, "div.postarea > p[align=center] > img[src*=images.oneddl]");
				if(pictureImg != null){
					final String imageURL = pictureImg.attr("src");
					
					final ImageInfo imageInfo = ImageInfo.createImageWithURL(imageURL);
					
					mediaDetail.setPosterImage(imageInfo);
					
					downloadPicture(mediaDetail, new ImageDownloadCallBack() {
						@Override
						public void errorDownloading(Throwable th) {
							log.fine("Could not download the poster image file. url: " + searchResult.getURL() + " image URL: " + imageURL);
						}
						@Override
						public void downloadComplete(BufferedImage image) throws MediaFileException {
							imageInfo.setBufferedImage(image);
							mediaDetail.save();
						}
					},
					imageURL);
				}
				else {
					//Log Picture not found
					log.fine("Picture img tag not found. url: " + searchResult.getURL());
				}
				
				//3: Get the iMDB link
				Element imdbLink = getSingleElement(searchResult.getURL(), doc, "div.postarea > p[align=center] > a[href*=imdb.com]:containsOwn(iMDB");
				if(imdbLink != null){
					SearchResult imdbSearchResult = new SearchResult(imdbLink.attr("href"), searchResult.getMediaType());
					scrapeMediaDetailsFromIMDB(mediaDetail, imdbSearchResult);
				}
				else{
					//Log imdn link not found
					log.fine("iMDB link not found. url: " + searchResult.getURL());
				}
				mediaDetail.save();
			}
			
		} catch (IOException e) {
			log.log(Level.WARNING, "Error fetching Media: " + mediaDetail + "  URL: " + searchResult.getURL(), e);
		}
	}

	private void scrapeMediaDetailsFromIMDB(IMediaDetail mediaDef, SearchResult searchResult) {
		
		IMDBMovieScraper imdbScraper = ScraperContext.getInstance().getScraper(IMDBMovieScraper.class);
		
		if(imdbScraper == null){
			log.info("IMDBScraper scraper not found! It will not retrieve info form iMDB");
			return;
		}
		
		imdbScraper.setSkipDownloadPoster(mediaDef.hasPoster());
		
		imdbScraper.setMediaDefinition(mediaDef);
		imdbScraper.setScrapingStatusObserver(observer);
		
		imdbScraper.scrape(searchResult);
	}
	
	private void scrapeNFOFileDetails(IMediaDetail mediaDef, String url) {
		// TODO Auto-generated method stub
		
	}

	

	private SearchResult search(String query, String titleToCompare, boolean normalizePostTitle) {
		
		String url = "http://www.oneddl.com/?s=" + query;
		
		//1: Try searching by the file name on OneDDL
		try {
			Document doc = Jsoup.connect(url)
				.userAgent(USER_AGENT)
				.timeout(5000)
				.get();
			
			//verify if the query has returned an error
			Elements pageNotFound = doc.select("div.postarea > h3:containsOwn(Page not Found)");
			
			if(pageNotFound.size() > 0){
				//query did not return any result
				return null;
			}
			else{			
				//go throught the posts
				Elements postsFound = doc.select("div.posttitle > h2 > a[href]");
				
				int totalFound = postsFound.size();
				
				for(Element element : postsFound){
					//A element containing the movie name and the link to the post page
					String postTitle = element.text();
					
					if(normalizePostTitle){
						//Normalise the postTitle name
						postTitle = normalizePostTitle(postTitle);
					}
					
					double score = Util.compareAndScore(titleToCompare, postTitle);
					
					if(totalFound == 1){
						//if only one was found increase the score by 50%
						score = score * 1.5;
					}
					
					log.fine("Score: " + score + " Total Found : " + totalFound + 
							" Media Title: " + titleToCompare + 
							" Post Title: " + postTitle + 
							" normalizePostTitle: " + normalizePostTitle + 
							" URL: " + url);
					
					String postURL = element.attr("href");
					MediaType mediaType = resolveMediaTypeByPostURL(postURL);
					
					SearchResult searchResult = new SearchResult(postURL, mediaType);
					
					//check if the score is above the minimum score
					if(score > minimumScore){
						//get the media type
						return searchResult;
					}
					else if(score > 20){
						if( ! candidatePosts.contains(searchResult) ){
							candidatePosts.add(searchResult);
						}
					}
				}
			}
			
		} catch (IOException e) {
			log.log(Level.WARNING, "Error fetching Media Name: " + titleToCompare + "  URL: " + url, e);
		}
		
		return null;
	}

	private MediaType resolveMediaTypeByPostURL(String postURL) {
		
		//Examples
		//http://www.oneddl.com/movies/papillon-1973-720p-bluray-x264-amiable/
		//http://www.oneddl.com/tv-shows/workaholics-s01e07-straight-up-juggahos-hdtv-xvid-fqm/
		
		//remove http://
		postURL = postURL.replaceAll("http://", "");
		String[] parts = postURL.split("/");
		
		if("movies".equals(parts[1])){
			return MediaType.MOVIE;
		}
		if("tv-shows".equals(parts[1])){
			return MediaType.TV_SHOW;
		}
		
		return null;
	}

	private String normalizePostTitle(String postTitle) {
		for(String pattern: cleanUpPostTitlePatterns){
			postTitle = postTitle.replaceAll(pattern, "");
		}
		
		postTitle = NormalizerFactory.getInstance().getFileNameNormalizer().replaceWithSpace(postTitle);
		
		return postTitle;
	}

	

	

	@Override
	public boolean preferedScraperFor(IMediaDetail mediaDefinition) {
		
		String fileName = mediaDefinition.getTitle();
		
		//check the file name
		for(String pattern : matchFileNamePatterns){
			if(fileName.matches(pattern)){
				return true;
			}
		}
		
		return false;
	}

}
