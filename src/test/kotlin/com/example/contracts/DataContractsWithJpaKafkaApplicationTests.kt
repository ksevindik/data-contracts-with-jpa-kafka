package com.example.contracts

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.h2.tools.Server
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@SpringBootTest
@EmbeddedKafka(count=1, partitions = 1)
@ActiveProfiles("test")
@Transactional
class DataContractsWithJpaKafkaApplicationTests {

	@Autowired
	private lateinit var fooService: FooService

	@Autowired
	private lateinit var dataSource: DataSource

	@Autowired
	private lateinit var fooRepository: FooRepository

	@Autowired
	private lateinit var dataContractMessageEntityRepository: DataContractMessageEntityRepository

	@Autowired
	private lateinit var entityManager: EntityManager

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	fun openH2Console() {
		Server.startWebServer(DataSourceUtils.getConnection(dataSource))
	}

	@Test
	fun `data contract messages of entity change events for insert should have been created`() {
		val foo1 = Foo("foo1",false)

		fooRepository.save(foo1)

		flushAndClear()

		verifyContractMessages("ENTITY_CHANGE", DataContractMessageExtract(
			"ENTITY_CHANGE", "INSERT",
			"com.example.contracts.EntityChangeEventPayloadForFoo",
			objectMapper.readValue(
				"""
					{
						"id": "${foo1.id}",
						"name": "foo1"
					}
					""".trimIndent(), Map::class.java
			), "data.contracts.foo", "${foo1.id}", false
		))
	}

	@Test
	fun `data contract messages of entity change events for insert with child entities should have been created`() {
		val foo1 = Foo("foo1",false)
		val bar1 = Bar("bar1",foo1)
		val bar2 = Bar("bar2",foo1)
		foo1.barSet.addAll(listOf(bar1,bar2))

		fooRepository.save(foo1)

		flushAndClear()

		verifyContractMessages("ENTITY_CHANGE", DataContractMessageExtract("ENTITY_CHANGE","INSERT",
			"com.example.contracts.EntityChangeEventPayloadForFoo",
			objectMapper.readValue("""
				{
					"id": "${foo1.id}",
  					"name": "foo1"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foo","${foo1.id}",false),
			DataContractMessageExtract("ENTITY_CHANGE","INSERT",
				"com.example.contracts.EntityChangeEventPayloadForBar",
				objectMapper.readValue("""
				{
					"id": "${bar1.id}",
					"fooId": "${foo1.id}",
  					"name": "bar1"
				}
				""".trimIndent(),Map::class.java),"data.contracts.bar","${bar1.id}",false),
			DataContractMessageExtract("ENTITY_CHANGE","INSERT",
				"com.example.contracts.EntityChangeEventPayloadForBar",
				objectMapper.readValue("""
				{
					"id": "${bar2.id}",
					"fooId": "${foo1.id}",
  					"name": "bar2"
				}
				""".trimIndent(),Map::class.java),"data.contracts.bar","${bar2.id}",false))

	}

	@Test
	fun `data contract messages of entity change events for update should have been created`() {
		val foo1 = Foo("foo1",false)
		fooRepository.save(foo1)

		flushAndClear()

		foo1.active= true

		fooRepository.save(foo1)

		flushAndClear()

		verifyContractMessages("ENTITY_CHANGE", DataContractMessageExtract("ENTITY_CHANGE","INSERT",
			"com.example.contracts.EntityChangeEventPayloadForFoo",
			objectMapper.readValue("""
				{
					"id": "${foo1.id}",
  					"name": "foo1"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foo","${foo1.id}",false),
			DataContractMessageExtract("ENTITY_CHANGE","UPDATE",
				"com.example.contracts.EntityChangeEventPayloadForFoo",
				objectMapper.readValue("""
				{
					"id": "${foo1.id}",
  					"name": "foo1",
  					"active": true
				}
				""".trimIndent(),Map::class.java),"data.contracts.foo","${foo1.id}",false))

	}

	@Test
	fun `data contract messages of entity change events for delete should have been created`() {
		val foo1 = Foo("foo1",false)
		fooRepository.save(foo1)

		flushAndClear()

		fooRepository.delete(foo1)

		flushAndClear()

		verifyContractMessages("ENTITY_CHANGE", DataContractMessageExtract("ENTITY_CHANGE","INSERT",
			"com.example.contracts.EntityChangeEventPayloadForFoo",
			objectMapper.readValue("""
				{
					"id": "${foo1.id}",
  					"name": "foo1"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foo","${foo1.id}",false),
			DataContractMessageExtract("ENTITY_CHANGE","DELETE",
				"com.example.contracts.EntityChangeEventPayloadForFoo",
				objectMapper.readValue("""
				{
					"id": "${foo1.id}",
  					"name": "foo1"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foo","${foo1.id}",false))

	}

	@Test
	fun `data contract messages of business operation events for submit, activate and deactivate should have been created`() {
		val foo1 = Foo("foo1",false)

		fooService.submit(foo1)

		flushAndClear()

		fooService.activate(1)

		flushAndClear()

		fooService.deactivate(1)

		flushAndClear()

		verifyContractMessages("BUSINESS_OPERATION", DataContractMessageExtract("BUSINESS_OPERATION","submit",
				"com.example.contracts.BusinessEventPayloadForFooOperations",
				objectMapper.readValue("""
				{
					"fooId": "${foo1.id}",
  					"operation": "submit"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foooperation","${foo1.id}",false),
			DataContractMessageExtract("BUSINESS_OPERATION","activate",
				"com.example.contracts.BusinessEventPayloadForFooOperations",
				objectMapper.readValue("""
				{
					"fooId": "${foo1.id}",
  					"operation": "activate"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foooperation","${foo1.id}",false),
			DataContractMessageExtract("BUSINESS_OPERATION","deactivate",
				"com.example.contracts.BusinessEventPayloadForFooOperations",
				objectMapper.readValue("""
				{
					"fooId": "${foo1.id}",
  					"operation": "deactivate"
				}
				""".trimIndent(),Map::class.java),"data.contracts.foooperation","${foo1.id}",false))
	}

	private fun verifyContractMessages(messageType:String, vararg expectedContractMessages: DataContractMessageExtract) {
		//openH2Console()
		val actualContractMessages = dataContractMessageEntityRepository.findAll().filter { it.messageType == messageType }.map {
			DataContractMessageExtract(
				it.messageType,
				it.eventType,
				it.payloadType,
				objectMapper.readValue(it.payload, Map::class.java),
				it.messageTopic,
				it.messageKey,
				it.published
			)
		}

		MatcherAssert.assertThat(actualContractMessages.size,Matchers.equalTo(expectedContractMessages.size))
		MatcherAssert.assertThat(actualContractMessages, Matchers.containsInAnyOrder(*expectedContractMessages))
	}

	private fun flushAndClear() {
		entityManager.flush()
		entityManager.clear()
	}

}

data class DataContractMessageExtract(val messageType: String,
							   val eventType: String,
							   val payloadType: String,
							   val payload: Map<*,*>,
							   val messageTopic: String,
							   val messageKey: String,
							   val published: Boolean)
