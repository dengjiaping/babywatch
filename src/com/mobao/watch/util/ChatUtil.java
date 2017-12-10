package com.mobao.watch.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BabyFragmentActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

public class ChatUtil {

	public static final String baseUrl = CommonUtil.baseUrl;

	private static final int MAX_AUDIO_COUNT = 500;

	public static final int SD_CARD_CAN_T_USER = 456;
	public static final int SD_CARD_FULL = 455;
	public static final int SD_CARD_ENOUGH = 454;
	
	private static MyImageCache imageCache = new MyImageCache();

	private static String audioDirPath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/zjwb/audio/";
	
	// private static String avatarDirPath = Environment
	// .getExternalStorageDirectory().getAbsolutePath() + "/zjwb/avatar/";

	/**
	 * 获取对应音频名的文件完整路径
	 * 
	 * @param audioFileName
	 *            音频文
	 * @return 对应音频名的文件完整路径
	 */
	public static String getAudioAbsolutePath(String audioFileName,
			boolean isComMsg, String userId) {
		if (!audioFileName.contains(".")) {
			audioFileName += ".amr";
		}

		String path = audioDirPath + userId + "/";

		// 确保文件夹存在
		File pathDir = new File(path);
		if (!pathDir.exists()) {
			pathDir.mkdirs();
		}

		return path + audioFileName;
	}

	// /**
	// * 获取对应头像名的文件完整路径
	// *
	// * @param avatarFileName
	// * 头像文件名
	// * @return 对应头像名的文件完整路径
	// */
	// public static String getAvatarAbsolutePath(String avatarFileName) {
	// if (!avatarFileName.contains(".")) {
	// avatarFileName += ".png";
	// }
	//
	// // 确保文件夹存在
	// File pathDir = new File(avatarDirPath);
	// if (!pathDir.exists()) {
	// pathDir.mkdirs();
	// }
	//
	// return avatarDirPath + avatarFileName;
	// }

	// /**
	// * 加载本地图片
	// *
	// * @param avatarFileName
	// * @return
	// */
	// public static Bitmap getLoacalBitmap(String avatarFileName) {
	//
	// try {
	// String path = getAvatarAbsolutePath(avatarFileName);
	// FileInputStream fis = new FileInputStream(path);
	// return BitmapFactory.decodeStream(fis);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// return null;
	//
	// }
	//
	// }

	/**
	 * 删除对应音频名的文件完整路径
	 * 
	 * @param audioFileName
	 *            音频文
	 */
	public static void deleteAudioFile(String audioFileName, boolean isComMsg,
			String userId) {
		File file = new File(getAudioAbsolutePath(audioFileName, isComMsg,
				userId));
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 发送语音时，获取当前事件时间,
	 * 
	 * @return 当前时间 格式为yyyy-MM-dd HH:mm:ss
	 */
	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	/**
	 * 检查音频是否超过指定文件个数MAX_AUDIO_COUNT
	 * 
	 * @param isDelete
	 *            false时，会检查是否超过MAX_AUDIO_COUNT，如果超过就把全部删除 true时，
	 * @throws ParseException
	 */
	public static void checkAudiosFile(boolean isDelete) throws ParseException {
		String audioDirPath = ChatUtil.getAudioDirPath();
		File audioDir = new File(audioDirPath);

		if (!audioDir.exists()) {
			return;
		}

		File[] list = audioDir.listFiles();
		int numFile = 0;
		if (list != null && list.length > 0) {
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					File[] files = list[i].listFiles();
					if (files != null && files.length > 0) {
						numFile = numFile + files.length;
						if (isDelete) {
							for (int j = 0; j < files.length; j++) {
								Log.w("yyy", "delete:" + files[j]);
								files[j].delete();
							}
						}
					}
				}
			}
		}

		if (isDelete) {
			return;
		}

		if (numFile > MAX_AUDIO_COUNT) {
			checkAudiosFile(true);
		}

	}

	/**
	 * 把时间格式，由yyyy-MM-dd HH:mm:ss变为显示的yyyy-MM-dd aa h:mm
	 * 
	 * @param dateStr
	 *            要转化的时间字符串
	 * @return 返回新格式的时间字符串
	 * @throws ParseException
	 */
	public static String changeDateShowFormat(String dateStr)
			throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
		String res = new SimpleDateFormat("yyyy-MM-dd aa h:mm").format(date);
		if (res.substring(12, 14).equals("上午")
				&& res.substring(15, 17).equals("12")) {
			res = res.substring(0, 15) + "0" + res.substring(17, res.length());
		}
		return res;
	}

	/**
	 * 通过time建立语音文件名
	 *
	 * @param id
	 * @param time
	 *            该时间参数格式必须为yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static String buildAudioId(String time) throws ParseException {
		return changeDateToSaveFormat(time);
	}

	/**
	 * 把时间格式由由yyyy-MM-dd HH:mm:ss变为显示的yyyyMMddHHmmss
	 * 
	 * @param time
	 *            时间
	 * @return
	 * @throws ParseException
	 */
	public static String changeDateToSaveFormat(String time)
			throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}

	/**
	 * 将时间个时为“yyyy-MM-dd HH:mm:ss”的时间字符串转为时间戳
	 * 
	 * @param user_time
	 * @return 时间戳
	 */
	public static long getLongTime(String user_time) {
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
	 * @param audioFileName
	 *            录音 amr音频文件
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

	// /**
	// * 删除对应用户id头像名的文件完整路径
	// *
	// * @param id 头像文件
	// */
	// public static void deleteAvatarFile(String id) {
	// File file = new File(getAvatarAbsolutePath(id));
	// if (file.exists()) {
	// file.delete();
	// }
	// }

	/**
	 * 获取对应id用户的像
	 * 
	 * @param userId
	 * @return
	 */
	public static Bitmap getAvatar(Context context, String userId,
			UserType userType) {
		Bitmap bitmap = getImageCache().getBitmap(userId);
		
		if (bitmap == null) {
			Log.w("getAvatar", userId + "的头像为空，准备开启线程");
			GetUserAvatarThread thread = new GetUserAvatarThread(context,
					userId, userType);
			thread.start();
		}
		
		Log.w("getAvatar", userId + "的头像已存在于缓存里");
		
		return bitmap;
	}

	public static void saveAudio(String audioStr, String audioId,
			boolean isComMsg, String userId) throws IOException, ParseException {
		byte[] audioArray = Base64.decode(audioStr, Base64.DEFAULT);

		String audioPath = getAudioAbsolutePath(audioId, isComMsg, userId); // 使用语音id来命名

		File audioFile = new File(audioPath);

		if (!audioFile.exists()) { // 文件已存在，不做保存操作，直接return
			audioFile.createNewFile();
		} else {
			return;
		}

		FileOutputStream fileOutStream = new FileOutputStream(audioFile);
		fileOutStream.write(audioArray);
		fileOutStream.close();

	}

	// public static void saveAvatar(String avatarStr, String babyId)
	// throws IOException, ParseException {
	// byte[] avatarArray = Base64.decode(avatarStr, Base64.DEFAULT);
	//
	// // byte[] avatarArray = avatarStr.getBytes();
	//
	// String avatarPath = ChatUtil.getAvatarAbsolutePath(babyId); //
	// 使用用户id命名头像名
	//
	// File avatarFile = new File(avatarPath);
	//
	// if (avatarFile.exists()) { // 如果该用户的头像已经存在，先把头像删除，再创建
	// ChatUtil.deleteAvatarFile(babyId);
	// avatarFile.createNewFile();
	// } else {
	// avatarFile.createNewFile();
	// }
	//
	// FileOutputStream fileOutStream = new FileOutputStream(avatarFile);
	// fileOutStream.write(avatarArray);
	// fileOutStream.close();
	//
	// }

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 比较两个字符串形式的数字的大小
	 * 
	 * @param strNum1
	 *            都为数字的字符串
	 * @param strNum2
	 *            都为数字的字符串
	 * @return 如果num1大于或等于num2时返回true,否则返回false
	 */
	public static boolean CompareStrNum(String strNum1, String strNum2) {
		if (strNum1.length() > strNum2.length()) {
			return true;
		} else if (strNum1.length() < strNum2.length()) {
			return false;
		}

		// 计算要比较的次数
		int compareTime = strNum1.length() / 4;
		if (strNum1.length() % 4 > 0) {
			compareTime++;
		}

		for (int i = 0; i < compareTime; i++) {

			int num1 = 0, num2 = 0;
			if (i == compareTime - 1) {
				num1 = Integer.parseInt(strNum1.substring(i * 4,
						strNum1.length()));
				num2 = Integer.parseInt(strNum2.substring(i * 4,
						strNum1.length()));
			} else {
				num1 = Integer.parseInt(strNum1.substring(i * 4, i * 4 + 4));
				num2 = Integer.parseInt(strNum2.substring(i * 4, i * 4 + 4));
			}

			if (num1 > num2) {
				return true;
			} else if (num1 < num2) {
				return false;
			}
		}

		return true;

	}

	/**
	 * 休眠指定时间alreadyRunTime到现在还没超过3秒的时间，如果已经超过3秒就不休眠
	 * 
	 * @param startTime
	 */
	public static void sleep1s(long startTime) {
		long now = System.currentTimeMillis();
		long interval = now - startTime;
		if (interval < 1 * 1000) {
			try {
				Thread.sleep(1 * 1000 - interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static String getAudioDirPath() {
		File dir = new File(audioDirPath);
		dir.mkdirs();
		return audioDirPath;
	}

	public static void setAudioDirPath(String audioDirPath) {
		ChatUtil.audioDirPath = audioDirPath;
	}

	public static MyImageCache getImageCache() {
		if(imageCache == null){
			imageCache = new MyImageCache();
		}
		
		return imageCache;
	}

	public static void removeNotify(Context context, int notifyId) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notifyId);
	}

	/** 检测sd卡剩余容量
	 * @param canNotUserTip sd卡不可用时toast提示的内容
	 * @param fullTip sd卡容量不足1兆时toast提示的内容
	 * @param context 
	 * @return 不可以和容量不足时返回false
	 */
	public static int CheckSdCardForAudio(Context context) {
		SDcardChecker checker = new SDcardChecker();

		// 检测sd卡是否可用
		boolean checkSDCard = checker.checkSDCard();
		if (checkSDCard == false) {
			return SD_CARD_CAN_T_USER;
		}

		long availableStore = checker.getAvailableStore(ChatUtil
				.getAudioDirPath());
		Log.w("sdcard", "sd卡剩余容量:" + availableStore);
		long minStore = 1 * 1024 * 1024; // 1兆
		if (availableStore <= minStore) {
			File audioDirPath = new File(ChatUtil.getAudioDirPath());
			CommonUtil.DeleteFile(audioDirPath, false);
			availableStore = checker.getAvailableStore(ChatUtil
					.getAudioDirPath()); // 删除语音后在此检测sd卡容量
			if (availableStore <= minStore) {
				return SD_CARD_FULL;
			}
		}
		return SD_CARD_ENOUGH;
	}

	// public static String getAvatarDirPath() {
	// File dir = new File(avatarDirPath);
	// dir.mkdirs();
	// return avatarDirPath;
	// }
	//
	// public static void setAvatarDirPath(String avatarDirPath) {
	// ChatUtil.avatarDirPath = avatarDirPath;
	// }
	//
}
