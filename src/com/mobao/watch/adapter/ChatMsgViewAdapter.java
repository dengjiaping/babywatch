package com.mobao.watch.adapter;

import java.io.File;
import java.text.ParseException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.BabyFragmentActivity;
import com.mobao.watch.bean.ChatAudioEntity;
import com.mobao.watch.bean.ChatAudioPlayer;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.myInterface.AudioPlayInterface;
import com.mobao.watch.util.ChatGetAudioThread;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.ToastUtil;

/**
 * 消息ListView的Adapter
 * 
 * @author yoline
 */
public class ChatMsgViewAdapter extends BaseAdapter implements
		AudioPlayInterface {

	private ChatAudioPlayer player;

	private List<ChatAudioEntity> coll;// 消息对象数组,此数组按时间倒叙的顺序排列的（即最新的index为0）
	private LayoutInflater mInflater;
	private Context context;

	public ChatMsgViewAdapter(Context context, List<ChatAudioEntity> coll) {
		this.coll = coll;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		player = new ChatAudioPlayer(context);
		analyzeData();
	}

	public void analyzeData() {
		if (coll.size() <= 1) {
			return;
		}
		long lastShowTime = ChatUtil.getLongTime(coll.get(coll.size() - 1)
				.getDate());
		coll.get(coll.size() - 1).setShowTime(true);

		for (int i = coll.size() - 2; i >= 0; i--) {
			long fiveMinute = 5 * 60 * 1000;
			long time = ChatUtil.getLongTime(coll.get(i).getDate());
			if ((time - lastShowTime) < fiveMinute) {
				coll.get(i).setShowTime(false);
			} else {
				coll.get(i).setShowTime(true);
				lastShowTime = time;
			}
		}

	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get((coll.size() - 1) - position);
	}

	public long getItemId(int position) {
		return (coll.size() - 1) - position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// coll此数组按时间倒叙的顺序排列的（即最新的index为0）
		ChatAudioEntity entity = coll.get((coll.size() - 1) - position);

		boolean isComMsg = entity.isComMsg();

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = initConverView(isComMsg, convertView, entity);
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			if (viewHolder.isComMsg != isComMsg) {
				convertView = initConverView(isComMsg, convertView, entity);
				viewHolder = (ViewHolder) convertView.getTag();
			}
		}

		// 判断是否为正在播放的语音
		if (player.isPlaying()
				&& entity.getAudioId().equals(player.getPlayingAudioId())) {
			player.startAmin(entity.getAudioId(), viewHolder.ivAudioIcon,
					entity.isComMsg());
		} else {
			if (entity.isComMsg()) {
				viewHolder.ivAudioIcon
						.setBackgroundResource(R.drawable.chatfragment_others_voice_icon);
			} else {
				viewHolder.ivAudioIcon
						.setBackgroundResource(R.drawable.chatfragment_myself_voice_icon);
			}
		}

		// 设置是否发送成功
		Log.w("sendState", "id = " + entity.getAudioId() + "的sendState = "
				+ entity.getSendState());

		if (entity.getSendState() == ChatAudioEntity.SEND_FAIL) {
			viewHolder.ivSendFailIcon.setVisibility(View.VISIBLE);
		} else {
			viewHolder.ivSendFailIcon.setVisibility(View.GONE);
		}

		// 看看是否显示时间
		try {
			if (!entity.isShowTime()) {
				viewHolder.tvSendTime.setVisibility(8);
			} else {
				viewHolder.tvSendTime.setText(ChatUtil
						.changeDateShowFormat(entity.getDate()));
				viewHolder.tvSendTime.setVisibility(0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 设置头像
		if (isComMsg) {
			Bitmap avatar = ChatUtil.getAvatar(context, entity.getUserId(),
					entity.getUserType());

			if (avatar != null) {
				viewHolder.ivUserAvatar.setImageBitmap(avatar); // 设置头像
			}
		}

		// 计算聊天泡的长度
		int audioDuration = (int) (entity.getDuration());
		audioDuration = audioDuration > 20 ? 20 : audioDuration;
		int per = getPerDuration();
		int Width = per * audioDuration;

		// 设置音频秒数
		viewHolder.tvDuration.setText(audioDuration + "\"");

		// 设置audio聊天内容框的宽度
		ImageView ivChatcontentMiddle = viewHolder.ivChatcontentMiddle;
		LayoutParams lp = ivChatcontentMiddle.getLayoutParams();
		lp.width = Width;
		ivChatcontentMiddle.setLayoutParams(lp);

		final ViewHolder finalViewHolder = viewHolder;
		final ChatAudioEntity audio = entity;

		// 设置点击语音泡播放事件
		if (isComMsg) {
			viewHolder.rlChatBubble.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					BubbleOnClick(finalViewHolder, audio);
				}
			});
		} else {
			viewHolder.llChatBubble.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					BubbleOnClick(finalViewHolder, audio);
				}
			});
		}
		return convertView;
	}

	private int getPerDuration() {
		int dimen1dp = context.getResources().getDimensionPixelSize(
				R.dimen.dimen_1dp);

		return (BabyFragmentActivity.metricWidth - (220 * dimen1dp))
				/ ChatFragment.MAX_TIME;
	}

	public void BubbleOnClick(ViewHolder finalViewHolder, ChatAudioEntity audio) {

		String playingAudioId = player.getPlayingAudioId();

		// 停止播放语音和动画
		player.stopPlay();

		// 如果点击的是正在播放的语音就直接return，不语音
		if (audio.getAudioId().equals(playingAudioId)) {
			return;
		}

		// 获取语音路径
		String path = ChatUtil.getAudioAbsolutePath(audio.getAudioId(),
				audio.isComMsg(), audio.getUserId());

		boolean isExist = (new File(path)).exists(); // 检查语音是否存在于本地

		if (isExist == false) {
			// 语音不存在于本地

			// 检查sd卡存储状态
			int res = ChatUtil.CheckSdCardForAudio(context);

			if (res == ChatUtil.SD_CARD_ENOUGH) { // sd卡容量充足
				// 播放动画
				player.startAmin(audio.getAudioId(),
						finalViewHolder.ivAudioIcon, audio.isComMsg());

				// 获取新语音
				ChatGetAudioThread getAudio = new ChatGetAudioThread(context,
						audio.getAudioId(), path, audio.isComMsg(),
						audio.getUserId(), this);
				getAudio.start();

			} else { // sd卡容量不足或不可用
				ShowCantGetNewAudioToast(res);
			}

		} else {
			// 播放动画
			player.startAmin(audio.getAudioId(), finalViewHolder.ivAudioIcon,
					audio.isComMsg());
			// 语音存在于本地就播放录音
			player.playAudio(path);
		}

	}

	public void ShowCantGetNewAudioToast(int res) {
		if (res == ChatUtil.SD_CARD_CAN_T_USER) { // sd卡不可用
			ToastUtil.show(
					context,
					context.getResources().getString(
							R.string.sd_card_can_t_user_can_t_get_audio));
		} else if (res == ChatUtil.SD_CARD_FULL) { // sd卡容量不足
			ToastUtil.show(
					context,
					context.getResources().getString(
							R.string.sd_card_full_can_t_get_audio));
		}
	}

	private View initConverView(boolean isComMsg, View convertView,
			ChatAudioEntity audio) {
		if (isComMsg) {
			convertView = mInflater.inflate(
					R.layout.chatfragment_item_audio_background_left, null);
		} else {
			convertView = mInflater.inflate(
					R.layout.chatfragment_item_audio_background_right, null);
		}

		ViewHolder viewHolder = new ViewHolder();

		if (isComMsg) {
			viewHolder.ivUserAvatar = (ImageView) convertView
					.findViewById(R.id.chatfragment_iv_user_avatar);
			viewHolder.rlChatBubble = (RelativeLayout) convertView
					.findViewById(R.id.chatfragment_rl_chat_bubble);
		} else {
			viewHolder.llChatBubble = (LinearLayout) convertView
					.findViewById(R.id.chatfragment_ll_chat_bubble);
		}

		viewHolder.tvSendTime = (TextView) convertView
				.findViewById(R.id.chatfragment_tv_sendtime);
		viewHolder.ivUserAvatar = (ImageView) convertView
				.findViewById(R.id.chatfragment_iv_user_avatar);
		viewHolder.tvDuration = (TextView) convertView
				.findViewById(R.id.chatfragment_iv_void_duration);
		viewHolder.ivChatcontentMiddle = (ImageView) convertView
				.findViewById(R.id.chatfragment_iv_chatcontent_middle);
		viewHolder.ivAudioIcon = (ImageView) convertView
				.findViewById(R.id.chatfragment_iv_audio_icon);
		viewHolder.ivSendFailIcon = (ImageView) convertView
				.findViewById(R.id.iv_chat_item_send_status);

		viewHolder.isComMsg = isComMsg;

		convertView.setTag(viewHolder);
		return convertView;
	}

	public ChatAudioPlayer getPlayer() {
		return player;
	}

	static class ViewHolder {
		public TextView tvSendTime; // 要显示的发送或收到的时间
		public ImageView ivUserAvatar; // 发语音过来的用户的头像
		public TextView tvDuration; // 显示语音播放时长
		public ImageView ivChatcontentMiddle; // 决定语音泡长度的控件
		public RelativeLayout rlChatBubble; // 整个收到的语音泡
		public LinearLayout llChatBubble; // 整个发出的语音泡
		public boolean isComMsg = true; // 语音类型，false为发出去的语音，true为收到的语音
		public ImageView ivAudioIcon; // 播放图标
		public ImageView ivSendFailIcon; // 发送不成功的状态图标
	}

	@Override
	public void playAudio(String path) {
		player.playAudio(path);
	}

}
