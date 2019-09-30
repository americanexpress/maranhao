package com.americanexpress.blockchain.example.flow

import co.paralleluniverse.fibers.Suspendable
import com.americanexpress.blockchain.maranhao.workflow.FlowContext
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.ReportToWitnessFlow
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleAcceptorWithWitnessNotifier
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleMultiStepFlowInitiator
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep
import com.americanexpress.blockchain.example.contract.DummyLoanContract
import com.americanexpress.blockchain.example.state.DummyLoanState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.ReceiveTransactionFlow
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction

/**
 * Example implementation for the framework-provided simple flow
 */
object DummyPayoffLoan {
    @InitiatingFlow
    @StartableByRPC
     class Initiator(private val id: UniqueIdentifier,
                          val borrower: Party,
                          val lender: Party) : com.americanexpress.blockchain.maranhao.workflow.simpleFlow
            .SimpleMultiStepFlowInitiator<UniqueIdentifier>(id) {

        @Suspendable
        override fun getListOfSigners(): List<Party> = listOf(lender)
        @Suspendable
        override fun getCommandData(): CommandData = DummyLoanContract.Commands.Payoff()
        @Suspendable
        override fun getStateId(): String = "com.americanexpress.blockchain.example.contract.DummyLoanContract"

        override var initialProcessingStep = object:
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep {

            @Suspendable
            override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow
                    .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>) {

                val inputCriteria = QueryCriteria.LinearStateQueryCriteria(
                        linearId = listOf(id), status = Vault.StateStatus.ALL)
                val queryResult = serviceHub.vaultService.queryBy<DummyLoanState>(inputCriteria)
                val inputStateAndRef = queryResult.states.single()
                val state = inputStateAndRef.state.data
                ctx.sharedData!!.outputState = state.copy(value = 0)
            }
        }
    }


    /**
     * Nothing special about this class. Enhancements to framework will include helper functionality for the
     * acceptor as well. It will send notification to Regulator
     *
     * @property otherPartySession FlowSession
     * @constructor
     */

    @InitiatedBy(DummyPayoffLoan.Initiator::class)
    class AcceptorWithNotifier(val otherPartySession: FlowSession) :
            com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleAcceptorWithWitnessNotifier() {

        companion object {
            const val WITNESS_NAME = "Regulator"
        }

        @Suspendable
        override fun getWitnesses() =
            listOf(serviceHub.identityService.partiesFromName(WITNESS_NAME, true).single())

        @Suspendable
        override fun preCall(): SignedTransaction {
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

    @InitiatedBy(com.americanexpress.blockchain.maranhao.workflow.simpleFlow.ReportToWitnessFlow::class)
    class ReceiveRegulatoryReportFlow(private val otherSideSession: FlowSession) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            subFlow(ReceiveTransactionFlow(otherSideSession, true, StatesToRecord.ALL_VISIBLE))
        }
    }
}
