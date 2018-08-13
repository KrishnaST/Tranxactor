package com.sil.npci.issuer;

import java.sql.Connection;

import com.sil.npci.cbs.CBSConnector;
import com.sil.npci.cbs.CBSRequest;
import com.sil.npci.cbs.CBSResponse;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.NPCIEncoderDecoder;
import com.sil.npci.nanolog.Logger;
import com.sil.npci.server.NPCIConnector;
import com.sil.npci.util.ByteHexUtil;

public abstract class IssuerTransaction extends Thread{

	protected boolean isvalidrequest 	= true;
	protected boolean send_dirty 		= false;
	
	protected final NPCIConnector npcon;
	protected final CBSConnector cbcon;
	protected final Connection dbcon;
	protected final ISO8583Message issreq;
	protected final CBSRequest cbsreq = new CBSRequest();
	protected final CBSResponse cbsres = new CBSResponse();
	protected final ISO8583Message issres = new ISO8583Message();
	
	protected Logger logger = null;
	
	public IssuerTransaction(NPCIConnector npcon, CBSConnector cbcon, Connection dbcon, ISO8583Message issreq) {
		this.npcon = npcon;
		this.cbcon = cbcon;
		this.dbcon = dbcon;
		this.issreq = issreq;
	}
	
	public abstract void preCBSRequestDo();
	public abstract void postCBSResponseDo();

	
	private void preCBSRequestDoCommon() {
		
	}
	
	private void postCBSResponseDoCommon() {
		
	}
	public final void run() {
		try(Connection connection = this.dbcon;
			Logger logger = npcon.getLogger(issreq.get(37))) {
			logger.log("issuer class name : "+this.getClass().getName());
			this.logger = logger;
			logger.log("issuerRequest : "+NPCIEncoderDecoder.log(issreq));
			preCBSRequestDoCommon();
			preCBSRequestDo();
			if(isvalidrequest) cbcon.send(cbsreq, cbsres, logger);
			postCBSResponseDoCommon();
			postCBSResponseDo();
			byte[] responseBytes = NPCIEncoderDecoder.encode(issres);
			logger.log("issuerResponse : "+ByteHexUtil.byteToHex(responseBytes));
			if(!send_dirty && responseBytes != null) npcon.send(responseBytes);
			else if(responseBytes != null) npcon.sendDirty(responseBytes);
			else logger.log("null response for "+this.getClass().getName());
			logger.log("issuerResponse : "+NPCIEncoderDecoder.log(issres));
			logger.log("transaction completed successfully.");
		} catch (Exception e) {
			logger.log(this.getClass().getName());
			logger.log(e);
		}
	}
	
}
