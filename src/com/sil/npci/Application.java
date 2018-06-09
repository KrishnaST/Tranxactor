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
			HikariDataSource dataSource = dataSources.get(bank.getBankName());
			if (dataSource == null) {
				dataSource = DataSourceUtil.getDataSource(bank.getBankName());
				dataSources.put(bank.getDataSourceName(), dataSource);
			}
			SILCBSConnector cbcon = new SILCBSConnector(bank.getCbsIp(), bank.getCbsPort());
			connectors[i] = new NPCIConnector(bank, dataSource, cbcon);
			connectors[i].setName(bank.getBankName());
			connectors[i].start();
		}

	}
}
