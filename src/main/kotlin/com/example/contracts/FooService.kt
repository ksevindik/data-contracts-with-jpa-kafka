package com.example.contracts

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FooService(private val fooRepository: FooRepository) {
    @BusinessOperationSource("submit")
    fun submit(foo: Foo): Foo = fooRepository.save(foo)
    @BusinessOperationSource("activate")
    fun activate(id:Long) {
        fooRepository.findById(id).get().active = true
    }
    @BusinessOperationSource("deactivate")
    fun deactivate(id:Long) {
        fooRepository.findById(id).get().active = false
    }
}