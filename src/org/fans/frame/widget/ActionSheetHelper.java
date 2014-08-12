package org.fans.frame.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ActionSheetHelper {

	public static Dialog createDialog(Context context, View view) {
		ActionSheet dialog =  ActionSheet.create(context);
		dialog.setActionContentView(view, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
}
