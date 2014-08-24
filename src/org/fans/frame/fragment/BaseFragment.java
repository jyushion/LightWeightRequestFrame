package org.fans.frame.fragment;

import org.fans.frame.FansApplication;
import org.fans.frame.Session;
import org.fans.frame.Session.NetworkChangedListener;
import org.fans.frame.utils.Logger;
import org.fans.frame.utils.NetworkUtil;
import org.fans.frame.widget.LoadingDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基本类
 * 
 * @author Bsn
 * @author Ludaiqian
 * 
 */
public abstract class BaseFragment extends Fragment implements NetworkChangedListener {

	private Session session;
	private boolean networkAvailable;
	private FansApplication applicationContext;
	private View mLayoutView;
	private boolean isCompleted = false;
	private LayoutInflater actinflater;


	private boolean actionBarSurrported = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		applicationContext = FansApplication.getInstance();
		session = applicationContext.getSession();
		networkAvailable = session.isNetworkAvailable();
		session.addNetworkChangedListener(this);
		Logger.i("onCreate:" + getClass().getSimpleName());

	}

	public void setActionBarSurrported(boolean actionBarSurrported) {
		this.actionBarSurrported = actionBarSurrported;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public  View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (mLayoutView == null) {
			onPreInflate(inflater, container, savedInstanceState);
			mLayoutView = inflate(inflater);
			onInflateView(mLayoutView);
			onPrepareData();
			isCompleted = true;
		} else {

			ViewGroup parent = (ViewGroup) mLayoutView.getParent();
			if (parent != null)
				parent.removeView(mLayoutView);
			onRefreshData();
		}
		actinflater = inflater;
		return mLayoutView;

	}

	protected void onPreInflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	}

	private View inflate(LayoutInflater inflater) {

//		if (actionBarSurrported) {
//			FragmentActionBarHost fragmentActionBarHost = new FragmentActionBarHost(getActivity());
//			RelativeLayout contentView = (RelativeLayout) fragmentActionBarHost.getContentView();
//			inflater.inflate(getContentViewId(), contentView);
//			fragmentActionBarHost.getActionBar().setOnActionBarListener(mActionBarListener);
//			mActionBarHost = fragmentActionBarHost;
//			return fragmentActionBarHost;
//		} else {
			return inflater.inflate(getContentViewId(), null);
//		}
	}

	public void onActionBarItemClick(View item, int position) {
	}

	// public ActionBarItem addActionBarItem(ActionBarItem item, int itemId) {
	// return getSupportedActionBar().addItem(item, itemId);
	// }
	//
	// public ActionBarItem addActionBarItem(ActionBarItem item) {
	// return getSupportedActionBar().addItem(item);
	// }



	// public ActionBarItem getLeftActionBarItem() {
	// // ensureLayout();
	// return isActionBarSurrpoted() ?
	// mActionBarHost.getActionBar().getLeftActionBarItem() : null;
	// }

	@Override
	public void onStart() {
		super.onStart();

	}

	public boolean isCompleted() {
		return isCompleted;
	}

	protected LayoutInflater getInflater() {
		return actinflater;
	}

	public View getContentView() {
		return mLayoutView;
	}

	/**
	 * 只运行一次
	 */
	public void onInflateView(View contentView) {

	}

	/**
	 * 只运行一次
	 */
	public void onPrepareData() {
	}

	public void onRefreshData() {

	}

	protected abstract int getContentViewId();

	@Override
	public void onNetworkStateChanged(boolean networkAvailable) {
		logI("net work changed......" + networkAvailable);
		this.networkAvailable = networkAvailable;
	}

	public final boolean isNetworkAvailable(boolean immediatly) {
		if (immediatly)
			return NetworkUtil.isNetworkAvailable(getActivity());
		return networkAvailable;
	}

	public final boolean isNetworkAvailable() {
		return isNetworkAvailable(true);
	}

	public final Session getSession() {
		return session;
	}

	public void logI(Object message) {
		Logger.i(getClass(), String.valueOf(message));
	}

	public void logE(Object message) {
		Logger.e(getClass(), String.valueOf(message));
	}

	public void quickCache(Object value) {
		quickCache(getClass().toString(), value);
	}

	public void quickCache(String key, Object value) {
		applicationContext.cache(key, value);
	}

	public Object popCache(String key) {
		return applicationContext.remove(key);
	}

	@Override
	public void onDestroy() {
		session.removeNetworkChangedListener(this);
		super.onDestroy();
	}

	/**
	 * 返回titlebar的高度 in pixels
	 * 
	 * @return
	 */
	// public int getTitleBarHeight() {
	// return getResources().getDimensionPixelSize(R.dimen.titile_height);
	// }

	protected Dialog createDialog() {
		return new LoadingDialog(getActivity());
	}

	protected void back() {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.popBackStack();
	}
}
