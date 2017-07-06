package com.chain.socket.code;

import java.io.PrintWriter;

import com.chain.socket.SocketClient;
import com.chain.socket.model.CMessage;
import com.chain.socket.model.ICode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 注册用户
 * @author chain
 *
 */
public class Code101 implements ICode {

	//命令编号
	private static final int CODE_ID = 101;
	//socket通道输出流
	private PrintWriter os = SocketClient.os;
	
	//接口监听
	private static OnReceiveSiginListener mListener;

	//内部接口
	public interface OnReceiveSiginListener{
		void receiveSiginSuccess(boolean temp);
	}

	public Code101(){
		
	}
	
	/**
	 * 带参数的构造方法
	 * @param mListener
	 */
	public Code101(OnReceiveSiginListener mListener){
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
			//初始化ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			//将对象转换成json字符串
			String userName = message.toString();
			
			//初始化一个CMessage
			CMessage msg = new CMessage();
			msg.setCode(CODE_ID);
			msg.setUserName(userName);
			// 将CMessage对象转换成json字符串
			String strMsg = objectMapper.writeValueAsString(msg);
			//在控制台打印出101所发送的消息
			System.out.println("code101 send:"+strMsg);
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
			//初始化ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			// 将json字符串转换成一个CMessage对象
			CMessage msg = objectMapper.readValue(message.toString(), CMessage.class);
			// 将CMessage中的content转换成一个Boolean对象
			boolean result = objectMapper.readValue(msg.getContent(),Boolean.class);
			//处理客户端操作
			if(mListener != null){
				mListener.receiveSiginSuccess(result);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
