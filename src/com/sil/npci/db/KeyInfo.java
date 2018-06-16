package com.sil.npci.db;

public class KeyInfo {

	private String acqId;
	private String zpk;
	private String pvk;
	private String dec;
	private String cvk1; 
	private String cvk2;
	private String hsmip;
	private int hsmport;
	private String pinMode;
	public String getAcqId() {
		return acqId;
	}
	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}
	public String getZpk() {
		return zpk;
	}
	public void setZpk(String zpk) {
		this.zpk = zpk;
	}
	public String getPvk() {
		return pvk;
	}
	public void setPvk(String pvk) {
		this.pvk = pvk;
	}
	public String getDec() {
		return dec;
	}
	public void setDec(String dec) {
		this.dec = dec;
	}
	public String getCvk1() {
		return cvk1;
	}
	public void setCvk1(String cvk1) {
		this.cvk1 = cvk1;
	}
	public String getCvk2() {
		return cvk2;
	}
	public void setCvk2(String cvk2) {
		this.cvk2 = cvk2;
	}
	public String getHsmip() {
		return hsmip;
	}
	public void setHsmip(String hsmip) {
		this.hsmip = hsmip;
	}
	public int getHsmport() {
		return hsmport;
	}
	public void setHsmport(int hsmport) {
		this.hsmport = hsmport;
	}
	public String getPinMode() {
		return pinMode;
	}
	public void setPinMode(String pinMode) {
		this.pinMode = pinMode;
	}
	
	@Override
	public String toString() {
		return "KeyInfo [acqId=" + acqId + ", zpk=" + zpk + ", pvk=" + pvk + ", dec=" + dec + ", cvk1=" + cvk1
				+ ", cvk2=" + cvk2 + ", hsmip=" + hsmip + ", hsmport=" + hsmport + ", pinMode=" + pinMode + "]";
	}
	
}
