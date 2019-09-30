package com.americanexpress.blockchain.example.contract

import com.americanexpress.blockchain.example.state.DummyLoanState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

/**
 * Sample contract to be used as reference for the Treasury use case
 */
class DummyLoanContract : Contract {
     /**
      * A transaction is valid if the verify() function of the contract of all the transaction's
      * sharedData and output states does not throw an exception.
      *
      * @param tx LedgerTransaction
      * */
    override fun verify(tx: LedgerTransaction) {
        val out = tx.outputsOfType<DummyLoanState>().single()

        requireThat {
            "No inputs should be consumed when requesting a loan." using (tx.inputs.isEmpty())
            "Only one output state should be created." using (tx.outputs.size == 1)
            "The value of the requested loan must be non-negative." using (out.value >= 0)
        }
    }

    /**
     * Used to indicate the transaction's intent.
     */
    interface Commands : CommandData {
        class Request : Commands
        class Payoff : Commands
    }
}
