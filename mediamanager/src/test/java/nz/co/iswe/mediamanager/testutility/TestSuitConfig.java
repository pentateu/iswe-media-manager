package nz.co.iswe.mediamanager.testutility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;



public class TestSuitConfig {

	private static final String TEST_IMDB_SAMPLES_FOLDER = "test.imdb.samples.folder";
	private static final String TEST_MEDIA_SWING_TEST_FOLDER = "test.media.swing.test.folder";
	private static final String TEST_MEDIA_TEMPLATE_FOLDER = "test.media.template.folder";
	private static final String TEST_MEDIA_JUNIT_FOLDER = "test.media.junit.folder";

	public static Properties getTestProperties(){
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = TestSuitConfig.class.getResourceAsStream("test_suite.properties");
			properties.load(is);
		}
		catch (IOException e) {
			throw new RuntimeException("Error trying to load test_suite.properties", e);
		}
		finally{
			try {
				is.close();
			}
			catch (Throwable e) {/*ignore*/}
		}
		return properties;
	}

	public static File getJUnitMediaFolder() {
		File file = new File(getTestProperties().getProperty(TEST_MEDIA_JUNIT_FOLDER));
		return file;
	}

	public static void createNewJUnitMediaFolder() throws IOException {
		//1 delete the existing folder
		File junitFolder = getJUnitMediaFolder();
		if(junitFolder.exists()){
			TestUtil.deleteAll(junitFolder);
		}
		
		File template = getTemplateMediaFolder();
		
		//2 copy the template folder and all its contents
		FileUtils.copyDirectory(template, junitFolder);
	}

	public static File getTemplateMediaFolder() {
		File file = new File(getTestProperties().getProperty(TEST_MEDIA_TEMPLATE_FOLDER));
		return file;
	}
	
	public static File getSwingTestMediaFolder() {
		File file = new File(getTestProperties().getProperty(TEST_MEDIA_SWING_TEST_FOLDER));
		return file;
	}

	public static void deleteJUnitMediaFolder() {
		File junitFolder = getJUnitMediaFolder();
		if(junitFolder.exists()){
			TestUtil.deleteAll(junitFolder);
		}
	}

	public static void createNewSwingTestMediaFolder() throws IOException {
		//1 delete the existing folder
		File folder = getSwingTestMediaFolder();
		if(folder.exists()){
			TestUtil.deleteAll(folder);
		}
		
		File template = getTemplateMediaFolder();
		
		//2 copy the template folder and all its contents
		FileUtils.copyDirectory(template, folder);
	}

	public static File getIMDBSamplesFolder() {
		File folder = new File(getTestProperties().getProperty(TEST_IMDB_SAMPLES_FOLDER));
		
		if(! folder.exists() || ! folder.isDirectory()){
			throw new RuntimeException("Invalid Property: " + TEST_IMDB_SAMPLES_FOLDER + ".\n Check the test_suite.propeties file!");
		}
		
		return folder;
	}

	
	
}
