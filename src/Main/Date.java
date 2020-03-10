package Main;

// from my other packages
import Material.*;
import Product.*;

public class Date {
	int y;
	int m;
	int d;

	public Date(int y, int m, int d) {
		this.y = y;
		this.m = m;
		this.d = d;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	@Override
	public String toString() {
		if (this.y < 2000 || this.y > 2099 || this.m < 1 || this.m > 12 || this.d < 1 || this.d > 31)
			return "";
		else
			return String.format("%d/%d/%d", y, m, d);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Date) return ((Date) o).y == y && ((Date) o).m == m && ((Date) o).d == d;
		else return false;
	}

}
