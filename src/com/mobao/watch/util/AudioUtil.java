package com.mobao.watch.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class AudioUtil {
	
	/**
	 * 获取对应音频名的文件完整路径
	 * 
	 * @param audioFileName
	 *            音频文
	 * @return 对应音频名的文件完整路径
	 */
	public static String getAudioFilePath(String audioFileName) {
		if (!audioFileName.startsWith("/")) {
			audioFileName = "/" + audioFileName;
		}
		if (!audioFileName.contains(".")) {
			audioFileName += ".amr";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/my" + audioFileName;
	}

	/**
	 * 删除对应音频名的文件完整路径
	 * 
	 * @param audioFileName
	 *            音频文
	 */
	public static void deleteAudioFile(String audioFileName) {
		File file = new File(Environment.getExternalStorageDirectory(), "my/"
				+ audioFileName + ".amr");
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 发送语音时，获取当前事件时间
	 * 
	 * @return 当前时间
	 */
	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	/** 把时间格式，由yyyy-MM-dd HH:mm:ss变为显示的yyyy年MM月dd号   h:mm a
	 * @param dateStr 要转化的时间字符串
	 * @return 返回新格式的时间字符串
	 * @throws ParseException
	 */
	public static String changeDateShowFormat(String dateStr) throws ParseException{
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
		return new SimpleDateFormat("yyyy年MM月dd号   h:mm a").format(date);	}
	
	/**
	 * 将字符串转为时间戳
	 * 
	 * @param user_time
	 * @return 时间戳
	 */
	public static Long getLongTime(String user_time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		long l = 0;
		try {
			
			d = sdf.parse(user_time);
			l = d.getTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * 获取录音时长，单位秒
	 * 
	 * @param audioFileName 录音 amr音频文件
	 * @return 返回如果文件存在返回音频时长，不存在返回-1
	 * @throws IOException
	 */
	public static long getAudioDuration(File file) {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
				0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();// 文件的长度
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;
			// ///////////////////////////////////////////////////
			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			// ///////////////////////////////////////////////////
			duration += frameCount * 20;// 帧数*20
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return duration / 1000;
	}
	
}
