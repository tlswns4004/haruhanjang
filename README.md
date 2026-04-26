# 하루한장

감사 일기 + 주문 서비스

## 실행 방법

### 1. 빌드
./mvnw clean package -DskipTests

### 2. Docker 실행
docker compose up --build

### 3. 접속
http://localhost:8080/diaries

## 주요 기능

- 감사 일기 작성
- 이미지 업로드
- 태그 필터링
- 주문 생성 (기록 → 책)
- 주문 상태 관리 (접수 / 제작중 / 완료 / 취소)
- 주문 취소 기능
- 상태 변경 이력 기록
- JSON 다운로드

## 기술 스택

### Backend
- Java 21
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL

### Frontend
- Thymeleaf 기반 SSR(Server Side Rendering)
- Bootstrap 5.3 (반응형 UI)
- Custom CSS (브랜드 UI 구성)
- Google Fonts (UI 가독성 개선)

### DevOps
- Docker / Docker Compose (실행 환경 표준화)

### Feature
- 이미지 업로드 (Multipart 처리)
- 주문 상태 관리 (PENDING → PROCESSING → COMPLETED → CANCELLED)
- 상태 변경 이력 관리
- JSON 다운로드 API
