package scheper.mateus.utils;

import java.math.BigInteger;

public class NumberUtils {

    private NumberUtils() {
    }

    public static Long castBigIntegerToLong(Object object) {
        return ((BigInteger) object).longValue();
    }
}
