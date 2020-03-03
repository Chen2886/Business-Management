package Product;

import Material.HandleError;

import java.io.*;

public class SerialNum {
	private static int serialNum;
	private static final String filepath = "ProdSerial.txt";

	public static void initSku() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line = br.readLine();
			serialNum = Integer.parseInt(line);
			br.close();
		} catch (FileNotFoundException e) {
			WriteSku();
		} catch (IOException e) {
			HandleError error = new HandleError("SerialNum", Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * This function goes to the sku.txt and retrieves the current sku
	 * @return the current sku
	 */
	public static int getSerialNum() {
		UpdateSku();
		return serialNum;
	}

	/**
	 * This function goes to sku.txt, add one to the current sku
	 * NOTE: SHOULD NOT USE UNLESS ADDING A NEW ORDER. THIS WILL NEVER DECREASE EVEN IF ORDER DELETED.
	 */
	public static void UpdateSku() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
			writer.write(String.valueOf(serialNum + 1));
			writer.close();
			serialNum++;
		} catch (IOException e) {
			HandleError error = new HandleError("SerialNum", Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	public static int getLatestSku() {
		return serialNum;
	}

	private static void WriteSku() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
			writer.write("1");
			writer.close();
		} catch (IOException e) {
			HandleError error = new HandleError("SerialNum", Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}
}
