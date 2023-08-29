package com.example.contracts

import org.apache.kafka.common.serialization.Deserializer

class DataContractMessageDeserializer : Deserializer<DataContractMessage> {
    override fun deserialize(topic: String?, data: ByteArray?): DataContractMessage {
        when (data) {
            null -> throw NullPointerException(
                "Byte array was null for ${DataContractMessageEntity::class.java.simpleName}"
            )

            else -> return DataContractMessage.parseFrom(data)
        }
    }
}
