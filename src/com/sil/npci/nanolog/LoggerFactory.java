package com.sil.npci.nanolog;


public class LoggerFactory {

	private final String bankName;
	
	public LoggerFactory(String bankName) {
		this.bankName = bankName;
	}

	public Logger getLogger(String uniqueId) {
		return new Logger(bankName, uniqueId);
	}
}
