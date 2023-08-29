package com.example.contracts

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Service
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
class FooServiceDataContractMessageAspect(
    private val dataContractMessageEntityRepository: DataContractMessageEntityRepository,
    transactionManager: PlatformTransactionManager
) {

    private var transactionTemplate: TransactionTemplate = TransactionTemplate(transactionManager,DefaultTransactionDefinition(
        TransactionDefinition.PROPAGATION_MANDATORY))

    @Around("@annotation(com.example.contracts.BusinessOperationSource)")
    fun intercept(pjp: ProceedingJoinPoint): Any? {
        try {
            return transactionTemplate.execute {
                val result = pjp.proceed()
                var fooId: Long? = null
                val method = (pjp.signature as MethodSignature).method
                val operationName = method.getAnnotation(BusinessOperationSource::class.java).operation
                when(operationName) {
                    "submit" -> {
                        val foo = pjp.args.get(0) as Foo
                        fooId = foo.id
                    }
                    "activate" -> {
                        fooId = pjp.args.get(0) as Long
                    }
                    "deactivate" -> {
                        fooId = pjp.args.get(0) as Long
                    }
                }

                val fooOperation = FooOperation(fooId!!,operationName)
                val messageEntity = DataContractMessageEntity(
                    DataContractMessageType.BUSINESS_OPERATION.name,
                    operationName,
                    fooOperation.getMessagePayloadType().qualifiedName!!,
                    fooOperation.getMessagePayloadAsJSON(),
                    DataContractMessageTopicResolver().resolve(fooOperation::class),
                    fooOperation.getMessageKey(),
                    false)
                dataContractMessageEntityRepository.save(messageEntity)
                result
            }
        } catch (t: Throwable) {
            throw t
        }
    }
}
