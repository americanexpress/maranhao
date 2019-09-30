package com.americanexpress.blockchain.example.flow

import co.paralleluniverse.fibers.Suspendable
import com.americanexpress.blockchain.example.contract.DummyLoanContract
import com.americanexpress.blockchain.example.state.DummyLoanState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.FlowSession
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction

/**
 * Example implementation for the framework-provided simple flow
 */
object DummyLoanFlow {
    data class Loan(
            val id: UniqueIdentifier,
            val amount: Int,
            val interest: Float,
            val borrower: Party,
            val lender: Party
    )

    @InitiatingFlow
    @StartableByRPC
    class Initiator(input: Loan) : com.americanexpress.blockchain.maranhao.workflow.simpleFlow
        .SimpleMultiStepFlowInitiator<Loan>(input) {

        @Suspendable override fun getListOfSigners(): List<Party> = listOf(input.lender, input.borrower)
        @Suspendable override fun getCommandData(): CommandData = DummyLoanContract.Commands.Request()
        @Suspendable override fun getState(): ContractState =
                DummyLoanState(value = input.amount, lender = input.lender,
                        borrower = input.borrower, interestRate = input.interest, linearId = input.id)
        @Suspendable override fun getStateId(): String =
                "com.americanexpress.blockchain.example.contract.DummyLoanContract"

        override var initialProcessingStep = object:
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep {
            override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow
                .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>) {

                val state = ctx.sharedData!!.outputState as DummyLoanState
                ctx.sharedData!!.outputState = state.copy(interestRate = 0.12F)
            }
        }
    }

    /**
     * Nothing special about this class. Enhancements to framework will include helper functionality for the
     * acceptor as well.
     *
     * @property otherPartySession FlowSession
     * @constructor
     */
    @InitiatedBy(DummyLoanFlow.Initiator::class)
    class Acceptor(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    val output = stx.tx.outputs.single().data
                    "This must be a loan request transaction." using (output is DummyLoanState)
                }
            }
            val txId = subFlow(signTransactionFlow).id

            return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
        }
    }
}


