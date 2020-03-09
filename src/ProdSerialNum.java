import java.io.*;

public class ProdSerialNum {
	private static int serialNum;
	private static final String filepath = "ProdSerial.txt";

	/**
	 * called when program first start, make sure file exists.
	 */
	public static void initProdSerialNum() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line = br.readLine();
			serialNum = Integer.parseInt(line);
			br.close();
		} catch (FileNotFoundException e) {
			WriteSku();
		} catch (IOException e) {
			HandleError error = new HandleError(ProdSerialNum.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * This function goes to the sku.txt and retrieves the current sku, update it, and return
	 * @return the current sku
	 */
	public static int getProdSerialNum() {
		UpdateProdSku();
		return serialNum;
	}

	/**
	 * This function goes to sku.txt, add one to the current sku
	 * NOTE: SHOULD NOT USE UNLESS ADDING A NEW ORDER. THIS WILL NEVER DECREASE EVEN IF ORDER DELETED.
	 */
	public static void UpdateProdSku() {
		try {
			serialNum++;
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
			writer.write(String.valueOf(serialNum));
			writer.close();
		} catch (IOException e) {
			HandleError error = new HandleError(ProdSerialNum.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	// first time creating file
	private static void WriteSku() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
			writer.write("1");
			serialNum = 1;
			writer.close();
		} catch (IOException e) {
			HandleError error = new HandleError(ProdSerialNum.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}
}
