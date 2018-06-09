package com.sil.npci.hsm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.sil.npci.config.GlobalConfig;

public class HSM {

	public static final String execute(String command) {
		String response = null;
		try(Socket socket = new Socket(GlobalConfig.HSM_IP, GlobalConfig.HSM_PORT);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream din = new DataInputStream(socket.getInputStream())){
			dos.writeUTF(command);
			response = din.readUTF();
		} catch (Exception e) {
			return null;
		}
		return response;
	}
}
