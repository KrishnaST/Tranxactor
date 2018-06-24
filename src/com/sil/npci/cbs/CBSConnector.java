package com.sil.npci.cbs;

import com.sil.npci.nanolog.Logger;

public abstract class CBSConnector {

	protected final String cbsIp;
	protected final int cbsPort;
	
	public CBSConnector(String cbsIp, int cbsPort) {
		this.cbsIp = cbsIp;
		this.cbsPort = cbsPort;
	}

	public abstract void send(CBSRequest cbsreq, CBSResponse cbsres, Logger logger);
	
}
