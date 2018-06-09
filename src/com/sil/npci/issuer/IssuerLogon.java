package com.sil.npci.issuer;

import java.sql.Connection;

import com.sil.npci.cbs.CBSConnector;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.constants.LogonType;
import com.sil.npci.iso8583.constants.MTI;
import com.sil.npci.server.NPCIConnector;

public class IssuerLogon extends IssuerTransaction {

	public IssuerLogon(NPCIConnector npcon, CBSConnector cbcon, Connection dbcon, ISO8583Message issreq) {
		super(npcon, cbcon, dbcon, issreq);
	}

	@Override
	public void preCBSRequestDo() {
		isvalidrequest 	= false;
		send_dirty 		= true;
	}

	@Override
	public void postCBSResponseDo() {
		if(LogonType.LOGON.toString().equals(issreq.get(70))) {
			if(!npcon.isLoggedOn()) npcon.setLogon(true);;
		}
		else if(LogonType.LOGOFF.toString().equals(issreq.get(70))) {
			if(!npcon.isLoggedOn()) npcon.setNPCILogOff();
		}
		else if(LogonType.ECHO_LOGON.toString().equals(issreq.get(70))) {
			if(!npcon.isLoggedOn()) logger.log("issuer logon success.");
		}
		issres.put(0, MTI.NET_MGMT_RESPONSE);
		issres.put(7, issreq.get(7));
		issres.put(11, issreq.get(11));
		issres.put(39, "00");
		issres.put(70, issreq.get(70));
	}

}
