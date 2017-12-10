package com.mobao.watch.util;

import android.os.Environment;
import android.os.StatFs;

public class SDcardChecker {
	
	// 
	/** 检查SD卡状态是否可用
	 * @return
	 */
	public boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/** 获取SD卡路径
	 * @return 如果sd卡不可用时返回null
	 */
	public String getExternalStoragePath() {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();

		// 判断SdCard是否存在并且是可用的

		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {

			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {

				return android.os.Environment.getExternalStorageDirectory()
						.getPath();

			}

		}

		return null;

	}

	/**
	 * 　　* 获取存储卡的剩余容量，单位为字节
	 * 
	 * 　　* @param filePath
	 * 
	 * 　　* @return availableSpare
	 * 
	 * 　　
	 */

	public long getAvailableStore(String filePath) {

		// 取得sdcard文件路径

		StatFs statFs = new StatFs(filePath);

		// 获取block的SIZE

		long blocSize = statFs.getBlockSize();

		// 获取BLOCK数量

		// long totalBlocks = statFs.getBlockCount();

		// 可使用的Block的数量

		long availaBlock = statFs.getAvailableBlocks();

		// long total = totalBlocks * blocSize;

		long availableSpare = availaBlock * blocSize;

		return availableSpare;

	}

}
