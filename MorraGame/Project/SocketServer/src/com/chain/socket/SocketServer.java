package com.chain.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.chain.socket.model.CMessage;
import com.chain.socket.model.ICode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SocketServer {

	//端口
	private static final int PORT = 1314;
	//服务端
	private ServerSocket server;
	//套接字
	private Socket socket;
	//输出流
	public static PrintWriter os;
	//输入流
	public static BufferedReader is;
	
	/**
	 * 构造方法
	 */
	public SocketServer(){
		try {
			//创建一个服务端监听端口
			server = new ServerSocket(PORT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打开连接
	 */
	private void open(){
		try {
			while(true){
				//建立与客户端之间的连接
				socket = server.accept();
				System.out.println("连接："+socket.isConnected());
				//基于socket通道创建输入流、输入流
				is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				os = new PrintWriter(socket.getOutputStream());
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						receiveMessage();
					}
				}).start();
			}
		} catch (Exception e) {
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
			server.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 接受消息
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
				}else{
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketServer mServer = new SocketServer();
		mServer.open();
	}
}
