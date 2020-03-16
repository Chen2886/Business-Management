package Main;

public enum DBOrder {
    MAT(0), PROD(1), SELLER(2), FORMULA(3);

    private final int value;

    private DBOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
