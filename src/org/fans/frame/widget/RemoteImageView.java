package org.fans.frame.widget;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.fans.frame.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * 
 * @author LuDaiqian
 */
@SuppressLint("NewApi")
public class RemoteImageView extends ImageView {

	/**
	 * 网络图片url
	 */
	private String mUrl;
	protected DisplayImageOptions mDefaultOptions;
	protected ImageLoader mImageLoader;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private Integer mDefaultImage = R.drawable.ic_launcher;
	private BitmapProcessor preProcessor;
	private BitmapProcessor postProcessor;

	static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

	private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
				if (imageLoadedListener != null)
					imageLoadedListener.onImageLoaded(loadedImage);

			}

		}
	}

	public interface ImageLoadedListener {
		public void onImageLoaded(Bitmap bitmap);
	}

	private ImageLoadedListener imageLoadedListener;

	public void setImageLoadedListener(ImageLoadedListener imageLoadedListener) {
		this.imageLoadedListener = imageLoadedListener;
	}

	public String getImageUrl() {
		return mUrl;
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RemoteImageView(Context context) {
		super(context);
		init();
	}

	@SuppressLint("NewApi")
	public void init() {
		mImageLoader = ImageLoader.getInstance();
		building();
	}

	private void building() {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnFail(mDefaultImage).showStubImage(mDefaultImage).showImageForEmptyUri(mDefaultImage)
				.showImageForEmptyUri(mDefaultImage).showImageOnFail(mDefaultImage).cacheInMemory().cacheOnDisc();
		if (preProcessor != null)
			builder.preProcessor(preProcessor);
		if (postProcessor != null)
			builder.postProcessor(postProcessor);
		mDefaultOptions = builder.build();
	}

	/**
	 * 加载网络图片
	 * 
	 * @param url
	 *            eg. http://yourwebsite.com/abz.jpg
	 */
	public void setImageUrl(String url) {
		if (!TextUtils.isEmpty(url))
			mImageLoader.displayImage(url, this, mDefaultOptions, animateFirstListener);
	}

	/**
	 * 设置默认图片资源id
	 * 
	 * @param resid
	 */
	public void setDefaultImageResource(Integer resId) {
		mDefaultImage = resId;
		setImageResource(resId);
		building();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mDefaultImage = resId;
		building();
	}

	public void setPreProcessor(BitmapProcessor preProcessor) {
		this.preProcessor = preProcessor;
		building();
	}

	public void setPostProcessor(BitmapProcessor postProcessor) {
		this.postProcessor = postProcessor;
		building();
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

}
