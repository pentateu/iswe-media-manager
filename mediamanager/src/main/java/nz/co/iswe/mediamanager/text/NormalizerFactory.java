package nz.co.iswe.mediamanager.text;

public class NormalizerFactory {

	
	private static NormalizerFactory instance = null;
	
	public static NormalizerFactory getInstance(){
		if(instance == null){
			instance = new NormalizerFactory();
			//perform IOC here
			instance.fileNameNormalizer = new FileNameNormalizer();
		}
		return instance;
	}

	private FileNameNormalizer fileNameNormalizer;
	
	private NormalizerFactory(){
	}
	
	public FileNameNormalizer getFileNameNormalizer() {
		return fileNameNormalizer;
	}
	
}
