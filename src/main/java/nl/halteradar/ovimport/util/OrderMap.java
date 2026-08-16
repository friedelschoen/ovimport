package nl.halteradar.ovimport.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class OrderMap<T> {
    private Map<T, BigInteger> orders = new HashMap<>();

    public synchronized BigInteger getOrder(T id, BigInteger orderValue) {
        if (orderValue == null) {
            return orders.compute(id, (k, v) -> (v == null) ? BigInteger.ONE : v.add(BigInteger.ONE));
        } else {
            return orders.compute(id, (k, v) -> (v == null) ? orderValue : orderValue.max(v.add(BigInteger.ONE)));
        }
    }
}
