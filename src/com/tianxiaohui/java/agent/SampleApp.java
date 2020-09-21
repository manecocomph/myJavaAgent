package com.tianxiaohui.java.agent;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SampleApp {

	public static void main(String[] args) {
		System.out.println("start SampleApp");
		try {
			InetAddress.getByName("stackoverflow.com");
			InetAddress.getByName("www.google.com");
			InetAddress.getByName("www.tianxiaohui.com");
			try {
				// bad DNS name
				InetAddress.getByName("notexist.tianxiaohui.com");
			} catch (UnknownHostException e) {
				// do nothing
			}
			
			System.out.println("SampleApp starts to sleep");
			Thread.sleep(500000);
		} catch (InterruptedException | UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("SampleApp end");
	}

}
