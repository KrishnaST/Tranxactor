package com.sil.npci.cbs;

import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.nanolog.Logger;

public abstract class CBSConnector {

	protected final String cbsIp;
	protected final int cbsPort;
	
	public CBSConnector(String cbsIp, int cbsPort) {
		this.cbsIp = cbsIp;
		this.cbsPort = cbsPort;
	}

	public abstract ISO8583Message send(ISO8583Message issuerRequest, ISO8583Message cbsRequest, Logger logger);
	
	public abstract void send(CBSRequest cbsreq, CBSResponse cbsres, Logger logger);
	
}
