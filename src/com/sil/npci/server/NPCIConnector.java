package com.sil.npci.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sil.npci.acquirer.AcquirerLogon;
import com.sil.npci.acquirer.AcquirerTransaction;
import com.sil.npci.cbs.CBSConnector;
import com.sil.npci.config.Bank;
import com.sil.npci.interfaces.Shutdownable;
import com.sil.npci.iso8583.CBSEncoderDecoder;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.NPCIEncoderDecoder;
import com.sil.npci.iso8583.constants.MTI;
import com.sil.npci.iso8583.constants.POSEntryMode;
import com.sil.npci.iso8583.constants.TransactionType;
import com.sil.npci.issuer.ECommercePurchase;
import com.sil.npci.issuer.FullMagstripePurchase;
import com.sil.npci.issuer.FullMagstripeReversal;
import com.sil.npci.issuer.IssuerLogon;
import com.sil.npci.issuer.IssuerTransaction;
import com.sil.npci.issuer.QuickEMVPurchase;
import com.sil.npci.nanolog.Logger;
import com.sil.npci.nanolog.LoggerFactory;
import com.sil.npci.util.ByteHexUtil;
import com.zaxxer.hikari.HikariDataSource;

public final class NPCIConnector extends Thread implements Shutdownable {

	private final ConcurrentHashMap<String, AcquirerTransaction> finmap = new ConcurrentHashMap<>(20);
	private final ConcurrentHashMap<String, AcquirerTransaction> revmap = new ConcurrentHashMap<>(5);
	private final ConcurrentHashMap<String, AcquirerTransaction> netmap = new ConcurrentHashMap<>(3);
	
	public final HashSet<String> binset = new HashSet<>();
	
	private final Bank config;
	private final LoggerFactory loggerFactory;
	private final Logger npciLogger;
	
	private NPCIConnector	npcon			= this;
	private boolean			shutdown		= false;
	private boolean			loggedon		= false;
	private boolean			loggedoff		= false;
	private boolean			socketbreak		= true;

	private Socket			socket;
	private InputStream		is;
	private OutputStream	os;

	private final HikariDataSource	ds;
	private final CBSConnector		cbcon;
	private final ScheduledThreadPoolExecutor schedular = new ScheduledThreadPoolExecutor(1);	
	private ScheduledFuture<?> future = null;

	private final AcquirerServer acquirerServer;
	
	public NPCIConnector(Bank config, HikariDataSource ds, CBSConnector cbcon) {
		this.config = config;
		this.ds = ds;
		this.cbcon = cbcon;
		loggerFactory = new LoggerFactory(config.getBankName());
		npciLogger = new Logger(config.getBankName(), config.getBankName());
		schedular.setRemoveOnCancelPolicy(true);
		acquirerServer = new AcquirerServer(config.getBankName());;
	}

	 
	
	public void run() {
		acquirerServer.setName("acq server");
		if(config.isAcquirer()) acquirerServer.start();
		npciLogger.log(config);
		main: while (!shutdown) {
			try {
				boolean isNPCISocketConnected = initSocket();
				npciLogger.log("npci socket connection status : "+isNPCISocketConnected);
				if (!isNPCISocketConnected) {
					Thread.sleep(1000);
					continue;
				}
				if (!loggedon && !loggedoff) {
					startLogon();
				}

				while (!shutdown) {
					npciLogger.log(netmap);
					if (socketbreak) {
						npciLogger.log("continue to main either sockbreak");
						continue main;
					}
					byte[] bytes = receive();
					if (bytes == null) continue;
					npciLogger.log("message from npci : " + ByteHexUtil.byteToHex(bytes));
					try {
						ISO8583Message issreqres = NPCIEncoderDecoder.decode(bytes);
						if (issreqres == null || issreqres.get(0) == null) continue;
						//npciLogger.log("message from npci : " + NPCIEncoderDecoder.log(issreqres));
						String mti 			= issreqres.get(0);
						String tx_type 		= issreqres.get(3)  == null ? "" : issreqres.get(3).substring(0, 2);
						String posEntryMode = issreqres.get(22) == null ? "" : issreqres.get(22).substring(0, 2);
						//String posConditionCode 		= isoMessage.get(25) == null ? "" : isoMessage.get(25);
						if (mti.equals(MTI.NET_MGMT_REQUEST)) {
							IssuerLogon logonTransaction = new IssuerLogon(npcon, cbcon, ds.getConnection(), issreqres);
							logonTransaction.start();
						}
						else if(MTI.TRANS_REQUEST.equals(mti)) {
							if(TransactionType.POS_PURCHASE.equals(tx_type)) {
								if(POSEntryMode.ECOMMERCE.equals(posEntryMode)) {
									IssuerTransaction issuerTransaction = new ECommercePurchase(npcon, cbcon, ds.getConnection(), issreqres);
									issuerTransaction.start();
								}
								else if(POSEntryMode.FULL_MAGSTRIPE.equals(posEntryMode)) {
									IssuerTransaction issuerTransaction = new FullMagstripePurchase(npcon, cbcon, ds.getConnection(), issreqres);
									issuerTransaction.start();
								}
								else if(POSEntryMode.ICC.equals(posEntryMode)) {
									IssuerTransaction issuerTransaction = new QuickEMVPurchase(npcon, cbcon, ds.getConnection(), issreqres);
									issuerTransaction.start();
								}
							}
						}
						else if(MTI.ISS_REVERSAL_REQUEST.equals(mti)) {
							if(POSEntryMode.FULL_MAGSTRIPE.equals(posEntryMode)) {
								IssuerTransaction issuerTransaction = new FullMagstripeReversal(npcon, cbcon, ds.getConnection(), issreqres);
								issuerTransaction.start();
							}
						}
						else if (mti.equals(MTI.NET_MGMT_RESPONSE) || mti.equals(MTI.TRANS_RESPONSE) || 
								 mti.equals(MTI.TRANS_ADVICE_RESPONSE) || mti.equals(MTI.ISR_FILE_UPDT_RESPONSE) || 
								 mti.equals(MTI.AUTH_RESPONSE) || mti.equals(MTI.AUTH_ADVICE_RESPONSE)) {
							AcquirerTransaction acquirerTransaction = null;
							
							if(MTI.ISS_REVERSAL_RESPONSE.equals(issreqres.get(0))) 	acquirerTransaction = revmap.get(issreqres.getKey());
							else if(MTI.TRANS_RESPONSE.equals(issreqres.get(0))) 		acquirerTransaction = finmap.get(issreqres.getKey());
							else if(MTI.NET_MGMT_RESPONSE.equals(issreqres.get(0))) 	acquirerTransaction = netmap.get(issreqres.getKey());
							npciLogger.log("acquirerTransaction is null: "+acquirerTransaction == null);
							if (acquirerTransaction == null) {
								npciLogger.log("response already commited for npci response : "+issreqres.get(39));
								continue;
							}
							acquirerTransaction.setResponse(issreqres);
						}
						else {
							npciLogger.log("unknown transaction type");
							npciLogger.log(NPCIEncoderDecoder.log(issreqres));
						}
					} catch (Exception e) {
						npciLogger.log("message parsing error");
						npciLogger.log(e);
					}
				}

			} catch (Exception e) {npciLogger.log(e);}
		}
		npciLogger.log("shutting down npci connector");
	}


	public synchronized boolean send(byte[] bytes) {
		if (shutdown || loggedoff || socketbreak) {
			npciLogger.log("socket break or signed off or shutdown : descarding message : " + ByteHexUtil.byteToHex(bytes));
			return false;
		}
		//npciLogger.log("sending : " + ByteHexUtil.byteToHex(bytes));
		try {
			os.write(bytes);
			os.flush();
			return true;
		} catch (Exception e) {
			npciLogger.log("error sending message to npci" + e.getMessage());
			//npciLogger.log(e);
			socketbreak = true;
			return false;
		}
	}
	
	public synchronized boolean sendDirty(byte[] bytes) {
		//npciLogger.log("sending : " + ByteHexUtil.byteToHex(bytes));
		try {
			os.write(bytes);
			os.flush();
			return true;
		} catch (Exception e) {
			npciLogger.log("error sending message to npci" + e.getMessage());
			//npciLogger.log(e);
			socketbreak = true;
			return false;
		}
	}

	public byte[] receive() {
		if (shutdown || socketbreak) {
			npciLogger.log("socket break  or shutdown : not reading message from npci");
			return null;
		}
		byte[] bytes = null;
		try {
			int b1 = is.read();
			int b2 = is.read();
			if (b1 < 0 || b2 < 0) {
				npciLogger.log("unexpected socket break");
				socketbreak = true;
				return null;
			}
			else {
				bytes = new byte[b1 * 256 + b2];
				is.read(bytes);
				return bytes;
			}
		} catch (Exception e) {
			npciLogger.log("error receiving message from npci socketexception : "+e.getMessage());
			//npciLogger.log(e);
			socketbreak = true;
		}
		return null;
	}

	public Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			npciLogger.log("error creating database connection for " + config.getBankName());
			npciLogger.log(e);
		}
		return null;
	}

	public boolean initSocket() {
		closeSocket();
		try {
			if (socketbreak) {
				loggedon = false;
				npciLogger.log("connection to "+config.getNpciIp()+":"+config.getNpciPort());
				socket = new Socket(config.getNpciIp(), config.getNpciPort());
				socket.setKeepAlive(true);
				is = socket.getInputStream();
				os = socket.getOutputStream();
				socketbreak = false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void closeSocket() {
		try {
			socket.close();
			socketbreak = true;
		} catch (Exception e) {
		}
	}
	
	public void setNPCILogOff() {
		this.loggedon = false;
		this.loggedoff = true;
		if(future != null && !future.isCancelled()) future.cancel(true);
	}

	public synchronized void startLogon() {
		loggedon = false;
		loggedoff = false;
		AcquirerLogon acqLogon = new AcquirerLogon(this, null, null);
		acqLogon.setName("acqlogon");
		if(future == null || future.isCancelled()) future = schedular.scheduleAtFixedRate(acqLogon, 2, 60, TimeUnit.SECONDS);
	}

	@Override
	public void shutdown() {
		this.shutdown 	= true;
		this.loggedoff 	= true;
		this.loggedon 	= false;
		if (future != null && !future.isCancelled()) future.cancel(true);
		schedular.shutdown();
		closeSocket();
		acquirerServer.shutdown();
	}

	public void shutdown(int refCount) {
		shutdown();
		System.out.println("refCount : "+refCount);
		if(refCount < 1) ds.close();
	}
	
	@Override
	public boolean isShutdowned() {
		return shutdown;
	}

	public boolean isLoggedOn() {
		return loggedon;
	}
	
	public void setLogon(boolean logon) {
		loggedon = logon;
	}

	public class AcquirerServer extends Thread implements Shutdownable {
		private ServerSocket ssc = null;
		public AcquirerServer(String bankName) {
			this.setName(bankName+" acquirer");
		}
		public void run() {
			try(ServerSocket ssc = new ServerSocket(config.getAcquiringPort())) {
				this.ssc = ssc;
				while (!shutdown) {
					Socket sc = ssc.accept();
					npciLogger.log("socket connected : "+sc.getRemoteSocketAddress()+":"+sc.getPort());
					new AcquirerTransactionDispatcher(sc).start();
				}
			} catch (Exception e) {
			}
			npciLogger.log("shutting down acquirer server.");
		}

		@Override
		public void shutdown() {
			try {
				ssc.close();
			} catch (IOException e) {
			}
		}

		@Override
		public boolean isShutdowned() {
			return ssc == null || ssc.isClosed();
		}
	}

	public class AcquirerTransactionDispatcher extends Thread {

		private final Socket socket;

		public AcquirerTransactionDispatcher(final Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try(Socket socket = this.socket; BufferedInputStream bin = new BufferedInputStream(socket.getInputStream()); BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
				while (!shutdown) {
					try {
						int b1 = bin.read();
						int b2 = bin.read();
						int b3 = bin.read();
						int b4 = bin.read();
						if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) break;
						int len = (b1 - 48) * 1000 + (b2 - 48) * 100 + (b3 - 48) * 10 + (b4 - 48);
						byte[] bytes = new byte[len];
						bin.read(bytes);
						ISO8583Message acqreq = CBSEncoderDecoder.decode(bytes);
						if (acqreq == null) continue;
						if (acqreq.get(0).equals("0200")) new AcquirerLogon(npcon, acqreq, ds.getConnection());
					} catch (Exception e) {
						npciLogger.log("error reading acquirer request.");
						break;
					}
				}
				npciLogger.log("shutting down acquirer transaction dispatcher.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Logger getLogger(String uniqueId) {
		return loggerFactory.getLogger(uniqueId);
	}
	
	public void addToTransactionQ(String mti, String key, AcquirerTransaction acquirerTransaction) {
		if(MTI.TRANS_REQUEST.equals(mti)) 			finmap.put(key, acquirerTransaction);
		if(MTI.ISS_REVERSAL_REQUEST.equals(mti)) 	revmap.put(key, acquirerTransaction);
		if(MTI.NET_MGMT_REQUEST.equals(mti)) 		netmap.put(key, acquirerTransaction);
	}

	public void removeFromTransactionQ(String mti, String key) {
		if(MTI.TRANS_REQUEST.equals(mti)) 		 	finmap.remove(key);
		if(MTI.ISS_REVERSAL_REQUEST.equals(mti))	revmap.remove(key);
		if(MTI.NET_MGMT_REQUEST.equals(mti)) 	 	netmap.remove(key);
	}

	public Bank getConfig() {
		return config;
	}


	
}
