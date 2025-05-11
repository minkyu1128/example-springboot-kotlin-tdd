package com.example.examplespringbootkotlintdd.repository

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String?,
    @Column(unique = true)
    var email: String?
){
    fun updateUserInfo(name: String?, email: String?){
        this.name = name
        email.let { this.email = it }
    }
}