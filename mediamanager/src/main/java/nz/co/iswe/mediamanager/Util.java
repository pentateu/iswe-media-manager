package nz.co.iswe.mediamanager;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import nz.co.iswe.mediamanager.media.file.MediaFileException;

/**
 * Utility methods.
 * 
 * @author Rafael Almeida
 *
 */
public class Util {

	public static String getExtensionFromImageURL(String url) {
		return url.substring(url.lastIndexOf('.') + 1).trim().toLowerCase();
	}

	public static double compareAndScore(String from, String to) {
		double score = 0;

		// 1: try exact match ignoring the case
		if (from.equalsIgnoreCase(to)) {
			score = 100;
			return score;
		}

		// 2: try work by word
		from = from.replace('(', ' ').replace(')', ' ');
		from = removeDoubleSpaces(from);
		
		to = to.replace('(', ' ').replace(')', ' ');
		to = removeDoubleSpaces(to);
		
		//split the terms and compare them
		String[] termsFrom = from.split("\\.|\\s|-|_");
		String[] termsTo = to.split("\\.|\\s|-|_");

		double numberOfMatches = 0;
		for (String fromTerm : termsFrom) {
			for (String toTerm : termsTo) {
				if (isCloseEnought(fromTerm, toTerm, 60)) {
					numberOfMatches++;
					continue;
				}
			}
		}

		double totalWords = Math.max(termsFrom.length, termsTo.length);
		score = (numberOfMatches / totalWords) * 100;
		return score;
	}

	protected static boolean isCloseEnought(String from, String to, double minimum) {
		int size = Math.max(from.length(), to.length());
		int lfd = StringUtils.getLevenshteinDistance(from, to);
		double ratio = ((double)lfd) / size;
		ratio = (1 - ratio) * 100;
		return ratio >= minimum;
	}

	public static String removeYearFromTitle(String title) {
		return removeDoubleSpaces( title.replaceAll("\\(\\d{4}\\)", "") ).trim();
	}

	public static String removeDoubleSpaces(String value) {
		return value.replaceAll("\\s{2,}", " ");
	}

	private static final String[] mediaTypes = {"avi", "mkv", "mp4"};
	
	
	public static boolean isMediaValidFile(File file) throws MediaFileException {
		if(file == null){
			throw new MediaFileException("Media File is null");
		}
		if ( ! file.exists()) {
			throw new MediaFileException("Media File does not exist in the file system: " + file.getPath());
		}
		if (!file.isFile()) {
			throw new MediaFileException("Media File is not a valid file: " + file.getPath());
		}
		
		String type = getFileExtension(file.getName());
		return validType(type, mediaTypes);
	}

	private static boolean validType(String type, String[] typeList) {
		for(String item : typeList){
			if(item.equalsIgnoreCase(type)){
				return true;
			}
		}
		return false;
	}

	public static boolean isSampleFile(File item) {
		if(item == null){
			return false;
		}
		
		//long fileSize = item.get
		
		// TODO: IMprove to test the file size
		return item.getName().matches(".+\\.sample\\..+");
	}

	public static String getCandidateFolderPrefix(String fileName) {
		String result = removeYearFromTitle(getFileSimpleName(fileName))+ "_candidate_";
		
		result = result.replaceAll("\\s", "_");
		
		return result;
	}

	
	private static final String[] imageTypes = {"jpg", "png", "gif"};
	
	public static boolean isValidImageFile(File file) {
		//1: valida the file
		if(!file.exists()){
			return false;
		}
		if(!file.isFile()){
			return false;
		}
		
		//2: Compare the extension
		String type = getFileExtension(file.getName());
		return validType(type, imageTypes);
		
		//3: Analize filesize ...
		//TODO: improve isValidImageFile ()  Analize filesize ...
	}

	private static final String[] subtitleTypes = {
		"sub", "idx", "srt", "sup", "ssa", "smi"};
	
	public static boolean isValidSubtitleFile(File file) {
		//1: valida the file
		if(!file.exists()){
			return false;
		}
		if(!file.isFile()){
			return false;
		}
		
		//2: Compare the extension
		String type = getFileExtension(file.getName());
		return validType(type, subtitleTypes);
		
		//3: Analize filesize ...
		//TODO: improve isValidSubtitleFile ()  Analize filesize ...
	}

	public static String normalizeFileName(String filename) {
		
		String name = getFileSimpleName(filename);
		String ext = getFileExtension(filename);
		
		//replace not accepted characteres
		name = name.replaceAll(Pattern.quote(":"), " - ");//replace : for -
		name = name.replaceAll(Pattern.quote(";"), " - ");//replace ; for -
		name = name.replaceAll(Pattern.quote("/"), " - ");//replace / for -
		name = name.replaceAll(Pattern.quote("\\"), " - ");//replace \ for -
		name = name.replaceAll(Pattern.quote("."), " ");//replace . for space
		name = name.replaceAll(Pattern.quote("_"), " ");//replace _ for space
		//remove double spaces
		name = removeDoubleSpaces(name).trim();
		
		//remove the - at the end if exists
		name = name.replaceAll("\\-$", "").trim();
		
		if(ext != null){
			return name + "." + ext;
		}
		else{
			return name;
		}
	}

	private static final String[] numberSufixPrefixes = {
		"part", "cd", "_"
		};
	/**
	 * This method given a file name will return the number sufix structure.
	 * It is used for finding mult-part media.
	 * Examples:
	 *  - 22.Bullets.2010 - cd2.avi : It will return result[0] = "cd" and result[1] = "2"
	 *  - Certified Copy (2010) - CD1.avi : It will return result[0] = "CD" and result[1] = "1"  
	 *  - Toy Story 3 - Part 1.avi : It will return result[0] = "Part " and result[1] = "1"  
	 *  - Toy Story 3 - Part 2.avi : It will return result[0] = "Part " and result[1] = "2"  
	 *  - transformers.2.part1.avi : It will return result[0] = "part " and result[1] = "1"  
	 *  - transformers.2.part2.avi : It will return result[0] = "part " and result[1] = "2" 
	 * @param fileName
	 * @return
	 */
	public static String[] getMultiPartSufix(String fileName) {
		//try to match for each prefix
		for(String prefix : numberSufixPrefixes){
			prefix = Pattern.quote(prefix);
			String regex = "(.+)((?i:" + prefix + ")[\\s\\.\\-\\:]?)(\\d+)(.+)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(fileName);
			if (matcher.find()) {
				String filename = matcher.group(1);
				String sufix = matcher.group(2);
				String number = matcher.group(3);
				String extension = matcher.group(4);
				return new String[]{filename, sufix, number, extension};
			}
		}
		
		return null;
	}
	
	private static final String fileNameRegex = "(.+)(\\.(\\w\\w\\w))$";
	
	public static String getFileExtension(String fileName) {
		/*
		return fileName.substring(fileName.lastIndexOf('.') + 1).trim()
				.toLowerCase();
				
				*/
		Pattern pattern = Pattern.compile(fileNameRegex);
		Matcher matcher = pattern.matcher(fileName);
		if(matcher.matches()){
			return matcher.group(3).toLowerCase();
		}
		else{
			return null;
			//return fileName;//no extension found
		}
	}

	public static String getFileSimpleName(String fileName) {
		Pattern pattern = Pattern.compile(fileNameRegex);
		Matcher matcher = pattern.matcher(fileName);
		if(matcher.find()){
			return matcher.group(1);
		}
		else{
			return fileName;//no extension found in the filename
		}
	}

	//TODO: Create the Dictionary classes under the text package.. and create a IMDB Dictionary class to manage all IMDB text, url and etc
	public static String getImdbTitleURL(String imdbId) {
		return "http://www.imdb.com/title/" + imdbId;
	}

	public static String getImdbSearchByTitleURL(String title) {
		return "http://www.imdb.com/find?s=all&q=" + buildURLQuery(title) ;
	}

	public static String buildURLQuery(String text) {
		return text.trim().replace(' ', '+');
	}

	/**
	 * Normalize the text 
	 * @param text
	 * @return
	 
	public static String normalizeTextForRegex(String text) {
		//normalize
		text = text.replaceAll("\\(", "\\\\(");
		text = text.replaceAll("\\)", "\\\\)");
		return text;
	}
	*/

}
