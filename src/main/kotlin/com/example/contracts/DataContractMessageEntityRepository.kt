package com.example.contracts

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DataContractMessageEntityRepository : JpaRepository<DataContractMessageEntity, Long> {
    fun findByPublishedOrderByCreatedAtAsc(published: Boolean): List<DataContractMessageEntity>
}
