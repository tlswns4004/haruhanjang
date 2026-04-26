package com.example.haruhanjang.domain.order;

import com.example.haruhanjang.domain.diary.DiaryEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_order_id")
    @JsonIgnore
    private BookOrder bookOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_entry_id")
    private DiaryEntry diaryEntry;
}