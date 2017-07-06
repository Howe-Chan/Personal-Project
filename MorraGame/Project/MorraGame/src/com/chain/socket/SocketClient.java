package com.chain.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.chain.socket.model.CMessage;
import com.chain.socket.model.ICode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * socket客户端
 * @author chain
 *
 */
public class SocketClient {

	//套接字
	public Socket socket;
	//输出流
	public static PrintWriter os;
	//输入流
	public static BufferedReader is;
	
	
	/**
	 * 打开
	 * @param ip
	 * @param port
	 * @return
	 */
	public void open(String ip, int port){
		try {
			//建一个与服务器的连接
			socket = new Socket(ip, port);
			//基于socket创建输入流、输出流
			os = new PrintWriter(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					receiveMessage();
				}
			}).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭
	 */
	public void close(){
		try {
			os.close();
			is.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 接收信息
	 */
	public void receiveMessage(){
		try{
			while(true){
				//读出信息
				String message = is.readLine();
				//在控制台打印出接受到的消息
				System.out.println("message:"+message);
				if(message != null){
					//初始化一个ObjectMapper
					ObjectMapper mObjectMapper = new ObjectMapper();
					//将json字符串转换成一个CMessage对象
					CMessage mMessage = mObjectMapper.readValue(message.toString(), CMessage.class);
					//java反射(通过类名反射出实体类)
					Class<?> cls = Class.forName("com.chain.socket.code.Code"+mMessage.getCode());
					if(cls == null){
						return;
					}else{
						//实例出一个命令类
						ICode code = (ICode) cls.newInstance();
						//调用其接受命令的方法
						code.receive(message);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
