package com.sil.npci.iso8583;

import static com.sil.npci.iso8583.constants.CBSFormat.length;
import static com.sil.npci.iso8583.constants.CBSFormat.type;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.sil.npci.iso8583.constants.FieldType;
import com.sil.npci.nanolog.Logger;
import com.sil.npci.util.ByteHexUtil;

public class CBSEncoderDecoder {

	public static final Logger logger = new Logger("central", "cbs_codec");
	
	public static final ISO8583Message decode(byte[] bytes) {
		ISO8583Message iso8583Message = new ISO8583Message();
		int p = 0;
		try {
			iso8583Message.putBytes(0, Arrays.copyOfRange(bytes, p, p + 4));
			//System.out.println("mti : " + new String(iso8583Message.data[0]));
			p = p + 4;
			byte[] primap = ByteHexUtil.hexToByte(new String(Arrays.copyOfRange(bytes, p, p + 16), StandardCharsets.US_ASCII));
			p = p + 16;
			byte[] secmap = null;
			if ((primap[0] & 0x80) == 0x80) {
				secmap = ByteHexUtil.hexToByte(new String(Arrays.copyOfRange(bytes, p, p + 16), StandardCharsets.US_ASCII));
				p = p + 16;
			}
			iso8583Message.bitmap.setPrimaryBytes(primap);
			if (secmap != null) iso8583Message.bitmap.setSecondaryBytes(secmap);
			byte[][] data = iso8583Message.data;
			final Bitmap bitmap = iso8583Message.bitmap;

			for (int i = 2; i <= 128; i++) {
				if (bitmap.get(i)) {
					//System.out.println(i+ " : "+length[i]);
					int len = 0;
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
					//System.out.println((i)+ " : '"+new String(data[i])+"'");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return iso8583Message;
	}


	public static final byte[] encode(ISO8583Message iso8583Message) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(150);
		try {
			baos.write(new byte[] {0, 0, 0, 0});
			if(iso8583Message.getBytes(0) != null) baos.write(iso8583Message.getBytes(0));
			Bitmap bitmap = iso8583Message.bitmap;
			baos.write(ByteHexUtil.byteToHex(bitmap.toBytes()).getBytes());
			for (int i = 2; i <= 128; i++) {
				if (bitmap.get(i)) {
					if (type[i] == FieldType.NUM || type[i] == FieldType.CHAR) {
						int len = length[i];
						baos.write(ByteHexUtil.padRight(iso8583Message.getBytes(i), (byte) ' ', len));
					}
					else if (type[i] == FieldType.LLNUM || type[i] == FieldType.LLCHAR) {
						int len = iso8583Message.getBytes(i).length;
						baos.write((len / 10) + 48);
						baos.write((len % 10) + 48);
						baos.write(iso8583Message.getBytes(i));
					}
					else if (type[i] == FieldType.LLLNUM || type[i] == FieldType.LLLCHAR) {
						int len = iso8583Message.getBytes(i).length;
						baos.write((len / 100) + 48);
						baos.write(((len % 100) / 10) + 48);
						baos.write(((len % 100) % 10) + 48);
						baos.write(iso8583Message.getBytes(i));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		byte[] iso8583Bytes = baos.toByteArray();
		int len = iso8583Bytes.length;
		byte[] lenBytes = String.format("%04d", len-4).getBytes(StandardCharsets.US_ASCII);
		iso8583Bytes[0] = lenBytes[0];
		iso8583Bytes[1] = lenBytes[1];
		iso8583Bytes[2] = lenBytes[2];
		iso8583Bytes[3] = lenBytes[3];
		return iso8583Bytes;
	}

	public static void main(String[] args) {
	
		ISO8583Message iso8583Message = decode(ByteHexUtil.hexToByte("303230304632334143343031323845313930313030303030303030303034303030303030313636303735353630303730303232313136333130303030303030303030303030303030303430353030343333343334313834303036313333333034303530343035303430353630313130323130363830303032373337363037353536303037303032323131363d323130383532303737383030303030303030303038303935303633333230393043505538303136202020202020202020435055383031362020202020202020424f492053415450555220494e442045535441544520204e415348494b202020202020204d48494e303434434153484e455420202020202020202020202020202020203430303030303335363030303030303030303030333536324632393138433942454237393430393031324555524f50524f312b3030303138303030373233202020203030303031323939"));
		System.out.println(NPCIEncoderDecoder.logMasked(iso8583Message));
		byte[] bytes = CBSEncoderDecoder.encode(iso8583Message);
		System.out.println("bytes : "+ByteHexUtil.byteToHex(bytes));
		
		iso8583Message = decode(ByteHexUtil.hexToByte("303230304632334143343031323845313930313030303030303030303034303030303030313636303731303239393930333038333232303030303030303030303030303636333030303332343231343130383030363430313033313130383033323530333235303332353538313330353130363730303030323337363037313032393939303330383332323D3232303836323036383830303030303030303030383038343033303036343031343030393132363053414E44454550204241522E20202053414E44454550204241522E20202020202020202020204D554D424149202020202020204D41494E303434434153484E455420202020202020202020202020202020203430303030303335363030303030303030303030333536414543424445393336344236383933363031324555524F50524F312B3030303135303037303230303030303037303435"));
		System.out.print(NPCIEncoderDecoder.logMasked(iso8583Message));
	}
}
