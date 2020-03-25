package Main;

import java.lang.reflect.Array;
import java.util.Arrays;

public class SerialNum {

    private static int[] serialNum;
    private static String[] tableNames = {"materialManagement", "productManagement", "seller", "formula", "productUnitPrice"};

    /**
     * called when program first start, make sure file exists.
     */
    public static void initSerialNum() {
        serialNum = new int[tableNames.length];
        try {
            for (int i = 0; i < tableNames.length; i++) {
                serialNum[i] = DatabaseUtil.GetNewestSerialNum(tableNames[i]);
            }
            System.out.println(Arrays.toString(serialNum));
        } catch (Exception e) {
            e.printStackTrace();
            HandleError error = new HandleError(SerialNum.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * This function returns the newest serialNum
     * @return the current sku
     */
    public static int getSerialNum(DBOrder table) {
        serialNum[table.getValue()]++;
        System.out.println(Arrays.toString(serialNum));
        return serialNum[table.getValue()];
    }
}
