# Spring Boot + Kotlin 실전 테스트 예제 프로젝트

이 프로젝트는 **Spring Boot + Kotlin + JDK21 + H2DB + Spring Data JPA** 환경에서  
실무적으로 권장되는 **단위 테스트와 통합 테스트 패키지 구조**,  
테스트 유형별 애노테이션, MockK 활용법, 테스트 계층 구분 등  
테스트 자동화의 모범 사례를 제공합니다.

---

## 📁 단위 테스트와 통합 테스트 패키지 구조

- **실제(main) 코드와 동일한 패키지 구조로 테스트 코드를 배치**하는 것이 실무에서 가장 권장되는 베스트 프랙티스입니다.
- 테스트의 목적에 따라 `unit`(단위 테스트), `integration`(통합 테스트)로 구분하여 관리하면,  
  테스트의 성격과 범위를 명확히 할 수 있습니다.

```text
src/main/kotlin/com/example/examplespringbootkotlintdd
    ├── controller
    ├── service
    ├── repository

src/test/kotlin/com/example/examplespringbootkotlintdd
    ├── unit
    │   ├── service
    │   └── repository
    └── integration
        ├── controller
```

---

## 🏷️ 테스트 유형별 주요 애노테이션

- `@SpringBootTest`:  
  전체 스프링 컨텍스트를 로드하는 통합 테스트에 사용.  
  실제 HTTP 요청, DB 연동, 여러 레이어가 함께 동작하는 시나리오 검증에 적합합니다.

- `@DataJpaTest`:  
  JPA 관련 컴포넌트만 로드하는 Repository 계층 테스트에 사용.  
  인메모리 DB(H2)와 함께 빠르고 격리된 환경에서 JPA 기능을 검증할 수 있습니다.

- `@WebMvcTest`:  
  Controller 계층만 슬라이스하여 테스트.  
  Service, Repository 등은 Mock 처리해야 하며, 웹 레이어의 동작만 검증합니다.

- `@Transactional`:  
  테스트 메서드 또는 클래스가 끝나면 트랜잭션을 롤백하여 DB 상태를 초기화합니다.  
  (단, TestRestTemplate 등 외부 API 호출 통합 테스트에서는 별도 데이터 초기화 필요)

---

## 🧩 MockK 주입 방법

- **단위 테스트에서 의존성(Repository, Service 등)을 MockK로 주입**하여  
  실제 DB, 외부 시스템과 격리된 상태에서 로직만 검증할 수 있습니다.

```kotlin
import io.mockk.mockk

class UserServiceTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val userService = UserService(userRepository)
    // ...
}
```

- **Spring 환경에서 Service를 MockK로 주입하려면 @MockkBean 사용**

```kotlin
@WebMvcTest(UserController::class)
class UserControllerTest(@Autowired val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var userService: UserService
    // ...
}
```

---

## 🧪 단위 테스트와 통합 테스트란?

- **단위 테스트(Unit Test)**
    - 한 클래스/메서드의 로직을 외부 의존성과 분리(Mock)하여 검증
    - 빠르고, 실패 원인 추적이 용이
    - 예: Service, Repository 메서드 테스트

- **통합 테스트(Integration Test)**
    - 여러 레이어(Controller, Service, Repository 등)가 실제로 함께 동작하는지 검증
    - 실제 DB, HTTP 요청 등 환경을 포함
    - 예: REST API 전체 플로우, DB 연동 검증

---

## 🏗️ 단위 테스트와 통합 테스트에 속하는 레이어들

| 테스트 유형 | 대표 레이어                            | 예시                                  |
|--------|-----------------------------------|-------------------------------------|
| 단위 테스트 | Service, Repository, Domain 등     | UserServiceTest, UserRepositoryTest |
| 통합 테스트 | Controller, (Service, Repository) | UserControllerIntegrationTest       |

- 단위 테스트는 **MockK**로 하위 레이어를 Mock 처리
- 통합 테스트는 실제 Bean, DB, HTTP 요청 등 환경을 사용

---

## 🛠️ MockK 테스트 코드 작성 방법

- **Mock 객체 생성 및 행위 정의**
    ```
    val userRepository = mockk<UserRepository>()
    every { userRepository.findById(1L) } returns Optional.of(User(1L, "홍길동", "hong@test.com"))
    ```

- **행위 검증**
    ```
    verify { userRepository.save(any()) }
    ```

- **Spring 환경에서 @MockkBean으로 주입**
    ```
    @MockkBean
    private lateinit var userService: UserService
    ```

- **예시: Service 단위 테스트**
    ```
    @Test
    fun `유저 생성`() {
        val req = CreateUserRequest("홍길동", "hong@test.com")
        val user = User(id = 1L, name = req.name, email = req.email)
        every { userRepository.save(any()) } returns user

        val result = userService.createUser(req)
        assertThat(result.id).isEqualTo(1L)
        verify { userRepository.save(any()) }
    }
    ```

---

## ✅ 결론

- **실제 코드와 동일한 패키지 구조로 단위/통합 테스트를 분리**하는 것은  
  유지보수성과 가독성, 협업 효율을 극대화하는 실무 베스트 프랙티스입니다.
- **테스트 유형에 따라 적절한 애노테이션과 MockK 활용법을 적용**하면  
  빠르고 신뢰성 높은 테스트 자동화가 가능합니다.
- 이 프로젝트의 예제 코드를 참고해,  
  여러분의 실무 프로젝트에도 테스트 자동화의 표준을 적용해보세요!

---

> **출처:**
> - [Baeldung: Spring Boot Testing with Kotlin](https://www.baeldung.com/kotlin/spring-boot-testing)[1]
> - [걷고 또 걷기: Kotlin과 Spring Boot에서의 테스팅 전략](https://walking-and-walking.com/entry/Kotlin%EA%B3%BC-Spring-Boot%EC%97%90%EC%84%9C%EC%9D%98-%ED%85%8C%EC%8A%A4%ED%8C%85-%EC%A0%84%EB%9E%B5)[3]
> - [Philipp Hauer: Best Practices for Unit Testing in Kotlin](https://phauer.com/2018/best-practices-unit-testing-kotlin/)[5]