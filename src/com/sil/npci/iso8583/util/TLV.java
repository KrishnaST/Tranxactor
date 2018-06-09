package com.sil.npci.iso8583.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class TLV {

	public static enum MODE {
		UNIQUE_TAGS, SORTED_BY_TAGS, INSERTION_ORDER,
	}
	
	private int tagSize = 3;
	private int lengthSize = 3;
	private Map<String, String> tlvMap = new LinkedHashMap<>();
	private boolean isChanged = true;
	private StringBuilder tlvBuilder;

	public TLV put(String key, String value) {
		isChanged = true;
		tlvMap.put(key, value);
		return this;
	}

	public String get(String key) {
		return tlvMap.get(key);
	}

	public TLV parse(String tlvString) {
		tlvBuilder = new StringBuilder(tlvString);
		int i = 0;
		try {
			while (i < tlvString.length()) {
				try {
					String tagName = tlvString.substring(i, i + tagSize);
					i = i + tagSize;
					int tagLength = Integer.parseInt(tlvString.substring(i, i + lengthSize));
					i = i + lengthSize;
					tlvMap.put(tagName, tlvString.substring(i, i + tagLength));
					i = i + tagLength;
				}
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			isChanged = true;
			return this;
		}
		isChanged = false;
		return this;
	}

	public TLV parseInsert(String tlvString) {
		tlvBuilder = new StringBuilder(tlvString);
		int i = 0;
		try {
			while (i < tlvString.length()) {
				try {
					String tagName = tlvString.substring(i, i + tagSize);
					i = i + tagSize;
					int tagLength = Integer.parseInt(tlvString.substring(i, i + lengthSize));
					i = i + lengthSize;
					tlvMap.put(tagName, tlvString.substring(i, i + tagLength));
					i = i + tagLength;
				}
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			isChanged = true;
			return this;
		}
		isChanged = false;
		return this;
	}
	
	public String build() {
		if(!isChanged) return tlvBuilder.toString();
		else{
			isChanged = false;
			tlvBuilder = new StringBuilder("");
			tlvMap.forEach((key, value)-> {
				tlvBuilder.append(key);
				tlvBuilder.append(String.format("%0" + lengthSize + "d", value.length()));
				tlvBuilder.append(value);
			});
			return tlvBuilder.toString();
		}
	}

	public String toFormatedString() {
		int k = 0;
		StringBuilder sb = new StringBuilder(300);
		sb.append(String.format("%116s", "").replace(' ', '-')+"\r\n");
		
		for (Map.Entry<String, String> entry : tlvMap.entrySet()) {  
			if(k%2 == 0) {
				sb.append("            ");
				sb.append("| "+String.format("%4s",entry.getKey())+" | "+String.format("%-50s", entry.getValue())+"|");
			}
			else{
				sb.append(String.format("%4s",entry.getKey())+" | "+String.format("%-50s", entry.getValue())+"|"+"\r\n");
			}
			k++;
		}
		sb.append("            ");
		sb.append(String.format("%116s", "").replace(' ', '-')+"\r\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		int k = 0;
		StringBuilder sb = new StringBuilder("");
		sb.append(String.format("%116s", "").replace(' ', '-')+"\r\n");
		
		for (Map.Entry<String, String> entry : tlvMap.entrySet()) {  
			if(k%2 == 0) sb.append("| "+String.format("%4s",entry.getKey())+" | "+String.format("%-50s", "'"+entry.getValue()+"'")+"|");
			else{
				sb.append(String.format("%4s",entry.getKey())+" | "+String.format("%-50s", "'"+entry.getValue()+"'")+"|"+"\r\n");
			}
			k++;
		}
		if(k%2 == 1) sb.append("\r\n");
		sb.append(String.format("%116s", "").replace(' ', '-')+"\r\n");
		return sb.toString();
	}

	public static void main(String[] args) {
		TLV tlv = new TLV();
		tlv.put("002", "PQR");
		tlv.put("001", "XYZ");
		tlv.put("001", "RST");
		System.out.println(tlv);
	}
}
