//Name: Zach Zador
//email: zazador@gmail.com
//CSID: sakz
//UTEID: zaz78
//
//Name: Mike Schiller
//email: schillbs@gmail.com
//CSID: schiller
//UTEID: mds3428

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class AES {

	public static int[][] sbox = new int[16][16];
	public static int[][] invsbox = new int[16][16];
	public static int[][] invCol = { { 14, 11, 13, 9 }, { 9, 14, 11, 13 },
			{ 13, 9, 14, 11 }, { 11, 13, 9, 14 } };
	public static int roundNum = 0;
	public static int decRoundNum = 10;
	public static String check;

	public static void main(String[] args) throws FileNotFoundException {
		sbox = sBox.getSBox();
		invsbox = sBox.getinvSBox();
		File file = new File(args[2]);
		long file1B = file.length();
		long file1K = file1B / 1024;
		long file1M = file1K / 1024;
		Scanner scan = new Scanner(file);
		File file2 = new File(args[1]);
		double file2B = file.length();
		double file2K = file2B / 1024;
		double file2M = file2K / 1024;
		Scanner scan2 = new Scanner(file2);
		int counter = 0;
		check = args[0];

		final long startTime = System.currentTimeMillis();

		String[][] plaintext = new String[4][4];
		String[][] cipherkey = new String[4][4];
		HashMap<Integer, String[][]> cipherTable = new HashMap<Integer, String[][]>();

		// Form the plaintext 2D array
		if (check.equals("e")) {
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
		} else {
			String s = scan.next();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					plaintext[j][i] = s.substring(counter, counter + 2);
					counter += 2;
				}
			}
			counter = 0;
		}

		counter = 0;
		String keytemp = scan2.next();
		// Form the cipherkey 2D array
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				cipherkey[i][j] = keytemp.substring(counter, counter + 2);
				counter += 2;
			}
		}

		String[][] rcon = {
				{ "01", "02", "04", "08", "10", "20", "40", "80", "1b", "36" },
				{ "00", "00", "00", "00", "00", "00", "00", "00", "00", "00" },
				{ "00", "00", "00", "00", "00", "00", "00", "00", "00", "00" },
				{ "00", "00", "00", "00", "00", "00", "00", "00", "00", "00" }, };

		// If encryption, begin steps
		if (check.equals("e")) {
			PrintWriter writer = new PrintWriter("plaintext.enc");
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
					writer.print(plaintext[j][i]);
				}
			}
			writer.close();
			// Otherwise, begin decryption
		} else {
			PrintWriter writer2 = new PrintWriter("plaintext.enc.dec");
			cipherTable.put(99, cipherkey);
			for (int w = 0; w < 10; w++) {
				cipherTable.put(w, cipherkey = keyExpansion(cipherkey, rcon));
				roundNum++;
			}
			cipherkey = cipherTable.get(9);
			addRoundKey(plaintext, cipherkey);
			decRoundNum--;
			roundNum = 0;
			int tableCount = 8;
			for (int i = 0; i < 9; i++) {
				cipherkey = cipherTable.get(tableCount);
				invSubBytes(plaintext);
				invShiftRows(plaintext);
				addRoundKey(plaintext, cipherkey);
				inverseMixColumns(plaintext);
				decRoundNum--;
				roundNum++;
				tableCount--;
			}

			cipherkey = cipherTable.get(99);
			invSubBytes(plaintext);
			invShiftRows(plaintext);
			addRoundKey(plaintext, cipherkey);
			decRoundNum--;

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					writer2.print(plaintext[i][j]);
				}
			}
			writer2.close();
		}
		scan.close();
		scan2.close();

		final long endTime = System.currentTimeMillis();
		// System.out.println("Bandwidth: " + file1B + "/" + (endTime -
		// startTime) + " Bytes/s");
	}

	// Key expansion step of AES
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

		// Store temp answers to result after looking up in sBox
		for (int i = 0; i < 4; i++) {
			row = String.valueOf(rotword[i].charAt(0));
			column = String.valueOf(rotword[i].charAt(1));
			result[i][0] = Integer
					.toHexString(sbox[Integer.parseInt(row, 16)][Integer
							.parseInt(column, 16)]);
		}

		// Form hex value from int
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

	// subBytes step of AES
	public static String[][] subBytes(String[][] plaintext) {
		String temp = "";
		String row = "";
		String column = "";
		StringBuilder builder = new StringBuilder();

		// Swap ints with those from sBox
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

	// InvSubBytes step for AES
	public static String[][] invSubBytes(String[][] plaintext) {
		String temp = "";
		String row = "";
		String column = "";
		String s;
		StringBuilder builder = new StringBuilder();

		// Replace values in plaintext with those from invSBox
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp = plaintext[j][i];
				row = String.valueOf(temp.charAt(0));
				column = String.valueOf(temp.charAt(1));
				s = Integer
						.toHexString(invsbox[Integer.parseInt(row, 16)][Integer
								.parseInt(column, 16)]);
				if (s.length() == 2)
					plaintext[j][i] = s;
				else {
					s = "0" + s;
					plaintext[j][i] = s;
				}
				builder.append(plaintext[j][i]);
			}
		}
		System.out.println("After invSubBytes:");
		System.out.println(builder.toString());
		return plaintext;
	}

	// shiftRows step of AES
	public static String[][] shiftRows(String[][] plaintext) {
		String[] temp = new String[4];

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			builder.append(plaintext[0][i]);
		}

		// Go column at a time for shifting
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[j] = plaintext[i][j];
			}

			// Does the actual shifting
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

	// invShiftRows step of AES
	public static String[][] invShiftRows(String[][] plaintext) {
		String[] temp = new String[4];

		StringBuilder builder = new StringBuilder();

		// Go column at a time for shifting
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[j] = plaintext[i][j];
			}

			// Does the actual inverse shifting
			temp = invShift(temp, i);

			for (int k = 0; k < 4; k++) {
				plaintext[i][k] = temp[k];
			}
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				builder.append(plaintext[j][i]);
			}
		}

		System.out.println("After invShiftRows:");
		System.out.println(builder.toString());
		return plaintext;
	}

	// Shifts the columns based on which row it is currently on
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

	// Shifts the columns based on which row it is currently on
	public static String[] invShift(String[] temp, int i) {
		String temp2 = "";

		for (int j = 1; j <= i; j++) {
			temp2 = temp[3];
			temp[3] = temp[2];
			temp[2] = temp[1];
			temp[1] = temp[0];
			temp[0] = temp2;
		}

		return temp;
	}

	// mixColumns step of AES
	public static String[][] mixColumns(String[][] plaintext) {
		String[] temp = new String[4];

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[j] = plaintext[j][i];
			}
			// Does the multiplication for mixColumns
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

	// inverseMixColumns step of AES
	public static String[][] inverseMixColumns(String[][] plaintext) {
		String[] temp = new String[4];

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[j] = plaintext[j][i];
			}
			// Begins the actual multiplication of this step
			temp = invMixColumns(temp);

			for (int k = 0; k < 4; k++) {
				plaintext[k][i] = temp[k];
				builder.append(temp[k]);
			}
		}
		System.out.println("After invMixColumns:");
		System.out.println(builder.toString());
		return plaintext;
	}

	// Calls to begin the multplication for invMixColumns
	public static String[] invMixColumns(String[] plaintext) {
		Byte[] myB = new Byte[4];
		int val, bee;
		Byte b, bb;
		String s;
		int invMat;
		String[] tempResults = new String[4];
		String[] results = new String[4];

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				s = plaintext[j];
				invMat = invCol[i][j];
				if (invMat == 9)
					tempResults[j] = Integer.toHexString(invMult(s, 9));
				else if (invMat == 11)
					tempResults[j] = Integer.toHexString(invMult(s, 11));
				else if (invMat == 13)
					tempResults[j] = Integer.toHexString(invMult(s, 13));
				else if (invMat == 14)
					tempResults[j] = Integer.toHexString(invMult(s, 14));

				val = Integer.parseInt(tempResults[j], 16);
				b = (byte) val;
				myB[j] = b;
			}
			Byte result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);
			String test = String.format("%02X", result);
			results[i] = test;
			builder.append(test);
		}
		return results;
	}

	// Very long way to iteratively process through the multiplication for
	// mixColumns step
	public static String[] mult(String[] temp) {
		Byte[] myB = new Byte[4];
		int val, bee;
		Byte b, bb;
		String s;
		String[] tempResults = new String[4];

		val = Integer.parseInt(temp[0], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[0] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[0] = b;
		}

		val = Integer.parseInt(temp[1], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[1], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[1] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[1], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[1] = b;
		}

		myB[2] = (byte) Integer.parseInt(temp[2], 16);

		myB[3] = (byte) Integer.parseInt(temp[3], 16);

		Byte result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);
		String test = String.format("%02X", result);
		tempResults[0] = test;

		/********************************************************/
		myB[0] = (byte) Integer.parseInt(temp[0], 16);

		val = Integer.parseInt(temp[1], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[1] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[1] = b;
		}

		val = Integer.parseInt(temp[2], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[2], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[2] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[2], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[2] = b;
		}

		myB[3] = (byte) Integer.parseInt(temp[3], 16);

		result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);
		test = String.format("%02X", result);
		tempResults[1] = test;

		/********************************************************/

		myB[0] = (byte) Integer.parseInt(temp[0], 16);

		myB[1] = (byte) Integer.parseInt(temp[1], 16);

		val = Integer.parseInt(temp[2], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[2] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[2] = b;
		}

		val = Integer.parseInt(temp[3], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[3], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[3] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[3], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[3] = b;
		}

		result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);
		test = String.format("%02X", result);
		tempResults[2] = test;

		/********************************************************/
		val = Integer.parseInt(temp[0], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[0], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[0] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			bee = Integer.parseInt(temp[0], 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[0] = b;
		}

		myB[1] = (byte) Integer.parseInt(temp[1], 16);

		myB[2] = (byte) Integer.parseInt(temp[2], 16);

		val = Integer.parseInt(temp[3], 16);
		b = (byte) val;
		s = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		b = (byte) (b << 1);
		if (s.charAt(0) == '1') {
			bee = Integer.parseInt("1b", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[3] = b;
		} else {
			bee = Integer.parseInt("00", 16);
			bb = (byte) bee;
			b = (byte) (b ^ bb);
			myB[3] = b;
		}

		result = (byte) (myB[0] ^ myB[1] ^ myB[2] ^ myB[3]);
		test = String.format("%02X", result);
		tempResults[3] = test;

		return tempResults;
	}

	// Nice and simple way to do the multplication
	public static int invMult(String s, int num) {
		int[] mul9 = sBox.get9Box();
		int[] mul11 = sBox.get11Box();
		int[] mul13 = sBox.get13Box();
		int[] mul14 = sBox.get14Box();

		if (num == 9)
			return mul9[Integer.parseInt(s, 16)];
		else if (num == 11)
			return mul11[Integer.parseInt(s, 16)];
		else if (num == 13)
			return mul13[Integer.parseInt(s, 16)];
		else
			return mul14[Integer.parseInt(s, 16)];

	}

	// XOR the roundKey to the plaintext
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

		if (check.equals("e")) {
			System.out.println("After addRoundKey(" + (roundNum + 1) + "):");
			System.out.println(builder.toString());
		} else {
			System.out.println("After addRoundKey(" + (decRoundNum) + "):");
			System.out.println(builder.toString());
		}
		return plaintext;
	}

}
