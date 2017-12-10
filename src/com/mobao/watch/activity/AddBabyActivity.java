package com.mobao.watch.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import com.mobao.watch.bean.Baby;
import com.mobao.watch.bean.RelactionShip;
import com.mobao.watch.fragment.ChatFragment;
import com.mobao.watch.fragment.LocationFragment;
import com.mobao.watch.fragment.MotionFragment;
import com.mobao.watch.fragment.RewardFragment;
import com.mobao.watch.server.BabyLocateServer;
import com.mobao.watch.server.BabyServer;
import com.mobao.watch.util.ActivityContainer;
import com.mobao.watch.util.BabyNameEditTextWatcher;
import com.mobao.watch.util.Base64Coder;
import com.mobao.watch.util.CharUtil;
import com.mobao.watch.util.ChatUtil;
import com.mobao.watch.util.CustomDialog;
import com.mobao.watch.util.DateTimePickDialogUtil;
import com.mobao.watch.util.DialogUtil;
import com.mobao.watch.util.SPUtil;
import com.mobao.watch.util.ToastUtil;
import com.testin.agent.TestinAgent;

public class AddBabyActivity extends Activity implements OnClickListener {
	private ImageButton ibLast = null; // 上一步按钮
	private RelativeLayout rel_back = null;
	private RelativeLayout rel_Finish = null;
	private TextView textFinish = null;// 完成保存
	private TextView text_mode = null;// 当前模式（添加/编辑）
	// 选择头像
	private RelativeLayout rel_head;// 显示头像
	private ImageView image_head_bg;// 头像背景圆圈
	private String str_photp = null;// Base64Coder字符串形式的头像
	private RelativeLayout rel_select_relationship;// 选择关系
	private TextView textRelationship; // 显示关系文本
	public static ArrayList<RelactionShip> relactionship;// 关系列表
	public static final String GET_RELACTIONSHIP_ACTION = "GET_RELACTIONSHIP_ACTION";
	private String value;// 关系值
	private RelativeLayout rel_select_date;// 选择日期
	private TextView text_babybirthtime;// 宝贝出生日期
	private String babybirthtime = null;// 宝贝出生日期
	private ImageView imag_birthdat_jiantou;
	private EditText edtPhone;// 昵称编辑框
	private EditText edtName;// 昵称编辑框
	private EditText edtAge;// 年龄编辑框
	private EditText edtHeight;// 身高编辑框
	private RelativeLayout laoutSex;// 性别
	private TextView edtSex;
	private EditText edtWeight;
	private EditText edtSteep;
	private String babyImei = null;// 宝贝imei
	private String name = null;// 宝贝名称
	private ImageView btnRelationship = null;// 选择关系箭头
	private ImageView btnsex = null;
	private DialogUtil dialogUitl = null;
	private boolean admin = true;
	private boolean edit_mode = false;
	private ProgressDialog progDialog = null; // 圆形进度条
	private boolean isChangeImage = false; // 是否更改头像
	private Bitmap bitmap;
	private String useridByIntent = null;
	private TextView text_deletebaby = null;
	private RelativeLayout lin_deletebaby =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.add_baby_activity);

		// 禁止自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// 防止软键盘挡住输入框
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		// 获取上个activity传过来的userid
		if (getIntent().getStringExtra("userid") != null) {
			useridByIntent = getIntent().getStringExtra("userid");
		}

		progDialog = new ProgressDialog(this);
		showDialog();
		initIbLast(); // 初始化上一步按钮
		admin = SPUtil.getIsAdmin(AddBabyActivity.this);// 判断是否管理员
		// 获取宝贝imei
		babyImei = getIntent().getStringExtra("imei");
		// 初始化组件
		initView();

		// 获取关系列表
		getRelactionShipList();
		// 注册监听广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(SelectGetPhotoMethodActivity.GET_PHOTO_METHOD);
		filter.addAction(GET_RELACTIONSHIP_ACTION);
		registerReceiver(broadcastReceiver, filter);
		dismissDialog();
		
		/*伍建鹏//////////////////////////////*/
		//为宝贝昵称的输入加上输入限制
		new BabyNameEditTextWatcher(AddBabyActivity.this, edtName);
		/*//////////////////////////////////*/
	}

	private void getRelactionShipList() {
		new Thread() {
			public void run() {
				final ArrayList<RelactionShip> list = BabyServer
						.getRelationShipList();
				if (list != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							relactionship = list;
							MbApplication.getGlobalData().setRelactionship(
									relactionship);
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AddBabyActivity.this,
									getString(R.string.serverbusy), 3000)
									.show();
						}
					});
				}
			}
		}.start();
	}

	private void initView() {
		rel_head = (RelativeLayout) findViewById(R.id.rel_head);
		image_head_bg = (ImageView) findViewById(R.id.image_head_bg);
		rel_select_relationship = (RelativeLayout) findViewById(R.id.rel_select_relationship);
		textRelationship = (TextView) findViewById(R.id.textRelationship);
		textFinish = (TextView) findViewById(R.id.textFinish);
		rel_Finish = (RelativeLayout) findViewById(R.id.rel_Finish);
		edtName = (EditText) findViewById(R.id.edtName);
		edtAge = (EditText) findViewById(R.id.edtAge);
		edtHeight = (EditText) findViewById(R.id.edtHeight);
		rel_back = (RelativeLayout) findViewById(R.id.rel_back);
		text_mode = (TextView) findViewById(R.id.text_mode);
		btnRelationship = (ImageView) findViewById(R.id.btnRelationship);
		edtPhone = (EditText) findViewById(R.id.edtPhone);
		rel_select_date = (RelativeLayout) findViewById(R.id.rel_select_date);
		text_babybirthtime = (TextView) findViewById(R.id.text_babybirthtime);
		edtSteep = (EditText) findViewById(R.id.edtSteep);
		laoutSex = (RelativeLayout) findViewById(R.id.rel_select_sex);
		edtSex = (TextView) findViewById(R.id.textsex);
		edtWeight = (EditText) findViewById(R.id.edtWeight);
		text_deletebaby = (TextView) findViewById(R.id.text_deletebaby);
		lin_deletebaby = (RelativeLayout) findViewById(R.id.lin_deletebaby);
		btnsex = (ImageView) findViewById(R.id.btnsex);
		imag_birthdat_jiantou = (ImageView) findViewById(R.id.imag_birthdat_jiantou);
		
		if (admin) {
			rel_head.setOnClickListener(this);
			image_head_bg.setOnClickListener(this);
			rel_select_relationship.setOnClickListener(this);
			textFinish.setOnClickListener(this);
			rel_back.setOnClickListener(this);
			rel_Finish.setOnClickListener(this);
			laoutSex.setOnClickListener(this);
			rel_select_date.setOnClickListener(this);
			text_deletebaby.setOnClickListener(this);
			// 用户为管理员可以编辑
//			edtName.setOnFocusChangeListener(new OnFocusChangeListener() {
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//
//					if (edtName.hasFocus() == false) {
//						String name = edtName.getText().toString();
//						if (name.contains(" ")) {
//							ToastUtil.show(AddBabyActivity.this, getResources()
//									.getString(R.string.namenocontainsapce));
//							return;
//						}
//						if (CharUtil.isChinese(name) && name.length() > 6) {
//							ToastUtil.show(AddBabyActivity.this, getResources()
//									.getString(R.string.nametolong));
//							return;
//						}
//					}
//				}
//			});
//
//			edtSteep.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//					if (edtSteep.hasFocus() == false) {
//						String str_long = edtSteep.getText().toString();
//						int int_long = Integer.valueOf(str_long);
//						if (int_long <= 0 || int_long >= 200) {
//							ToastUtil.show(AddBabyActivity.this, getResources()
//									.getString(R.string.editturesteep));
//							return;
//						}
//					}
//				}
//			});
//			edtWeight.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//					if (edtWeight.hasFocus() == false) {
//						String weight = edtWeight.getText().toString();
//						int wei = Integer.valueOf(weight);
//						if (wei <= 0) {
//							ToastUtil.show(AddBabyActivity.this, getResources()
//									.getString(R.string.edittureweight));
//							return;
//						}
//					}
//				}
//			});
		}
		// 审核情况下进入宝贝添加列表
		if (getIntent().getStringExtra("shenhe") != null) {
			ToastUtil.show(this,
					getResources().getString(R.string.selectRelactionship));
			edtName.setKeyListener(null);
			edtAge.setKeyListener(null);
			edtHeight.setKeyListener(null);
			edtPhone.setKeyListener(null);
			edtSteep.setKeyListener(null);
			edtWeight.setKeyListener(null);
			lin_deletebaby.setVisibility(View.INVISIBLE);
			btnsex.setVisibility(View.INVISIBLE);
			imag_birthdat_jiantou.setVisibility(View.INVISIBLE);
			rel_select_date.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					return;
				}
			});
			laoutSex.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					return;
				}
			});
			rel_head.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					return;
				}
			});
			image_head_bg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					return;
				}
			});
			rel_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AddBabyActivity.this.finish();
				}
			});
			babyImei = getIntent().getStringExtra("BabyImei");
			final String adminid = getIntent().getStringExtra("adminid");
			text_mode.setText(getResources().getString(
					R.string.selectRelactionship));
			new Thread() {
				public void run() {
					final Baby getBaby = BabyServer.getBaby(adminid, babyImei);
					if (getBaby != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								// 头像转换
								str_photp = getBaby.getPortrait();
								if (TextUtils.isEmpty(str_photp)) {
									image_head_bg
											.setBackgroundResource(R.drawable.babymark);
								} else {
									int len = str_photp.length();
									String head_of_str_photp = str_photp
											.substring(len - 20, len);
									Log.i("AAA", "22head_of_str_photp = "
											+ head_of_str_photp);
									bitmap = ChatUtil.getImageCache()
											.getBitmap(head_of_str_photp);

									if (bitmap == null) {
										byte[] bitmapArray;
										bitmapArray = Base64.decode(str_photp,
												Base64.DEFAULT);
										BitmapFactory.Options options = new BitmapFactory.Options();
										options.inSampleSize = 2;
										bitmap = ChatUtil
												.toRoundBitmap(BitmapFactory
														.decodeByteArray(
																bitmapArray,
																0,
																bitmapArray.length,
																options));

										// ChatUtil.getImageCache().putBitmap(head_of_str_photp,
										// bitmap);
									}

									Drawable babyDrawable = new BitmapDrawable(
											bitmap);
									image_head_bg
											.setBackgroundDrawable(babyDrawable);
								}

								value = getBaby.getRelate();
								textRelationship.setText(getBaby.getValue());
								edtName.setText(getBaby.getBabyname());
								edtAge.setText(getBaby.getAge());
								edtHeight.setText(getBaby.getHeight());
								edtPhone.setText(getBaby.getBabyphone());
								edtWeight.setText(getBaby.getWeight());
								text_babybirthtime.setText(getBaby
										.getBirthday());
								// 初始化时将性别存入全局变量
								if (getResources().getConfiguration().locale
										.getCountry().equals("CN")) {
									// 中文环境下转换成中文再储存
									String nowsex = getBaby.getSex();
									if (nowsex.equals("boy")) {
										nowsex = "男";
									} else {
										nowsex = "女";
									}
									edtSex.setText(nowsex);
									MbApplication.getGlobalData().setNowsex(
											nowsex);
								} else {
									// 台湾也使用中文
									if (getResources().getConfiguration().locale
											.getCountry().equals("TW")) {
										String nowsex = getBaby.getSex();
										if (nowsex.equals("boy")) {
											nowsex = "男";
										} else {
											nowsex = "女";
										}
										edtSex.setText(nowsex);
										MbApplication.getGlobalData()
												.setNowsex(nowsex);
										return;
									}
									// 英文环境下直接储存
									edtSex.setText(getBaby.getSex());
									MbApplication.getGlobalData().setNowsex(
											getBaby.getSex());
								}
								edtSteep.setText(getBaby.getSteepLong());
								dismissDialog();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								dismissDialog();
								Toast.makeText(AddBabyActivity.this,
										getString(R.string.serverbusy), 3000)
										.show();
							}
						});
					}
				}
			}.start();
		}
		// 编辑宝贝的情况下赋值
		if (getIntent().getStringExtra("edit") != null) {
			edit_mode = true;
			if (SPUtil.getIsAdmin(AddBabyActivity.this) == false) {
				admin = false;
				edtName.setKeyListener(null);
				edtAge.setKeyListener(null);
				edtHeight.setKeyListener(null);
				edtPhone.setKeyListener(null);
				edtWeight.setKeyListener(null);
				edtSteep.setKeyListener(null);
				lin_deletebaby.setVisibility(View.INVISIBLE);
				textFinish.setVisibility(View.INVISIBLE);
				btnRelationship.setVisibility(View.INVISIBLE);
				btnsex.setVisibility(View.INVISIBLE);
				imag_birthdat_jiantou.setVisibility(View.INVISIBLE);
				rel_select_date.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				});
				laoutSex.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				});
				rel_head.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				});
				image_head_bg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				});
				rel_select_relationship
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								return;
							}
						});
				rel_back.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AddBabyActivity.this.finish();
					}
				});

				babyImei = getIntent().getStringExtra("imei");
				text_mode.setText(R.string.babyinfo);
				new Thread() {
					public void run() {
						final Baby getBaby = BabyServer.getBaby(MbApplication
								.getGlobalData().getNowuser().getUserid(),
								babyImei);
						if (getBaby != null) {
							runOnUiThread(new Runnable() {
								public void run() {
									// 头像转换
									str_photp = getBaby.getPortrait();
									if (TextUtils.isEmpty(str_photp)) {
										image_head_bg
												.setBackgroundResource(R.drawable.babymark);
									} else {
										int len = str_photp.length();
										String head_of_str_photp = str_photp
												.substring(len - 20, len);
										Log.i("AAA", "33head_of_str_photp = "
												+ head_of_str_photp);
										// Bitmap bitmap =
										// ChatUtil.getImageCache().getBitmap(head_of_str_photp);

										if (bitmap == null) {
											byte[] bitmapArray;
											bitmapArray = Base64.decode(
													str_photp, Base64.DEFAULT);
											BitmapFactory.Options options = new BitmapFactory.Options();
											options.inSampleSize = 2;
											bitmap = ChatUtil
													.toRoundBitmap(BitmapFactory
															.decodeByteArray(
																	bitmapArray,
																	0,
																	bitmapArray.length,
																	options));

											// ChatUtil.getImageCache().putBitmap(head_of_str_photp,
											// bitmap);
										}

										Drawable babyDrawable = new BitmapDrawable(
												bitmap);
										image_head_bg
												.setBackgroundDrawable(babyDrawable);
									}

									value = getBaby.getRelate();
									textRelationship
											.setText(getBaby.getValue());
									edtName.setText(getBaby.getBabyname());
									edtAge.setText(getBaby.getAge());
									edtHeight.setText(getBaby.getHeight());
									edtPhone.setText(getBaby.getBabyphone());
									edtWeight.setText(getBaby.getWeight());
									text_babybirthtime.setText(getBaby
											.getBirthday());
									// 初始化时将性别存入全局变量
									if (getResources().getConfiguration().locale
											.getCountry().equals("CN")) {
										// 中文环境下转换成中文再储存
										String nowsex = getBaby.getSex();
										if (nowsex.equals("boy")) {
											nowsex = "男";
										} else {
											nowsex = "女";
										}
										edtSex.setText(nowsex);
										MbApplication.getGlobalData()
												.setNowsex(nowsex);
									} else {
										// 台湾也使用中文
										if (getResources().getConfiguration().locale
												.getCountry().equals("TW")) {
											String nowsex = getBaby.getSex();
											if (nowsex.equals("boy")) {
												nowsex = "男";
											} else {
												nowsex = "女";
											}
											edtSex.setText(nowsex);
											MbApplication.getGlobalData()
													.setNowsex(nowsex);
											return;
										}
										// 英文环境下直接储存
										edtSex.setText(getBaby.getSex());
										MbApplication.getGlobalData()
												.setNowsex(getBaby.getSex());
									}
									edtSteep.setText(getBaby.getSteepLong());
									dismissDialog();
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									dismissDialog();
									Toast.makeText(AddBabyActivity.this,
											getString(R.string.serverbusy),
											1000).show();
								}
							});
						}
					}
				}.start();
			} else {
				babyImei = getIntent().getStringExtra("imei");
				text_mode.setText(R.string.editinfo);
				new Thread() {
					public void run() {
						final Baby getBaby = BabyServer.getBaby(MbApplication
								.getGlobalData().getNowuser().getUserid(),
								babyImei);
						if (getBaby != null) {
							runOnUiThread(new Runnable() {
								public void run() {
									// 头像转换
									str_photp = getBaby.getPortrait();
									if (TextUtils.isEmpty(str_photp)) {
										image_head_bg
												.setBackgroundResource(R.drawable.babymark);
									} else {
										int len = str_photp.length();
										String head_of_str_photp = str_photp
												.substring(len - 20, len);
										Log.i("AAA", "44head_of_str_photp = "
												+ head_of_str_photp);
										bitmap = ChatUtil.getImageCache()
												.getBitmap(head_of_str_photp);

										if (bitmap == null) {
											byte[] bitmapArray;
											bitmapArray = Base64.decode(
													str_photp, Base64.DEFAULT);
											BitmapFactory.Options options = new BitmapFactory.Options();
											options.inSampleSize = 2;
											bitmap = ChatUtil
													.toRoundBitmap(BitmapFactory
															.decodeByteArray(
																	bitmapArray,
																	0,
																	bitmapArray.length,
																	options));

											// ChatUtil.getImageCache().putBitmap(head_of_str_photp,
											// bitmap);
										}

										Drawable babyDrawable = new BitmapDrawable(
												bitmap);
										image_head_bg
												.setBackgroundDrawable(babyDrawable);
									}

									value = getBaby.getRelate();
									textRelationship
											.setText(getBaby.getValue());
									edtName.setText(getBaby.getBabyname());
									edtAge.setText(getBaby.getAge());
									edtHeight.setText(getBaby.getHeight());
									edtPhone.setText(getBaby.getBabyphone());
									edtWeight.setText(getBaby.getWeight());
									text_babybirthtime.setText(getBaby
											.getBirthday());
									// 初始化时将性别存入全局变量
									if (getResources().getConfiguration().locale
											.getCountry().equals("CN")) {
										// 中文环境下转换成中文再储存
										String nowsex = getBaby.getSex();
										if (nowsex.equals("boy")) {
											nowsex = "男";
										} else {
											nowsex = "女";
										}
										edtSex.setText(nowsex);
										MbApplication.getGlobalData()
												.setNowsex(nowsex);
									} else {
										// 台湾也使用中文
										if (getResources().getConfiguration().locale
												.getCountry().equals("TW")) {
											String nowsex = getBaby.getSex();
											if (nowsex.equals("boy")) {
												nowsex = "男";
											} else {
												nowsex = "女";
											}
											edtSex.setText(nowsex);
											MbApplication.getGlobalData()
													.setNowsex(nowsex);
											return;
										}
										// 英文环境下直接储存
										edtSex.setText(getBaby.getSex());
										MbApplication.getGlobalData()
												.setNowsex(getBaby.getSex());
									}
									edtSteep.setText(getBaby.getSteepLong());
									dismissDialog();
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									dismissDialog();
									Toast.makeText(AddBabyActivity.this,
											getString(R.string.serverbusy),
											1000).show();
								}
							});
						}
					}
				}.start();
			}

		}
	}

	private void initIbLast() {
		ibLast = (ImageButton) findViewById(R.id.btnBack);
		ibLast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MbApplication.getGlobalData().isAdmin() == false) {
					finish();
				} else {
					saveInfoOrNo();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rel_head:
			// 弹出选择框
			startActivity(new Intent(AddBabyActivity.this,
					SelectGetPhotoMethodActivity.class));
			break;
		case R.id.image_head_bg:
			// 弹出选择框
			startActivity(new Intent(AddBabyActivity.this,
					SelectGetPhotoMethodActivity.class));
			break;
		case R.id.rel_select_relationship:
			// 弹出选择框
			startActivity(new Intent(AddBabyActivity.this,
					SelectRelactionShipActivity.class));
			break;

		case R.id.textFinish:
			// 保存信息
			saveBabyInfo();
			break;
		case R.id.rel_back:
			saveInfoOrNo();
			break;
		case R.id.rel_Finish:
			saveBabyInfo();
			break;
		// 选择性别
		case R.id.rel_select_sex:
			startActivity(new Intent(AddBabyActivity.this,
					SelectSetSexActivity.class));
			edtSex.setText(MbApplication.getGlobalData().getNowsex());
			System.out.println("GlobalData.getInstance().getNowsex():"
					+ MbApplication.getGlobalData().getNowsex());
			break;
		case R.id.rel_select_date:
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					AddBabyActivity.this, "");
			dateTimePicKDialog.dateTimePicKDialog(text_babybirthtime);
			break;

		case R.id.text_deletebaby:
			deletebaby();
			break;

		default:
			break;
		}

	}

	private void deletebaby() {
		String msg = getString(R.string.confirmthedeletion);
		CustomDialog.Builder builder = new CustomDialog.Builder(
				AddBabyActivity.this);
		builder.setMessage(msg);
		builder.setTitle(getResources().getString(R.string.dialog_body_text));
		builder.setPositiveButton(getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						new Thread() {
							public void run() {
								final boolean getResult = BabyServer
										.deleteBaby(MbApplication
												.getGlobalData().getNowuser()
												.getUserid(), babyImei);
								if (getResult) {
									runOnUiThread(new Runnable() {
										public void run() {
											if (MbApplication.getGlobalData()
													.getBabycount() < 2) {
												ToastUtil
														.show(AddBabyActivity.this,
																getString(R.string.deletesuccess));
												Intent i=new Intent();
												i.setClass(AddBabyActivity.this,
														LoginActivity.class);
												i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(i);
												ActivityContainer.getInstance().finshActivity("BabyFragmentActivity");
												finish();
											} else {
												Intent intent = new Intent();
												intent.setAction("baby_info_change");
												sendBroadcast(intent);
												ToastUtil
														.show(AddBabyActivity.this,
																getString(R.string.deletesuccess));
												startActivity(new Intent(
														AddBabyActivity.this,
														WatchMangerActivity.class));
												finish();
											}
										}
									});
								} else {
									runOnUiThread(new Runnable() {
										public void run() {
											ToastUtil
													.show(AddBabyActivity.this,
															getString(R.string.deletefail));
										}
									});
								}
							}
						}.start();
						return;
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						return;
					}
				});
		builder.create().show();

	}

	private void saveBabyInfo() {
		if (str_photp == null) {
			Toast.makeText(AddBabyActivity.this, R.string.setheadimage, 3000)
					.show();
			return;
		}
		if (textRelationship.getText().equals(
				getResources().getString(R.string.selectRelactionship))) {
			Toast.makeText(AddBabyActivity.this, R.string.selectRelactionship,
					3000).show();
			return;
		}
		if (TextUtils.isEmpty(text_babybirthtime.getText())) {
			Toast.makeText(AddBabyActivity.this, getString(R.string.editbirthday), 3000).show();
			return;
		}
		babybirthtime = text_babybirthtime.getText().toString();
		if (TextUtils.isEmpty(edtPhone.getText())) {
			Toast.makeText(AddBabyActivity.this, R.string.editwatchphone, 3000)
					.show();
			return;
		}
		String babyphone = edtPhone.getText().toString();
		if (TextUtils.isEmpty(edtName.getText())) {
			Toast.makeText(AddBabyActivity.this,
					getString(R.string.pleaseeditname), 3000).show();
			return;
		}
//		if (edtName.getText().toString().contains(" ")) {
//			ToastUtil.show(AddBabyActivity.this,
//					getResources().getString(R.string.namenocontainsapce));
//			return;
//		}
//		if (CharUtil.isChinese(edtName.getText().toString())
//				&& edtName.getText().toString().length() > 6) {
//			ToastUtil.show(AddBabyActivity.this,
//					getResources().getString(R.string.nametolong));
//			return;
//		}
//		if (TextUtils.isEmpty(edtAge.getText())) {
//			Toast.makeText(AddBabyActivity.this,
//					getString(R.string.pleaseeditage), 3000).show();
//			return;
//		}
//		int age = Integer.valueOf(edtAge.getText().toString().trim());
//		if (age < 1 || age > 100) {
//			Toast.makeText(AddBabyActivity.this,
//					getString(R.string.pleaseedittrueage), 3000).show();
//			return;
//		}
//		if (TextUtils.isEmpty(edtSex.getText())) {
//			Toast.makeText(AddBabyActivity.this,
//					getString(R.string.editturesex), 3000).show();
//			return;
//		}
//		String sex = edtSex.getText().toString();
		// 转为英文
//		if (sex.equals("男")) {
//			sex = "boy";
//		} else {
//			if (sex.equals("女")) {
//				sex = "girl";
//			}
//		}
		if (TextUtils.isEmpty(edtHeight.getText())) {
			Toast.makeText(AddBabyActivity.this,
					getString(R.string.pleaseeidtheight), 3000).show();
			return;
		}
		int height = Integer.valueOf(edtHeight.getText().toString().trim());
		if (height <= 10 || height > 250) {
			Toast.makeText(AddBabyActivity.this,
					getString(R.string.pleaseedittrueheight), 3000).show();
			return;
		}
		if (TextUtils.isEmpty(edtWeight.getText())) {
			Toast.makeText(AddBabyActivity.this,
					getString(R.string.edittureweight), 3000).show();
			return;
		}
		String weight = edtWeight.getText().toString();
//		if (TextUtils.isEmpty(edtSteep.getText())) {
//			Toast.makeText(AddBabyActivity.this,
//					getString(R.string.editturesteep), 3000).show();
//			return;
//		}
//		String steep = edtSteep.getText().toString();
		String relate = textRelationship.getText().toString();
		name = edtName.getText().toString();

		final AddBaby addbaby = new AddBaby(babyImei, str_photp, value, name,
				"1", height + "", babyphone + "", "男", weight, "1",
				babybirthtime);
		// 如果intent传递的userid不为空，优先采用intent传递的userid
		final String userid;
		if (useridByIntent != null) {
			userid = useridByIntent;
		} else {
			userid = MbApplication.getGlobalData().getNowuser().getUserid();
		}
		dialogUitl = new DialogUtil(this, getResources().getString(
				R.string.saving));
		dialogUitl.showDialog();
		new Thread() {
			public void run() {
				final AddBabyResult getResult = BabyServer.addBaby(addbaby,
						userid);
				if (getResult != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (getResult.getMsg().equals("need authorize")) {
								dialogUitl.dismissDialog();
								String msg = getString(R.string.sendrequestsuccess1)
										+ getResult.getPhone()
										+ getString(R.string.sendrequestsuccess2);
								CustomDialog.Builder builder = new CustomDialog.Builder(
										AddBabyActivity.this);
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
								boolean isadmin=BabyServer.isAdmin(MbApplication.getGlobalData().getNowuser().getUserid());					
								//ToastUtil.show(AddBabyActivity.this, "添加登陆是否管理员："+isadmin);
								SPUtil.setIsAdmin(AddBabyActivity.this, isadmin);
								// 如果不是第一次添加宝贝,就跳转到宝贝管理界面,否则跳到主界面
								if (MbApplication.getGlobalData().isIsinto() == true) {
									System.out.println("是否进入过主页面"+MbApplication.getGlobalData().isIsinto());
									startActivity(new Intent(
											AddBabyActivity.this,
											WatchMangerActivity.class));
									if (edit_mode == true) {
										if (LocationFragment.now_babyimei
												.equals(babyImei)) {
											LocationFragment.selectBabyText
													.setText(name);
											if (isChangeImage == true) {
												Intent mIntent = new Intent(
														"CHANGE_IMAGE_ACTION");
												mIntent.putExtra("str_photp",
														addbaby.getPortrait());
												sendBroadcast(mIntent);
											}
										}
										if (babyImei
												.equals(MotionFragment.now_baby
														.getBabyimei())) {
											MotionFragment.text_select_baby
													.setText(name);
										}
										if (babyImei
												.equals(ChatFragment.now_baby
														.getBabyimei())) {
											ChatFragment.text_select_baby
													.setText(name);
										}
										if (babyImei
												.equals(RewardFragment.now_baby
														.getBabyimei())) {
											RewardFragment.text_select_baby
													.setText(name);
										}
									}
									// 刷新宝贝列表
									reflashBabyList(babyImei);
								} else {
									dialogUitl.dismissDialog();
									Intent intent=new Intent(AddBabyActivity.this,BabyFragmentActivity.class);
									intent.putExtra("ifadmin",isadmin);
									startActivity(intent);
									
								}
								Toast.makeText(AddBabyActivity.this,
										getString(R.string.savesuccess), 3000)
										.show();
								finish();
							}
							if (getResult.getMsg().equals("already has admin")) {
								dialogUitl.dismissDialog();
								String msg = getString(R.string.wacthisActivited)
										+ getResult.getPhone();
								CustomDialog.Builder builder = new CustomDialog.Builder(
										AddBabyActivity.this);
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
										AddBabyActivity.this);
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
										AddBabyActivity.this);
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
							Toast.makeText(AddBabyActivity.this,
									getString(R.string.codeiunusable), 3000)
									.show();
						}
					});
				}
			}
		}.start();
	}

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
			Bitmap c_photo = ChatUtil.toRoundBitmap(photo);
			Drawable drawable = new BitmapDrawable(c_photo);
			// 页面显示头像
			image_head_bg.setBackgroundDrawable(drawable);
			isChangeImage = true;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			c_photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			byte[] b = stream.toByteArray(); // 将图片流以字符串形式存储下来
			str_photp = Base64.encodeToString(b, Base64.DEFAULT);
			str_photp = new String(Base64Coder.encodeLines(b));
			/**
			 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上 传到服务器，QQ头像上传采用的方法跟这个类似
			 */

			/*
			 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 * photo.compress(Bitmap.CompressFormat.JPEG, 60, stream); byte[] b
			 * = stream.toByteArray(); // 将图片流以字符串形式存储下来
			 * 
			 * tp = new String(Base64Coder.encodeLines(b));
			 * 这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了， 服务器处理的方法是服务器那边的事了
			 * 
			 * 如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换 为我们可以用的图片类型就OK啦 Bitmap
			 * dBitmap = BitmapFactory.decodeFile(tp); Drawable drawable = new
			 * BitmapDrawable(dBitmap);
			 */

		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	// 退出提示是否保存操作
	public void saveInfoOrNo() {
		// 创建自定义的提示框
		CustomDialog.Builder builder = new CustomDialog.Builder(
				AddBabyActivity.this);
		builder.setMessage(getString(R.string.ornosaveinfo));
		builder.setTitle(getResources().getString(R.string.dialog_body_text));
		builder.setPositiveButton(getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						saveBabyInfo();
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MbApplication.getGlobalData().isAdmin() == false) {
				finish();
				return true;
			}
			saveInfoOrNo();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 创建广播
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

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
			} else {
				String relate = intent.getExtras().getString("relate");
				value = intent.getExtras().getString("value");
				textRelationship.setText(relate);
			}
		}
	};

	public void reflashBabyList(String imei) {

		// 编辑的imei
		final String edit_imei = imei;
		// 当前选择的iemi
		final String now_imei = MbApplication.getGlobalData().getNowBaby()
				.getBabyimei();
		new Thread() {
			public void run() {
				final ArrayList<Baby> list = BabyLocateServer
						.getBabyList(MbApplication.getGlobalData().getNowuser()
								.getUserid());
				if (list != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							for (int i = 0; i < list.size(); i++) {
								Baby baby = list.get(i);
								if (baby.getBabyimei().equals(edit_imei)) {
									if (now_imei.equals(edit_imei)) {
										MbApplication.getGlobalData()
												.setNowBaby(baby);
										LocationFragment.now_baby = baby;
										MotionFragment.now_baby = baby;
										RewardFragment.now_baby = baby;
										ChatFragment.now_baby = baby;

										LocationFragment.selectBabyText
												.setText(baby.getBabyname());
										MotionFragment.text_select_baby
												.setText(baby.getBabyname());
										ChatFragment.text_select_baby
												.setText(baby.getBabyname());
										RewardFragment.text_select_baby
												.setText(baby.getBabyname());
									}
								}
								if (baby.getBabyimei().equals(now_imei)) {
									list.remove(i);
								}
							}
							MbApplication.getGlobalData().setGroups(list);
							LocationFragment.groups = list;
							MotionFragment.groups = list;
							RewardFragment.groups = list;
							ChatFragment.groups = list;
						}
					});
				}
			}
		}.start();
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(getString(R.string.gettingdata));
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		TextView textsex = (TextView) findViewById(R.id.textsex);
		textsex.setText(MbApplication.getGlobalData().getNowsex());
	}

}
