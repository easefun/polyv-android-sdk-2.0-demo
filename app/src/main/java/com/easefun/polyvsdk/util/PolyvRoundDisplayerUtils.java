package com.easefun.polyvsdk.util;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class PolyvRoundDisplayerUtils extends RoundedBitmapDisplayer {
	public PolyvRoundDisplayerUtils(int cornerRadiusPixels) {
		super(cornerRadiusPixels);
	}

	// 显示位图
	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageDrawable(new CircleDrawable(bitmap, margin));
	}

	public static class CircleDrawable extends Drawable {
		private final int margin;
		private final RectF mRect = new RectF();
		private final BitmapShader bitmapShader;
		private final Paint paint;
		private RectF mBitmapRect;

		public CircleDrawable(Bitmap bitmap, int margin) {
			this.margin = 0;
			// 创建着色器
			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF(margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
			// 设置画笔
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
		}

		// 画圆，覆盖原来的位图
		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);

			// 调整位图，设置该矩阵，转换映射源矩形和目的矩形
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			// 设置着色器矩阵
			bitmapShader.setLocalMatrix(shaderMatrix);
		}

		// 画出其边界（通过设置的setBounds）
		@Override
		public void draw(Canvas canvas) {
			canvas.drawRoundRect(mRect, mRect.width() / 2, mRect.height() / 2, paint);
		}

		/**
		 * 返回此绘制对象的不透明度/透明度 ，返回的值是抽象的格式常数的PixelFormat之一：未知，半透明，透明或不透明
		 */
		@Override
		public int getOpacity() {
			// 半透明
			return PixelFormat.TRANSLUCENT;
		}

		// 设置透明度
		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		// 彩色滤光片（通过设置setColorFilter）
		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}
}
