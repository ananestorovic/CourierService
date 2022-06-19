//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.etf.sab.test;

import java.math.BigDecimal;

public class Util {
    public Util() {
    }

    static double euclidean(int x1, int y1, int x2, int y2) {
        return Math.sqrt((double)((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    static BigDecimal getPackagePrice(int type, BigDecimal weight, double distance) {
        switch (type) {
            case 0:
                return new BigDecimal(115.0 * distance);
            case 1:
                return new BigDecimal((175.0 + weight.doubleValue() * 100.0) * distance);
            case 2:
                return new BigDecimal((250.0 + weight.doubleValue() * 100.0) * distance);
            case 3:
                return new BigDecimal((350.0 + weight.doubleValue() * 500.0) * distance);
            default:
                return null;
        }
    }

    static double getDistance(Pair<Integer, Integer>... addresses) {
        double distance = 0.0;

        for(int i = 1; i < addresses.length; ++i) {
            distance += euclidean((Integer)addresses[i - 1].getKey(), (Integer)addresses[i - 1].getValue(), (Integer)addresses[i].getKey(), (Integer)addresses[i].getValue());
        }

        return distance;
    }
}
