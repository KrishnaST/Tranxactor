package com.sil.npci.config;

import com.google.gson.GsonBuilder;

public class GlobalConfig {

	public static final GsonBuilder	GSON_BUILDER	= new GsonBuilder().setPrettyPrinting();
	public static final int			CBS_TIMEOUT_MS	= 15000;
	public static final int			NPCI_TIMEOUT_MS	= 20000;
	public static final String		HSM_IP			= "10.100.5.21";
	public static final int			HSM_PORT		= 6046;
}
