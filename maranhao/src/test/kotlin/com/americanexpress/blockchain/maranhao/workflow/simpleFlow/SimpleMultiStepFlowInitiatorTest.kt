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

import com.americanexpress.blockchain.maranhao.workflow.*
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

data class ExampleState(
        val value: Int,
        override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState {
    override val participants: List<AbstractParty> = listOf()
}


class SimpleMultiStepFlowInitiatorTest {

    @Before
    fun init() {
        simpleFlow.initialProcessingStep = object :
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep {

            override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow
                .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>) {
                ctx.sharedData?.outputState = ExampleState(1000)
            }
        }
    }

    @Test
    fun getFlowTracker() {
        assertNotNull(simpleFlow.getFlowTracker())
    }

    @Test
    fun getFlowSteps() {
        assertNotNull(simpleFlow.getFlowSteps(DummyData()))
    }

    @Test
    fun initialProcessingStepAddedCorrectly() {
        simpleFlow.initialProcessingStep.execute(simpleFlowContext)
        assertNotNull(simpleFlowContext.sharedData!!.outputState)
    }

}