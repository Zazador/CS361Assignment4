
public class AES {

	public static int[][] sbox = new int[16][16];
	
	public static void main(String[] args) {
		sbox = sBox.getSBox();
		
		
		String[][] plaintext = {
			{ "00", "00", "00", "00" },
			{ "00", "00", "00", "00" },
			{ "00", "00", "00", "00" },
			{ "00", "00", "00", "00" },
		};
		
		int[][] cipherkey = {
				{ 0x00, 0x00, 0x00, 0x00 },
				{ 0x00, 0x00, 0x00, 0x00 },
				{ 0x00, 0x00, 0x00, 0x00 },
				{ 0x00, 0x00, 0x00, 0x00 },
			};
		
		subBytes(plaintext);
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
				System.out.println(row + " " + column);
				plaintext[i][j] = Integer.toHexString(sbox[Integer.parseInt(column)][Integer.parseInt(row)]);
				//System.out.println(plaintext[i][j]);
				builder.append(plaintext[i][j]);
			}
		}
		System.out.println("After subBytes:");
		System.out.println(builder.toString());
		return plaintext;
	}

}
