package com.click.batchServer.type;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    TRANSFER(1, "이체"),
    PAYMENT(2, "결제"),
    INSTALLMENT(3, "할부"),
    DEPOSIT(0, "입금");

    private final int value;
    private final String name;

    public static TransactionType fromValue(int value) {
        for (TransactionType transactionType : TransactionType.values()) {
            if (transactionType.getValue() == value) {
                return transactionType;
            }
        }
        return DEPOSIT;
    }
}
