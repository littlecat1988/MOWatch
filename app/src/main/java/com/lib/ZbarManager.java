package com.lib;

/**
 * 浣滆�: 闄堟稕(1076559197@qq.com)
 * 
 * 鏃堕棿: 2014骞�鏈�鏃�涓嬪崍12:25:46
 *
 * 鐗堟湰: V_1.0.0
 *
 * 鎻忚堪: zbar璋冪敤绫� */
public class ZbarManager {

	static {
		System.loadLibrary("zbar");
	}

	public native String decode(byte[] data, int width, int height, boolean isCrop, int x, int y, int cwidth, int cheight);
}
