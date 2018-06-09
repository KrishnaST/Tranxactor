package com.sil.npci.issuer;

import java.sql.Connection;

import com.sil.npci.cbs.CBSConnector;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.server.NPCIConnector;

public class VoidTransaction extends IssuerTransaction {

	public VoidTransaction(NPCIConnector npcon, CBSConnector cbcon, Connection dbcon, ISO8583Message issreq) {
		super(npcon, cbcon, dbcon, issreq);
	}


	@Override
	public void preCBSRequestDo() {
		// TODO Auto-generated method stub

	}


	@Override
	public void postCBSResponseDo() {
		// TODO Auto-generated method stub

	}

}
