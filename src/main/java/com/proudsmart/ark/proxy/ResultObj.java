package com.proudsmart.ark.proxy;

import java.io.Serializable;

public class ResultObj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2667469405535897205L;
	public String msg;
	public int code;
	public ResultData data;
	
	public class ResultData{
		public int count;
		public String[] proxy_list;
	}
}
