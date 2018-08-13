package com.sil.npci.server;

import com.sil.npci.cbs.CBSConnector;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.NPCIEncoderDecoder;
import com.sil.npci.iso8583.constants.MTI;
import com.sil.npci.iso8583.constants.POSEntryMode;
import com.sil.npci.iso8583.constants.ProcessingCode;
import com.sil.npci.issuer.ECommercePurchase;
import com.sil.npci.issuer.FullMagstripePurchase;
import com.sil.npci.issuer.FullMagstripeReversal;
import com.sil.npci.issuer.IssuerLogon;
import com.sil.npci.issuer.IssuerTransaction;
import com.sil.npci.issuer.QuickEMVPurchase;
import com.sil.npci.nanolog.Logger;
import com.zaxxer.hikari.HikariDataSource;

public final class TransactionDispatcher {

	public static final void inspectAndDispatch(NPCIConnector npcon, CBSConnector cbcon, HikariDataSource ds, ISO8583Message issreq, Logger logger) {
		try {
			if(issreq == null) return;
			String mti = issreq.get(0);
			String pcode = issreq.get(3) == null ? "" : issreq.get(3).substring(0, 2);
			String posEntryMode = issreq.get(22) == null ? "" : issreq.get(22).substring(0, 2);
			if (MTI.NET_MGMT_RESPONSE.equals(mti) || MTI.TRANS_RESPONSE.equals(mti) || MTI.TRANS_ADVICE_RESPONSE.equals(mti) || MTI.ISR_FILE_UPDT_RESPONSE.equals(mti) || MTI.AUTH_RESPONSE.equals(mti)
					|| MTI.AUTH_ADVICE_RESPONSE.equals(mti)) {
				npcon.dispatchAcquirerResponse(issreq);
			}
			else if(MTI.NET_MGMT_REQUEST.equals(mti)) new IssuerLogon(npcon, cbcon, ds.getConnection(), issreq).start();
			else if (MTI.TRANS_REQUEST.equals(mti)) {
				if (ProcessingCode.POS_PURCHASE.equals(pcode)) {
					if (POSEntryMode.ECOMMERCE.equals(posEntryMode)) {
						IssuerTransaction issuerTransaction = new ECommercePurchase(npcon, cbcon, ds.getConnection(), issreq);
						issuerTransaction.start();
					}
					else if (POSEntryMode.FULL_MAGSTRIPE.equals(posEntryMode)) {
						IssuerTransaction issuerTransaction = new FullMagstripePurchase(npcon, cbcon, ds.getConnection(), issreq);
						issuerTransaction.start();
					}
					else if (POSEntryMode.ICC.equals(posEntryMode)) {
						IssuerTransaction issuerTransaction = new QuickEMVPurchase(npcon, cbcon, ds.getConnection(), issreq);
						issuerTransaction.start();
					}
				}
			}
			else if (MTI.ISS_REVERSAL_REQUEST.equals(mti)) {
				if (POSEntryMode.FULL_MAGSTRIPE.equals(posEntryMode)) {
					IssuerTransaction issuerTransaction = new FullMagstripeReversal(npcon, cbcon, ds.getConnection(), issreq);
					issuerTransaction.start();
				}
			}
			else {
				logger.log("unknown transaction type");
				logger.log(NPCIEncoderDecoder.log(issreq));
			}
		
		} catch (Exception e) {
			logger.log(e);
		}
	}
}
