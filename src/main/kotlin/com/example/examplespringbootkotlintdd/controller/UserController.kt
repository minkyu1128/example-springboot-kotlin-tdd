package com.example.examplespringbootkotlintdd.controller

import com.example.examplespringbootkotlintdd.controller.dto.CreateUserRequest
import com.example.examplespringbootkotlintdd.controller.dto.UpdateUserRequest
import com.example.examplespringbootkotlintdd.repository.User
import com.example.examplespringbootkotlintdd.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @GetMapping
    fun getUsers(): List<User> = userService.getAllUsers()

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> =
        userService.getUserById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun createUser(@RequestBody req: CreateUserRequest): User =
        userService.createUser(req)

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<User> =
        userService.updateUser(id, request)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> =
        if (userService.deleteUser(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()

}