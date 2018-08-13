package com.sil.npci;

import java.util.HashMap;
import java.util.List;

import com.sil.npci.cbs.SILCBSConnector;
import com.sil.npci.config.Bank;
import com.sil.npci.db.DataSourceUtil;
import com.sil.npci.server.NPCIConnector;
import com.zaxxer.hikari.HikariDataSource;

public class Application {

	public static void main(String args[]) {
		List<Bank> banks = DataSourceUtil.getBanks();
		HashMap<String, HikariDataSource> dataSources = new HashMap<>();
		NPCIConnector[] connectors = new NPCIConnector[banks.size()];
		for (int i = 0; i < connectors.length; i++) {
			Bank bank = banks.get(i);
			HikariDataSource dataSource = dataSources.get(bank.bankName);
			if (dataSource == null) {
				dataSource = DataSourceUtil.getDataSource(bank.bankName);
				dataSources.put(bank.dataSourceName, dataSource);
			}
			SILCBSConnector cbcon = new SILCBSConnector(bank.cbsIp, bank.cbsPort);
			connectors[i] = new NPCIConnector(bank, dataSource, cbcon);
			connectors[i].setName(bank.bankName);
			connectors[i].start();
		}

	}
}
