package nz.co.iswe.mediamanager.testutility;

import java.io.File;
import java.io.IOException;

public class CreateMediaFolderAndFilesStructure {

	public static void main(String[] args)   {

		String from = "/Volumes/Completed Downloads/error";
		String to = "/Users/pentateu/git/iswe-media-manager/mediamanager/src/test/resources/template-media-folders";

		File fileFrom = new File(from);
		File fileTo = new File(to);

		copyStructure(fileFrom, fileTo);
	}

	private static void copyStructure(File from, File to)   {

		if (!from.exists()) {
			return;
		}
		if (from.isDirectory()) {
			System.out.println("Copying folder: " + from.getPath());
			if (!to.exists()) {
				to.mkdirs();
			}
			//go throught the sub items
			for(File file : from.listFiles()){
				File newTo = new File(to, file.getName());
				copyStructure(file, newTo);
			}
		}
		else if(from.isFile()){
			System.out.println("Copying file: " + from.getPath());
			try {
				to.createNewFile();
			} catch (IOException e) {
				System.out.println("Error trying to create the file: " + to.getPath());
				e.printStackTrace();
			}
		}
	}

}
