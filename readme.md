# Trip Member Application

이 프로젝트는 Spring Boot를 기반으로 한 Java 웹 애플리케이션으로, 데이터베이스 연동, 보안, 캐싱, 메시징 등의 기능을 제공합니다.

## 구성도
![image](https://github.com/user-attachments/assets/3202a9b2-45e4-4f3c-85bc-c3e36c64cda1)

## 요구사항

```plaintext
- Java Development Kit (JDK) 23 이상
- Gradle (빌드 도구)
- Maven Central Repository 접근 권한 (의존성 다운로드)
```

## 사용된 기술

```plaintext
- Spring Boot
  - spring-boot-starter-web: RESTful API와 웹 기능 개발
  - spring-boot-starter-thymeleaf: 서버 사이드 렌더링 뷰 제공
  - spring-boot-starter-data-jpa: 데이터베이스 연동
  - spring-boot-starter-security: 애플리케이션 보안
  - spring-boot-starter-data-redis: Redis를 이용한 캐싱 및 세션 관리
  - spring-boot-starter-cache: 캐싱 기능 제공
  - spring-kafka: Apache Kafka를 이용한 메시징
- H2 데이터베이스: 개발 및 테스트를 위한 인메모리 데이터베이스
- QueryDSL: 타입 안전한 쿼리 작성을 위한 라이브러리
- JWT (JSON Web Token): jjwt-api를 활용한 토큰 기반 인증
- Logback 및 SLF4J: 효과적인 로깅ß
- 테스트
  - JUnit 5
  - Spring Security Test
```

## 프로젝트 설정

### 레포지토리 클론

```bash
git clone https://github.com/your-repo/trip-application.git
cd trip-application
```

### 프로젝트 빌드 및 실행

1. **클린 및 빌드:**

```bash
./gradlew clean build
```

2. **애플리케이션 실행:**

```bash
./gradlew bootRun
```

### QueryDSL 설정

```plaintext
- 생성된 QueryDSL QClass 파일은 src/main/generated에 저장됩니다.
- clean 명령을 실행하면 generated 디렉토리가 자동으로 삭제됩니다.
```

### 단위 테스트 실행

```bash
./gradlew test
```

테스트 결과는 **passed**, **skipped**, **failed** 상태로 출력됩니다.

## 패키지 구조

```plaintext
- auth: 인증과 관련된 처리를 담당하는 패키지 (로그인, 로그아웃, JWT 토큰 발급 등).
- common: 프로젝트 전반에서 사용되는 공통 기능과 클래스를 포함하는 패키지 (공통 에러 처리 등).
- config: 스프링 부트 설정 파일을 관리하는 패키지 (보안, 데이터베이스, 캐싱, Kafka 설정 등).
- kafka: Kafka와 관련된 메시지 프로듀서를 관리하는 패키지 (메시지 생성 및 전송).
- user: 사용자와 관련된 처리를 담당하는 패키지 (회원 가입, 정보 조회 및 수정).
```

## 주요 기능

```plaintext
1. 인증 및 권한 관리
   - Spring Security를 사용하여 엔드포인트 보호.
   - JWT 기반 토큰 인증.

2. 데이터베이스 관리
   - Spring Data JPA를 활용하여 데이터베이스와 연동.
   - 개발 및 테스트를 위해 H2 데이터베이스 통합.

3. 메시징
   - Kafka를 통한 실시간 메시징 지원.

4. 캐싱
   - Redis 기반 캐싱으로 성능 최적화.

5. 타입 안전 쿼리
   - QueryDSL을 사용하여 타입 안전한 데이터베이스 쿼리 작성.

6. 로깅
   - SLF4J 및 Logback을 활용하여 로깅과 디버깅.
```

## 개발 노트

```plaintext
1. 의존성 관리
   - Gradle과 io.spring.dependency-management 플러그인을 사용하여 의존성 관리.
   - 모든 의존성은 Maven Central에서 다운로드.

2. Java Toolchain
   - Java 23 버전을 사용하도록 설정.

3. 애노테이션 프로세싱
   - Lombok을 사용하여 반복 코드를 최소화.
   - QueryDSL 애노테이션 프로세서를 구성하여 쿼리 클래스를 생성.

4. 개발 도구
   - Spring Boot DevTools를 활용하여 개발 중 핫 리로드 지원.
```

