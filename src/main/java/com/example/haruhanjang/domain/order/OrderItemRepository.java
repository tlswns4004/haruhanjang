package com.example.haruhanjang.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByDiaryEntryId(Long diaryEntryId);
}