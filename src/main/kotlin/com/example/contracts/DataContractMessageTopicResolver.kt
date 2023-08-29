package com.example.contracts

import kotlin.reflect.KClass

class DataContractMessageTopicResolver {
    fun resolve(entityType: KClass<out Any>): String {
        return "data.contracts.${entityType.simpleName!!.lowercase()}"
    }
}
