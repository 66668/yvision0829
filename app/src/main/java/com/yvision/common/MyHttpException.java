package com.yvision.common;

/**
 * Customized Exception
 * 
 * 
 */
public class MyHttpException extends MyException {
	public MyHttpException(int resId) {
		super(resId); 
	}

	public MyHttpException(String message) {
		super(message);
	}
	
	public MyHttpException(String message,int status) {
		super(message,status);
	}

	private static final long serialVersionUID = 1L;
 
}
