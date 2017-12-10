package com.mobao.watch.view;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.mb.zjwb1.R;
import com.mobao.watch.util.ChatUtil;

public class RoundProgressView extends View {
	private Paint mPaint;

	private int mWidth;
	private int mHeight;
	private int mLeft;
	private int mTop;
	private int mRight;
	private int mBottom;

	private Rect mRect;

	private Bitmap downCircle;
	private Bitmap upCircle;

	// 此值由外部传入并控制修改
	private float mPercent = 0.0f;

	// 此值用来做bitmap缓存的KEY
	private String strPercent = "";

	public RoundProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		Options opts = new Options();
		opts.inSampleSize = 2;

		downCircle = BitmapFactory.decodeResource(getResources(),
				R.drawable.motionfragment_roundprogressbar_befor, opts);
		try {
			upCircle = BitmapFactory.decodeResource(getResources(),
					R.drawable.motionfragment_roundprogressbar_last);
		} catch (Exception e) {
			upCircle = BitmapFactory.decodeResource(getResources(),
					R.drawable.motionfragment_roundprogressbar_last, opts);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mWidth = getWidth();
		mHeight = getHeight();

		mLeft = (mWidth - mHeight) / 2;
		mRight = mLeft + mHeight;
		mTop = 0;
		mBottom = mHeight;

		mRect = new Rect(mLeft, mTop, mRight, mBottom);

		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(downCircle, null, mRect, mPaint);

		canvas.drawBitmap(getPercentClip(), null, mRect, mPaint);

		super.onDraw(canvas);

	}

	/**
	 * @param bitmap
	 *            原始的黄色圈
	 * @param percent
	 *            完成的百分比
	 * @return 百分之多少的黄色圈
	 */
	private Bitmap getPercentClip() {
		Bitmap targetBitmap = ChatUtil.getImageCache().getBitmap(strPercent);

		if (targetBitmap != null) {
			return targetBitmap;
		}

		int width = upCircle.getWidth();
		int height = upCircle.getHeight();
		int center_X = width / 2;
		int center_Y = height / 2;

		try {
			targetBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
		} catch (Exception e) {
			targetBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_4444);
		}

		Canvas canvas = new Canvas(targetBitmap);

		int radius = width;

		Path path = new Path();

		path.moveTo(center_X, center_Y); // 圆心

		path.lineTo(center_X, center_Y - radius); // 圆心正上方

		if (mPercent > 0.25f) {
			path.lineTo(center_X + radius, center_Y); // 连到最右
		}

		if (mPercent > 0.5f) {
			path.lineTo(center_X, center_Y + radius); // 连到最下
		}

		if (mPercent > 0.75f) {
			path.lineTo(center_X - radius, center_Y); // 连到最左
		}

		// 连接终点
		double angle = 2 * Math.PI * mPercent;
		float endx = (float) (center_X + radius * Math.sin(angle));
		float endy = (float) (center_Y - radius * Math.cos(angle));
		path.lineTo(endx, endy);
		path.close();

		// 画图并返回Bitmap
		canvas.clipPath(path);

		canvas.drawBitmap(upCircle, new Rect(0, 0, width, height), new Rect(0,
				0, width, height), mPaint);

		ChatUtil.getImageCache().putBitmap(strPercent, targetBitmap);

		return targetBitmap;
	}

	public void setmPercent(float percent) {
		if (percent > 1) {
			percent = 1f;
		}

		if (percent < 0) {
			percent = 0.0f;
		}

		mPercent = percent;
		DecimalFormat decimalFormat = new DecimalFormat(".00");
		strPercent = decimalFormat.format(percent);

		postInvalidate();
	}

	public float getmPercent() {
		return mPercent;
	}

}
