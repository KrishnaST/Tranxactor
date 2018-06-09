package com.sil.npci.issuer;

import java.sql.Connection;

import com.sil.npci.cbs.CBSConnector;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.constants.MTI;
import com.sil.npci.server.NPCIConnector;

public class FullMagstripeReversal extends IssuerTransaction {

	public FullMagstripeReversal(NPCIConnector npcon, CBSConnector cbcon, Connection dbcon, ISO8583Message issreq) {
		super(npcon, cbcon, dbcon, issreq);
	}


	@Override
	public void preCBSRequestDo() {
		cbsreq.mti 			= issreq.get(0);
		cbsreq.pan 			= issreq.get(2);
		cbsreq.pcode 		= issreq.get(3);
		cbsreq.amount 		= issreq.get(4);
		cbsreq.de7 			= issreq.get(7);
		cbsreq.stan 		= issreq.get(11);
		cbsreq.time		 	= issreq.get(12);
		cbsreq.day 			= issreq.get(13);
		cbsreq.countrycode 	= issreq.get(19);
		cbsreq.acqid 		= issreq.get(32);
		cbsreq.rrn 			= issreq.get(37);
		cbsreq.tid 			= issreq.get(41);
		cbsreq.currencycode = issreq.get(49);
		cbsreq.account 		= "0002SB    00079250";
	}


	@Override
	public void postCBSResponseDo() {
		issres.put(0, MTI.ISS_REVERSAL_RESPONSE);
		issres.put(2, issreq.get(2));
		issres.put(3, issreq.get(3));
		issres.put(4, issreq.get(4));
		issres.put(7, issreq.get(7));
		issres.put(11, issreq.get(11));
		issres.put(12, issreq.get(12));
		issres.put(13, issreq.get(13));
		issres.put(19, issreq.get(19));
		issres.put(32, issreq.get(32));
		issres.put(37, issreq.get(37));
		issres.put(41, issreq.get(41));
		issres.put(49, issreq.get(49));
		if(cbsres == null) issres.put(39, "91");
		else {
			issres.put(38, cbsres.authcode);
			issres.put(39, cbsres.responsecode);
		}
	}
}
