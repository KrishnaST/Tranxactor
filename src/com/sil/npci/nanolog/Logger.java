package com.sil.npci.nanolog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.sil.npci.iso8583.ISO8583Message;

public class Logger implements AutoCloseable {

	private final String	uniqueId;
	private BufferedWriter		writer;
	private boolean 		logToFile = true;

	private static final DateTimeFormatter	dateFormat	= DateTimeFormatter.ofPattern("dd-MM-yy");
	private static final DateTimeFormatter	timeFormat	= DateTimeFormatter.ofPattern("HH:mm:ss:SSS");


	public Logger(String bankName, String uniqueId) {
		this.uniqueId = uniqueId;
		if(uniqueId != null) {
			File file = new File("logs/" + bankName + "/" + LocalDate.now().format(dateFormat) + "/" + uniqueId + ".log");
			try {
				file.getParentFile().mkdirs();
				if (!file.exists()) file.createNewFile();
				writer = new BufferedWriter(new FileWriter(file,  true));
			} catch (IOException e) {}
		}
		else logToFile = false;
	}


	public String getUniqueId() {
		return uniqueId;
	}


	@SuppressWarnings({ "deprecation" })
	public void log(String message) {
		String className = sun.reflect.Reflection.getCallerClass(2).getSimpleName();
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		String thread = Thread.currentThread().getName();
		try {
			String logEntry = "[" + LocalTime.now().format(timeFormat) + "] " + thread + " "+className + "." + methodName + "(" + lineNumber + ") " + message+"\r\n";
			System.out.print(logEntry);
			if(logToFile) {
				writer.write(logEntry);
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings({ "deprecation" })
	public void log(Object message) {
		String className = sun.reflect.Reflection.getCallerClass(2).getSimpleName();
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		try {
			String logEntry = "[" + LocalTime.now().format(timeFormat) + "] " + className + "." + methodName + "(" + lineNumber + ") " + message+"\r\n";
			System.out.print(logEntry);
			if(logToFile) {
				writer.write(logEntry);
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void log(ISO8583Message message) {

	}


	@SuppressWarnings({ "deprecation" })
	public void log(Exception e) {
		String className = sun.reflect.Reflection.getCallerClass(2).getSimpleName();
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String log = sw.toString();
		try {
			String logEntry = "[" + LocalTime.now().format(timeFormat) + "] " + className + "." + methodName + "(" + lineNumber + ") " + log+"\r\n";
			System.out.print(logEntry);
			if(logToFile) {
				writer.write(logEntry);
				writer.flush();
			}
		} catch (IOException ioe) {
			e.printStackTrace();
		}
	}


	@Override
	public void close() throws Exception {
		try {
			if(writer != null) writer.close();
		} catch (Exception e) {}
	}

	public void setLogToFile(boolean logToFile) {
		this.logToFile = logToFile;
	}


	

	
}
