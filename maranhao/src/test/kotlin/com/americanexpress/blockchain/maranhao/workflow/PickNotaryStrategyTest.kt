package com.americanexpress.blockchain.maranhao.workflow

import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.NetworkMapCache
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito
import java.security.PublicKey

class PickNotaryStrategyTest {

    @Test
    fun getNotary() {
        val notaryId = "O=Alice,L=New York,C=US"
        val x500Name = CordaX500Name.parse(notaryId)
        val networkMapCache = Mockito.mock(NetworkMapCache::class.java)
        Mockito.`when`(serviceHub.networkMapCache)
                .thenReturn(networkMapCache)
        val notary = Mockito.mock(Party::class.java)

        Mockito.`when`(networkMapCache.getNotary(x500Name))
                .thenReturn(notary)

        assertNotNull(
                PickNotaryStrategy(notaryId)
                        .getNotary(serviceHub)
        )
    }
}