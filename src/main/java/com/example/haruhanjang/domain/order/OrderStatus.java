package com.example.haruhanjang.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING("주문 접수"),
    PROCESSING("제작 중"),
    COMPLETED("제작 완료"),
    CANCELLED("주문 취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}