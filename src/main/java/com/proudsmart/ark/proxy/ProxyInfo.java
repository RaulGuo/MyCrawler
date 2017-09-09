package com.proudsmart.ark.proxy;

/**
 * @author: 黄文轩		@email: huangwenxuan@proudsmart.com
 *
 * @date: 2016年4月16日	@version: 1.0
 */
public class ProxyInfo implements Comparable{
	
	private String ip;	
	private int port;
	private long response_time = -1;
	
	public ProxyInfo(String ip, int port){	
		this.ip = ip;
		this.port = port;
	}
	
	/**
	 * @param ip
	 * @param port
	 * @param response_time
	 */
	public ProxyInfo(String ip, int port, long response_time){
		this.ip = ip;
		this.port = port;
		this.response_time = response_time;
	}
	
	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the response_time
	 */
	public long getResponseTime() {
		return response_time;
	}
	
	public void setResponseTime(long response_time){
		this.response_time = response_time;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override  
    public int compareTo(Object o) {  
		ProxyInfo obj = (ProxyInfo) o;  
		return (int)(response_time-obj.response_time); 
    }  
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "ip:"+ip+" port:"+port+" response_time:"+response_time;
	}
	
}
