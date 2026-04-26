package com.example.haruhanjang.domain.diary;

import com.example.haruhanjang.domain.order.BookOrderRepository;
import com.example.haruhanjang.domain.order.OrderItemRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryController {

    private final DiaryRepository diaryRepository;
    private final BookOrderRepository bookOrderRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public String list(@RequestParam(required = false) String tag, Model model) {

        if (tag != null && !tag.isEmpty()) {
            model.addAttribute("diaries",
                    diaryRepository.findAll().stream()
                            .filter(d -> d.getTags() != null && d.getTags().contains(tag))
                            .toList());
        } else {
            model.addAttribute("diaries", diaryRepository.findAllByOrderByEntryDateDesc());
        }

        model.addAttribute("tag", tag);

        long totalDiaryCount = diaryRepository.count();
        long totalOrderCount = bookOrderRepository.count();

        LocalDate latestEntryDate = diaryRepository.findAllByOrderByEntryDateDesc()
                .stream()
                .map(DiaryEntry::getEntryDate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        model.addAttribute("totalDiaryCount", totalDiaryCount);
        model.addAttribute("totalOrderCount", totalOrderCount);
        model.addAttribute("latestEntryDate", latestEntryDate);

        return "diary/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("diaryEntry", new DiaryEntry());
        return "diary/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute DiaryEntry diaryEntry,
                         BindingResult bindingResult,
                         @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (bindingResult.hasErrors()) {
            return "diary/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String savedFileName = saveFile(imageFile);
            diaryEntry.setImagePath("/uploads/" + savedFileName);
        }

        diaryRepository.save(diaryEntry);
        return "redirect:/diaries";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DiaryEntry diary = diaryRepository.findById(id).orElseThrow();
        model.addAttribute("diary", diary);
        return "diary/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("diaryEntry", diaryRepository.findById(id).orElseThrow());
        return "diary/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute DiaryEntry diaryEntry,
                       BindingResult bindingResult,
                       @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (bindingResult.hasErrors()) {
            diaryEntry.setId(id);
            return "diary/form";
        }

        DiaryEntry existingDiary = diaryRepository.findById(id).orElseThrow();

        existingDiary.setEntryDate(diaryEntry.getEntryDate());
        existingDiary.setTitle(diaryEntry.getTitle());
        existingDiary.setContent(diaryEntry.getContent());
        existingDiary.setEmotion(diaryEntry.getEmotion());
        existingDiary.setTags(diaryEntry.getTags());

        if (imageFile != null && !imageFile.isEmpty()) {
            String savedFileName = saveFile(imageFile);
            existingDiary.setImagePath("/uploads/" + savedFileName);
        }

        diaryRepository.save(existingDiary);
        return "redirect:/diaries/" + id;
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {

        // 주문에 포함된 기록인지 체크
        if (orderItemRepository.existsByDiaryEntryId(id)) {
            model.addAttribute("errorMessage", "주문에 포함된 기록은 삭제할 수 없습니다.");
            model.addAttribute("diary", diaryRepository.findById(id).orElseThrow());
            return "diary/detail";
        }

        diaryRepository.deleteById(id);
        return "redirect:/diaries";
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID() + "_" + originalFilename;

        Path filePath = uploadPath.resolve(savedFileName);
        file.transferTo(filePath.toFile());

        return savedFileName;
    }
}