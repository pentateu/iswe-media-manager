package nz.co.iswe.mediamanager.text;

public class FileNameNormalizer {

	//TODO: Put this info inside a database so the system can learn overtime and not be constrained to these values
	
	//Patterns used to clean-up the file names by removing 
	//all the text sequences that does not make up the name of the movie
	private String [] cleanUpFileNamePatterns = {
			"OneDDL[\\.]com[-|_|.]",
			"1-3-3-8[\\.]com[-|_|.]",
			"720p",
			"BluRay",
			"x264",
			"NODLABS",
			"'"};
	
	//Patterns used to clean-up the file names by removing non desirable chars
	private char [] replaceWithSpaceFileNamePatterns = {
			'-',
			'.',
			'_'};
	
	public String cleanUp(String fileName) {
		//remove the text sequences
		for(String pattern: cleanUpFileNamePatterns){
			fileName = fileName.replaceAll(pattern, "");
		}
		return fileName;
	}
	
	
	public String normalize(String fileName) {
		//remove the text sequences
		fileName = cleanUp(fileName);
		
		fileName = replaceWithSpace(fileName);
		return fileName;
	}
	
	public String replaceWithSpace(String fileName) {
		//remove the individual characters with space
		for(char charItem: replaceWithSpaceFileNamePatterns){
			fileName = fileName.replace(charItem, ' ');
		}
		return fileName;
	}
	
}
