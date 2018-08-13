package com.sil.npci.acquirer;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.NPCIEncoderDecoder;
import com.sil.npci.nanolog.Logger;
import com.sil.npci.server.NPCIConnector;

public abstract class AcquirerTransaction extends Thread {

	protected boolean acquirerValidation = true;

	protected Object					lock			= new Object();
	protected final SimpleDateFormat	de7Formatter	= new SimpleDateFormat("MMddHHmmss");
	protected final ISO8583Message		acqreq;
	protected final ISO8583Message		npreq			= new ISO8583Message();
	protected ISO8583Message			npres;
	protected final ISO8583Message		acqres			= new ISO8583Message();

	protected final Connection		dbcon;
	protected final NPCIConnector	npcon;


	public abstract int getNPCITimeout();


	public abstract void constructNPCIRequest();


	public abstract void constructAcquirerResonse();

	protected Logger logger;


	public AcquirerTransaction(NPCIConnector npcon, ISO8583Message acqreq, Connection dbcon) {
		this.acqreq = acqreq;
		this.dbcon = dbcon;
		this.npcon = npcon;
	}

	private void preNPCIRequestDoCommon() {

	}


	private void postNPCIResponsetDoCommon() {

	}


	public final void run() {
		if(npcon.isShutdowned()) return;
		try(Connection connection = this.dbcon; 
			Logger logger = npcon.getLogger(acqreq == null ? null : acqreq.get(37))) {
			this.logger = logger;
			if (acqreq != null) logger.log(NPCIEncoderDecoder.log(acqreq));
			preNPCIRequestDoCommon();
			constructNPCIRequest();
			if (acquirerValidation) {
				logger.log("npci resuest : " + NPCIEncoderDecoder.log(npreq));
				byte[] logonBytes = NPCIEncoderDecoder.encode(npreq);
				npcon.addToTransactionQ(npreq.get(0), npreq.getKey(), this);
				npcon.send(logonBytes);
				synchronized (lock) {
					try {
						lock.wait(getNPCITimeout());
					} catch (InterruptedException e) {}
				}
				npcon.removeFromTransactionQ(npreq.get(0), npreq.getKey());
			}
			logger.log("npci response : " + NPCIEncoderDecoder.log(npres));
			postNPCIResponsetDoCommon();
			constructAcquirerResonse();
		} catch (Exception e) {
			logger.log(e);
		}
	}


	public Object getLock() {
		return lock;
	}


	public void setResponse(ISO8583Message npres) {
		this.npres = npres;
		synchronized (lock) {
			lock.notify();
		}
	}
}
