package com.example.haruhanjang.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
    List<BookOrder> findAllByOrderByCreatedAtDesc();
}