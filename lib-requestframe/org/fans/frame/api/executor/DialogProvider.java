package org.fans.frame.api.executor;

import android.app.Dialog;
import android.content.Context;

/**
 * 提供一个Dialog
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public interface DialogProvider {

	public Dialog provide(Context context);
}
