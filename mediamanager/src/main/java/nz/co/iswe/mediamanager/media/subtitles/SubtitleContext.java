package nz.co.iswe.mediamanager.media.subtitles;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.co.iswe.mediamanager.Util;
import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.folder.MediaFolder;

public class SubtitleContext {
	private static final String IDX = "idx";

	private static final String SUB = "sub";

	private static Logger log = Logger.getLogger(SubtitleContext.class.getName());

	private static SubtitleContext instance;
	
	public static SubtitleContext getInstance(){
		if(instance == null){
			instance = new SubtitleContext();
		}
		return instance;
	}
	
	private SubtitleContext(){}

	public ISubtitle lookUp(MediaDetail mediaFile) throws MediaFileException {
		ISubtitle result = null;
		
		//search for sub/idx files
		result = lookUpSubIdx(mediaFile);
		
		if(result == null){
			//lookup for ...
			
		}
		
		return result;
	}

	private ISubtitle lookUpSubIdx(MediaDetail mediaFile) throws MediaFileException {
		log.finest("lookUp for Sub Idx subtitles for mediaFile: " + mediaFile);
		
		ISubtitle result = null;
		
		try{
			//1 mediaFolder
			MediaFolder mediaFolder = mediaFile.getMediaFolder();
			
			//2 file name pattern
			String namePrefix = mediaFile.getFileName();
			//normalize the name prefix
			namePrefix = Pattern.quote(namePrefix);
			final Pattern pattern = Pattern.compile("(" + namePrefix + ")\\.{1}(sub|idx)");
			
			//3 search for sub and idx files
			File[] subtitleFiles = mediaFolder.getFile().listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					Matcher matcher = pattern.matcher(file.getName());
					boolean mathes = matcher.find();
					
					return file.isFile() && 
						mathes &&
						Util.isValidSubtitleFile(file);
				}
			});
			
			//create a SubIdxSubtitles instance
			if (subtitleFiles.length > 0) {
				SubIdxSubtitles subIdxSubtitles = new SubIdxSubtitles(mediaFolder);
				for(File item: subtitleFiles){
					String extension = Util.getFileExtension(item.getName());
					if(SUB.equals(extension)){
						subIdxSubtitles.setSubFile(item);
					}
					if(IDX.equals(extension)){
						subIdxSubtitles.setIdxFile(item);
					}
				}
				result = subIdxSubtitles;
				mediaFile.addListener(subIdxSubtitles);
			}
		}
		catch (MediaFileException e) {
			log.log(Level.SEVERE, "Error looking up for subtitles for mediafile: " + mediaFile, e);
			throw new MediaFileException("Error looking up for subtitles for mediafile: " + mediaFile, e);
		}
		
		return result;
	}
	
}
