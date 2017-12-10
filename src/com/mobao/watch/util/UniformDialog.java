package com.mobao.watch.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mb.zjwb1.R;

/**
 * 自定义dialog，样式可查看layout目录下的dialog.xml 是个单例类
 * 
 * @author yoine
 * 
 */
public class UniformDialog extends Dialog {

	private static UniformDialog uniformDialog;
	private Context context;
	private static View view;
	private TextView tvTitle;
	private TextView tvBody;
	private Button btOk;
	private Button btCancel;
	private boolean isTwoBtn;

	private UniformDialog(Context context, int dialogStyle, boolean isTwoBtn) {
		super(context, dialogStyle);
		this.context = context;
		this.isTwoBtn = isTwoBtn;
		view = LayoutInflater.from(context).inflate(
				R.layout.dialog_normal_layout, null);
		tvTitle = (TextView) view.findViewById(R.id.title);
		tvBody = (TextView) view.findViewById(R.id.message);
		btOk = (Button) view.findViewById(R.id.positiveButton);
		btCancel = (Button) view.findViewById(R.id.negativeButton);
		if (!isTwoBtn) {
			btCancel.setVisibility(8);
		}
	}

	/**
	 * 获取Dialog实例，当dialog不存在时会返回null，
	 * 
	 * @return Dialog实例
	 */
	public static UniformDialog getInstance() {
		return uniformDialog;
	}

	/**
	 * 获取标题的TextView组件
	 * 
	 * @return
	 */
	public TextView getTvTitle() {
		return tvTitle;
	}

	/**
	 * 获取提示内容的TextView组件
	 * 
	 * @return
	 */
	public TextView getTvBody() {
		return tvBody;
	}

	/**
	 * 获取左下角的按钮组件
	 * 
	 * @return
	 */
	public Button getBtOk() {
		return btOk;
	}

	/**
	 * 获取右下角的按钮组件
	 * 
	 * @return
	 */
	public Button getBtCancel() {
		if (isTwoBtn) {
			return btCancel;
		}
		return null;
	}

	public void setTvTitle(TextView tvTitle) {
		this.tvTitle = tvTitle;
	}

	public void setTvBody(TextView tvBody) {
		this.tvBody = tvBody;
	}

	public void setBtOk(Button btOk) {
		this.btOk = btOk;
	}

	public void setBtCancel(Button btCancel) {
		if (isTwoBtn) {
			this.btCancel = btCancel;
		}
	}

	public static void setInstanceToNull() {
		uniformDialog = null;
	}

	public static void initDialog(Context context, boolean isTwoBtn) {
		uniformDialog = new UniformDialog(context,
				R.style.chatfragment_call_dialog_style, isTwoBtn);
		uniformDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉dialog的标题
		uniformDialog.setContentView(view); // 设置dialog主体部分的内容View
		uniformDialog.setCanceledOnTouchOutside(false); // 设置不能通过点击外面
		uniformDialog.setCancelable(false); // 设置dialog不能通过按返回键关闭
		//设置位置
		Window dialogWindow = uniformDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();

		//设置位置
		dialogWindow.setGravity(Gravity.CENTER);
//		lp.x = context.getResources().getDimensionPixelSize(R.dimen.dimen_10dp);
		lp.y = context.getResources().getDimensionPixelSize(R.dimen.dimen_120dp);

	}

}
