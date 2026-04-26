package com.example.haruhanjang;

import com.example.haruhanjang.domain.diary.DiaryEntry;
import com.example.haruhanjang.domain.diary.DiaryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInit {

    @Bean
    public org.springframework.boot.CommandLineRunner init(DiaryRepository repo) {
        return args -> {
            if (repo.count() == 0) {

                DiaryEntry d1 = new DiaryEntry();
                d1.setTitle("따뜻한 커피");
                d1.setContent("동료가 커피를 사줘서 기분이 좋았다.");
                d1.setEmotion("happy");
                d1.setTags("동료, 커피, 배려");
                repo.save(d1);

                DiaryEntry d2 = new DiaryEntry();
                d2.setTitle("산책");
                d2.setContent("날씨가 좋아서 기분이 맑아졌다.");
                d2.setEmotion("calm");
                d2.setTags("날씨, 산책, 여유");
                repo.save(d2);

                DiaryEntry d3 = new DiaryEntry();
                d3.setTitle("가족과 저녁");
                d3.setContent("오랜만에 가족과 식사해서 행복했다.");
                d3.setEmotion("warm");
                d3.setTags("가족, 식사, 행복");
                repo.save(d3);

                DiaryEntry d4 = new DiaryEntry();
                d4.setTitle("퇴근길 노을");
                d4.setContent("하늘이 너무 예뻐서 잠깐 멈춰서 봤다.");
                d4.setEmotion("peaceful");
                d4.setTags("노을, 하늘, 여유");
                repo.save(d4);

                DiaryEntry d5 = new DiaryEntry();
                d5.setTitle("운동");
                d5.setContent("땀 흘리고 나니 개운했다.");
                d5.setEmotion("refresh");
                d5.setTags("운동, 건강, 뿌듯");
                repo.save(d5);
            }
        };
    }
}