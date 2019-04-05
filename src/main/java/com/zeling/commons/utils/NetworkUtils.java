package com.zeling.commons.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络工具类
 * 
 * @author chenbd 2018年8月17日
 */
public class NetworkUtils {
	
	 /**
     * ping ip
     * 
     * @return 能ping通返回true，否则false
     */
	public static boolean ping(String ip, long time) throws Exception {
		boolean isReach = false;
		String cmd = "ping -c 1 " + " -w " + time + " " + ip;
		Process p = Runtime.getRuntime().exec(cmd);
		int status = p.waitFor();
		if (status == 0) {
			isReach = true;
		}
		return isReach;
	}
	
	/**
     * 检查ipv4格式的IP是否合法
     * 
     * @param ip ip字符串
     * @return 正确的ip格式返回true，否则false
     */
    public static boolean ip4Valid(String ip) {
        String regex0 = "(2[0-4]\\d)" + "|(25[0-5])";
        String regex1 = "1\\d{2}";
        String regex2 = "[1-9]\\d";
        String regex3 = "\\d";
        String regex = "(" + regex0 + ")|(" + regex1 + ")|(" + regex2 + ")|(" + regex3 + ")";
        regex = "(" + regex + ").(" + regex + ").(" + regex + ").(" + regex  + ")";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ip);
        return m.matches();
    }
    
    /**
     * 获取所有网卡的ip地址列表
     * 
     * @return ip地址列表
     */
    public static List<String> getAllNetworkAddress() throws Exception {
		List<String> result = new ArrayList<>();
		Enumeration<NetworkInterface> netInterfaces;
		netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> addresses = ni.getInetAddresses();
			while (addresses.hasMoreElements()) {
				ip = addresses.nextElement();
				if (ip.isSiteLocalAddress()) {
					result.add(ip.getHostAddress());
				}
			}
		}
		return result;
	}
    
    /**
     * 获取客户机的ip地址
     * 
     * @param request
     * @return 客户机的ip地址
     */
//	public static String getClientIp(HttpServletRequest request) {
//		String ip = request.getHeader("x-forwarded-for");
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("Proxy-Client-IP");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("WL-Proxy-Client-IP");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getRemoteAddr();
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("http_client_ip");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//		}
//		// 如果是多级代理，那么取第一个ip为客户ip
//		if (ip != null && ip.indexOf(",") != -1) {
//			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
//		}
//		return ip;
//	}
	
	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	private NetworkUtils() {
		throw new AssertionError(NetworkUtils.class.getName() + ": 禁止实例化");
	}
}
