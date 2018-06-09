package com.sil.npci.cbs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

import com.sil.npci.config.GlobalConfig;
import com.sil.npci.iso8583.CBSEncoderDecoder;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.NPCIEncoderDecoder;
import com.sil.npci.nanolog.Logger;
import com.sil.npci.util.ByteHexUtil;

public class SILCBSConnector extends CBSConnector{

	public SILCBSConnector(String cbsIp, int cbsPort) {
		super(cbsIp, cbsPort);
	}

	/**
	-----------------------------------------------------------------------------------------------------------------------
	|000 : '0200'                                              |002 : '6077990020000011'                                  |
	|003 : '000000'                                            |004 : '000000060000'                                      |
	|007 : '0529080731'                                        |011 : '000001'                                            |
	|012 : '133731'                                            |013 : '0529'                                              |
	|019 : '356'                                               |032 : '720001'                                            |
	|037 : '814913000001'                                      |041 : 'TEST1234'                                          |
	|049 : '356'                                               |102 : '0002SB    00079250'                                |
	-----------------------------------------------------------------------------------------------------------------------
	**/
	
	@Override
	public void send(CBSRequest cbsreq, CBSResponse cbsres, Logger logger) {
		
		logger.log(GlobalConfig.GSON_BUILDER.create().toJson(cbsreq));
		cbsres.responsecode = "91";
		ISO8583Message cbsreqiso = new ISO8583Message();
		cbsreqiso.put(0, cbsreq.mti);
		cbsreqiso.put(2, cbsreq.pan);
		cbsreqiso.put(3, cbsreq.pcode);
		cbsreqiso.put(4, cbsreq.amount);
		cbsreqiso.put(11, cbsreq.stan);
		cbsreqiso.put(12, cbsreq.time);
		cbsreqiso.put(13, cbsreq.day);
		cbsreqiso.put(19, cbsreq.countrycode);
		cbsreqiso.put(32, cbsreq.acqid);
		cbsreqiso.put(37, cbsreq.rrn);
		cbsreqiso.put(41, cbsreq.tid);
		cbsreqiso.put(49, cbsreq.currencycode);
		cbsreqiso.put(102, cbsreq.account);
		logger.log("cbs request : "+NPCIEncoderDecoder.log(cbsreqiso));
		
		try(Socket socket = new Socket(cbsIp, cbsPort);
			BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
			socket.setSoTimeout(100000);
			byte[] requestBytes = CBSEncoderDecoder.encode(cbsreqiso);
			logger.log("cbs request : "+ByteHexUtil.byteToHex(requestBytes));
			bos.write(requestBytes);
			bos.flush();
			int b1 = bin.read();
			int b2 = bin.read();
			int b3 = bin.read();
			int b4 = bin.read();
			int len = Integer.parseInt(new String(new byte[] {(byte) b1, (byte) b2, (byte) b3, (byte) b4}));
			byte[] responseBytes = new byte[len];
			bin.read(responseBytes);
			logger.log("cbs response : "+ByteHexUtil.byteToHex(responseBytes));
			ISO8583Message cbsresiso = CBSEncoderDecoder.decode(responseBytes);
			logger.log(NPCIEncoderDecoder.log(cbsresiso));
			if(cbsresiso == null) return;
			cbsres.responsecode = cbsresiso.get(39);
			cbsres.authcode = cbsresiso.get(38);
			cbsres.balance = 0;
			logger.log(GlobalConfig.GSON_BUILDER.create().toJson(cbsres));
		} catch (Exception e) {
			logger.log("null or invalid cbs response : "+e.getMessage());
		}
	}
	
	@Override
	public ISO8583Message send(ISO8583Message issuerRequest, ISO8583Message cbsRequest, Logger logger) {
		cbsRequest.put(0, issuerRequest.get(0));
		cbsRequest.put(2, issuerRequest.get(2));
		cbsRequest.put(3, issuerRequest.get(3));
		cbsRequest.put(4, issuerRequest.get(4));
		cbsRequest.put(11, issuerRequest.get(11));
		cbsRequest.put(12, issuerRequest.get(12));
		cbsRequest.put(13, issuerRequest.get(13));
		cbsRequest.put(19, issuerRequest.get(19));
		cbsRequest.put(32, issuerRequest.get(32));
		cbsRequest.put(37, issuerRequest.get(37));
		cbsRequest.put(41, issuerRequest.get(41));
		cbsRequest.put(49, issuerRequest.get(49));
		cbsRequest.put(102, issuerRequest.get(102));
		logger.log(NPCIEncoderDecoder.log(cbsRequest));
		try(Socket socket = new Socket(cbsIp, cbsPort);
			BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream()))
		{
			socket.setSoTimeout(100000);
			byte[] requestBytes = CBSEncoderDecoder.encode(cbsRequest);
			logger.log("cbs request : "+ByteHexUtil.byteToHex(requestBytes));
			bos.write(requestBytes);
			bos.flush();
			int b1 = bin.read();
			int b2 = bin.read();
			int b3 = bin.read();
			int b4 = bin.read();
			int len = Integer.parseInt(new String(new byte[] {(byte) b1, (byte) b2, (byte) b3, (byte) b4}));
			byte[] responseBytes = new byte[len];
			bin.read(responseBytes);
			logger.log("cbs response : "+ByteHexUtil.byteToHex(responseBytes));
			ISO8583Message cbsResponse = CBSEncoderDecoder.decode(responseBytes);
			logger.log(NPCIEncoderDecoder.log(cbsResponse));
			return cbsResponse;
		} catch (Exception e) {
			logger.log("null or invalid cbs response : "+e.getMessage());
		}
		return null;
	}

	/**
	-----------------------------------------------------------------------------------------------------------------------
	|000 : '0210'                                              |002 : '6077990020000011'                                  |
	|003 : '000000'                                            |004 : '000000060000'                                      |
	|007 : '0529080731'                                        |011 : '000001'                                            |
	|012 : '133731'                                            |013 : '0529'                                              |
	|019 : '356'                                               |032 : '720001'                                            |
	|037 : '814913000001'                                      |038 : '002917'                                            |
	|039 : '00'                                                |041 : 'TEST1234        '                                  |
	|049 : '356'                                               |054 : '1001356C000094973760 1002356C000094973760'          |
	|102 : '0002SB    00079250'                                |                                                          |
	-----------------------------------------------------------------------------------------------------------------------
	**/
}
