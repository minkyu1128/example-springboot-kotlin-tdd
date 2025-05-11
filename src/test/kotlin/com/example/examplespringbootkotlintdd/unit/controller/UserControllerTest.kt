package com.example.examplespringbootkotlintdd.unit.controller

import com.example.examplespringbootkotlintdd.controller.UserController
import com.example.examplespringbootkotlintdd.controller.dto.CreateUserRequest
import com.example.examplespringbootkotlintdd.controller.dto.UpdateUserRequest
import com.example.examplespringbootkotlintdd.repository.User
import com.example.examplespringbootkotlintdd.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(UserController::class)
class UserControllerTest(
    @Autowired val mockMvc: MockMvc
) {
    @MockkBean
    private lateinit var userService: UserService

    private val objectMapper = ObjectMapper()

    @Test
    fun `전체 사용자 목록 조회`() {
        every { userService.getAllUsers() } returns listOf(
            User(1L, "홍길동", "hong@test.com"),
            User(2L, "김철수", "kim@test.com")
        )

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("홍길동"))
            .andExpect(jsonPath("$[1].name").value("김철수"))
    }

    @Test
    fun `ID로 사용자 조회 - 성공`() {
        every { userService.getUserById(1L) } returns User(1L, "홍길동", "hong@test.com")

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("홍길동"))
    }

    @Test
    fun `ID로 사용자 조회 - 실패`() {
        every { userService.getUserById(999L) } returns null

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `사용자 생성`() {
        val req = CreateUserRequest("홍길동", "hong@test.com")
        val user = User(1L, req.name, req.email)
        every { userService.createUser(req) } returns user

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("홍길동"))
            .andExpect(jsonPath("$.email").value("hong@test.com"))
    }

    @Test
    fun `사용자 수정 - 성공`() {
        val updateReq = UpdateUserRequest("김철수", null)
        val updatedUser = User(1L, "김철수", "hong@test.com")
        every { userService.updateUser(1L, updateReq) } returns updatedUser

        mockMvc.perform(
            put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("김철수"))
    }

    @Test
    fun `사용자 수정 - 실패`() {
        val updateReq = UpdateUserRequest("없는사람", null)
        every { userService.updateUser(999L, updateReq) } returns null

        mockMvc.perform(
            put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `사용자 삭제 - 성공`() {
        every { userService.deleteUser(1L) } returns true

        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `사용자 삭제 - 실패`() {
        every { userService.deleteUser(999L) } returns false

        mockMvc.perform(delete("/api/users/999"))
            .andExpect(status().isNotFound)
    }
}
