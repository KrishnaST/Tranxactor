package com.sil.npci.iso8583.constants;

public class ResponseCode {

	public static final String	SUCCESS								= "00";
	public static final String	FAILURE								= "01";
	public static final String	INVALID_MERCHANT					= "03";
	public static final String	PICKUP								= "04";
	public static final String	VERIFICATION_FAILURE				= "05";
	public static final String	UNKNOWN_RESPONMSE					= "12";
	public static final String	INVALID_AMOUNT						= "13";
	public static final String	INVALID_CARD						= "14";
	public static final String	INVALID_ISSUER						= "15";
	public static final String	CUSTOMER_CANCELLATION				= "17";
	public static final String	INVALID_RESPONSE					= "20";
	public static final String	NO_ACTION_TAKEN						= "21";
	public static final String	SUSPECTED_MALFUNCTION				= "22";
	public static final String	UNABLE_TO_LOCATE_RECORD				= "25";
	public static final String	FILE_UPDATE_ERROR					= "27";
	public static final String	RECORD_ALREADY_EXISTS				= "28";
	public static final String	FILE_UPDATE_FAILURE					= "29";
	public static final String	FORMAT_ERROR						= "30";
	public static final String	BANK_NOT_SUPPORTED					= "31";
	public static final String	EXPIRED_CARD						= "33";
	public static final String	SUSPECTED_FRAUD						= "34";
	public static final String	RESTRICTED_CARD						= "36";
	public static final String	PIN_TRIES_EXCEEDED_CAPTURE			= "38";
	public static final String	NO_CREDIT_ACCOUNT					= "39";
	public static final String	FUCNTION_NOT_SUPPORTED				= "40";
	public static final String	LOST_CARD							= "41";
	public static final String	NO_UNIVERSAL_ACCOUNT				= "42";
	public static final String	STOLLEN_CARD						= "43";
	public static final String	INSUFFICIENT_FUNDS					= "51";
	public static final String	NO_CHECKING_ACCOUNT					= "52";
	public static final String	NO_SAVING_ACCOUNT					= "53";
	public static final String	INCORRECT_PIN						= "55";
	public static final String	NO_CARD_RECORD						= "56";
	public static final String	TRANSACTION_NOT_PERMITED_CARD		= "57";
	public static final String	TRANSACTION_NOT_PERMITED_TERMINAL	= "58";
	public static final String	DECLINE_RISK						= "59";
	public static final String	CONTACT_ACQUIRER					= "60";
	public static final String	EXCEEDS_WITHDRAWAL_AMOUNT			= "61";
	public static final String	RESTRICTED_CARD_DECLINE				= "62";
	public static final String	SECURITY_VIOLATION					= "63";
	public static final String	EXCEEDS_WITHDRAWAL_FREQUENCY		= "65";
	public static final String	CARD_ACCEPTOR_CALLS_ACQUIRER		= "66";
	public static final String	HARD_CAPTURE						= "67";
	public static final String	ACQUIRER_TIMEOUT					= "68";
	public static final String	MOBILE_NO_NOT_FOUND					= "69";

	public static final String	DEEMED_ACCEPTANCE	= "71";
	public static final String	ISSUER_DECLINE_RISK	= "74";
	public static final String	PIN_TRIES_EXCEEDED	= "75";

	public static final String	CRYPTOGRAPHIC_ERROR		= "81";
	public static final String	CUTOFF_IN_PROGRESS		= "90";
	public static final String	ISSUER_INOPERATIVE		= "91";
	public static final String	NO_ROUTING_AVAILABLE	= "92";
	public static final String	COMPLIANCE_VIOLATION	= "93";
	public static final String	DUPLICATE_TRANSMISSION	= "94";
	public static final String	RECONSILE_ERROR			= "95";
	public static final String	SYSTEM_MALFUNCTION		= "96";

	public static final String	ARQC_VALIDATION_FAILURE	= "E3";
	public static final String	TVR_VALIDATION_FAILURE	= "E4";
	public static final String	CVR_VALIDATION_FAILURE	= "E5";

	public static final String	NO_AADHAR_LINKED		= "MU";
	public static final String	INVALID_BIOMETRIC_DATA	= "UG";
	public static final String	NO_BIOMETRIC_MATCH		= "U3";
	public static final String	TECHNICAL_DECLINE_UIDAI	= "WZ";

	public static final String	ISSUER_COMPLIANCE_ERROR		= "CI";
	public static final String	ACQUIRER_COMPLIANCE_ERROR	= "CA";
	public static final String	LLM_COMPLIANCE_ERROR		= "M6";
	public static final String	ECOMMERCE_DECLINE			= "ED";

	public static final String FULL_REVERSAL = "22";
}
