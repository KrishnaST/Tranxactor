package com.sil.npci.hsm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.pmw.tinylog.Logger;
import com.sil.npci.config.GlobalConfig;

public class HSM {

	private static final String MINIMUM_PIN_LENGTH 		= "04";
	private static final String MAXIMUM_PIN_LENGTH 		= "12";
	private static final String DECIMALIZATION_TABLE 	= "0123456789012345";
	public static final String  PVKI = "1";
	
	public static final String execute(String command) {
		String response = null;
		System.out.println("command : "+command);
		try(Socket socket = new Socket(GlobalConfig.HSM_IP, GlobalConfig.HSM_PORT);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream din  = new DataInputStream(socket.getInputStream())){
			dos.writeUTF(command);
			response = din.readUTF();
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
		System.out.println("response : "+response);
		return response;
	}
	
	private static final String encryptPin(final String pin, final String pan) {
		String resultBA = null;
		try {
			String pan12 = pan.substring(pan.length()-13, pan.length()-1);
			String pinPadded = pin+"F";
			String commandBA = new StringBuilder(23).append("0000").append("BA").append(pinPadded).append(pan12).toString();
			resultBA = execute(commandBA);
		} catch (Exception e) {
			Logger.error(e);
		}
		return resultBA;
	}
	
	public static final String calculateOffsetUsingPin(final String pin, final String pan, final String pvklmk) {
		String offset = null;
		try {
			String pan12 			= pan.substring(pan.length()-13, pan.length()-1);
			String validationData 	= pan.substring(pan.length()-16, pan.length()-6)+"N"+pan.substring(pan.length()-1);
			String responseBA 		= encryptPin(pin, pan);
			if(responseBA != null && responseBA.substring(6,8).equals("00")) {
				String commandDE 	= new StringBuilder(70).append("0000").append("DE").append(pvklmk).append(responseBA.substring(8))
									.append(MINIMUM_PIN_LENGTH).append(pan12).append(DECIMALIZATION_TABLE).append(validationData).toString();
				String responseDE 	= execute(commandDE);
				return responseDE == null || !responseDE.subSequence(6, 8).equals("00") ? null : responseDE.substring(8,12);
			}
			else return null;
		} catch (Exception e) {
			Logger.error(e);
		}
		return offset;
	}
	
	public static final String offsetUsingPinBlockZPK(final String keylmk, final String pvklmk, final String pinBlock, final String pan) {
		String offset = null;
		try {
			String pan12 		  = pan.substring(pan.length()-13, pan.length()-1);
			String validationData = pan.substring(pan.length()-16, pan.length()-6)+"N"+pan.substring(pan.length()-1);
			String commandBK 	  = new StringBuilder(118).append("0000BK").append("001").append(keylmk).append(pvklmk).append(pinBlock).append("01")
										.append(MINIMUM_PIN_LENGTH).append(pan12).append(DECIMALIZATION_TABLE).append(validationData).toString();
			String responseBK 	  = execute(commandBK);
			if(responseBK != null && responseBK.substring(6, 8).equals("00")) offset = responseBK.substring(8, 12);
		} catch (Exception e) {
			Logger.error(e);
		}
		return offset;
	}
	
	public static final String changePinOffset(final String keylmk, final String pvklmk, final String pinBlock, final String pan, final String currentOffset, final String newPinBlock) {
		String offset = null;
		try {
			String pan12 			= pan.substring(pan.length()-13, pan.length()-1);
			String validationData 	= pan.substring(pan.length()-16, pan.length()-6)+"N"+pan.substring(pan.length()-1);
			String commandDU 		= new StringBuilder(150).append("0000DU001").append(keylmk).append(pvklmk).append(pinBlock)
									  .append("01").append(MINIMUM_PIN_LENGTH).append(pan12).append(DECIMALIZATION_TABLE).append(validationData)
									  .append(String.format("%4s", currentOffset).replaceAll(" ", "0")).append("FFFFFFFF").append(newPinBlock).toString();
			String responseDU 		= execute(commandDU);
			if(responseDU != null && responseDU.substring(6, 8).equals("00")) offset =  responseDU.substring(8, 12) ;		
		} catch (Exception e) {
			Logger.error(e);
		}
		return offset;
	}
	
	public static final String validateInterchangePin(final String zpklmk, final String pvklmk, final String pinBlock, final String pan, final String offset) {
		String response = "91";
		try {
			String pan12 			= pan.substring(pan.length()-13, pan.length()-1);
			String validationData 	= pan.substring(pan.length()-16, pan.length()-6)+"N"+pan.substring(pan.length()-1);
			String commandEA 		= new StringBuilder().append("0000EA").append(zpklmk).append(pvklmk).append(MAXIMUM_PIN_LENGTH)
									  .append(pinBlock).append("01").append(MINIMUM_PIN_LENGTH).append(pan12).append(DECIMALIZATION_TABLE)
									  .append(validationData).append(String.format("%4s", offset).replaceAll(" ", "0")).append("FFFFFFFF").toString();
			String responseEA = execute(commandEA);
			if(responseEA != null) response = responseEA.substring(6, 8);
		} catch (Exception e) {
			Logger.error(e);
		}
		return response;
	}
	
	public static final String validateCVV(final String cvk1, final String cvk2, final String pan, final String expiry, final String serviceCode, final String cvv) {
		String response = "91";
		try {
			String commandCY  = new StringBuilder(65).append("0000").append("CY").append(cvk1).append(cvk2).append(cvv).append(pan).append(";").append(expiry).append(serviceCode).toString();
			String responseCY = execute(commandCY);
			if(responseCY != null) response = responseCY.substring(6, 8);
		} catch (Exception e) {
			Logger.error(e);
		}
		return response;
	}
	
	public static final String importKey(final String zmklmk, final String keyzmk) {
		String zpklmk = null;
		try {
			String commandA6 = new StringBuilder().append("0000A6001").append(zmklmk).append(keyzmk).append("U").toString();
			String responseA6 = execute(commandA6);
			if(responseA6 != null && responseA6.substring(6, 8).equals("00")) zpklmk = responseA6.substring(8,41);
		} catch (Exception e) {
			Logger.error(e);
		}
		return zpklmk;
	}
	
	public static final String calculatePVV(final String pan, final String pin, final String pvklmk,final String pvki){
		String pvv = null;
		try {
			String pan12 = pan.substring(pan.length() - 13, pan.length() - 1);
			String commandBA = new StringBuilder("0000BA").append(pin).append("F").append(pan12).toString();
			String generatedPin = execute(commandBA).substring(8);
			String commandDG = new StringBuilder("0000DG").append(pvklmk).append(generatedPin).append(pan12).append(pvki).toString();
			String responseDG = execute(commandDG);
			if(responseDG != null && responseDG.substring(6, 8).equals("00")) pvv = responseDG.substring(8);
		} catch (Exception e) {
			Logger.error(e);
		}
		return pvv;
	}
	
	public static void main(String[] args) {
		System.out.println(validateCVV("150D8C0DF3348295", "B75E6BCE8B0A1D07", "6071029990306987", "2210", "620", "123") );
		System.out.println(calculateOffsetUsingPin("1234", "4135080750032932", "ADDA0BD09687610B"));
		System.out.println(importKey("U869393350325267BA5CE86B9283A4291", "X116A0DBF3E7521D9E392D698AEB20AC6"));
	}
}
