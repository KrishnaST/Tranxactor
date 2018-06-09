package com.sil.npci.acquirer;

import java.sql.Connection;
import java.util.Date;
import java.util.Random;

import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.constants.LogonType;
import com.sil.npci.iso8583.constants.MTI;
import com.sil.npci.server.NPCIConnector;

public class AcquirerLogon extends AcquirerTransaction {

	private final Random random = new Random();
	private int counter = 0;
	public AcquirerLogon(NPCIConnector npcon, ISO8583Message acqreq, Connection dbcon) {
		super(npcon, acqreq, dbcon);
	}

	public AcquirerLogon(NPCIConnector npcon, ISO8583Message acqreq, Connection dbcon, int counter) {
		super(npcon, acqreq, dbcon);
		this.counter = counter;
	}

	@Override
	public void constructNPCIRequest() {
		logger.log("counter : "+counter);
		if(counter > 0) try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			logger.log("extended logon sleep error.");
		}
		npreq.put(0, MTI.NET_MGMT_REQUEST);
		npreq.put(11, String.format("%06d", random.nextInt(999999)));
		npreq.put(7, de7Formatter.format(new Date()));
		if (!npcon.isLoggedOn()) npreq.put(70, LogonType.LOGON);
		else npreq.put(70, LogonType.ECHO_LOGON);
	}

	@Override
	public void constructAcquirerResonse() {
		if (npres == null) {
			logger.log("no logon response");
			if(npreq.get(70).equals(LogonType.LOGON) && counter < 4) new AcquirerLogon(npcon, acqreq, dbcon, counter + 1).start();
			return;
		}
		if(npres.get(39).equals("00")) {
			if (npres.get(70).equals(LogonType.LOGON)) {
				npcon.setLogon(true);
			}
			else if (npres.get(70).equals(LogonType.ECHO_LOGON)) {
				if (npres.get(39).equals("00")) logger.log("echo logon success");
			}
		}
		else if(npreq.get(70).equals(LogonType.LOGON) && counter < 4) new AcquirerLogon(npcon, acqreq, dbcon, counter + 1).start();
	}


	@Override
	public int getNPCITimeout() {
		return 10000;
	}
}
