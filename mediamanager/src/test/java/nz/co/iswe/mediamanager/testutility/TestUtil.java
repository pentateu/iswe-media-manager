package nz.co.iswe.mediamanager.testutility;

import java.io.File;

public class TestUtil {

	public static void move(File from, File to) {
		if( ! from.exists()){
			return;
		}
		
		boolean moved = from.renameTo(to);
		if(! moved){
			System.out.println("### Test cleanup Error ### Cannot move file: " + from.getPath() + " to: " + to.getPath());
		}
	}
	
	public static void rename(File folder, String fileFrom, String fileTo) {
		File from = new File(folder, fileFrom);
		if( ! from.exists()){
			return;
		}
		
		File to = new File(folder, fileTo);
		
		boolean renamed = from.renameTo(to);
		if(! renamed){
			System.out.println("### Test cleanup Error ### Cannot rename file: " + from.getPath() + " to: " + to.getPath());
		}
	}
	
	public static void delete(File file) {
		if(file.exists()){
			boolean deleted = deleteAll(file);
			if(! deleted){
				System.out.println("### Test cleanup Error ### Cannot delete file/folder : " + file.getPath());
			}
		}
	}
	
	public static boolean deleteAll(File file) {
		if( ! file.exists()){
			return true;
		}
		
		File[] files = file.listFiles();
		if(file.isDirectory() && files.length > 0){
			boolean result = true;
			for(File item : files){
				boolean deleted = deleteAll(item);
				if(! deleted){
					result = false;
				}
			}
			file.delete();
			return result;
		}
		else if(file.isDirectory() && files.length == 0){
			return file.delete();
		}
		else if(file.isFile()){
			return file.delete();
		}
		return false;
	}
	
	public static void delete(File folder, String fileName) {
		File temp = new File(folder, fileName);
		delete(temp);
	}
}
