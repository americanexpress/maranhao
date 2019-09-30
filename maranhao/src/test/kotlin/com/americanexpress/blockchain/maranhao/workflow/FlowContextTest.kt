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

import org.junit.Assert.*
import org.junit.Test

class FlowContextTest {
    @Test
    fun `should create FlowContext and allow for tracking`() {
        try {
            com.americanexpress.blockchain.maranhao.workflow.flowContext
                    .track(
                            com.americanexpress.blockchain.maranhao.workflow.simpleFlow
                                    .SimpleFlowTracker.GeneratingTransaction
                    )
        } catch (e: Exception) {
            fail("Should not have thrown any exception")
        }
    }

}

