package com.chain.morragame.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputCheck {
	
	/**
	 * 验证Port是否合法
	 * @param port
	 * @return true:Port合法	false:Port非法
	 */
	public boolean portCheck(String port){
		boolean temp = false;
		Pattern pattern = Pattern.compile(
				"^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}"
				+ "|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}"
				+ "|655[0-2]\\d|6553[0-5])$");
		Matcher matcher = pattern.matcher(port);
		temp = matcher.matches();
		return temp;
	}
	
	/**
	 * 验证IP是否合法
	 * @param ip
	 * @return true:IP合法	false:IP非法
	 */
	public boolean ipCheck(String ip){
		boolean temp = false;
		Pattern pattern = Pattern.compile(
				"\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."
				+ "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."
				+ "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."
				+ "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		temp = matcher.matches();
		return temp;
	}
}
