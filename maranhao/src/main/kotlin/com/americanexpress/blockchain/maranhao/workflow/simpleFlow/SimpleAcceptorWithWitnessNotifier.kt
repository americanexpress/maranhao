package com.americanexpress.blockchain.maranhao.workflow.simpleFlow

/**
 * Copyright 2019 American Express Travel Related Services Company, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.SendTransactionFlow
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow

abstract class SimpleAcceptorWithWitnessNotifier : FlowLogic<SignedTransaction>() {

    /**
     * No need to override this function, as it will initiate the witness
     * (observer) notification
     *
     * @return SignedTransaction
     */

    @Suspendable
    override fun call(): SignedTransaction {
        val signedTx = preCall()
        subFlow(ReportToWitnessFlow(getWitnesses(), signedTx))
        return signedTx
    }

    /**
     * Function needs to be overridden to return the witness party
     *
     * @return Party
     */
    @Suspendable
    abstract fun getWitnesses() : List<Party>

    /**
     *
     * @return SignedTransaction
     */
    @Suspendable
    abstract fun preCall() : SignedTransaction
}

/**
 * Acceptor flow needs to refer to this class by mean of @InitiatedBy
 *
 * @property witness Party - the party receiving copy of committed transactions
 * @property finalTx SignedTransaction
 * @constructor
 */
@InitiatingFlow
class ReportToWitnessFlow(
        private val witnesses: List<Party>,
        private val finalTx: SignedTransaction) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val sessions = witnesses.map{initiateFlow(it)}
        sessions.map{subFlow(SendTransactionFlow(it, finalTx))}
    }
}
