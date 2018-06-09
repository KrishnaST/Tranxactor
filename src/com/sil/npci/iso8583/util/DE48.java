package com.sil.npci.iso8583.util;

public class DE48 {
	
	private TLV tlv = new TLV();
	
	
	public String getProductCode() {
		return tlv.get("051");
	}
	public void setProductCode(PRODUCT_CODE productCode) {
		tlv.put("051", productCode.productCode);
	}
	
	public String getCVD2() {
		return tlv.get("052");
	}
	
	public void setCVD2(String cvd2) {
		tlv.put("052", cvd2);
	}
	
	public String getCvd2MatchResult() {
		return tlv.get("053");
	}
	
	public void setCVD2MatchResult(CVD_MATCH cvd2MatchResult) {
		tlv.put("053", cvd2MatchResult.cvdMatch);
	}
	
	public String getICVDMatchResult() {
		return tlv.get("054");
	}
	
	public void setIcvdMatchResult(CVD_MATCH icvdMatchResult) {
		tlv.put("054", icvdMatchResult.cvdMatch);
	}
	
	public String getEciIndicator() {
		return tlv.get("056");
	}
	
	public void setEciIndicator(ECI_INDICATOR eciIndicator) {
		tlv.put("056", eciIndicator.eciIndicator);
	}
	
	public String getIcs1ResultCode() {
		return tlv.get("057");
	}
	
	public void setIcs1ResultCode(ICS1_RESULT_CODE ics1ResultCode) {
		tlv.put("057", ics1ResultCode.ics1Result);
	}
	
	public String getFraudScore() {
		return tlv.get("058");
	}
	
	public void setFraudScore(String fraudScore) {
		tlv.put("058", fraudScore);
	}
	
	public String getEmiAmount() {
		return tlv.get("059");
	}
	
	public void setEmiAmount(String emiAmount) {
		tlv.put("059", emiAmount);
	}
	
	public String getTransAuthIndicator() {
		return tlv.get("060");
	}
	
	public void setTransAuthIndicator(TRANS_AUTH_INDICATOR transAuthIndicator) {
		tlv.put("060", transAuthIndicator.transAuthIndicator);
	}
	
	public String getEcomTransactionId() {
		return tlv.get("061");
	}
	
	public void setEcomTransactionId(String ecomTransactionId) {
		tlv.put("061", ecomTransactionId);
	}
	
	public String getLoyaltyPointsDebit() {
		return tlv.get("062");
	}
	
	public void setLoyaltyPointsDebit(String loyaltyPointsDebit) {
		tlv.put("062", loyaltyPointsDebit);
	}
	
	public String getLoyaltyBalance() {
		return tlv.get("063");
	}
	
	public void setLoyaltyBalance(String loyaltyBalance) {
		tlv.put("063", loyaltyBalance);
	}
	
	public String getIcs2ResultCode() {
		return tlv.get("064");
	}
	
	public void setIcs2ResultCode(ICS2_RESULT_CODE ics2ResultCode) {
		tlv.put("064", ics2ResultCode.ics2Result);
	}
	
	public String getUid() {
		return tlv.get("066");
	}
	
	public void setUid(String uid) {
		tlv.put("066", uid);
	}
	
	public String getIncomeTaxPan() {
		return tlv.get("067");
	}
	
	public void setIncomeTaxPan(String incomeTaxPan) {
		tlv.put("067", incomeTaxPan);
	}
	
	public String getIcs1() {
		return tlv.get("068");
	}
	
	public void setIcs1(String ics1) {
		tlv.put("068", ics1);
	}
	
	public String getIcs2() {
		return tlv.get("069");
	}
	
	public void setIcs2(String ics2) {
		tlv.put("069", ics2);
	}
	
	public String getIcs1Data() {
		return tlv.get("070");
	}
	
	public void setIcs1Data(String ics1Data) {
		tlv.put("070", ics1Data);
	}
	
	public String getEcomIp() {
		return tlv.get("071");
	}
	
	public void setEcomIp(String ecomIp) {
		tlv.put("071", ecomIp);
	}
	
	public String getIcsTranId() {
		return tlv.get("072");
	}
	
	public void setIcsTranId(String icsTranId) {
		tlv.put("072", icsTranId);
	}
	
	public String getNetworkData() {
		return tlv.get("073");
	}
	
	public void setNetworkData(String networkData) {
		tlv.put("073", networkData);
	}
	
	public String getMobileNo() {
		return tlv.get("074");
	}
	
	public void setMobileNo(String mobileNo) {
		tlv.put("074", mobileNo);
	}
	
	public String getImageCode() {
		return tlv.get("075");
	}
	
	public void setImageCode(String imageCode) {
		tlv.put("075", imageCode);
	}
	
	public String getPersonalPhrase() {
		return tlv.get("076");
	}
	
	public void setPersonalPhrase(String personalPhrase) {
		tlv.put("076", personalPhrase);
	}
	
	public String getEcomUniqueId() {
		return tlv.get("077");
	}
	
	public void setEcomUniqueId(String ecomUniqueId) {
		tlv.put("077", ecomUniqueId);
	}
	
	public String getCryptTechnique() {
		return tlv.get("078");
	}
	
	public void setCryptTechnique(String cryptTechnique) {
		tlv.put("078", cryptTechnique);
	}
	
	public String getAddAcqInfo() {
		return tlv.get("079");
	}
	
	public void setAddAcqInfo(String addAcqInfo) {
		tlv.put("079", addAcqInfo);
	}
	
	public String getAuthResponseCode() {
		return tlv.get("081");
	}
	
	public void setAuthResponseCode(String authResponseCode) {
		tlv.put("081", authResponseCode);
	}
	
	public String parse(String de48) {
		tlv.parse(de48);
		return tlv.toFormatedString();
	}
	
	public String pack() {
		return tlv.build();		
	}
	
	
	@Override
	public String toString() {
		return tlv.toString();
	}


	public static enum PRODUCT_CODE {
		ATM01("ATM01"),
		POS01("POS01");
		
		private String productCode;
		
		private PRODUCT_CODE(String productCode) {
			this.productCode = productCode;
		}
		
		public String toString() {
			return productCode;
		}
	}
	
	public static enum CVD_MATCH {
		M("M"),
		N("N");
		
		private String cvdMatch;
		
		private CVD_MATCH(String cvdMatch) {
			this.cvdMatch = cvdMatch;
		}
		
		public String toString() {
			return cvdMatch;
		}
	}
	
	public static enum ECI_INDICATOR {
		ECOMMERCE_3D_SECURE("05"),
		UNAUTHENTICATED_3D_SECURE("06"),
		NOT_SECURE_DATA_ENCRYPTED("07"),
		NOT_SECURE("08"),
		ECOMMERCE_SECURE_OTP("15"),
		ECOMMERCE_SECURE_IB("16"),
		ECOMMERCE_SECURE_OTHER("17"),
		ECOMMERCE_SECURE_VALID_IMAGE_OTP("21"),
		ECOMMERCE_INSECURE_INVALID_IMAGE_DAYLOCK("22"),
		ECOMMERCE_INSECURE_INVALID_IMAGE_PERMLOCK("23"),
		ECOMMERCE_INSECURE_BROWSER_CLOSE_DAYLOCK("24"),
		ECOMMERCE_INSECURE_BROWSER_CLOSE_PERMLOCK("25"),
		ECOMMERCE_OTP_AUTH_IAS("31"),
		ECOMMERCE_OTP_AUTH_NPCI("32"),
		ECOMMERCE_OTP_AUTH_CARD_ONLY("33"),
		ECOMMERCE_OTP_AUTH_ONLINE_PIN("33"),
		IVR_PAYSECURE_AUTH_IAS("41");
		
		private String eciIndicator;
		
		private ECI_INDICATOR(String eciIndicator) {
			this.eciIndicator = eciIndicator;
		}
		
		public String toString() {
			return eciIndicator;
		}
	}
	
	public static enum ICS1_RESULT_CODE {
		AUTH_RESULT_INVALID("16"),
		AUTH_RESULT_FAILED("17"),
		AUTH_RESULT_SUCCESS("02");
		
		private String ics1Result;
		
		private ICS1_RESULT_CODE(String ics1Result) {
			this.ics1Result = ics1Result;
		}
		
		public String toString() {
			return ics1Result;
		}
	}
	
	public static enum ICS2_RESULT_CODE {
		INVALID_CRYPTOGRAM("I"),
		UNABLE_TO_PROCESS("U"),
		VALID_RESULT("V");
		
		private String ics2Result;
		
		private ICS2_RESULT_CODE(String ics2Result) {
			this.ics2Result = ics2Result;
		}
		
		public String toString() {
			return ics2Result;
		}
	}
	
	
	public static enum TRANS_AUTH_INDICATOR {
		SUCCESSFUL_AUTH_STIP_EMV("1"),
		SUCCESSFUL_AUTH_STIP_MAG_FALLBACK("2"),
		SUCCESSFUL_AUTH_STIP_QUICK_EMV("3"),
		DECLINED_STIP("4"),
		SUCCESSFUL_ARQC_VALIDATION("5"),
		DECLINED_NPCI_CVR_QUICK_EMV("6"),
		DECLINED_ARQC_VALIDATION("7"),
		DECLINED_NPCI_TVR_QUICK_EMV("8"),
		SUCCESSFUL_AUTH_UIDAI("9");
		
		private String transAuthIndicator;
		
		private TRANS_AUTH_INDICATOR(String transAuthIndicator) {
			this.transAuthIndicator = transAuthIndicator;
		}
		
		public String toString() {
			return transAuthIndicator;
		}
	}
	
	
	public static void main(String[] args) {
		DE48 de48 = new DE48();
		de48.parse("051005POS010520031230560023105800500030061030200000000000000000000524701124071042103.000.007.111                        35607702045066620170911205341");
		System.out.println(de48.toString());
	}
}
