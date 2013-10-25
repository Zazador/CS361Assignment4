public class AES {

	public static int[][] sbox = new int[16][16];

	public static void main(String[] args) {
		sbox = sBox.getSBox();

		String[][] plaintext = { { "00", "00", "00", "00" },
				{ "00", "00", "00", "00" }, { "00", "00", "00", "00" },
				{ "00", "00", "00", "00" }, };

		String[][] cipherkey = { { "00", "00", "00", "00" },
				{ "00", "00", "00", "00" }, { "00", "00", "00", "00" },
				{ "00", "00", "00", "00" }, };

		subBytes(plaintext);
		shiftRows(plaintext);
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
				plaintext[i][j] = Integer.toHexString(sbox[Integer
						.parseInt(row)][Integer.parseInt(column)]);
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

}
