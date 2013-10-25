public class AES {

	public static int[][] sbox = new int[16][16];

	public static void main(String[] args) {
		sbox = sBox.getSBox();

		String[][] plaintext = { { "19", "a0", "9a", "e9" },
				{ "3d", "f4", "c6", "f8" }, { "e3", "e2", "8d", "48" },
				{ "be", "2b", "2a", "08" }, };

		String[][] cipherkey = { { "a0", "88", "23", "2a" },
				{ "fa", "54", "a3", "6c" }, { "fe", "2c", "39", "76" },
				{ "17", "b1", "39", "05" }, };

		subBytes(plaintext);
		shiftRows(plaintext);
		mixColumns(plaintext);
		addRoundKey(plaintext, cipherkey);
	}

	public static String[][] subBytes(String[][] plaintext) {
		String temp = "";
		String row = "";
		String column = "";
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp = plaintext[i][j];
				row = String.valueOf(temp.charAt(0));
				column = String.valueOf(temp.charAt(1));
				plaintext[i][j] = Integer.toHexString(sbox[Integer.parseInt(
						row, 16)][Integer.parseInt(column, 16)]);
				builder.append(plaintext[i][j]);
			}
		}
		System.out.println("After subBytes:");
		System.out.println(builder.toString());
		return plaintext;
	}

	public static String[][] shiftRows(String[][] plaintext) {
		String[] temp = new String[4];

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			builder.append(plaintext[0][i]);
		}

		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[j] = plaintext[i][j];
			}

			temp = shift(temp, i);

			for (int k = 0; k < 4; k++) {
				plaintext[i][k] = temp[k];
				builder.append(temp[k]);
			}

		}
		System.out.println("After shiftRows:");
		System.out.println(builder.toString());
		return plaintext;
	}

	public static String[] shift(String[] temp, int i) {
		String temp2 = "";

		for (int j = 1; j <= i; j++) {
			temp2 = temp[0];
			temp[0] = temp[1];
			temp[1] = temp[2];
			temp[2] = temp[3];
			temp[3] = temp2;
		}

		return temp;
	}

	public static String[][] mixColumns(String[][] plaintext) {
		String[] temp = new String[4];

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[j] = plaintext[j][i];
			}
			temp = mult(temp);

			for (int k = 0; k < 4; k++) {
				plaintext[k][i] = temp[k];
				builder.append(temp[k]);
			}
		}
		System.out.println("After mixColumns:");
		System.out.println(builder.toString());
		return plaintext;
	}

	public static String[] mult(String[] temp) {
		Byte[] myB = new Byte[4];
		int val, bee;
		Byte b, bb;
		String s;
		String[] tempResults = new String[4];

		val = Integer.parseInt(temp[0], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[0] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[0] = b;
		}

		val = Integer.parseInt(temp[1], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[1], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[1] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[1], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[1] = b;
		}

		myB[2] = (byte) Integer.parseInt(temp[2], 16);

		myB[3] = (byte) Integer.parseInt(temp[3], 16);

		Byte result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);

		// System.out.println("FINAL BOX 1 = "
		// + Integer.toBinaryString((result & 0xFF) + 0x100).substring(1));
		String test = String.format("%02X", result);
		tempResults[0] = test;
		// System.out.println(tempResults[0]);

		/********************************************************/
		myB[0] = (byte) Integer.parseInt(temp[0], 16);

		val = Integer.parseInt(temp[1], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[1] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[1] = b;
		}

		val = Integer.parseInt(temp[2], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[2], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[2] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[2], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[2] = b;
		}

		myB[3] = (byte) Integer.parseInt(temp[3], 16);

		result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);

		// System.out.println("FINAL BOX 2 = "
		// + Integer.toBinaryString((result & 0xFF) + 0x100).substring(1));
		test = String.format("%02X", result);
		tempResults[1] = test;
		// System.out.println(tempResults[1]);

		/********************************************************/

		myB[0] = (byte) Integer.parseInt(temp[0], 16);

		myB[1] = (byte) Integer.parseInt(temp[1], 16);

		val = Integer.parseInt(temp[2], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[2] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[2] = b;
		}

		val = Integer.parseInt(temp[3], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[3], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[3] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[3], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[3] = b;
		}

		result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);

		// System.out.println("FINAL BOX 3 = "
		// + Integer.toBinaryString((result & 0xFF) + 0x100).substring(1));
		test = String.format("%02X", result);
		tempResults[2] = test;
		// System.out.println(tempResults[2]);

		/********************************************************/
		val = Integer.parseInt(temp[0], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[0], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[0] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			bee = Integer.parseInt(temp[0], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[0] = b;
		}

		myB[1] = (byte) Integer.parseInt(temp[1], 16);

		myB[2] = (byte) Integer.parseInt(temp[2], 16);

		val = Integer.parseInt(temp[3], 16);
		b = (byte) val;
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
		// .substring(1));
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[3] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			// System.out.println(Integer.toBinaryString((bb & 0xFF) + 0x100)
			// .substring(1));
			b = (byte) (b ^ bb);
			// System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100)
			// .substring(1));
			myB[3] = b;
		}

		result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);

		// System.out.println("FINAL BOX 4 = "
		// + Integer.toBinaryString((result & 0xFF) + 0x100).substring(1));
		test = String.format("%02X", result);
		tempResults[3] = test;
		// System.out.println(tempResults[3]);

		return tempResults;
	}

	public static String[][] addRoundKey(String[][] plaintext,
			String[][] roundkey) {
		int val, val2;
		Byte b, b2;
		String result;
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				val = Integer.parseInt(plaintext[j][i], 16);
				b = (byte) val;
				
				val2 = Integer.parseInt(roundkey[j][i], 16);
				b2 = (byte) val2;
				
				b = (byte) (b ^ b2);
				result = String.format("%02X", b);
				plaintext[j][i] = result;
				builder.append(result);
			}
		}

		System.out.println("After addRoundKey:");
		System.out.println(builder.toString());
		return plaintext;
	}

}
