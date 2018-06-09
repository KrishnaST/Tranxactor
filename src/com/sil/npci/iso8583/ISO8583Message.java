package com.sil.npci.iso8583;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.sil.npci.iso8583.constants.ISO8583Field;

public class ISO8583Message {

	protected final byte[][]	data				= new byte[129][];
	protected final Bitmap		bitmap				= new Bitmap();

	public ISO8583Message() {
	}

	public String get(int i) {
		if(data[i] == null) return null;
		return new String(data[i], StandardCharsets.US_ASCII);
	}


	public byte[] getBytes(int i) {
		return data[i];
	}


	// Insert a field in ISO8583 Message
	public ISO8583Message put(int i, String string) {
		if (string == null || string.length() == 0) return this;
		data[i] = string.getBytes(StandardCharsets.US_ASCII);
		bitmap.set(i);
		return this;
	}


	// Insert a field in ISO8583 Message
	public ISO8583Message put(int i, ISO8583Field field) {
		if (field == null || field.toString().length() == 0) return this;
		data[i] = field.toString().getBytes(StandardCharsets.US_ASCII);
		bitmap.set(i);
		return this;
	}


	// Insert a field in ISO8583 Message
	public ISO8583Message putBytes(int i, byte[] bytes) {
		if (bytes == null) return this;
		data[i] = bytes;
		bitmap.set(i);
		return this;
	}


	// Remove a field from ISO8583 Message
	public String remove(int i) {
		bitmap.remove(i);
		if (data[i] == null) return "";
		String string = new String(data[i], StandardCharsets.US_ASCII);
		data[i] = null;
		return string;
	}


	public String getKey() {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append((data[2] == null)  ? "" : new String(data[2],  StandardCharsets.US_ASCII).hashCode());
		keyBuilder.append((data[11] == null) ? "" : new String(data[11], StandardCharsets.US_ASCII).hashCode());
		keyBuilder.append((data[32] == null) ? "" : new String(data[32], StandardCharsets.US_ASCII).hashCode());
		keyBuilder.append((data[37] == null) ? "" : new String(data[37], StandardCharsets.US_ASCII).hashCode());
		keyBuilder.append((data[41] == null) ? "" : new String(data[41], StandardCharsets.US_ASCII).hashCode());
		return keyBuilder.toString();
	}

	public ISO8583Message copyOf() {
		ISO8583Message copyMessage = new ISO8583Message();
		for(int i=0; i<data.length;i++) {
			if(data[i] != null) copyMessage.data[i] = Arrays.copyOf(data[i], data[i].length);
		}
		copyMessage.bitmap.setPrimaryLong(bitmap.getPrimaryLong());
		copyMessage.bitmap.setSecondaryLong(bitmap.getSecondaryLong());
		return copyMessage;
	}
	
	public boolean isEmpty() {
		return bitmap.isEmpty();
	}
}
