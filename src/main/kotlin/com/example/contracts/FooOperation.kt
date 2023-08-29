package com.example.contracts

import com.google.protobuf.Message
import kotlin.reflect.KClass

data class FooOperation(val id: Long, val operationName:String) : DataContractMessageAware {
    override fun getMessagePayload(): Message {
        return BusinessEventPayloadForFooOperations.newBuilder()
            .setFooId(id)
            .setOperation(operationName)
                .build()
    }

    override fun getMessagePayloadType(): KClass<out Any> {
       return BusinessEventPayloadForFooOperations::class
    }

    override fun getMessageKey(): String {
        return id.toString()
    }

}