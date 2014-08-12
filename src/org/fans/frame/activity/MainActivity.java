package org.fans.frame.activity;

import org.fans.frame.api.packet.demo.request.Request;
import org.fans.frame.db.ormlite.DataHelpeFactory;
import org.fans.frame.db.ormlite.DataHelper;
import org.fans.frame.utils.Utils;

import android.os.Bundle;

public class MainActivity extends NetworkActivity {
	//保存  BaseApiRequest的实例对象，以String类型作为id
	DataHelper<Request, String> helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.)
		helper = DataHelpeFactory.newDataHelper(this, Request.class);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		helper.close();
	}
}
