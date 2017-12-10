package com.mobao.watch.util;

import java.io.File;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.widget.LinearLayout;

public class AudioRecorder {
	private static int SAMPLE_RATE_IN_HZ = 8000;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

	final MediaRecorder recorder = new MediaRecorder();
	final String path;

	public AudioRecorder(String audioFileName, boolean isComMsg, String userId) {
		this.path = ChatUtil.getAudioAbsolutePath(audioFileName, isComMsg,
				userId);
	}

	public void start() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state
					+ ".");
		}
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);

		// recorder.setAudioSamplingRate();
		// AudioTrack.getMinBufferSize(SAMPLE_RATE_IN_HZ, channelConfig,
		// audioFormat);

		recorder.setOutputFile(path);
		try {
			recorder.prepare();
			recorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() throws IOException {
		try {
			recorder.stop();
			recorder.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getAmplitude() {
		try {
			if (recorder != null) {
				return (recorder.getMaxAmplitude());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return 0;
	}

}