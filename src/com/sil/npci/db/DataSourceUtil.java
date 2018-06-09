package com.sil.npci.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.sil.npci.Application;
import com.sil.npci.config.Bank;
import com.sil.npci.nanolog.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceUtil {
	
	private static final Properties dbProperties = new Properties();
	private static final Logger dbLogger = new Logger("central", "db"); 
	static {
		try {
			dbProperties.load(Application.class.getResourceAsStream("/localdb.properties"));
		} catch (Exception e) {
			dbLogger.log("Application Initialization Error : localdb.properties not found");
			dbLogger.log(e);
			System.exit(0);
		}
	}
	
	public static final Connection getConnection() throws ClassNotFoundException, SQLException {
		SQLServerDataSource ds = new SQLServerDataSource();  
		ds.setUser(dbProperties.getProperty("db_user"));  
		ds.setPassword(dbProperties.getProperty("db_pass"));  
		ds.setServerName(dbProperties.getProperty("db_ip"));  
		ds.setPortNumber(Integer.parseInt(dbProperties.getProperty("db_port")));   
		ds.setDatabaseName(dbProperties.getProperty("db_name"));  
		Connection connection = ds.getConnection();
		connection.prepareStatement(dbProperties.getProperty("connectionInitSql")).execute();
		return connection;
	}
	
	public static final HikariDataSource getDataSource(String dataSourceName) {
		try(Connection connection = getConnection();
			PreparedStatement ps = connection.prepareStatement("select DATA_SOURCE_NAME, DATA_SOURCE_CLASS, DATA_SOURCE_IP, DATA_SOURCE_PORT, CATALOG_NAME, convert(VARCHAR, decryptbykey(DATA_SOURCE_USER)) DATA_SOURCE_USER, convert(VARCHAR, decryptbykey(DATA_SOURCE_PASS)) DATA_SOURCE_PASS, convert(VARCHAR(100), decryptbykey(INIT_QUERY)) INIT_QUERY from DATA_SOURCE where DATA_SOURCE_NAME = '"+dataSourceName+"'");
			ResultSet rs = ps.executeQuery()){
			if(rs.next()) {
				HikariConfig hikariConfig = new HikariConfig();
				hikariConfig.setPoolName(rs.getString("DATA_SOURCE_NAME"));
				hikariConfig.setDataSourceClassName(rs.getString("DATA_SOURCE_CLASS"));
				hikariConfig.setCatalog(rs.getString("CATALOG_NAME"));
				hikariConfig.setUsername(rs.getString("DATA_SOURCE_USER"));
				hikariConfig.setPassword(rs.getString("DATA_SOURCE_PASS"));
				hikariConfig.addDataSourceProperty("serverName", rs.getString("DATA_SOURCE_IP"));
				hikariConfig.addDataSourceProperty("portNumber", rs.getInt("DATA_SOURCE_PORT"));
				hikariConfig.addDataSourceProperty("databaseName", rs.getString("CATALOG_NAME"));
				hikariConfig.setConnectionInitSql(rs.getString("INIT_QUERY"));
				return new HikariDataSource(hikariConfig);
			}
		} catch (Exception e) {
			dbLogger.log("Application Initialization Error : could not create datasource : "+dataSourceName);
			dbLogger.log(e);
			System.exit(0);
		}
		return null;
	}
	
	public static final List<Bank> getBanks() {
		List<Bank> banks = new ArrayList<>();
		try(Connection connection = DataSourceUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement("SELECT ACQ_ID, BANK_NAME, IS_ACQUIRER, IS_ISSUER, ACQ_IP, ACQ_PORT, NPCI_IP, NPCI_PORT, CBS_IP, CBS_PORT, DATA_SOURCE_NAME, OFFSET_TYPE, IS_ACTIVE FROM BANK");
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				banks.add(new Bank(rs.getString("ACQ_ID"), rs.getString("BANK_NAME"), rs.getBoolean("IS_ACQUIRER"), rs.getBoolean("IS_ISSUER"),
								   rs.getString("ACQ_IP"), rs.getInt("ACQ_PORT"), rs.getString("CBS_IP"), rs.getInt("CBS_PORT"), 
								   rs.getString("NPCI_IP"), rs.getInt("NPCI_PORT"), rs.getString("DATA_SOURCE_NAME"), rs.getString("OFFSET_TYPE"), rs.getBoolean("IS_ACTIVE")));
				
			}
		} catch (Exception e) {
			dbLogger.log("Application Initialization Error : could not load banks");
			dbLogger.log(e);
			System.exit(0);
		}
		return banks;
	}
	
}
