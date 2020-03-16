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


package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.logging

import com.americanexpress.blockchain.maranhao.workflow.party
import com.google.gson.GsonBuilder
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import org.junit.Test
import kotlin.test.assertNotNull

class ContractStateAdapterTest {

    @Test
    fun write() {
        val gson = GsonBuilder()
                .registerTypeAdapter(ContractState::class.java, ContractStateAdapter())
                .serializeNulls()
                .create()

        val state = object: ContractState {
            val value: Int = 0

            override val participants: List<AbstractParty>
                get() = listOf(party)
        }
        val ret = gson.toJson(state, ContractState::class.java)
        assertNotNull(ret)
    }

    @Test
    fun read() {
        //NOP
    }
}