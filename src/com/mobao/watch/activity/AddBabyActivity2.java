package com.mobao.watch.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.zjwb1.R;
import com.mobao.watch.bean.AddBaby;
import com.mobao.watch.bean.AddBabyResult;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.fragment.MotionFragment;
import com.mobao.watch.fragment.RewardFragment;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.Base64Coder;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.DateTimePickDialogUtil;
import com.mobao.watch.util.DialogUtil;
import com.mobao.watch.util.SPUtil;
import com.mobao.watch.util.SubmitBabyPartInfoThread;
import com.mobao.watch.util.ToastUtil;

public class AddBabyActivity2 extends Activity {

	private RelativeLayout rel_Finish;// 完成
	private TextView textFinish;
	private RelativeLayout rel_back;// 返回
	private ImageButton btnBack;
	private TextView hit_ignore;// 跳过步骤
	private RelativeLayout rel_select_date;// 选择日期
	private TextView text_babybirthtime;// 宝贝出生日期
	private String babybirthtime = null;// 宝贝出生日期
	private RelativeLayout rel_selectsex;// 宝贝性别
	private EditText edittext_age;// 宝贝年龄
	private EditText edittext_height;// 宝贝身高
	private EditText edittext_weight;// 宝贝体重
	private EditText edittext_steplong;// 步长
	private String sex = null;
	private TextView text_selectsex;
	private RelativeLayout rel_head;
	private ImageView image_head_bg;
	private String str_photp = null; // 宝贝头像
	private DialogUtil dialogUitl = null;
	boolean isadmin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		setContentView(R.layout.add_baby_activity2);
		// 禁止自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// 防止软键盘挡住输入框
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		initView();
		setOnClickListener();

		IntentFilter filter = new IntentFilter();
		filter.addAction(SelectSetSexActivity.SELECT_SEX_ACTION);
		filter.addAction(SelectGetPhotoMethodActivity.GET_PHOTO_METHOD);
		filter.addAction("okonciclk");
		registerReceiver(broadcastReceiver, filter);
		dialogUitl = new DialogUtil(AddBabyActivity2.this,getString(R.string.gettingdata));
		dialogUitl.showDialog();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isadmin=BabyServer.isAdmin(MbApplication.getGlobalData().getNowuser().getUserid());		
				//ToastUtil.show(AddBabyActivity2.this, "添加2登陆是否管理员："+isadmin);
				SPUtil.setIsAdmin(AddBabyActivity2.this, isadmin);
				Intent i=new Intent();
				i.setAction("okonciclk");
				sendBroadcast(i);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	private void initView() {
		rel_Finish = (RelativeLayout) findViewById(R.id.rel_Finish);
		textFinish = (TextView) findViewById(R.id.textFinish);
		rel_back = (RelativeLayout) findViewById(R.id.rel_back);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		hit_ignore = (TextView) findViewById(R.id.hit_ignore);
		rel_select_date = (RelativeLayout) findViewById(R.id.rel_select_date);
		text_babybirthtime = (TextView) findViewById(R.id.text_babybirthtime);
		rel_selectsex = (RelativeLayout) findViewById(R.id.rel_selectsex);
		text_selectsex = (TextView) findViewById(R.id.text_selectsex);
		rel_head = (RelativeLayout) findViewById(R.id.rel_head);
		image_head_bg = (ImageView) findViewById(R.id.image_head_bg);
		edittext_age = (EditText) findViewById(R.id.edittext_age);
		edittext_height = (EditText) findViewById(R.id.edittext_height);
		edittext_weight = (EditText) findViewById(R.id.edittext_weight);
		edittext_steplong = (EditText) findViewById(R.id.edittext_steplong);
	}

	private void setOnClickListener() {
		hit_ignore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent("ADD_BABY_ACTION");
				sendBroadcast(intent);
				Intent i=new Intent(AddBabyActivity2.this,
						BabyFragmentActivity.class);
				i.putExtra("ifadmin",isadmin);	
				startActivity(i);
				finish();
			}
		});
		rel_Finish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAndGetViewText();
			}
		});
		textFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAndGetViewText();
			}
		});
		rel_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveInfoOrNo();
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveInfoOrNo();
			}
		});
		rel_select_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						AddBabyActivity2.this, "");
				dateTimePicKDialog.dateTimePicKDialog(text_babybirthtime);
			}
		});
		rel_selectsex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(AddBabyActivity2.this,
						SelectSetSexActivity.class));
			}
		});

		rel_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 弹出选择框
				startActivity(new Intent(AddBabyActivity2.this,
						SelectGetPhotoMethodActivity.class));
			}
		});
	}

	// 退出提示是否保存操作
	public void saveInfoOrNo() {
		// 创建自定义的提示框
		CustomDialog.Builder builder = new CustomDialog.Builder(
				AddBabyActivity2.this);
		builder.setMessage(getString(R.string.ornosaveinfo));
		builder.setTitle(getResources().getString(R.string.dialog_body_text));
		builder.setPositiveButton(getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						checkAndGetViewText();
					}
				});

		builder.setNegativeButton(getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});

		builder.create().show();
	}

	private void checkAndGetViewText() {
		if (str_photp == null) {
			ToastUtil.show(this, getString(R.string.setheadimage));
			return;
		}
		if (TextUtils.isEmpty(text_babybirthtime.getText())) {
			ToastUtil.show(this, getString(R.string.editbirthday));
			return;
		}
//		if (TextUtils.isEmpty(text_selectsex.getText())) {
//			ToastUtil.show(this, getString(R.string.selectsex));
//			return;
//		}
//		if (TextUtils.isEmpty(edittext_age.getText())) {
//			ToastUtil.show(this, getString(R.string.pleaseeditage));
//			return;
//		}
		if (TextUtils.isEmpty(edittext_height.getText())) {
			ToastUtil.show(this, getString(R.string.pleaseeidtheight));
			return;
		}
		if (TextUtils.isEmpty(edittext_weight.getText())) {
			ToastUtil.show(this, getString(R.string.edittureweight));
			return;
		}
//		if (TextUtils.isEmpty(edittext_steplong.getText())) {
//			ToastUtil.show(this, getString(R.string.editturesteep));
//			return;
//		}
		String imei = getIntent().getStringExtra(
				SubmitBabyPartInfoThread.INTENT_EXTRA_IMEI);
		String babyphone = getIntent().getStringExtra(
				SubmitBabyPartInfoThread.INTENT_EXTRA_PHONE);
		String name = getIntent().getStringExtra(
				SubmitBabyPartInfoThread.INTENT_EXTRA_NAME);
		String value = getIntent().getStringExtra(
				SubmitBabyPartInfoThread.INTENT_EXTRA_RELATION);
		String birthday = text_babybirthtime.getText().toString();
//		sex = text_selectsex.getText().toString();
//		String age = edittext_age.getText().toString();
		String height = edittext_height.getText().toString();
		String weight = edittext_weight.getText().toString();
//		String steep_long = edittext_steplong.getText().toString();
		AddBaby addbaby = new AddBaby(imei, str_photp, value, name, "1",
				height, babyphone, "男", weight, "1", birthday);
		saveinfo(addbaby);
	}

	private void saveinfo(final AddBaby addbaby) {
		dialogUitl = new DialogUtil(this, getString(R.string.saving));
		dialogUitl.showDialog();
		new Thread() {
			public void run() {
				final AddBabyResult getResult = BabyServer.addBaby(addbaby,
						MbApplication.getGlobalData().getNowuser().getUserid());
				if (getResult != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (getResult.getMsg().equals("need authorize")) {
								dialogUitl.dismissDialog();
								String msg = getString(R.string.sendrequestsuccess1)
										+ getResult.getPhone()
										+ getString(R.string.sendrequestsuccess2);
								CustomDialog.Builder builder = new CustomDialog.Builder(
										AddBabyActivity2.this);
								builder.setMessage(msg);
								builder.setTitle(getResources().getString(
										R.string.dialog_body_text));
								builder.setPositiveButton(
										getString(R.string.sure),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												finish();
												return;
											}
										});
								builder.create().show();
								return;
							}
							if (getResult.getMsg().equals("success")) {
								dialogUitl.dismissDialog();
								str_photp = null;
								// 如果不是第一次添加宝贝,就跳转到宝贝管理界面,否则跳到主界面
								if (MbApplication.getGlobalData().isIsinto() == true) {
									System.out.println("是否进入过主页面2"+MbApplication.getGlobalData().isIsinto());
									startActivity(new Intent(
											AddBabyActivity2.this,
											WatchMangerActivity.class));
								} else {
									dialogUitl.dismissDialog();
									Intent intent=new Intent(AddBabyActivity2.this,BabyFragmentActivity.class);
									intent.putExtra("ifadmin",isadmin);
									startActivity(intent);
								}
								Toast.makeText(AddBabyActivity2.this,
										getString(R.string.savesuccess), 3000)
										.show();
								finish();
							}
							if (getResult.getMsg().equals("already has admin")) {
								dialogUitl.dismissDialog();
								String msg = getString(R.string.wacthisActivited)
										+ getResult.getPhone();
								CustomDialog.Builder builder = new CustomDialog.Builder(
										AddBabyActivity2.this);
								builder.setMessage(msg);
								builder.setTitle(getResources().getString(
										R.string.dialog_body_text));
								builder.setPositiveButton(
										getString(R.string.sure),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												return;
											}
										});
								builder.create().show();
								return;
							}
							if (getResult.getMsg().equals("chongtu")) {
								dialogUitl.dismissDialog();
								String msg = getString(R.string.relationshipbeused);
								CustomDialog.Builder builder = new CustomDialog.Builder(
										AddBabyActivity2.this);
								builder.setMessage(msg);
								builder.setTitle(getResources().getString(
										R.string.dialog_body_text));
								builder.setPositiveButton(
										getString(R.string.sure),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												return;
											}
										});
								builder.create().show();
								return;
							}
							if (getResult.getMsg().equals("need a admin")) {
								dialogUitl.dismissDialog();
								String msg = getResources().getString(
										R.string.nopermission);
								CustomDialog.Builder builder = new CustomDialog.Builder(
										AddBabyActivity2.this);
								builder.setMessage(msg);
								builder.setTitle(getResources().getString(
										R.string.dialog_body_text));
								builder.setPositiveButton(
										getString(R.string.sure),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												return;
											}
										});
								builder.create().show();
								return;
							}
						}
					});
				} else {
					dialogUitl.dismissDialog();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AddBabyActivity2.this,
									getString(R.string.codeiunusable), 3000)
									.show();
						}
					});
				}
			}
		}.start();
	}

	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					SelectSetSexActivity.SELECT_SEX_ACTION)) {
				String getsex = intent.getExtras().getString("sex");
				text_selectsex.setText(getsex);
			}
			if(intent.getAction().equals("okonciclk")){
				dialogUitl.dismissDialog();
				setOnClickListener();
			}
			if (intent.getAction().equals(
					SelectGetPhotoMethodActivity.GET_PHOTO_METHOD)) {
				String method = intent.getExtras().getString("method");
				if (method.equals(SelectGetPhotoMethodActivity.TAKE_PHOTO)) {
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 下面这句指定调用相机拍照后的照片存储的路径
					intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(Environment
									.getExternalStorageDirectory(),
									"xiaoma.jpg")));
					startActivityForResult(intent2, 2);

				} else {
					Intent intent1 = new Intent(Intent.ACTION_PICK, null);
					intent1.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent1, 1);
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 如果是直接从相册获取
		case 1:
			if (data == null) {
				return;
			}
			startPhotoZoom(data.getData());
			break;
		// 如果是调用相机拍照时
		case 2:
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/xiaoma.jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		// 取得裁剪后的图片
		case 3:
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			// Bitmap c_photo = ChatUtil.toRoundBitmap(photo);
			Drawable drawable = new BitmapDrawable(photo);
			// 页面显示头像
			image_head_bg.setBackgroundDrawable(drawable);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			byte[] b = stream.toByteArray(); // 将图片流以字符串形式存储下来
			str_photp = Base64.encodeToString(b, Base64.DEFAULT);
			str_photp = new String(Base64Coder.encodeLines(b));
			/**
			 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上 传到服务器，QQ头像上传采用的方法跟这个类似
			 */

		}
	}
}
