package com.sil.npci.iso8583.constants;

public class CBSFormat {
	public static final FieldType[] type = new FieldType[129];
	public static final int length[] = new int[129];
	
	static {
		
		type[0] = FieldType.NUM;
		length[0] = 4;

		type[1] = FieldType.BINARY;
		length[1] = 8;

		type[2] = FieldType.LLNUM;
		length[2] = 19;

		type[3] = FieldType.NUM;
		length[3] = 6;

		type[4] = FieldType.NUM;
		length[4] = 12;

		type[5] = FieldType.NUM;
		length[5] = 12;

		type[6] = FieldType.NUM;
		length[6] = 12;

		type[7] = FieldType.NUM;
		length[7] = 10;

		type[9] = FieldType.NUM;
		length[9] = 8;

		type[10] = FieldType.NUM;
		length[10] = 8;

		type[11] = FieldType.NUM;
		length[11] = 6;

		type[12] = FieldType.NUM;
		length[12] = 6;

		type[13] = FieldType.NUM;
		length[13] = 4;

		type[14] = FieldType.NUM;
		length[14] = 4;

		type[15] = FieldType.NUM;
		length[15] = 4;

		type[16] = FieldType.NUM;
		length[16] = 4;
		
		type[17] = FieldType.NUM;
		length[17] = 4;

		type[18] = FieldType.NUM;
		length[18] = 4;

		type[19] = FieldType.NUM;
		length[19] = 3;

		type[22] = FieldType.NUM;
		length[22] = 3;

		type[23] = FieldType.NUM;
		length[23] = 3;

		type[25] = FieldType.NUM;
		length[25] = 2;

		type[28] = FieldType.NUM;
		length[28] = 9;

		type[32] = FieldType.LLNUM;
		length[32] = 11;

		type[33] = FieldType.LLNUM;
		length[33] = 11;

		type[35] = FieldType.LLNUM;
		length[35] = 37;

		type[37] = FieldType.CHAR;
		length[37] = 12;

		type[38] = FieldType.CHAR;
		length[38] = 6;

		type[39] = FieldType.CHAR;
		length[39] = 2;

		type[40] = FieldType.CHAR;
		length[40] = 3;

		type[41] = FieldType.CHAR;
		length[41] = 16;

		type[42] = FieldType.CHAR;
		length[42] = 15;

		type[43] = FieldType.CHAR;
		length[43] = 40;

		type[44] = FieldType.LLCHAR;
		length[44] = 25;

		type[45] = FieldType.LLCHAR;
		length[45] = 76;

		type[48] = FieldType.LLLCHAR;
		length[48] = 999;

		type[49] = FieldType.CHAR;
		length[49] = 3;

		type[50] = FieldType.CHAR;
		length[50] = 3;

		type[51] = FieldType.CHAR;
		length[51] = 3;

		type[52] = FieldType.CHAR;
		length[52] = 16;

		type[54] = FieldType.LLLCHAR;
		length[54] = 120;

		type[55] = FieldType.LLLCHAR;
		length[55] = 999;

		type[60] = FieldType.LLLCHAR;
		length[60] = 999;

		type[61] = FieldType.LLLCHAR;
		length[61] = 999;

		type[62] = FieldType.LLLCHAR;
		length[62] = 999;

		type[63] = FieldType.LLLCHAR;
		length[63] = 999;

		type[70] = FieldType.NUM;
		length[70] = 3;

		type[90] = FieldType.NUM;
		length[90] = 42;

		type[91] = FieldType.CHAR;
		length[91] = 1;

		type[95] = FieldType.CHAR;
		length[95] = 42;

		type[101] = FieldType.LLCHAR;
		length[101] = 17;

		type[102] = FieldType.LLCHAR;
		length[102] = 19;
		
		type[105] = FieldType.LLLCHAR;
		length[105] = 999;

		type[120] = FieldType.LLLCHAR;
		length[120] = 999;

		type[121] = FieldType.LLLCHAR;
		length[121] = 999;

		type[122] = FieldType.LLLCHAR;
		length[122] = 999;

		type[123] = FieldType.LLLCHAR;
		length[123] = 999;

		type[124] = FieldType.LLLCHAR;
		length[124] = 999;

		type[126] = FieldType.LLLCHAR;
		length[126] = 999;

		type[127] = FieldType.LLLCHAR;
		length[127] = 999;
		
	}
	
}
