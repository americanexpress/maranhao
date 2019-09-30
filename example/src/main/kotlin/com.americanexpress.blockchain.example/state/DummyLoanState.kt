package com.americanexpress.blockchain.example.state

import com.americanexpress.blockchain.example.contract.DummyLoanContract
import com.americanexpress.blockchain.example.schema.LoanSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

/**
 * State object for loan request
 *
 * @property value Int - the amount of the loan
 * @property borrower Party - the borrower; this party initiates the loan request
 * @property lender Party - the lender
 * @property approver Party - third party that endorses the transaction; part of the initial flow
 * @property participants List<AbstractParty> mandatory field inherited from ContractState
 * @constructor
 */

@BelongsToContract(DummyLoanContract::class)
data class DummyLoanState(
        val value: Int,
        val borrower: Party,
        val lender: Party,
        val interestRate: Float = 0F,
        override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState, QueryableState {
    override val participants: List<AbstractParty> = listOf(borrower, lender)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is LoanSchemaV1 -> LoanSchemaV1.PersistentLoan(
                    this.value,
                    this.borrower.name.toString(),
                    this.lender.name.toString(),
                    this.interestRate,
                    this.linearId.id
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(LoanSchemaV1)

}
