package com.example.haruhanjang.domain.diary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
    List<DiaryEntry> findAllByOrderByEntryDateDesc();
    List<DiaryEntry> findByEntryDateBetweenOrderByEntryDateDesc(java.time.LocalDate startDate, java.time.LocalDate endDate);
}