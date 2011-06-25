package nz.co.iswe.mediamanager.media.nfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nz.co.iswe.mediamanager.media.file.MediaDetail;
import nz.co.iswe.mediamanager.media.file.MediaFileException;
import nz.co.iswe.mediamanager.media.nfo.xml.movie.Movie;
import nz.co.iswe.mediamanager.media.nfo.xml.movie.ObjectFactory;

public class MediaNFOFactory {

	private static MediaNFOFactory instance;

	public static MediaNFOFactory getInstance() {
		if (instance == null) {
			instance = new MediaNFOFactory();
		}
		return instance;
	}

	public IMediaNFO loadFromFile(File file) throws MediaFileException {
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}

		// ## Check the type of file

		// 1 try to parse to a movie XML file
		Movie movie = parseToMovie(file);
		if (movie != null) {
			MovieFileNFO movieFileNFO = new MovieFileNFO(file, movie);
			return movieFileNFO;
		}

		// 2 : Try tv-show

		// ..

		return null;
	}

	private Movie parseToMovie(File file) {
		JAXBContext jaxbContext;
		InputStream input = null;
		try {
			jaxbContext = JAXBContext.newInstance(Movie.class.getPackage().getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			input = new FileInputStream(file);
			Movie movie = (Movie) unmarshaller.unmarshal(input);
			return movie;
		} catch (JAXBException e) {/* ignore exception -> do nothing */
		} catch (FileNotFoundException e) {/* ignore exception -> do nothing */
		} catch (ClassCastException e) {/* ignore exception -> do nothing */
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) { /* ignore exception -> do nothing */
				}
			}
		}
		return null;
	}

	public IMediaNFO createMovieNFO(MediaDetail mediaFileDefinition) throws MediaFileException {
		ObjectFactory objectFactory = new ObjectFactory();
		Movie movie = objectFactory.createMovie();
		return new MovieFileNFO(mediaFileDefinition, movie);
	}
	
	public IMediaNFO createTVShowNFO(MediaDetail mediaFileDefinition) throws MediaFileException {
		ObjectFactory objectFactory = new ObjectFactory();
		Movie movie = objectFactory.createMovie();
		return new TVShowFileNFO(mediaFileDefinition, movie);
	}
	
	public IMediaNFO createDocumentaryNFO(MediaDetail mediaFileDefinition) throws MediaFileException {
		ObjectFactory objectFactory = new ObjectFactory();
		Movie movie = objectFactory.createMovie();
		return new DocumentaryFileNFO(mediaFileDefinition, movie);
	}

	public void saveToXMLFile(MovieFileNFO movieFileNFO, File file) {
		// Serialize the object to a xml file
		FileOutputStream fis = null;
		try {
			//delete previous file is already exists
			if(file.exists()){
				file.delete();
			}
			
			JAXBContext context = JAXBContext.newInstance(Movie.class.getPackage().getName());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			fis = new FileOutputStream(file);
			marshaller.marshal(movieFileNFO.getMovie(), fis);
			
		} catch (JAXBException e) {
			throw new RuntimeException("Error saving movie NFO xml", e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error saving movie NFO xml", e);
		}
		finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {/*ignore*/}
			}
		}

	}

}
