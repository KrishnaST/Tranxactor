package com.sil.npci.util;

public class Strings {

	public static boolean isNullOrEmptyOrSpace(String string) {
		return string == null || string.trim().length() == 0;
	}


	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}


	public static boolean isNull(String string) {
		return string == null;
	}


	public static boolean isEmpty(String string) {
		return string.length() == 0;
	}


	public static String padRight(String string, char padChar, int len) {
		if (string == null || string.length() >= len) return string;
		StringBuilder sb = new StringBuilder(len);
		sb.append(string);
		while (sb.length() != len) sb.append(padChar);
		return sb.toString();
	}
	
	public static String padRightSpecial(String string, char padChar, int len) {
		if (string == null || string.length() >= len) return string;
		StringBuilder sb = new StringBuilder(len);
		sb.append(string);
		while (sb.length() != len) sb.append(padChar);
		return sb.toString();
	}
	
	public static String padRight(StringBuilder sb, char padChar, int len) {
		if (sb == null || sb.length() >= len) return sb.toString();
		while (sb.length() != len) sb.append(padChar);
		return sb.toString();
	}

	public static String padLeft(String string, char padChar, int len) {
		if (string == null || string.length() >= len) return string;
		StringBuilder sb = new StringBuilder(len);
		int padLen = len - string.length();
		while (sb.length() != padLen) sb.append(padChar);
		sb.append(string);
		return sb.toString();
	}
	
	public static final String padLeft(long l, char padChar, int len) {
		int padLen = len;
		StringBuilder sb = new StringBuilder(len);
		if(l < 0) {
			padLen--;
			sb.append("-");
			l = -l;
		}
		String string = ""+l;
		if(string.length() >= len) return string;
		padLen = padLen - string.length();
		while(padLen != 0) {
			sb.append(padChar);
			padLen--;
		}
		sb.append(string);
		return sb.toString();		
	}
	
	public static void main(String[] args) {
		System.out.println(padLeft(125l, '0', 10));
	}
}
