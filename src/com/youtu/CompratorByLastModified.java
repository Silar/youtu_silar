package com.youtu;

import java.io.File;
import java.util.Comparator;

/**
 * 对比文件创建时间
 * 
 * @author Simple_liu
 * 
 */
public class CompratorByLastModified implements Comparator<File> {
	public int compare(File f1, File f2) {
		long diff = f1.lastModified() - f2.lastModified();
		if (diff > 0)
			return -1;
		else if (diff == 0)
			return 0;
		else
			return 1;
	}

	public boolean equals(Object obj) {
		return true;
	}
}
