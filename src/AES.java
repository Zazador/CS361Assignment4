import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class AES {

	public static int[][] sbox = new int[16][16];
	public static int roundNum = 0;

	public static void main(String[] args) throws FileNotFoundException {
		sbox = sBox.getSBox();
		File file = new File(args[2]);
		Scanner scan = new Scanner(file);
		File file2 = new File(args[1]);
		Scanner scan2 = new Scanner(file2);
		int counter = 0;
		String check = args[0];
		PrintWriter writer = new PrintWriter("plaintext.enc");
		PrintWriter writer2 = new PrintWriter("plaintext.enc.dec");

		String[][] plaintext = new String[4][4];
		String[][] cipherkey = new String[4][4];

		while (scan.hasNext()) {
			String s = scan.next();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					plaintext[i][j] = s.substring(counter, counter + 2);
					counter += 2;
				}
			}
			counter = 0;
		}

		counter = 0;
		String keytemp = scan2.next();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				cipherkey[i][j] = keytemp.substring(counter, counter + 2);
				counter += 2;
			}
		}

//		String[][] tempplaintext = { { "19", "a0", "9a", "e9" },
//				{ "3d", "f4", "c6", "f8" }, { "e3", "e2", "8d", "48" },
//				{ "be", "2b", "2a", "08" }, };
//
//		String[][] tempcipherkey = { { "2b", "28", "ab", "09" },
//				{ "7e", "ae", "f7", "cf" }, { "15", "d2", "15", "4f" },
//				{ "16", "a6", "88", "3c" }, };

		String[][] rcon = {
				{ "01", "02", "04", "08", "10", "20", "40", "80", "1b", "36" },
				{ "00", "00", "00", "00", "00", "00", "00", "00", "00", "00" },
				{ "00", "00", "00", "00", "00", "00", "00", "00", "00", "00" },
				{ "00", "00", "00", "00", "00", "00", "00", "00", "00", "00" }, };

		if (check.equals("e")) {
			System.out.println("encrypt");
			for (int i = 0; i < 9; i++) {
				cipherkey = keyExpansion(cipherkey, rcon);
				subBytes(plaintext);
				shiftRows(plaintext);
				mixColumns(plaintext);
				addRoundKey(plaintext, cipherkey);
				roundNum++;
			}

			cipherkey = keyExpansion(cipherkey, rcon);
			subBytes(plaintext);
			shiftRows(plaintext);
			addRoundKey(plaintext, cipherkey);
			roundNum++;
			
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					writer.print(plaintext[i][j]);
				}
			}
		}
		else {
			System.out.println("decrypt");
			
		}

		scan.close();
		scan2.close();
		writer.close();
		writer2.close();
	}

	public static String[][] keyExpansion(String[][] cipherkey, String[][] rcon) {
		String[] rotword = new String[4];
		String[][] result = new String[4][4];
		String row, column, test;
		int val, val2, val3;
		Byte b, b2, b3;

		rotword[0] = cipherkey[1][3];
		rotword[1] = cipherkey[2][3];
		rotword[2] = cipherkey[3][3];
		rotword[3] = cipherkey[0][3];

		for (int i = 0; i < 4; i++) {
			row = String.valueOf(rotword[i].charAt(0));
			column = String.valueOf(rotword[i].charAt(1));
			result[i][0] = Integer
					.toHexString(sbox[Integer.parseInt(row, 16)][Integer
							.parseInt(column, 16)]);
		}

		for (int j = 0; j < 4; j++) {
			val = Integer.parseInt(result[j][0], 16);
			b = (byte) val;

			val2 = Integer.parseInt(cipherkey[j][0], 16);
			b2 = (byte) val2;

			val3 = Integer.parseInt(rcon[j][roundNum], 16);
			b3 = (byte) val3;

			b = (byte) (b ^ b2 ^ b3);

			test = String.format("%02X", b);
			result[j][0] = test;
		}

		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				val = Integer.parseInt(result[j][i - 1], 16);
				b = (byte) val;

				val2 = Integer.parseInt(cipherkey[j][i], 16);
				b2 = (byte) val2;

				b = (byte) (b ^ b2);

				test = String.format("%02X", b);
				result[j][i] = test;
			}
		}
		return result;
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

		System.out.println("After addRoundKey(" + (roundNum + 1) + "):");
		System.out.println(builder.toString());
		return plaintext;
	}

}
