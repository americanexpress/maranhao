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

import net.corda.core.identity.Party
import net.corda.core.node.services.NetworkMapCache
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.security.PublicKey

class FirstNotaryStrategyTest {


    @Test
    fun `should return first notary`() {
        val networkMapCache = Mockito.mock(NetworkMapCache::class.java)
        val publicKey = Mockito.mock(PublicKey::class.java)
        Mockito.`when`(networkMapCache.notaryIdentities)
                .thenReturn(listOf(Party(partyName, publicKey)))
        Mockito.`when`(serviceHub.networkMapCache)
                .thenReturn(networkMapCache)
        Assert.assertNotNull(
                FirstNotaryStrategy()
                        .getNotary(serviceHub)
        )
    }
}
