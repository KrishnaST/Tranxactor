package com.sil.npci.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;

import org.pmw.tinylog.Logger;

public class DBCon {

	public static final String getMaskedPan(String pan) {
		if(pan == null) return null;
		return pan.substring(0, 6)+ "XXXXXX" + pan.substring(pan.length()-4);
	}
	
	public static final String getTPKLMK(String luno) {
		String tpkLmk = "";
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT TPKLMK FROM ATMMASTER where LUNO = ?")){
			ps.setString(1, luno);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) tpkLmk = rs.getString("TPKLMK");
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return tpkLmk;
	}
	
	public static final KeyInfo getKeyInfo(String bin) {
		KeyInfo keyInfo = new KeyInfo();
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM KEYMASTER WHERE ACQ_ID = ?")){
			ps.setString(1, bin);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					keyInfo.setAcqId(rs.getString("ACQ_ID"));
					keyInfo.setCvk1(rs.getString("CVK"));
					keyInfo.setCvk2(rs.getString("CVK2"));
					keyInfo.setHsmip(rs.getString("HSMIP"));
					keyInfo.setHsmport(Integer.parseInt(rs.getString("HSMPORT")));
					keyInfo.setZpk(rs.getString("ZPK"));
					keyInfo.setPvk(rs.getString("PVK"));
					keyInfo.setDec(rs.getString("DEC"));
					keyInfo.setPinMode(rs.getString("PIN_MODE"));
				}
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return keyInfo;
	}
	
	public static final String getPinOffset(String pan) {
		String pinOffset = "";
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT PinOffSet FROM D390060 where CardId = ? and convert(varchar(16), decryptbykey(encry_pan , 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, getMaskedPan(pan));
			ps.setString(2, pan);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) pinOffset = rs.getString("PinOffSet");
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return pinOffset;
	}
	
	public static final int updatePinOffset(String pan, String pinOffset) {
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("UPDATE D390060 SET PinOffSet = ? WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, pinOffset);
			ps.setString(2, getMaskedPan(pan));
			ps.setString(3, pan);
			return ps.executeUpdate();
		} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static final int updateBadPin(String pan) {
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("UPDATE D390060 SET BadPin = BadPin + 1,  Status = CASE WHEN (BadPin >= 2) THEN 2 ELSE Status END WHERE  CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, getMaskedPan(pan));
			ps.setString(2, pan);
			return ps.executeUpdate();
		} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static final int clearBadPin(String pan) {
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("UPDATE D390060 SET BadPin = 0 WHERE  CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, getMaskedPan(pan));
			ps.setString(2, pan);
			return ps.executeUpdate();
		} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static final HashMap<String, Object> getCardInfo(String pan) {
		HashMap<String, Object> cardInfo = new HashMap<>();
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT Status, ExpDate, CardLimit, TrnAmount, TrnDate, PinOffSet FROM D390060 where CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, getMaskedPan(pan));
			ps.setString(2, pan);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					cardInfo.put("cardstatus", new Integer(rs.getInt("Status")));
					cardInfo.put("expdate", rs.getDate("ExpDate"));
					cardInfo.put("cardlimit", new Float(rs.getFloat("CardLimit")));
					cardInfo.put("cardusage", new Float(rs.getFloat("TrnAmount")));
					cardInfo.put("trndate", rs.getDate("TrnDate"));
					cardInfo.put("PinOffSet", rs.getString("PinOffSet"));
				}else System.out.println("No Card Found");
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return cardInfo;
	}
	
	
	public static final String[] getATMAddressInfo(String atmId) {
		String[] addressInfo  = new String[2];
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("select Address, PINCODE from ATMMASTER where ATMID = ?")){
			ps.setString(1, atmId);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					addressInfo[0] = rs.getString("Address");
					addressInfo[1] = rs.getString("PINCODE");
				}
			} catch (Exception e) {Logger.info(e);}
		} catch (Exception e) {Logger.info(e);}
		return addressInfo;
	}
	
	public static final int registerIssuerRequest(String ATMID, String TRANSACTIONDATE, String PAN, String PROCESSINGCODE, String AMOUNT, String TRANSACTIONDATETIME, String ACQUIRINGINSTIDCODE, String RETRIEVALREFERENCENUMBER, String ATMLOCATION, String CURRENCYCODE, String MSGTYPE, String certName, String keyword, String bankcd) {
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("INSERT INTO ISOREQUEST_MON (ATMID , TRANSACTIONDATE , PAN , PROCESSINGCODE , AMOUNT , TRANSACTIONDATETIME , ACQUIRINGINSTIDCODE ,"
					+ " RETRIEVALREFERENCENUMBER , ATMLOCATION , CURRENCYCODE , MSGTYPE , encry_pan, BANKCD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, encryptbykey(key_guid(?), ?, 1, '"+keyword+"'), ?)")){
			ps.setString(1, ATMID);
			ps.setString(2, TRANSACTIONDATE);
			ps.setString(3, getMaskedPan(PAN));
			ps.setString(4, PROCESSINGCODE);
			ps.setString(5, AMOUNT);
			ps.setString(6, TRANSACTIONDATETIME);
			ps.setString(7, ACQUIRINGINSTIDCODE);
			ps.setString(8, RETRIEVALREFERENCENUMBER);
			ps.setString(9, ATMLOCATION);
			ps.setString(10, CURRENCYCODE);
			ps.setString(11, MSGTYPE);
			ps.setString(12, certName);
			ps.setBytes(13, PAN.getBytes());
			ps.setString(14, bankcd);
			return ps.executeUpdate();
		} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	
	public static final int registerIssuerResponse(String ATMID, String TRANSACTIONDATE, String RETRIEVALREFERENCENUMBER, String RCODE, String MSGTYPE) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try(Connection connection = ConPool.getCon();
				PreparedStatement ps = connection.prepareStatement("INSERT INTO ISORESPONSE_MON (ATMID, TRANSACTIONDATE, TRANSACTIONRETRIEVALNUMBER, RESPONSECODE, MSGTYPE) VALUES(?, ?, ?, ?, ?)")){
				ps.setString(1, ATMID);
				ps.setTimestamp(2, new Timestamp(sdf.parse(TRANSACTIONDATE).getTime()));
				ps.setString(3, RETRIEVALREFERENCENUMBER);
				ps.setString(4, RCODE);
				ps.setString(5, MSGTYPE);
				return ps.executeUpdate();
			} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static final String getBankCode(String bin) {
		String bankCode = "";
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT BANKCD from LOCALBINMASTER WHERE BIN = ?")){
			ps.setString(1, bin);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) bankCode = rs.getString("BANKCD");
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return bankCode;
	}
	
	public static final String getIssuerChargesFlag(String bin) {
		String issuerChargesFlag = "N";
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT ISSUERCHG FROM LOCALBINMASTER WHERE BIN = ?")){
			ps.setString(1, bin);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) issuerChargesFlag = rs.getString("ISSUERCHG");
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return issuerChargesFlag;
	}
	
	public static final String getAccountFormatFlag(String bin) {
		String accountFlag = "N";
		Logger.info("accountFlag bin : "+bin);
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT ActFormat FROM LOCALBINMASTER WHERE BIN =?")){
			ps.setString(1, bin);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) accountFlag = rs.getString("ActFormat");
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		Logger.info("AcctFlag : "+accountFlag);
		return accountFlag;
	}
	
	public static final int updateIssuerCharges(String pan, boolean isMetro) {
		try(Connection connection = ConPool.getCon();
				PreparedStatement ps = isMetro ? connection.prepareStatement("UPDATE D390060 SET METROCNT = (METROCNT+1) WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")
											   : connection.prepareStatement("UPDATE D390060 SET NONMETROCNT = (NONMETROCNT+1) WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
				ps.setString(1, getMaskedPan(pan));
				ps.setString(2, pan);
				return ps.executeUpdate();
			} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static final int updateCardLimit(String pan, int amount) {
		try(Connection connection = ConPool.getCon();
				PreparedStatement ps = connection.prepareStatement("UPDATE D390060 SET trnamount = CASE WHEN CONVERT(VARCHAR(11),TRNDATE,106) = CONVERT(VARCHAR(11),GETDATE(),106) THEN trnamount + ? ELSE ? END, trndate = CASE WHEN CONVERT(VARCHAR(11),TRNDATE,106) = CONVERT(VARCHAR(11),GETDATE(),106) THEN CONVERT(VARCHAR(11),trndate,106) ELSE CONVERT(VARCHAR(11),GETDATE(),106) END WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
				ps.setInt(1, amount);
				ps.setInt(2, amount);	
				ps.setString(3, getMaskedPan(pan));
				ps.setString(4, pan);
				return ps.executeUpdate();
			} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static final String checkCardLimit(String pan, int amount) {
		String limitFlag = "N";
		try(Connection connection = ConPool.getCon();
				PreparedStatement ps = connection.prepareStatement("SELECT CASE CONVERT(VARCHAR(11),TRNDATE,106) WHEN CONVERT(VARCHAR(11),GETDATE(),106) THEN (CardLimit - (TrnAmount + ?)) ELSE (CardLimit - (0 + ?)) END AS LIMTAVAIL from D390060 WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
				ps.setInt(1, amount);
				ps.setInt(2, amount);	
				ps.setString(3, getMaskedPan(pan));
				ps.setString(4, pan);
				try(ResultSet rs = ps.executeQuery()){
					if(rs.next()) {
						int limitAvailable = rs.getInt("LIMTAVAIL");
						if(limitAvailable >= 0) return "Y";
					}
				} catch (Exception e) {Logger.info(e);}
			} catch (Exception e) {Logger.info(e);}
		return limitFlag;
	}
	
	public static final String checkIssCharges(String bin) {
		String limitFlag = "N";
		try(Connection connection = ConPool.getCon();
				PreparedStatement ps = connection.prepareStatement("SELECT ISSCHARGES FROM LOCALBINMASTER WHERE BIN = ?")){
				ps.setString(1, bin);
				try(ResultSet rs = ps.executeQuery()){
					if(rs.next()) {
						limitFlag = rs.getString("ISSCHARGES");
					}
				} catch (Exception e) {Logger.info(e);}
			} catch (Exception e) {Logger.info(e);}
		return limitFlag;
	}
	
	public static final String[] getHostInfo(String bin) {
		String[] hostInfo = new String[2];
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT ISSATMIP, ISSATMPORT from LOCALBINMASTER WHERE BIN = ?")){
			ps.setString(1, bin);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					hostInfo[0] = rs.getString("ISSATMIP");
					hostInfo[1] = rs.getString("ISSATMPORT");
				}
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return hostInfo;
	}
	
	
	/**
	 * Will not be used (Possibility)
	 */
	public static final String checkAcquirerId(String acqId) {
		String acqIdCheckFlag = "N";
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("select ISSUERID from D350102 where ISSUERID = ?")){
			ps.setString(1, acqId);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) acqIdCheckFlag = "Y";
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return acqIdCheckFlag;
	}
	
	public static final String getAccountNo(String pan) {
		String accountNo = "14";
		String bin = pan.substring(0, 6);
		String accountFormat = getAccountFormatFlag(bin);
		Logger.info("AcctFlag : '"+accountFormat);
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = accountFormat.equalsIgnoreCase("K") ? connection.prepareStatement("SELECT NBrCode, PrdAcctId FROM D390061 where CardId = ? and convert(varchar(16), decryptbykey(encry_pan , 1, rtrim(substring(CardId, 13,16)))) = ?")
																	   : connection.prepareStatement("SELECT LBrCode, PrdAcctId FROM D390061 where CardId = ? and convert(varchar(16), decryptbykey(encry_pan , 1, rtrim(substring(CardId, 13,16)))) = ?") ) {
			ps.setString(1, getMaskedPan(pan));
			ps.setString(2, pan);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					int brCode = accountFormat.equalsIgnoreCase("K") ? rs.getInt("NBrCode") : rs.getInt("LBrCode");
					String acctNo = rs.getString("PrdAcctId").trim();
					if(accountFormat.equalsIgnoreCase("Y")) accountNo = acctNo;
					else accountNo = String.format("%04d", brCode) + acctNo.substring(0, 6) + acctNo.substring(acctNo.length()-16, acctNo.length()-8);
				}
				else return "14";
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		accountNo = String.format("%02d", accountNo.length()) + accountNo;
		Logger.info("AcctFlag : "+accountNo);
		return accountNo;
	}
	
	

	public static final String[] getNetworkInfo(String bin) {
		String[] networkInfo = new String[8];
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT t1.NETWORKID , t2.NETWORKIP, t2.NETWORKPORT, t2.NETWORKHEAD, t2.NETWORKGlHEAD, t2.NETWORKGlBR, t2.NETWORKTIMEOUT FROM D350102 t1, D350104 t2 WHERE t2.NETWORKID = t1.NETWORKID AND t1.PRIORITY = 0 AND t1.ISSUERID = ?")){
			ps.setString(1, bin);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					networkInfo[0] =  rs.getString("NETWORKID");
					networkInfo[1] =  rs.getString("NETWORKIP");
					networkInfo[2] =  rs.getString("NETWORKPORT");
					networkInfo[3] =  rs.getString("NETWORKHEAD");
					networkInfo[4] =  rs.getString("NETWORKGlHEAD");
					networkInfo[5] =  rs.getString("NETWORKGlBR");
					networkInfo[6] =  rs.getString("NETWORKTIMEOUT");
				}
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return networkInfo;
	}
	
	public static final String[] getNetworkInfo1(String bin, String acqId) {
		Logger.info("bin : "+bin);
		Logger.info("acqId : "+acqId);
		String[] networkInfo = new String[8];
		try(Connection connection = ConPool.getCon();
				PreparedStatement binS = connection.prepareStatement("SELECT * FROM D350102 WHERE ISSUERID = ?");
				PreparedStatement acqS = connection.prepareStatement("SELECT NETWORKIP, NETWORKPORT, NETWORKHEAD, NETWORKGlHEAD, NETWORKGlBR, NETWORKTIMEOUT FROM D350104 WHERE NETWORKID = ?")){
			binS.setString(1, bin);
			try (ResultSet rs = binS.executeQuery()){
				if(rs.next()) {
					acqS.setString(1, acqId);
					try(ResultSet acqSet = acqS.executeQuery()){
						if(acqSet.next()) {
							networkInfo[0] =  acqSet.getString("NETWORKIP").trim();
							networkInfo[1] =  acqSet.getString("NETWORKPORT").trim();
							networkInfo[3] =  "ACQUIRER";
							networkInfo[2] =  acqSet.getString("NETWORKHEAD").trim();
							networkInfo[4] =  acqSet.getString("NETWORKGlHEAD").trim();
							networkInfo[5] =  acqSet.getString("NETWORKGlBR").trim();
							networkInfo[6] =  acqSet.getString("NETWORKTIMEOUT").trim();
						}
					} catch (Exception e) {Logger.info(e);}
				}
				else {
					
				}
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return networkInfo;
	}

	
	public static final HashMap<String, Object> getTransactionCount(String pan) {
		HashMap<String, Object> txCountMap = new HashMap<>();
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("SELECT CARDID, TXNMONTH, TXNYEAR, METROCNT, NONMETROCNT, (METROCNT+NONMETROCNT) AS TOTTXNCNT FROM D390060 WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, getMaskedPan(pan));
			ps.setString(2, pan);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					txCountMap.put("TXNMONTH",		rs.getString("TXNMONTH").trim());
					txCountMap.put("TXNYEAR",		rs.getString("TXNYEAR").trim());
					txCountMap.put("METROCNT",		rs.getInt("METROCNT"));
					txCountMap.put("NONMETROCNT",	rs.getInt("NONMETROCNT"));
					txCountMap.put("TOTTXNCNT",		rs.getInt("TOTTXNCNT"));
				}
			} catch (Exception e) {}
		} catch (Exception e) {Logger.info(e);}
		return txCountMap;
	}
	
	public static final int updateTransactionCount(String pan, String txnMonth, String txnYear) {
		try(Connection connection = ConPool.getCon();
			PreparedStatement ps = connection.prepareStatement("UPDATE D390060 SET TXNMONTH = ? , TXNYEAR = ? , METROCNT = 0 , NONMETROCNT = 0 WHERE CardId = ? and convert(VARCHAR(16), decryptbykey(encry_pan, 1, rtrim(substring(CardId, 13,16)))) = ?")){
			ps.setString(1, txnMonth);
			ps.setString(2, txnYear);
			ps.setString(3, getMaskedPan(pan));
			ps.setString(4, pan);
			return ps.executeUpdate();
		} catch (Exception e) {Logger.info(e);}
		return 0;
	}
	
	public static void main(String[] args) {
		String pan = "6077990020000011";
		String bin = "607799";
		//registerIssuerRequest("DMKJ0001", "05-05-2018", "4135080750032932", "000000", "100", "05-05-2018", "356", "888888888888", "vfvfv", "356", "", "sk_card", "2932", "ABCD");
		//updateIssuerCharges("6077990020000011", true);
		System.out.println(checkCardLimit(pan, 5000));
		System.exit(0);
		System.out.println(updateCardLimit(pan, 5000));
		
		System.out.println(checkCardLimit(pan, 5000));
		
		System.out.println(Arrays.toString(getNetworkInfo(bin)));
		System.out.println(getAccountNo(pan));
		System.exit(0);
		System.out.println(getCardInfo("6077990020000011"));;
		System.out.println(getTPKLMK("004"));
		System.out.println(getKeyInfo("607799"));
		System.out.println(getPinOffset("6077990020000011"));;
		System.out.println(updatePinOffset("6077990020000011", "6700"));
		System.out.println(updateBadPin("6077990020000011"));
		System.out.println(getCardInfo(pan));
		System.out.println(getBankCode("607799"));
		System.out.println(getIssuerChargesFlag(bin));
		
		System.out.println(Arrays.toString(getHostInfo(bin)));
		System.out.println(checkAcquirerId(bin));
		
	}
}

