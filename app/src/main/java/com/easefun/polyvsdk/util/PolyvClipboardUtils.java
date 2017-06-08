package com.easefun.polyvsdk.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class PolyvClipboardUtils {

	// 复制到剪贴板(相同的文字不会重复复制)
	public static void copyToClipboard(Context context, CharSequence msg) {
		ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = ClipData.newPlainText("clip", msg);
		clipboardManager.setPrimaryClip(clipData);
	}

	// 从剪贴板获取最新的复制内容粘贴
	public static CharSequence pasteByClipboard(Context context) {
		ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = clipboardManager.getPrimaryClip();
		if (clipData == null)
			return "";
		return clipData.getItemAt(0).getText();
	}
}
