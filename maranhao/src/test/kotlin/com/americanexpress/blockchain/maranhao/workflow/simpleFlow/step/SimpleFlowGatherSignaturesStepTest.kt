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


package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step

import com.americanexpress.blockchain.maranhao.workflow.simpleFlowContext
import net.corda.core.transactions.SignedTransaction
import org.junit.Test

import org.junit.Assert.*
import kotlin.test.assertFailsWith

class SimpleFlowGatherSignaturesStepTest {

    @Test
    fun execute() {
        val exception = assertFailsWith<IllegalStateException> {
            com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step
                    .SimpleFlowGatherSignaturesStep<SignedTransaction>().execute(simpleFlowContext)
        }
        assertEquals("You cannot access the flow's state machine until the flow has been started.", exception.message)
    }
}