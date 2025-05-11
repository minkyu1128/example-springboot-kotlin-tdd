package com.example.examplespringbootkotlintdd.integration.api

import com.example.examplespringbootkotlintdd.controller.dto.CreateUserRequest
import com.example.examplespringbootkotlintdd.controller.dto.UpdateUserRequest
import com.example.examplespringbootkotlintdd.repository.User
import com.example.examplespringbootkotlintdd.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import kotlin.test.Test


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest @Autowired constructor(
    val restTemplate: TestRestTemplate,
    val userRepository: UserRepository
) {

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun `전체 사용자 목록 조회`() {
        // 사전 데이터 생성
        val createReq1 = CreateUserRequest("홍길동", "hong@test.com")
        val createReq2 = CreateUserRequest("김철수", "kim@test.com")
        restTemplate.postForEntity("/api/users", createReq1, User::class.java)
        restTemplate.postForEntity("/api/users", createReq2, User::class.java)

        val response = restTemplate.getForEntity("/api/users", Array<User>::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.size).isGreaterThanOrEqualTo(2)
    }

    @Test
    fun `ID로 사용자 조회 - 성공`() {
        // 사용자 생성
        val createReq = CreateUserRequest("홍길동", "hong@test.com")
        val createResponse = restTemplate.postForEntity("/api/users", createReq, User::class.java)
        val userId = createResponse.body?.id

        val response = restTemplate.getForEntity("/api/users/$userId", User::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.name).isEqualTo("홍길동")
    }

    @Test
    fun `ID로 사용자 조회 - 실패`() {
        val response = restTemplate.getForEntity("/api/users/999999", User::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `사용자 생성`() {
        val createReq = CreateUserRequest("홍길동", "hong@test.com")
        val response = restTemplate.postForEntity("/api/users", createReq, User::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.name).isEqualTo("홍길동")
    }

    @Test
    fun `사용자 수정 - 성공`() {
        val createReq = CreateUserRequest("홍길동", "hong@test.com")
        val createResponse = restTemplate.postForEntity("/api/users", createReq, User::class.java)
        val userId = createResponse.body?.id

        val updateReq = UpdateUserRequest(name = "김철수", email = null)
        val updateEntity = HttpEntity(updateReq)
        val updateResponse = restTemplate.exchange(
            "/api/users/$userId", HttpMethod.PUT, updateEntity, User::class.java
        )
        assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(updateResponse.body?.name).isEqualTo("김철수")
    }

    @Test
    fun `사용자 수정 - 실패`() {
        val updateReq = UpdateUserRequest(name = "없는사람", email = null)
        val updateEntity = HttpEntity(updateReq)
        val updateResponse = restTemplate.exchange(
            "/api/users/999999", HttpMethod.PUT, updateEntity, User::class.java
        )
        assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `사용자 삭제 - 성공`() {
        val createReq = CreateUserRequest("홍길동", "hong@test.com")
        val createResponse = restTemplate.postForEntity("/api/users", createReq, User::class.java)
        val userId = createResponse.body?.id

        val deleteResponse = restTemplate.exchange(
            "/api/users/$userId", HttpMethod.DELETE, null, Void::class.java
        )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `사용자 삭제 - 실패`() {
        val deleteResponse = restTemplate.exchange(
            "/api/users/999999", HttpMethod.DELETE, null, Void::class.java
        )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}