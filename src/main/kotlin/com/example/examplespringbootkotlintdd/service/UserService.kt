package com.example.examplespringbootkotlintdd.service

import com.example.examplespringbootkotlintdd.controller.dto.CreateUserRequest
import com.example.examplespringbootkotlintdd.controller.dto.UpdateUserRequest
import com.example.examplespringbootkotlintdd.repository.User
import com.example.examplespringbootkotlintdd.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    /**
     * 유저 전체 목록 조회
     */
    fun getAllUsers(): List<User> = userRepository.findAll()

    /**
     * pk 로 유저정보 조회
     */
    fun getUserById(id: Long): User? =
        userRepository.findById(id).orElse(null)
    /**
     * 이메일주소로 유저정보 조회
     */
    fun getUserByEmail(email: String): User? = userRepository.findByEmail(email)

    /**
     * 유저 생성
     */
    fun createUser(request: CreateUserRequest): User =
        userRepository.save(User(name = request.name, email = request.email))

    /**
     * 유저정보 수정
     */
    @Transactional
    fun updateUser(id: Long, request: UpdateUserRequest): User? {
        val user = userRepository.findById(id).orElse(null) ?: return null
        user.updateUserInfo(name = request.name, email = request.email)
        return user
    }

    /**
     * 유저 삭제
     */
    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}