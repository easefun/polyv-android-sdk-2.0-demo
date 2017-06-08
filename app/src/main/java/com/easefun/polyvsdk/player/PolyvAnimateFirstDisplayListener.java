package com.easefun.polyvsdk.player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PolyvAnimateFirstDisplayListener extends SimpleImageLoadingListener{
	boolean cacheFound;
	public static List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

	@Override
	public void onLoadingStarted(String imageUri, View view){
		
	}
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
		if (loadedImage != null){
			ImageView imageView = (ImageView) view;
			boolean firstDisplay = !displayedImages.contains(imageUri);
			if (firstDisplay){
				FadeInBitmapDisplayer.animate(imageView, 500);
				displayedImages.add(imageUri);
			}
		}
	}
}