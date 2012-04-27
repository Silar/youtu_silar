package com.youtu;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Modify {

	/**
	 * 动态计算恰当的inSampleSize方法.
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public boolean IsSdCard() {
		boolean nIsSaveSdCard = false;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			nIsSaveSdCard = true;
		else
			nIsSaveSdCard = false;
		return nIsSaveSdCard;
	}

	public boolean MakeDirs(String aPath) {
		boolean nIsExist = false;
		if (IsSdCard()) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			String p = sdCardDir.getAbsolutePath() + "/" + aPath;
			Log.i("New_Visit.MakeDirs", p);
			File nPath = new File(p);
			nIsExist = nPath.exists();
			if (!nIsExist)
				nIsExist = nPath.mkdirs();
		} else {
			File nPath = new File("./" + aPath);
			nIsExist = nPath.exists();
			if (!nIsExist)
				nIsExist = nPath.mkdirs();
		}
		return nIsExist;
	}

	public FileOutputStream GetFileOutStream(String aFileName, boolean aIsApp) {
		try {
			if (IsSdCard()) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				File saveFile = new File(sdCardDir, aFileName);
				FileOutputStream outStream = new FileOutputStream(saveFile,
						aIsApp);
				return outStream;
			} else {
				File saveFile = new File("./", aFileName);
				FileOutputStream outStream = new FileOutputStream(saveFile,
						aIsApp);
				return outStream;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 判断文件是否是JPEG文件.
	 * 
	 * @param stri
	 * @return
	 */
	protected boolean isJPEG(String stri) {

		if (stri.endsWith(".JPEG") || stri.endsWith(".jpg")
				|| stri.endsWith(".png") || stri.endsWith(".bmp"))
			return true;

		else
			return false;

	}

}
