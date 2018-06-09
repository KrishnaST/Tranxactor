package com.sil.npci.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML private PasswordField pinText;
	@FXML private ComboBox<Label> userText;
	@FXML private Button loginButton;
	@FXML private Circle close;

	private Parent	parent;
	private Stage	stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Label krishna = new Label("Krishna");
		Label shankar = new Label("shankar");
		krishna.textAlignmentProperty().set(TextAlignment.CENTER);
		shankar.textAlignmentProperty().set(TextAlignment.CENTER);
		userText.getItems().add(krishna);
		userText.getItems().add(shankar);
		userText.requestFocus();
		
		loginButton.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent event) {
				
				stage.close();
				parent = null;
				stage = new Stage();
				loginButton.setDisable(true);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
				Parent root = null;
				try {
					root = loader.load();
				} catch (IOException e) {e.printStackTrace();}
				DashBoarController controller = loader.getController();
				Scene scene = new Scene(root);
				stage.setScene(scene);
				//controller.setParent(root);
				controller.setStage(stage);
				stage.show();
				loginButton.setDisable(false);
			}
		});
	}


	public Parent getParent() {
		return parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	public Stage getStage() {
		return stage;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}


	public synchronized void setPin(String key) {
		pinText.setText(pinText.getText() == null ? "" : pinText.getText() + key);
		if(pinText.getText().length() == 4) {
			loginButton.setDisable(true);
			loginButton.fire();
			loginButton.setDisable(false);
		}

	}


	public synchronized void deletePin() {
		String pin = pinText.getText();
		pinText.setText(pin != null && pin.length() < 1 ? "" : pin.substring(0, pin.length() - 1));
	}

	@FXML
    void closeclick(MouseEvent event) {
		System.exit(0);
    }

}
