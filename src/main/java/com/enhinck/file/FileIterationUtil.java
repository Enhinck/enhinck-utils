package com.enhinck.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileIterationUtil {
	private Set<String> filelist;

	public Set<String> getFilelist() {
		return filelist;
	}

	public void setFilelist(Set<String> filelist) {
		this.filelist = filelist;
	}

	private void init() {
		filelist = new HashSet<String>();
	}

	public static Set<String> getChildrenFiles(final String filePath) {
		FileIterationUtil fileUtils = new FileIterationUtil();
		fileUtils.init();
		fileUtils.getFiles(filePath);
		return fileUtils.getFilelist();
	}
	
	public static Set<String> getChildrenFiles(final String filePath,final String... ext) {
		FileIterationUtil fileUtils = new FileIterationUtil();
		fileUtils.init();
		fileUtils.getFiles(filePath,ext);
		return fileUtils.getFilelist();
	}
	
	private void getFiles(final String filePath,final String... exts) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				/*
				 * 递归调用
				 */
				getFiles(file.getAbsolutePath(),exts);
				// filelist.add(file.getAbsolutePath());
				// System.out.println("显示"+filePath+"下所有子目录及其文件"+file.getAbsolutePath());
			} else {
				// System.out.println("显示"+filePath+"下所有子目录"+file.getAbsolutePath());
				for (String ext : exts) {
					if (file.getName().endsWith(ext)) {
						filelist.add(file.getAbsolutePath());
					}
				}
			}
		}
	}
}
