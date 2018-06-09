package com.sil.npci.ui;

import javafx.beans.property.SimpleStringProperty;

public class BankProperty {

	private SimpleStringProperty	acqIdProperty		= new SimpleStringProperty();
	private SimpleStringProperty	bankNameProperty	= new SimpleStringProperty();
	private SimpleStringProperty	isAcqProperty		= new SimpleStringProperty();
	private SimpleStringProperty	isIssProperty		= new SimpleStringProperty();
	private SimpleStringProperty	acqIpProperty		= new SimpleStringProperty();
	private SimpleStringProperty	issIpProperty		= new SimpleStringProperty();
	private SimpleStringProperty	cbsIpProperty		= new SimpleStringProperty();
	private SimpleStringProperty	offsetTypeProperty	= new SimpleStringProperty();


	public SimpleStringProperty acqIdProperty() {
		return acqIdProperty;
	}


	public SimpleStringProperty bankNameProperty() {
		return bankNameProperty;
	}


	public SimpleStringProperty isAcqProperty() {
		return isAcqProperty;
	}


	public SimpleStringProperty isIssProperty() {
		return isIssProperty;
	}


	public SimpleStringProperty acqIpProperty() {
		return acqIpProperty;
	}


	public SimpleStringProperty issIpProperty() {
		return issIpProperty;
	}


	public SimpleStringProperty cbsIpProperty() {
		return cbsIpProperty;
	}


	public SimpleStringProperty offsetTypeProperty() {
		return offsetTypeProperty;
	}


	public String getAcqId() {
		return acqIdProperty.get();
	}


	public void setAcqId(String acqId) {
		this.acqIdProperty.set(acqId);
	}


	public String getBankName() {
		return bankNameProperty.get();
	}


	public void setBankName(String bankName) {
		this.bankNameProperty.set(bankName);
	}


	public String getIsAcq() {
		return isAcqProperty.get();
	}


	public void setIsAcq(String isAcq) {
		this.isAcqProperty.set(isAcq);
	}


	public String getIsIss() {
		return isIssProperty.get();
	}


	public void setIsIss(String isIss) {
		this.isIssProperty.set(isIss);
	}


	public String getAcqIp() {
		return acqIpProperty.get();
	}


	public void setAcqIp(String acqIp) {
		this.acqIpProperty.set(acqIp);
	}


	public String getIssIp() {
		return issIpProperty.get();
	}


	public void setIssIp(String issIp) {
		this.issIpProperty.set(issIp);
	}


	public String getCbsIp() {
		return cbsIpProperty.get();
	}


	public void setCbsIp(String cbsIp) {
		this.cbsIpProperty.set(cbsIp);
	}


	public String getOffsetType() {
		return offsetTypeProperty.get();
	}


	public void setOffsetType(String offsetType) {
		this.offsetTypeProperty.set(offsetType);
	}

}
