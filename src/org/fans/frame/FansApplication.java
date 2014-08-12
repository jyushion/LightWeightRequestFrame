package org.fans.frame;

import java.io.File;
import java.util.HashMap;

import org.fans.frame.utils.Constants;
import org.fans.frame.utils.Utils;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class FansApplication extends Application {
	private static FansApplication instance = null;
	private static Context appContext = null;
	private Session session;
	private HashMap<String, Object> globalObjCache;
	private ImageLoaderConfiguration config;
	private File cacheDir;

	public static FansApplication getInstance() {
		return instance;
	}

	public static Context getContext() {
		return appContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		appContext = getApplicationContext();
		session = new Session(this);
		globalObjCache = new HashMap<String, Object>(10);
		cacheDir = Utils.getDiskCachePath(this, Constants.CACHE_DIR_NAME);
		if (!cacheDir.exists())
			cacheDir.mkdir();
		initImageLoader(this);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.out.println("low...");
	}

	public Session getSession() {
		return session;
	}

	public void cache(String key, Object value) {
		globalObjCache.put(key, value);
	}

	public Object remove(String key) {
		return globalObjCache.remove(key);
	}

	public Object get(String key) {
		return globalObjCache.get(key);
	}

	private void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		try {

			DiscCacheAware discCache = new FileCountLimitedDiscCache(cacheDir, new Md5FileNameGenerator(), 100);

			config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
					.denyCacheImageMultipleSizesInMemory().discCache(discCache).tasksProcessingOrder(QueueProcessingType.LIFO)
					.enableLogging() // Not
					.build();
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);

		} catch (Exception e) {
		}
	}

	public ImageLoaderConfiguration getImageLoaderConfig() {
		return config;
	}

	public File getCacheFolder() {
		return null;
	}

}
