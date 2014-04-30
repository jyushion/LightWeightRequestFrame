package org.fans.frame.api.executor;

/**
 * 对一个ListView行异步填充,在填充之前，必须保证已经完成对 Adapter的配置
 * 
 * @author LuDaiqian
 * 
 */
public interface AsyncListViewFiller {

	// /**
	// * 最大失败尝试次数
	// *
	// */
	// static final int MAX_FAILURES = 3;

	/**
	 * 开始一异步填充的任务
	 */
	public void startFillList();

	/**
	 * 是否为空，未填充返回true,若填充过一次数据之后将返回false
	 * 
	 * @return
	 */
	public boolean isEmpty();

}