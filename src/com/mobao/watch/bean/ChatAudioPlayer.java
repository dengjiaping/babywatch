package com.mobao.watch.bean;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.mb.zjwb1.R;

/**
 * 记录聊天界面正在播放的数据
 * 
 * @author wu-yoline
 * 
 */
public class ChatAudioPlayer {

	private AnimationDrawable rocketAnim;
	private MediaPlayer mediaPlayer;
	private String playingAudioId = "-1"; // 正在播放的语音id
	private ImageView playingIcon; // 正在播放动画的图标
	private boolean playingIsComMsg; // 正在播放的是否是他人的语音
	private boolean isPlaying = false; // 是否正在播放
	private Context context;

	public ChatAudioPlayer(Context context) {
		this.context = context;
		mediaPlayer = new MediaPlayer();
	}

	public void startAmin(String playingAudioId, ImageView playingIcon,
			boolean playingIsComMsg) {

		if (playingIsComMsg) {
			playingIcon
					.setBackgroundResource(R.drawable.chatfragment_audio_icon_changing_others_anim);
		} else {
			playingIcon
					.setBackgroundResource(R.drawable.chatfragment_audio_icon_changing_myself_anim);

		}

		rocketAnim = (AnimationDrawable) playingIcon.getBackground();
		rocketAnim.start();

		this.playingAudioId = playingAudioId;
		this.playingIcon = playingIcon;
		this.playingIsComMsg = playingIsComMsg;

		isPlaying = true;
	}

	public void stopPlay() {
		stopMediaPlayer();
		stopAmin();
		setPlaying(false);
		setPlayingAudioId("-1");
		setPlayingIcon(null);
	}

	private void stopAmin() {
		if (rocketAnim != null && rocketAnim.isRunning()) {
			rocketAnim.stop();
		}
		
		if (playingIcon != null) {
			playingIcon.clearAnimation();

			if (playingIsComMsg) {
				playingIcon
						.setBackgroundResource(R.drawable.chatfragment_others_voice_icon);
			} else {
				playingIcon
						.setBackgroundResource(R.drawable.chatfragment_myself_voice_icon);
			}
		}
	}

	public String getPlayingAudioId() {
		return playingAudioId;
	}

	private void setPlayingAudioId(String playingAudioId) {
		this.playingAudioId = playingAudioId;
	}

	public ImageView getPlayingIcon() {
		return playingIcon;
	}

	private void setPlayingIcon(ImageView playingIcon) {
		this.playingIcon = playingIcon;
	}

	public boolean isPlayingIsComMsg() {
		return playingIsComMsg;
	}

	public void setPlayingIsComMsg(boolean playingIsComMsg) {
		this.playingIsComMsg = playingIsComMsg;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	private void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void playAudio(String path) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setLooping(false);

			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					stopPlay();
				}
			});

			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			Toast toast = Toast.makeText(context, context.getResources()
					.getString(R.string.palyfailer), 3000);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setMargin(0f, 0f);
			toast.show();

			stopAmin(); // 停止播放动画

			e.printStackTrace();
		}

	}

	private void stopMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}

}
