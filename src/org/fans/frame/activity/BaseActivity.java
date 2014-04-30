package org.fans.frame.activity;

import android.app.Activity;

public class BaseActivity extends Activity /*implements OnClickListener, NetworkChangedListener*/ {

//	private Session session;
//	private int screenWidth;
//	private int screenHeight;
//	private boolean networkAvailable;
//	private JczhApplication applicationContext;
//
//	@SuppressWarnings("deprecation")
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		applicationContext = JczhApplication.getInstance();
//		session = applicationContext.getSession();
//		// session
//		DisplayMetrics displayMetrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//		if (session.getDisplayMetrics() == null)
//			session.setDisplayMetrics(displayMetrics);
//		networkAvailable = session.isNetworkAvailable();
//		session.addNetworkChangedListener(this);
//		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		screenWidth = display.getWidth();
//		screenHeight = display.getHeight();
//
//	}
//
//	public void onPostContentChanged() {
//		super.onPostContentChanged();
//		// getLeftActionBarItem().setContent("back").setDrawable(R.drawable.btn_back);
//		initNavigation();
//	};
//
//	private void initNavigation() {
//		NavigationConfig navigationConfig = getClass().getAnnotation(NavigationConfig.class);
//		if (navigationConfig != null) {
//			if (navigationConfig.resId() != -1)
//				setTitle(navigationConfig.resId());
//			else if (navigationConfig.value() != null)
//				setTitle(navigationConfig.value());
//		}
//	}
//
//	@Override
//	public void onNetworkStateChanged(boolean networkAvailable) {
//		logI("net work changed......" + networkAvailable);
//		this.networkAvailable = networkAvailable;
//	}
//
//	public final boolean isNetworkAvailable(boolean immediatly) {
//		if (immediatly)
//			return NetworkChecker.isNetworkAvailable(this);
//		return networkAvailable;
//	}
//
//	public final boolean isNetworkAvailable() {
//		return isNetworkAvailable(true);
//	}
//
//	public final Session getSession() {
//		return session;
//	}
//
//	@Override
//	public void onClick(View v) {
//	}
//
//	public int getScreenWidth() {
//		return screenWidth;
//	}
//
//	public int getScreenHeight() {
//		return screenHeight;
//	}
//
//	public void logI(Object message) {
//		Logger.i(getClass(), String.valueOf(message));
//	}
//
//	public void logE(Object message) {
//		Logger.e(getClass(), String.valueOf(message));
//	}
//
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return false;
//	}
//
//	@Override
//	public void onActionBarItemClick(ActionBarItem item, int position) {
//		if (position == -1) {
//			onLeftActionBarItemClick();
//		}
//	}
//
//	protected void onLeftActionBarItemClick() {
//		onBackPressed();
//	}
//
//	protected void back() {
//		finish();
//	}
//
//	public void quickCache(Object value) {
//		quickCache(getClass().toString(), value);
//	}
//
//	public void quickCache(String key, Object value) {
//		applicationContext.cache(key, value);
//	}
//
//	public Object popCache(String key) {
//		return applicationContext.remove(key);
//	}
//
//	public int getActionBarHeight() {
//		return getSupportedActionBar().getHeight();
//	}
//
//	public int getContentViewHeight() {
//		return getWindow().findViewById(R.id.gd_action_bar_content_view).getHeight();
//	}
//
//	public ActionBarItem addNormalActionBarItem(String text) {
//		ActionBarItem item = new NormalActionBarItem();
//		item.setContent(text);
//		addActionBarItem(item, R.id.action_bar_right);
//		return item;
//	}
//
//	public void removeLeftActionBarItem() {
//		getSupportedActionBar().removeItem(getLeftActionBarItem());
//	}
//
//	public void hideLeftActionBarItem() {
//		ActionBarItem item = getLeftActionBarItem();
//		if (item != null)
//			item.getItemView().setVisibility(View.GONE);
//	}
//
//	public void showLeftActionBarItem() {
//		ActionBarItem item = getLeftActionBarItem();
//		if (item == null) {
//			getSupportedActionBar().setLeftActionBarItem(getSupportedActionBar().newActionBarItem(NormalActionBarItem.class));
//
//		}
//		item.getItemView().setVisibility(View.VISIBLE);
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		session.removeNetworkChangedListener(this);
//	}
}
