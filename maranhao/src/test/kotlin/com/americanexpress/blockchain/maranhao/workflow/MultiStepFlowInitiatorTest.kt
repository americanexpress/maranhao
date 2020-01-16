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

package com.americanexpress.blockchain.maranhao.workflow

import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.integration.SomeState
import net.corda.core.contracts.UniqueIdentifier
import org.junit.Test
import org.junit.Assert.*
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class MultiStepFlowInitiatorTest {

    private val flow = TestMultiStepFlowInitiator(DummyData())

    @Test
    fun getNotaryStrategy() {
        assertNotNull(flow.getNotaryStrategy())
    }

    @Test
    fun getProgressTracker() {
        assertNotNull(flow.progressTracker)
    }

    @Test
    fun call() {
        val exception = assertFailsWith<IllegalStateException> { flow.call() }
        assertEquals("You cannot access the flow's state machine until the flow has been started.", exception.message)
    }

    @Test
    fun getInput() {
        assertNotNull(flow.input)
    }

    @Test
    fun getUnconsumedLinearStateByLinearId() {
        val exception = assertFailsWith<IllegalStateException> { flow.getUnconsumedLinearStateByLinearId<SomeState>(UniqueIdentifier()) }
        assertEquals("You cannot access the flow's state machine until the flow has been started.", exception.message)
    }
}