package org.fans.frame.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * 基础Adapter
 * @author Ludaiqian
 * @since 1.0
 * 
 * @param <T>
 */
public abstract class ListAdapter<T> extends BaseAdapter implements RecyclerListener, OnScrollListener {

	protected List<T> mList;
	protected Context mContext;
	protected ListView mListView;
	private List<OnScrollListener> mListeners = new ArrayList<OnScrollListener>();

	public ListAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mList != null ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);

	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	public List<T> getList() {
		return mList;
	}

	public void setList(T[] list) {
		ArrayList<T> arrayList = new ArrayList<T>(list.length);
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList);
	}

	public void addAll(Collection<T> list) {
		this.mList.addAll(list);
		notifyDataSetChanged();
	}

	public ListView getListView() {
		return mListView;
	}

	public void setListView(ListView listView) {
		mListView = listView;
	}

	public Object getItemAtPosition(int position) {
		return mList.get(position);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		// System.out.println("on scrll state changed....");
		for (OnScrollListener l : mListeners) {
			l.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		for (OnScrollListener l : mListeners) {
			l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void addOnScrllStateChangeLinstener(OnScrollListener l) {
		mListeners.add(l);
	}

	@Override
	public void onMovedToScrapHeap(View view) {
		// 必要时回收资源
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	public Context getContext() {
		return mContext;
	}

}
