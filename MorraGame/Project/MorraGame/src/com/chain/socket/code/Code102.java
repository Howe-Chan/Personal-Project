package com.chain.socket.code;

import java.io.PrintWriter;

import com.chain.socket.SocketClient;
import com.chain.socket.model.CMessage;
import com.chain.socket.model.GameResult;
import com.chain.socket.model.ICode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 请求服务器模拟出拳/判断用户输赢
 * 
 * @author chain
 *
 */
public class Code102 implements ICode {

	// 命令编号
	private static final int CODE_ID = 102;
	// socket通道输出流
	private PrintWriter os = SocketClient.os;


	//接口监听
	private static OnReceiveResultListener mListener;

	//内部接口
	public interface OnReceiveResultListener{
		void receiveResultSuccess(GameResult gameResult);
	}

	public Code102(){
		
	}
	
	/**
	 * 带参数的构造方法
	 * @param mListener
	 */
	public Code102(OnReceiveResultListener mListener){
		this.mListener = mListener;
	}
	
	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return CODE_ID;
	}

	@Override
	public void send(Object message) {
		// TODO Auto-generated method stub
		try {
			// 初始化ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			//将对象转换成json字符串
			String strMsg = objectMapper.writeValueAsString(message);

			//在控制台打印出102所发送的消息
			System.out.println("code102 send:"+strMsg);
			//发送
			os.println(strMsg);
			os.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void receive(Object message) {
		// TODO Auto-generated method stub
		try {
			// 初始化ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			// 将json字符串转换成一个CMessage对象
			CMessage msg = objectMapper.readValue(message.toString(), CMessage.class);
			//将CMessage中的content转换成一个GameResult对象
			GameResult gameResult = objectMapper.readValue(msg.getContent(), GameResult.class);
			//打印客户端102接收到的消息
			System.out.println("client 102 receive:"+message);
			//执行客户端操作
			if(mListener != null){
				mListener.receiveResultSuccess(gameResult);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
