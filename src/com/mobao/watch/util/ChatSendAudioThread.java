package com.mobao.watch.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.activity.LoginActivity;
import com.mobao.watch.activity.MbApplication;
import com.mobao.watch.bean.ChatAudioEntity;
import com.mobao.watch.bean.GlobalData;
import com.mobao.watch.bean.NowUser;
import com.mobao.watch.fragment.ChatFragment;

/**
 * 聊天的发送语音线程
 * 
 * @author 幽灵登录者
 *
 */
public class ChatSendAudioThread extends Thread {
	private static String baseUrl = CommonUtil.baseUrl;
	private static final String SEND_MESSAGE = "sendmessage";
	private static final int MSG_ARG1_CODE_SEND_SUCCESS = 4541;
	private static final int MSG_ARG1_CODE_SEND_FAIL = 4330;

	private Context context;
	private ChatAudioEntity audio;
	private boolean sendResult = false; // 记录发送信息结果，发送成功后result为true，否则为false,默认为false

	/**
	 * 聊天的发送语音线程
	 * 
	 * @param context
	 *            开启线程的上下文
	 * @param audio
	 *            要发送的语音
	 */
	public ChatSendAudioThread(Context context, ChatAudioEntity audio) {
		this.context = context;
		this.audio = audio;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Log.w("ppp", "handler ： " + (String) msg.obj);

			String showStr = (String) msg.obj;
			Toast toast = Toast.makeText(context, showStr, 3000);
			toast.setGravity(Gravity.CENTER, 0, -10);
			toast.show();
			
			ChatFragment.updateListData(audio);
			
			if (msg.what == MSG_ARG1_CODE_SEND_SUCCESS) {
				/* 发送成功后要做什么 */
				WaitDialog.getIntence(context).dismiss();
				ChatFragment.updateListView();

			} else if (msg.what == MSG_ARG1_CODE_SEND_FAIL) {
				/* 发送失败后要什么 把录音文件删除 */
				audio.setSendState(ChatAudioEntity.SEND_FAIL);
			}
			
		};
	};

	protected String ChangeAudioFileToString(String audioFileName2,
			boolean isComMsg, String userId) throws IOException {

		// 建立流，获取audio文件
		FileInputStream filein = null;
		File audioFile = new File(ChatUtil.getAudioAbsolutePath(audioFileName2,
				isComMsg, userId));
		filein = new FileInputStream(audioFile);
		byte[] buffer = new byte[(int) audioFile.length()];
		filein.read(buffer);
		filein.close();
		String audioStr = Base64.encodeToString(buffer, Base64.DEFAULT);
		return audioStr;
	}

	@Override
	public void run() {
		
		// 设置url
				String ip = LoginActivity.dnspodIp;
				if (ip != null) {
					baseUrl = "http://" + ip + ":8088/api/";
				}
		
		HttpPost post = new HttpPost(baseUrl.concat(SEND_MESSAGE));

		try {
			// 封装数据
			JSONObject params = new JSONObject();
			params.put("userid", MbApplication.getGlobalData().getNowuser().getUserid());
			params.put("imei", MbApplication.getGlobalData().getNowuser().getImei());
			params.put(
					"content",
					ChangeAudioFileToString(audio.getAudioId(),
							audio.isComMsg(), audio.getUserId()));
			params.put("audioid", audio.getAudioId());
			params.put("audiolen", audio.getDuration());
			
			// 绑定到请求 Entry
			post.setEntity(new StringEntity(params.toString()));

			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient().execute(post);

			// 判断服务器是否连接成功
			int res = httpResponse.getStatusLine().getStatusCode();

			if (res == 200) { // 链接成功
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);

				// 判断发送是否成功
				int status = result.getInt("status");
				if (status == 200) { // 发送成功
					Log.w("sendaudio", "id = " + audio.getAudioId() + "发送成功");
					Message msg = Message.obtain();
					msg.obj = context.getResources().getString(
							R.string.send_success);
					msg.what = MSG_ARG1_CODE_SEND_SUCCESS;
					sendResult = true;
					handler.sendMessage(msg);

				} else {
					Message msg = Message.obtain();
					msg.obj = context.getResources().getString(
							R.string.internet_exception);
					msg.what = MSG_ARG1_CODE_SEND_FAIL;
					sendResult = false;
					handler.sendMessage(msg);
				}

			} else { // 链接失败
				Message msg = Message.obtain();
				msg.obj = context.getResources().getString(
						R.string.internet_exception);
				msg.what = MSG_ARG1_CODE_SEND_FAIL;
				sendResult = false;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
		}

	}

	public boolean getResult() {
		return sendResult;
	}
}
