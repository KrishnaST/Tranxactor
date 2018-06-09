package com.sil.npci.config;

import com.google.gson.Gson;

public class Bank {
	private final String acquirerId;
	private final String bankName;
	private final boolean isAcquirer;
	private final boolean isIssuer;
	private final String acquiringIp;
	private final int acquiringPort;
	private final String cbsIp;
	private final int cbsPort;
	private final String npciIp;
	private final int npciPort;
	private final String dataSourceName;
	private final String offsetType;
	private final boolean isActive;
	
	
	public Bank(String acquirerId, String bankName, boolean isAcquirer, boolean isIssuer, String acquiringIp, int acquiringPort, String cbsIp, int cbsPort, String npciIp, int npciPort, String dataSourceName, String offsetType, boolean isActive) {
		this.acquirerId = acquirerId;
		this.bankName = bankName;
		this.isAcquirer = isAcquirer;
		this.isIssuer = isIssuer;
		this.acquiringIp = acquiringIp;
		this.acquiringPort = acquiringPort;
		this.cbsIp = cbsIp;
		this.cbsPort = cbsPort;
		this.npciIp = npciIp;
		this.npciPort = npciPort;
		this.dataSourceName = dataSourceName;
		this.offsetType = offsetType;
		this.isActive = isActive;
	}
	
	public String getAcquirerId() {
		return acquirerId;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	public boolean isAcquirer() {
		return isAcquirer;
	}
	
	public boolean isIssuer() {
		return isIssuer;
	}
	
	public String getAcquiringIp() {
		return acquiringIp;
	}
	
	public int getAcquiringPort() {
		return acquiringPort;
	}
	
	public String getCbsIp() {
		return cbsIp;
	}
	
	public int getCbsPort() {
		return cbsPort;
	}
	
	public String getNpciIp() {
		return npciIp;
	}
	
	public int getNpciPort() {
		return npciPort;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}
	
	public String getOffsetType() {
		return offsetType;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public String toString() {
		Gson gson = GlobalConfig.GSON_BUILDER.create();
		try {
			return gson.toJson(this);
		} catch (Exception e) {
		}
		return "";
	}
}
