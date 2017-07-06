package com.chain.socket.code;

import java.io.PrintWriter;

import com.chain.socket.SocketServer;
import com.chain.socket.model.CMessage;
import com.chain.socket.model.GameMarks;
import com.chain.socket.model.ICode;
import com.chain.socket.service.IGameMarksService;
import com.chain.socket.service.impl.GameMarksService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 用户注册
 * 
 * @author chain
 *
 */
public class Code101 implements ICode {

	// 命令编号
	private static final int CODE_ID = 101;
	// socket通道输出流
	private PrintWriter os = SocketServer.os;
	
	private IGameMarksService gameMarksService = new GameMarksService();

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
			String strMsg = objectMapper.writeValueAsString(message);
			//在控制台打印出101所发送的消息
			System.out.println("101 send:"+strMsg);
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
			String userName = msg.getUserName();
			
			// 初始化一个GameMarks对象
			GameMarks gameMarks = new GameMarks();
			gameMarks.setUserName(userName);
			//添加一个用户到数据库
			boolean result = gameMarksService.add(gameMarks);
			// 将GameMarks对象转换成json字符串
			String content = objectMapper.writeValueAsString(result);
			msg.setContent(content);
			//调用发送
			this.send(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
