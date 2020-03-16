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
import net.corda.core.identity.Party
import org.junit.Test

import org.junit.Assert.*

class PartyAdapterTest {

    @Test
    fun write() {
        val gson = GsonBuilder()
                .registerTypeAdapter(Party::class.java, PartyAdapter())
                .serializeNulls()
                .create()

        val ret = gson.toJson(party, Party::class.java)

        assertNotNull(ret)
    }

    @Test
    fun read() {
    }
}