package dev.powercyphe.coffins.common.util;

public interface BiSupplier<A, B> {

    A getFirst();

    B getSecond();

    static <A, B> BiSupplier<A, B> create(A first, B second) {
        return new BiSupplier<>() {

            @Override
            public A getFirst() {
                return first;
            }

            @Override
            public B getSecond() {
                return second;
            }
        };
    }
}
