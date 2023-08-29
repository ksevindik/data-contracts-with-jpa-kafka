package com.example.contracts

import org.springframework.data.jpa.repository.JpaRepository

interface FooRepository : JpaRepository<Foo,Long> {
}