package com.chain.socket.code;

import java.io.PrintWriter;

import com.chain.socket.SocketServer;
import com.chain.socket.model.GameMarks;
import com.chain.socket.model.ICode;
import com.chain.socket.model.CMessage;
import com.chain.socket.service.IGameMarksService;
import com.chain.socket.service.impl.GameMarksService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 提交分数
 * @author chain
 *
 */
public class Code104 implements ICode {

	//命令编号
	private static final int CODE_ID = 104;
	//socket通道输出流
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
			//初始化一个ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			//将对象转换成一个json字符串
			String strMsg = objectMapper.writeValueAsString(message);
			//在控制台打印104所发出的消息
			System.out.println("104 send:"+strMsg);
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
			// 初始化一个ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			// 将json字符串转换成一个CMessage对象
			CMessage msg = objectMapper.readValue(message.toString(), CMessage.class);
			// 将CMessage对象中的content转换成一个GameMarks对象
			GameMarks gameMarks = objectMapper.readValue(msg.getContent(), GameMarks.class);
			//将该用户的分数进行更新
			boolean result = gameMarksService.update(gameMarks);
			
			
			//将boolean转换成json字符串
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
