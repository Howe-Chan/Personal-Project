package com.chain.socket.code;

import java.io.PrintWriter;

import com.chain.socket.SocketServer;
import com.chain.socket.model.CMessage;
import com.chain.socket.model.GameResult;
import com.chain.socket.model.ICode;
import com.chain.socket.util.MorraGame;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 请求电脑出拳/并返回游戏结果
 * @author chain
 *
 */
public class Code102 implements ICode {

	// 命令编号
	private static final int CODE_ID = 102;
	// socket通道输出流
	private PrintWriter os = SocketServer.os;

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return CODE_ID;
	}

	@Override
	public void send(Object message) {
		// TODO Auto-generated method stub
		try {
			// 初始化一个ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			//将对象转换成一个json字符串
			String strMsg = objectMapper.writeValueAsString(message);
			//在控制台中打印出102发送出去的消息
			System.out.println("102 send:"+strMsg);
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
			// 将CMessage对象中的content转换成一个GameResult对象
			GameResult gameResult = objectMapper.readValue(msg.getContent(), GameResult.class);
			// 通过UserShot初始化一个MorraGame
			MorraGame morraGame = new MorraGame(gameResult.getUserShot());
			gameResult = morraGame.getRuselt();
			
			//将GameResult对象转换成一个json字符串
			String content = objectMapper.writeValueAsString(gameResult);
			msg.setContent(content);
			//调用发送
			this.send(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
