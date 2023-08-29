package com.example.contracts

import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import kotlin.reflect.KClass

interface DataContractMessageAware {
    fun getMessagePayload(): Message
    fun getMessagePayloadType(): KClass<out Any>
    fun getMessageKey(): String

    fun getMessagePayloadAsJSON(): String {
        val msgBuilder = this.getMessagePayload()
        val printer = JsonFormat.printer()
        return printer.print(msgBuilder)
    }
}
