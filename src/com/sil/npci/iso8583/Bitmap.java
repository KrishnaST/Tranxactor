package com.sil.npci.iso8583;


public class Bitmap {

	private static final long one 	= Long.MIN_VALUE;
	private static final long op 	= 0xFF;
	
	private long primap = 0;
	private long secmap = 0;

	public Bitmap() {}
	
	public Bitmap(String primap, String secmap) {
		this.primap = Long.parseUnsignedLong(primap,16);
		this.secmap = Long.parseUnsignedLong(secmap,16);
	}
	
	public Bitmap(long primap, long secmap) {
		this.primap = primap;
		this.secmap = secmap;
	}
	
	public boolean get(int i) {
		if (i>0 && i<=64) return (primap & (one >>> (i-1))) == (one >>> (i-1));
		else if(i>64 && i<=128) return (secmap & (one >>> (i-65))) == (one >>> (i-65));
		return false;
	}

	public void set(int i) {
		if (i>0 && i<=64) primap |= (one >>> (i-1));
		else if(i>64 && i<=128) secmap |= (one >>> (i-65));
	}
	
	public void remove(int i) {
		if (i>0 && i<=64) primap &= ~(one >>> (i-1));
		else if(i>64 && i<=128) secmap &= ~(one >>> (i-65));
	}
	
	public void setPrimaryHexmap(String primap) {
		this.primap = Long.parseUnsignedLong(primap,16);
	}
	
	public void setPrimaryLong(long primap) {
		this.primap = primap;
	}
	
	
	public void setPrimaryBytes(byte[] bytes) {
		primap = 0;
		for(int i=0; i<bytes.length; i++) {
			primap = primap  | ((long) (bytes[i] & 0xFF) << (7-i)*8);
		}
	}
	
	public void setSecondaryBytes(byte[] bytes) {
		secmap = 0;
		for(int i=0; i<bytes.length; i++) {
			secmap = secmap  | ((long) (bytes[i] & 0xFF) << (7-i)*8);
		}
	}
	
	public void setSecondaryHexmap(String secmap) {
		this.secmap = Long.parseUnsignedLong(secmap,16);
	}
	
	public void setSecondaryLong(long secmap) {
		this.secmap = secmap;
	}
	public boolean isSecondaryBitmapExists() {
		return secmap !=0;
	}
	
	public long getPrimaryLong() {
		return primap;
	}
	
	public long getSecondaryLong() {
		return secmap;
	}
	
	public byte[] toBytes() {
		byte[] bitmap = null;
		if(secmap == 0) {
			bitmap = new byte[8];
			for(int i=0; i<8; i++) {
				bitmap[i] =  (byte) ((primap>>>((7-i)*8)) & op);
			}
		}
		else {
			set(1);
			bitmap = new byte[16];
			for(int i=0; i<8; i++) {
				bitmap[i] =  (byte) ((primap>>>((7-i)*8)) & op);
			}
			for(int i=0; i<8; i++) {
				bitmap[8+i] =  (byte) ((secmap>>>((7-i)*8)) & op);
			}
			remove(1);
		}
		return bitmap;				
	}
	
	public Bitmap copy() {
		return new Bitmap(primap, secmap);
	}
	
	public boolean isEmpty() {
		return (primap == 0 && secmap == 0);
	}
}
