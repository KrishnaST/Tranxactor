package com.sil.npci.config;

import com.google.gson.Gson;

public class Bank {
	public final String acquirerId;
	public final String bankName;
	public final boolean isAcquirer;
	public final boolean isIssuer;
	public final String acquiringIp;
	public final int acquiringPort;
	public final String cbsIp;
	public final int cbsPort;
	public final String npciIp;
	public final int npciPort;
	public final String dataSourceName;
	public final String offsetType;
	public final boolean isActive;
	
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
