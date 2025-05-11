package com.example.examplespringbootkotlintdd.unit.service

import com.example.examplespringbootkotlintdd.controller.dto.CreateUserRequest
import com.example.examplespringbootkotlintdd.controller.dto.UpdateUserRequest
import com.example.examplespringbootkotlintdd.repository.User
import com.example.examplespringbootkotlintdd.repository.UserRepository
import com.example.examplespringbootkotlintdd.service.UserService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import kotlin.test.Test

class UserServiceTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val userService = UserService(userRepository)

    @Test
    fun `유저 전체 목록 조회`() {
        val users = listOf(
            User(id = 1L, name = "홍길동", email = "hong@test.com"),
            User(id = 2L, name = "김철수", email = "kim@test.com")
        )
        every { userRepository.findAll() } returns users

        val result = userService.getAllUsers()
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("홍길동")
        assertThat(result[1].email).isEqualTo("kim@test.com")
    }

    @Test
    fun `PK로 유저정보 조회 - 성공`() {
        val user = User(id = 1L, name = "홍길동", email = "hong@test.com")
        every { userRepository.findById(1L) } returns Optional.of(user)

        val result = userService.getUserById(1L)
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("홍길동")
    }

    @Test
    fun `PK로 유저정보 조회 - 실패`() {
        every { userRepository.findById(2L) } returns Optional.empty()

        val result = userService.getUserById(2L)
        assertThat(result).isNull()
    }

    @Test
    fun `이메일로 유저정보 조회 - 성공`() {
        val user = User(id = 1L, name = "홍길동", email = "hong@test.com")
        every { userRepository.findByEmail("hong@test.com") } returns user

        val result = userService.getUserByEmail("hong@test.com")
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(1L)
    }

    @Test
    fun `이메일로 유저정보 조회 - 실패`() {
        every { userRepository.findByEmail("notfound@test.com") } returns null

        val result = userService.getUserByEmail("notfound@test.com")
        assertThat(result).isNull()
    }

    @Test
    fun `유저 생성`() {
        val req = CreateUserRequest(name = "홍길동", email = "hong@test.com")
        val user = User(id = 1L, name = req.name, email = req.email)
        every { userRepository.save(any()) } returns user

        val result = userService.createUser(req)
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("홍길동")
        verify { userRepository.save(any()) }
    }

    @Test
    fun `유저정보 수정 - 성공`() {
        val user = spyk(User(id = 1L, name = "홍길동", email = "hong@test.com"))
        every { userRepository.findById(1L) } returns Optional.of(user)

        val updateReq = UpdateUserRequest(name = "김철수", email = "kim@test.com")
        val result = userService.updateUser(1L, updateReq)

        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("김철수")
        assertThat(result?.email).isEqualTo("kim@test.com")
        verify { user.updateUserInfo(name = "김철수", email = "kim@test.com") }
    }

    @Test
    fun `유저정보 수정 - 실패`() {
        every { userRepository.findById(2L) } returns Optional.empty()

        val updateReq = UpdateUserRequest(name = "김철수", email = "kim@test.com")
        val result = userService.updateUser(2L, updateReq)

        assertThat(result).isNull()
    }

    @Test
    fun `유저 삭제 - 성공`() {
        every { userRepository.existsById(1L) } returns true
        every { userRepository.deleteById(1L) } just Runs

        val result = userService.deleteUser(1L)
        assertThat(result).isTrue()
        verify { userRepository.deleteById(1L) }
    }

    @Test
    fun `유저 삭제 - 실패`() {
        every { userRepository.existsById(2L) } returns false

        val result = userService.deleteUser(2L)
        assertThat(result).isFalse()
        verify(exactly = 0) { userRepository.deleteById(any()) }
    }
}