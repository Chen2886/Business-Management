package Material;

// from my other packages
import Main.*;

import java.io.*;

public class MatSellerId {
	private static int sellerId;
	private static String filepath = "MatSellerId.txt";

	public static void initMatSellerId() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line = br.readLine();
			sellerId = Integer.parseInt(line);
			br.close();
		} catch (FileNotFoundException e) {
			WriteSellerId();
		} catch (IOException e) {
			HandleError error = new HandleError("sellerId", Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * This function goes to the sku.txt and retrieves the current sku
	 * @return the current sku
	 */
	public static int getMatSellerId() {
		UpdateMatSellerId();
		return sellerId;
	}

	/**
	 * This function goes to sku.txt, add one to the current sku
	 * NOTE: SHOULD NOT USE UNLESS ADDING A NEW ORDER. THIS WILL NEVER DECREASE EVEN IF ORDER DELETED.
	 */
	public static void UpdateMatSellerId() {
		try {
			sellerId++;
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
			writer.write(String.valueOf(sellerId));
			writer.close();
		} catch (IOException e) {
			HandleError error = new HandleError("sellerId", Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	private static void WriteSellerId() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
			writer.write("1");
			sellerId = 1;
			writer.close();
		} catch (IOException e) {
			System.out.println("BW & BR Error");
		}
	}
}
