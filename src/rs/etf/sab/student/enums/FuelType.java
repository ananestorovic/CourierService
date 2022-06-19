package rs.etf.sab.student.enums;

public enum FuelType {
    FULL(12),
    DIESEL(32),
    PETROL(36);


    private final int priceByL;

    FuelType(int priceByL) {
        this.priceByL = priceByL;
    }

    public int getPriceByL() {
        return priceByL;
    }
}
