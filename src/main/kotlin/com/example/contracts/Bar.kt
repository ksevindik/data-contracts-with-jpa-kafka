package com.example.contracts

import com.google.protobuf.Message
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import kotlin.reflect.KClass

@Entity
data class Bar(var name: String, @ManyToOne var foo:Foo) : DataContractMessageAware {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null



    override fun getMessagePayload(): Message {
        return EntityChangeEventPayloadForBar
            .newBuilder().setId(id!!).setFooId(foo.id!!).setName(name).build()
    }

    override fun getMessagePayloadType(): KClass<out Any> {
        return EntityChangeEventPayloadForBar::class
    }

    override fun getMessageKey(): String {
        return id!!.toString()
    }
}