package org.fans.frame.api.packet;

/**
 * 对返回接口的抽象，没有任何字段，所以不会和任何服务端接口耦合， <br>
 * 虽然服务器返回的接口字段不同，但大致方法归类如下
 * 
 * @author Ludaiqian
 * 
 * @param <Result>
 * @since 1.0
 */
public interface ApiResponse<Result> extends ApiPacket {
	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess();

	/**
	 * 响应代码
	 * 
	 * @return
	 */
	public int getResultCode();

	/**
	 * 数据
	 * 
	 * @return
	 */
	public Result getData();

	/**
	 * 文本消息描述
	 * 
	 * @return
	 */
	public String getMessage();

	/**
	 * 错误信息，这里不用泛型，因为大多时候，我们并不太关注Error。 如果设计成ApiResponse<Result,Error>
	 * ，那么每一个返回接口，都需要一个Error
	 * 
	 * @return
	 */
	public Error getError();
}
