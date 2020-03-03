package Product;

import java.util.Calendar;

public class Date {
    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        if (this.year <= Calendar.getInstance().get(Calendar.YEAR) - 10 ||
                this.year > Calendar.getInstance().get(Calendar.YEAR) + 10 ||
                this.month <= 0 || this.month > 12 || this.day <= 0 || this.day > 31) {
            return "";
        }
        return String.format("%d/%d/%d", year, month, day);
    }

    public static Date stringToDate(String date) {
        if (date == null) {
            return new Date(0, 0, 0);
        }
        else if (date.equals("没有数据") || date.equals("") || date.equals("~")) {
            return new Date(0, 0, 0);
        }
        else {
            String[] dateArr = date.split("/");
            Date newDate = new Date(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
            return newDate;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Date && ((Date) obj).getYear() == this.year && ((Date) obj).getMonth() == this.month &&
                ((Date) obj).getDay() == this.day;
    }
}
