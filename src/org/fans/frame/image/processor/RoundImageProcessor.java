package org.fans.frame.image.processor;

import org.fans.frame.utils.ImageUtils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class RoundImageProcessor implements BitmapProcessor {

	@Override
	public Bitmap process(Bitmap bitmap) {
		return ImageUtils.toRoundBitmap(bitmap);
	}

}
