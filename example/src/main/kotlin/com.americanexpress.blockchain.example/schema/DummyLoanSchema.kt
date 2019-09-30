package com.americanexpress.blockchain.example.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object LoanSchema

/**
 * Persistence for the loan request state object
 */
object LoanSchemaV1 : MappedSchema(
        schemaFamily = LoanSchema.javaClass,
        version = 1,
        mappedTypes = listOf(LoanSchemaV1.PersistentLoan::class.java)) {
    @Entity
    @Table(name = "loan_states")
    class PersistentLoan(
            @Column(name = "value")
            var value: Int,

            @Column(name = "borrower")
            var borrower: String,

            @Column(name = "lender")
            var lender: String,

            @Column(name = "interestRate")
            var interestRate: Float,

            @Column(name = "linear_id")
            var linearId: UUID
    ) : PersistentState() {
        // Default constructor required by hibernate.
        constructor(): this(0, "", "", 0F, UUID.randomUUID())
    }
}
