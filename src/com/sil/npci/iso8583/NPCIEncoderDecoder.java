package com.sil.npci.iso8583;

import static com.sil.npci.iso8583.constants.NPCIFormat.length;
import static com.sil.npci.iso8583.constants.NPCIFormat.type;
import static com.sil.npci.iso8583.util.LoggerUtils.DE;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.sil.npci.iso8583.constants.FieldType;
import com.sil.npci.iso8583.util.ParseException;
import com.sil.npci.nanolog.Logger;
import com.sil.npci.util.ByteHexUtil;

public class NPCIEncoderDecoder {

	public static final Logger logger = new Logger("central", "npci_codec");
	
	private static final StringBuilder	dassedLine	= new StringBuilder("-----------------------------------------------------------------------------------------------------------------------\r\n");
	private static final StringBuilder	spaces		= new StringBuilder("                                                          ");
	private static final int			width		= 50;


	public static final ISO8583Message decode(byte[] bytes) throws ParseException {
		ISO8583Message iso8583Message = new ISO8583Message();
		int p = 0;
		int i = 2;
		int len = 0;
		try {
			iso8583Message.putBytes(0, Arrays.copyOfRange(bytes, p, p + 4));
			p = p + 4;
			byte[] primap = Arrays.copyOfRange(bytes, p, p + 8);
			p = p + 8;
			byte[] secmap = null;
			if ((primap[0] & 0x80) == 0x80) {
				secmap = Arrays.copyOfRange(bytes, p, p + 8);
				p = p + 8;
			}
			iso8583Message.bitmap.setPrimaryBytes(primap);
			if (secmap != null) iso8583Message.bitmap.setSecondaryBytes(secmap);
			byte[][] data = iso8583Message.data;
			final Bitmap bitmap = iso8583Message.bitmap;

			for (; i <= 128; i++) {
				if (bitmap.get(i)) {
					len = 0;
					if (type[i] == FieldType.NUM || type[i] == FieldType.CHAR) {
						len = length[i];
						data[i] = Arrays.copyOfRange(bytes, p, p + len);
						p = p + length[i];
					}
					else if (type[i] == FieldType.LLNUM || type[i] == FieldType.LLCHAR) {
						len = (bytes[p] - 48) * 10 + (bytes[p + 1] - 48);
						p = p + 2;
						data[i] = Arrays.copyOfRange(bytes, p, p + len);
						p = p + len;
					}
					else if (type[i] == FieldType.LLLNUM || type[i] == FieldType.LLLCHAR) {
						len = (bytes[p] - 48) * 100 + (bytes[p + 1] - 48) * 10 + (bytes[p + 2] - 48);
						p = p + 3;
						data[i] = Arrays.copyOfRange(bytes, p, p + len);
						p = p + len;
					}
					//System.out.println(i+" : "+new String(data[i]));
				}
			}
		} catch (Exception e) {
			String errorMessage = "error while parsing where field no : " + i + ", pointer at : " + p + ", len : " + len ;
			throw new ParseException(errorMessage);
		}
		return iso8583Message;
	}

	public static final byte[] encode(ISO8583Message iso8583Message) throws ParseException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(166);
		int i = 2;
		int len = 0;
		byte[][] data = iso8583Message.data;
		try {
			baos.write(new byte[] { 0, 0 });
			if(iso8583Message.getBytes(0) != null) baos.write(iso8583Message.getBytes(0));
			else return null;
			Bitmap bitmap = iso8583Message.bitmap;
			baos.write(bitmap.toBytes());
			for (; i <= 128; i++) {
				if (bitmap.get(i)) {
					len = 0;
					if (type[i] == FieldType.NUM) {
						len = length[i];
						baos.write(ByteHexUtil.padLeft(data[i], (byte) '0', len));
					}
					else if (type[i] == FieldType.CHAR) {
						len = length[i];
						baos.write(ByteHexUtil.padRight(data[i], (byte) ' ', len));
					}
					else if (type[i] == FieldType.LLNUM || type[i] == FieldType.LLCHAR) {
						len = data[i].length;
						baos.write((len / 10) + 48);
						baos.write((len % 10) + 48);
						baos.write(data[i]);
					}
					else if (type[i] == FieldType.LLLNUM || type[i] == FieldType.LLLCHAR) {
						len = data[i].length;
						baos.write((len / 100) + 48);
						baos.write(((len % 100) / 10) + 48);
						baos.write(((len % 100) % 10) + 48);
						baos.write(data[i]);
					}
				}
			}
		} catch (Exception e) {
			String errorMessage = "error while parsing where field no : " + i + ", len : " + len + " and remaining message is " + ByteHexUtil.byteToHex(data[i]);
			e.printStackTrace();
			throw new ParseException(errorMessage);
		}
		byte[] iso8583Bytes = baos.toByteArray();
		len = iso8583Bytes.length - 2;
		iso8583Bytes[0] = (byte) (len / 256);
		iso8583Bytes[1] = (byte) (len % 256);
		return iso8583Bytes;
	}

	public static final StringBuilder log(ISO8583Message iso8583Message) {
		StringBuilder logBuilder = new StringBuilder(1700);
		if (iso8583Message == null || iso8583Message.isEmpty()) return null;
		try {
			int flip = 0;
			logBuilder.append("\r\n");
			logBuilder.append(dassedLine);
			byte[][] data = iso8583Message.data;
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null) {
					logBuilder.append(DE[i]);
					if(i != 55) padRightSpecial(logBuilder, data[i], ' ', width);
					else padRightSpecial(logBuilder, ByteHexUtil.byteToHex(data[i]), ' ', width);
					if (flip % 2 == 1) logBuilder.append("|\r\n");
					flip++;
				}
			}
			if (flip % 2 == 1) {
				logBuilder.append("|");
				logBuilder.append(spaces);
				logBuilder.append("|\r\n");
			}
			logBuilder.append(dassedLine);
			return logBuilder;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logBuilder;
	}

	public static final StringBuilder logMasked(ISO8583Message iso8583Message) {
		StringBuilder logBuilder = new StringBuilder(1700);
		if (iso8583Message == null) return null;
		try {
			int flip = 0;
			logBuilder.append(dassedLine);
			byte[][] data = iso8583Message.data;

			if (data[0] != null) {
				logBuilder.append(DE[0]);
				padRightSpecial(logBuilder, data[0], ' ', width);
				if (flip % 2 == 1) logBuilder.append("|\r\n");
				flip++;
			}

			if (data[2] != null) {
				logBuilder.append(DE[2]);
				padRightSpecial(logBuilder, getMaskedPan(new String(data[2])), ' ', width);
				if (flip % 2 == 1) logBuilder.append("|\r\n");
				flip++;
			}

			for (int i = 3; i < 35; i++) {
				if (data[i] != null) {
					logBuilder.append(DE[i]);
					padRightSpecial(logBuilder, data[i], ' ', width);
					if (flip % 2 == 1) logBuilder.append("|\r\n");
					flip++;
				}
			}
			if (data[35] != null) {
				logBuilder.append(DE[35]);
				padRightSpecial(logBuilder, getMaskedPTrack(new String(data[35])), ' ', width);
				if (flip % 2 == 1) logBuilder.append("|\r\n");
				flip++;
			}
			for (int i = 36; i < data.length; i++) {
				if (data[i] != null) {
					logBuilder.append(DE[i]);
					padRightSpecial(logBuilder, data[i], ' ', width);
					if (flip % 2 == 1) logBuilder.append("|\r\n");
					flip++;
				}
			}
			if (flip % 2 == 1) {
				logBuilder.append("|");
				logBuilder.append(spaces);
				logBuilder.append("|");
			}
			logBuilder.append(dassedLine);
			return logBuilder;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logBuilder;
	}


	public static final String getMaskedPan(String pan) {
		if (pan == null || pan.length() < 6) return "";
		else return new StringBuilder(16).append(pan.substring(0, 6)).append("XXXXXX").append(pan.substring(pan.length() - 4)).toString();
	}


	public static final String getMaskedPTrack(String track) {
		int index = track.indexOf('=');
		if (track == null || track.length() < 6) return "";
		else return new StringBuilder(16).append(track.substring(0, 6)).append("XXXXXX").append(track.substring(index - 4, index)).append("=XXXXXXXXXXXXXXXX").toString();
	}


	public static StringBuilder padRightSpecial(StringBuilder sb, String string, char padChar, int padLen) {
		if (string.length() >= padLen) return sb.append(string);
		sb.append(string);
		sb.append("'");
		padLen = padLen - string.length();
		int len = sb.length() + padLen;
		while (sb.length() != len)
			sb.append(padChar);
		return sb;
	}


	public static StringBuilder padRightSpecial(StringBuilder sb, byte[] bytes, char padChar, int padLen) {
		if (bytes.length >= padLen) return sb.append(new String(bytes));
		sb.append(new String(bytes));
		sb.append("'");
		padLen = padLen - bytes.length;
		int len = sb.length() + padLen;
		while (sb.length() != len)
			sb.append(padChar);
		return sb;
	}


	public static void main(String[] args) {
		byte[] bytes  = ByteHexUtil.hexToByte("30323030723C648108E18008313636303735353630303730303233303830303030303030303030303030303039373438303430373137343532333234343930393233313434353034303732313039343831343335363831303539303633303030323338303937323332343439303931313335393939363030303030303031313335393939364F4E45393720434F4D4D554E49434154494F4E53204C494E4F49444120202020202020205550494E313639303531303035504F533031303532303033303439303536303032333130353830303530303939393036313033303130303030303030303030303030303030303030303832373937373534313037313034323130362E3037372E3031322E313635202020202020202020202020202020202020202020202020333536303737303430666665346336326561382D306634612D333838372D376330652D30653562346533343438333434363335363034313531313636313547413030303030303230313330313132303437373034373730202020202020202020");
		byte[] bytes1 = ByteHexUtil.hexToByte("30343230F23864810AE08000000000400000000031363630373535363030373030323330383030303030303030303030303030303937343830343037313734353338323434393039323331343435303430373438313433353638313035393036333030303233383039373233323434393039393131313335393939363030303030303031313335393939364F4E45393720434F4D4D554E49434154494F4E53204C494E4F49444120202020202020205550494E333536303230303234343930393034303731373435323330303030303330303032333030303030303030303030");
		try {
			ISO8583Message iso8583Message = decode(bytes);
			System.out.print(log(iso8583Message));
			ISO8583Message iso8583Message1 = decode(bytes1);
			System.out.print(log(iso8583Message1));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
