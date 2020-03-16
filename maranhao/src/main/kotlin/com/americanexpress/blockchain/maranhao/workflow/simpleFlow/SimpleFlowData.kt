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

package com.americanexpress.blockchain.maranhao.workflow.simpleFlow

import com.google.gson.annotations.Expose
import net.corda.core.contracts.*
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowSession
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

/**
 * Simple flow context object. Used to pass down objects that are needed by subsequent steps.
 * Simple flow implemented steps depend on this structure.
 *
 * @property transactionBuilder TransactionBuilder?
 * @property signedTransaction SignedTransaction?
 * @property fullySignedTransaction SignedTransaction?
 * @property flowSessions List<FlowSession>?
 * @constructor
 */
data class SimpleFlowData(
        @Expose
        var stateId : String,
        @Expose
        var inputState: ContractState? = null,
        var stateAndRef: StateAndRef<ContractState>? = null,
        var referenceState: ReferencedStateAndRef<ContractState>? = null,
        var attachment: SecureHash? = null,
        @Expose
        var outputState: ContractState? = null,
        var commandData: CommandData,
        @Expose
        var signatories: List<Party>,
        var transactionBuilder: TransactionBuilder? = null,
        var signedTransaction: SignedTransaction? = null,
        var fullySignedTransaction: SignedTransaction? = null,
        var flowSessions: List<FlowSession>? = null,
        var timeWindow: TimeWindow? = null
)
