package com.chain.socket.model;

/**
 * 命令接口
 * @author chain
 *
 */
public interface ICode {
	/**
	 * 获取命令号
	 * 
	 * @return
	 */
	public int getCode();

	/**
	 * 发送消息
	 * 
	 * @param message
	 */
	public void send(Object message);

	/**
	 * 接受消息
	 * 
	 * @param message
	 */
	public void receive(Object message);
}
