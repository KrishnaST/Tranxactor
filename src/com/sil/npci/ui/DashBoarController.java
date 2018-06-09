package com.sil.npci.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.sil.npci.cbs.SILCBSConnector;
import com.sil.npci.config.Bank;
import com.sil.npci.db.DataSourceUtil;
import com.sil.npci.server.NPCIConnector;
import com.zaxxer.hikari.HikariDataSource;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class DashBoarController implements Initializable {

	private Stage stage;
	
	private static NPCIConnector[] connectors = null;
	private static final HashMap<String, HikariDataSource> dataSources = new HashMap<>();
	private static final HashMap<String, Integer> dataSourceRefCount   = new HashMap<>();
	private static final ObservableList<BankProperty> banks 		   = FXCollections.observableArrayList();
	private static final ObjectProperty<TableRow<BankProperty>> lastSelectedRow = new SimpleObjectProperty<>();
	
	private final ContextMenu tableContext = new ContextMenu();
	private final MenuItem start = new MenuItem("start");
	private final MenuItem stop = new MenuItem("stop");
	private final MenuItem restart = new MenuItem("restart");
	private final MenuItem send_logon = new MenuItem("Send Logon");
	private final MenuItem send_echo = new MenuItem("Send Echo Logon");
	
	@FXML private  TableView<BankProperty> bankTable;
    @FXML private  TableColumn<BankProperty, String> acqIdCol;
    @FXML private  TableColumn<BankProperty, String> bankNameCol;
    @FXML private  TableColumn<BankProperty, String> isAcqCol;
    @FXML private  TableColumn<BankProperty, String> isIssCol;
    @FXML private  TableColumn<BankProperty, String> acqIpCol;
    @FXML private  TableColumn<BankProperty, String> issIpCol;
    @FXML private  TableColumn<BankProperty, String> cbsIpCol;
    @FXML private  TableColumn<BankProperty, String> offsetTypeCol;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	bankTable.setColumnResizePolicy(param -> true );
    	
    	tableContext.getItems().add(start);
    	tableContext.getItems().add(stop);
    	tableContext.getItems().add(restart);
    	tableContext.getItems().add(send_logon);
    	tableContext.getItems().add(send_echo);

    	acqIdCol.setCellValueFactory(cellData -> cellData.getValue().acqIdProperty());
    	bankNameCol.setCellValueFactory(cellData -> cellData.getValue().bankNameProperty());
    	isAcqCol.setCellValueFactory(cellData -> cellData.getValue().isAcqProperty());
    	isIssCol.setCellValueFactory(cellData -> cellData.getValue().isIssProperty());
    	acqIpCol.setCellValueFactory(cellData -> cellData.getValue().acqIpProperty());
    	issIpCol.setCellValueFactory(cellData -> cellData.getValue().issIpProperty());
    	cbsIpCol.setCellValueFactory(cellData -> cellData.getValue().cbsIpProperty());
    	offsetTypeCol.setCellValueFactory(cellData -> cellData.getValue().offsetTypeProperty());
    	
    	List<Bank> banks = DataSourceUtil.getBanks();
		
		connectors = new NPCIConnector[banks.size()];
		for (int i = 0; i < connectors.length; i++) {
			Bank bank = banks.get(i);
			HikariDataSource dataSource = dataSources.get(bank.getBankName());
			if (dataSource == null) {
				dataSource = DataSourceUtil.getDataSource(bank.getBankName());
				dataSources.put(bank.getDataSourceName(), dataSource);
				dataSourceRefCount.put(bank.getDataSourceName(), 2);
			}
			else dataSourceRefCount.put(bank.getDataSourceName(), dataSourceRefCount.get(bank.getDataSourceName())+1);
			SILCBSConnector cbcon = new SILCBSConnector(bank.getCbsIp(), bank.getCbsPort());
			connectors[i] = new NPCIConnector(bank, dataSource, cbcon);
			BankProperty bankProperty = new BankProperty();
	    	bankProperty.setBankName(bank.getBankName());
	    	bankProperty.setAcqId(bank.getAcquirerId());
	    	bankProperty.setAcqIp(bank.getAcquiringIp()+":"+bank.getAcquiringPort());
	    	bankProperty.setCbsIp(bank.getCbsIp()+":"+bank.getCbsPort());
	    	bankProperty.setIssIp(bank.getNpciIp()+":"+bank.getNpciPort());
	    	bankProperty.setIsIss(bank.isIssuer() ? "Y" : "N");
	    	bankProperty.setIsAcq(bank.isAcquirer() ? "Y" : "N");
	    	bankProperty.setOffsetType("IBM");
	    	DashBoarController.banks.add(bankProperty);
			connectors[i].start();
		}
    	this.bankTable.setItems(DashBoarController.banks);
    	
    	bankTable.addEventHandler(MouseEvent.MOUSE_RELEASED, new ContextMouseEvent());
    	bankTable.addEventHandler(KeyEvent.KEY_RELEASED, new ContextKeyEvent());
    	
    	start.setOnAction(new StartAction());
    	stop.setOnAction(new StopAction());
    	
    	bankTable.setRowFactory(bankTable -> {
    	    TableRow<BankProperty> row = new TableRow<BankProperty>();
    	    row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
    	        if (isNowSelected) {
    	        	lastSelectedRow.set(row);
    	        } 
    	    });
    	    return row ;
    	});
    }
    
    public void showAlert(AlertType alertType, String title, String header, String context) {
    	Alert alert = new Alert(alertType);
    	alert.setContentText(context);
    	alert.setHeaderText(header);
    	alert.setTitle(title);
    	alert.initModality(Modality.APPLICATION_MODAL);
    	alert.initOwner(stage);
    	alert.showAndWait();
    }

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	
	public class StartAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			BankProperty bankProperty =  bankTable.getSelectionModel().getSelectedItem();
			if(bankProperty == null) return;
			NPCIConnector connector = null;
			for (int i = 0; i < connectors.length; i++) {
				if(connectors[i].getConfig().getAcquirerId().equalsIgnoreCase(bankProperty.getAcqId())) connector = connectors[i];
			}
			if(connector == null) return;
			if(connector.isAlive() && !connector.isInterrupted()) showAlert(AlertType.WARNING, "Start NPCI Connector", "NPCI Connector Already Started", "NPCI Connector Already Started");
		}
	}
	
	public class StopAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			BankProperty bankProperty =  bankTable.getSelectionModel().getSelectedItem();
			if(bankProperty == null) return;
			NPCIConnector connector = null;
			for (int i = 0; i < connectors.length; i++) {
				if(connectors[i].getConfig().getAcquirerId().equalsIgnoreCase(bankProperty.getAcqId())) connector = connectors[i];
			}
			if(connector == null) return;
			else if(connector.isShutdowned() || connector.isInterrupted()) {
				showAlert(AlertType.WARNING, "Shutdown NPCI Connector", "NPCI Connector Already stopped", "NPCI Connector Already stopped");
			}
			else {
				int refCount = dataSourceRefCount.get(bankProperty.getBankName()) - 1;
				dataSourceRefCount.put(bankProperty.getBankName(), refCount);
				connector.shutdown(refCount);
			}
		}
	}

	public class ContextMouseEvent implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			TableRow<BankProperty> row = lastSelectedRow.get();
			boolean isBound = false;
			if(row != null) {
				Bounds bounds = row.localToScreen(row.getBoundsInLocal());
				isBound = bounds.contains(new Point2D(event.getSceneX(), event.getScreenY()));
			}
			if(event.getButton() == MouseButton.PRIMARY) {
				if(row != null) {
					if(isBound) bankTable.getSelectionModel().select(row.getItem());
					else bankTable.getSelectionModel().select(null);
					tableContext.hide();
				}
			}
			else if(event.getButton() == MouseButton.SECONDARY) {
				if(isBound) tableContext.show(bankTable, event.getScreenX(), event.getScreenY());
				else tableContext.hide();
	        }
		}
	}
	
	public class ContextKeyEvent implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			if(event.getCode() == KeyCode.CONTEXT_MENU && bankTable.getSelectionModel().getSelectedIndex() >= 0) {
				TableRow<BankProperty> row = lastSelectedRow.get();
				if(row == null) return; 
				Bounds bounds = row.localToScreen(row.getBoundsInLocal());
				if(tableContext.isShowing()) tableContext.hide();
				else tableContext.show(bankTable, bounds.getMaxX()-(bounds.getWidth()/2), bounds.getMaxY()-(bounds.getHeight()/2));
			}
		}
	}
}


