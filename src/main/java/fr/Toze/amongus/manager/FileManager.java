package fr.Toze.amongus.manager;

import java.io.File;

public class FileManager {

	private String path;
	
	public FileManager(File file) {
		this.path = file.getPath()+"/";
	}
	
	public File getFile(String name){
		return new File(path+name+".yml");
	}
	
}
