package com.example.contracts

import com.google.protobuf.Message
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import kotlin.reflect.KClass

@Entity
data class Foo(var name: String,
               var active: Boolean = true) : DataContractMessageAware {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "foo")
    var barSet: MutableSet<Bar> = mutableSetOf()

    override fun getMessagePayload(): Message {
        return EntityChangeEventPayloadForFoo
            .newBuilder().setId(id!!).setName(name).setActive(active).build()
    }

    override fun getMessagePayloadType(): KClass<out Any> {
        return EntityChangeEventPayloadForFoo::class
    }

    override fun getMessageKey(): String {
        return id!!.toString()
    }
}