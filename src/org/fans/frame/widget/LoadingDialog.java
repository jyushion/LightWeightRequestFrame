package org.fans.frame.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;

import com.fans.frame.R;

/**
 * SimpleDialog
 * 
 * @author LuDaiqian
 * 
 */
public class LoadingDialog extends Dialog {

	protected static final String TAG = "IphoneDialog";

	private View mView;

	public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public LoadingDialog(Context context) {
		super(context, R.style.Theme_Transparent);
		mView = LayoutInflater.from(context).inflate(R.layout.progress_bar, null);
		ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.progressbar);
		progressBar.setIndeterminate(true);
		progressBar.setIndeterminateDrawable(new LoadingDrawable(context));
		setContentView(mView);
		LayoutParams a = getWindow().getAttributes();
		a.dimAmount = 0; 
		getWindow().setAttributes(a);
	}

}
