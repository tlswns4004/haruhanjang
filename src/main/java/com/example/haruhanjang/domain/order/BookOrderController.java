package com.example.haruhanjang.domain.order;

import com.example.haruhanjang.domain.diary.DiaryEntry;
import com.example.haruhanjang.domain.diary.DiaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class BookOrderController {

    private final BookOrderRepository bookOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiaryRepository diaryRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", bookOrderRepository.findAllByOrderByCreatedAtDesc());
        return "order/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) LocalDate startDate,
                             @RequestParam(required = false) LocalDate endDate,
                             Model model) {

        List<DiaryEntry> diaries = null;

        if (startDate != null && endDate != null) {
            diaries = diaryRepository.findByEntryDateBetweenOrderByEntryDateDesc(startDate, endDate);
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("diaries", diaries);

        return "order/form";
    }

    @PostMapping
    public String create(@RequestParam String title,
                         @RequestParam LocalDate startDate,
                         @RequestParam LocalDate endDate) {

        List<DiaryEntry> diaries = diaryRepository.findByEntryDateBetweenOrderByEntryDateDesc(startDate, endDate);

        BookOrder bookOrder = new BookOrder();
        bookOrder.setTitle(title);
        bookOrder.setStartDate(startDate);
        bookOrder.setEndDate(endDate);
        bookOrder.setStatus(OrderStatus.PENDING);

        for (DiaryEntry diary : diaries) {
            OrderItem item = new OrderItem();
            item.setBookOrder(bookOrder);
            item.setDiaryEntry(diary);
            bookOrder.getOrderItems().add(item);
        }

        bookOrderRepository.save(bookOrder);

        // 생성 이력 저장
        OrderStatusHistory history = new OrderStatusHistory();
        history.setBookOrder(bookOrder);
        history.setFromStatus("CREATED");
        history.setToStatus(OrderStatus.PENDING.name());
        orderStatusHistoryRepository.save(history);

        return "redirect:/orders";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BookOrder order = bookOrderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("statusHistories",
                orderStatusHistoryRepository.findAllByBookOrderIdOrderByChangedAtDesc(id));
        return "order/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               Model model) {
        BookOrder order = bookOrderRepository.findById(id).orElseThrow();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            model.addAttribute("order", order);
            model.addAttribute("statusHistories",
                    orderStatusHistoryRepository.findAllByBookOrderIdOrderByChangedAtDesc(id));
            model.addAttribute("errorMessage", "완료되었거나 취소된 주문은 상태를 변경할 수 없습니다.");
            return "order/detail";
        }

        OrderStatus previousStatus = order.getStatus();

        order.setStatus(status);
        bookOrderRepository.save(order);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setBookOrder(order);
        history.setFromStatus(previousStatus.name());
        history.setToStatus(status.name());
        orderStatusHistoryRepository.save(history);

        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, Model model) {
        BookOrder order = bookOrderRepository.findById(id).orElseThrow();

        if (order.getStatus() != OrderStatus.PENDING) {
            model.addAttribute("order", order);
            model.addAttribute("statusHistories",
                    orderStatusHistoryRepository.findAllByBookOrderIdOrderByChangedAtDesc(id));
            model.addAttribute("errorMessage", "제작 중이거나 완료된 주문은 취소할 수 없습니다.");
            return "order/detail";
        }

        OrderStatus previousStatus = order.getStatus();

        order.setStatus(OrderStatus.CANCELLED);
        bookOrderRepository.save(order);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setBookOrder(order);
        history.setFromStatus(previousStatus.name());
        history.setToStatus(OrderStatus.CANCELLED.name());
        orderStatusHistoryRepository.save(history);

        return "redirect:/orders/" + id;
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadJson(@PathVariable Long id) throws Exception {

        BookOrder order = bookOrderRepository.findById(id).orElseThrow();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(order);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-" + id + ".json")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(data);
    }
}