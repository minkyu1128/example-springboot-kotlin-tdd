package com.example.examplespringbootkotlintdd.unit.repository

import com.example.examplespringbootkotlintdd.repository.User
import com.example.examplespringbootkotlintdd.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.Test

@DataJpaTest
class UserRepositoryTest @Autowired constructor(
    val userRepository: UserRepository
) {
    @Test
    fun `이메일로 사용자 찾기`() {
        val user = User(name = "홍길동", email = "hong@test.com")
        userRepository.save(user)

        val found = userRepository.findByEmail("hong@test.com")
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("홍길동")
    }
}