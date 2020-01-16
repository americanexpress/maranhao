package com.americanexpress.blockchain.maranhao.workflow

import net.corda.core.cordapp.CordappConfig
import net.corda.core.cordapp.CordappContext
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.NetworkMapCache
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.NullPointerException
import java.security.PublicKey

class ConfigurableNotaryStrategyTest {

    @Test
    fun `should return notary specified in the cordapp config file`() {
        val networkMapCache = Mockito.mock(NetworkMapCache::class.java)
        val publicKey = Mockito.mock(PublicKey::class.java)
        val cordappContext = Mockito.mock(CordappContext::class.java)
        val cordappConfig = Mockito.mock(CordappConfig::class.java)

        Mockito.`when`(networkMapCache.getNotary(CordaX500Name.parse("O=Alice,L=New York,C=US")))
                .thenReturn(Party(partyName, publicKey))
        Mockito.`when`(serviceHub.networkMapCache)
                .thenReturn(networkMapCache)
        Mockito.`when`(cordappConfig.getString("notary"))
                .thenReturn("O=Alice,L=New York,C=US")
        Mockito.`when`(cordappContext.config)
                .thenReturn(cordappConfig)
        Mockito.`when`(serviceHub.getAppContext())
                .thenReturn(cordappContext)
        Assert.assertNotNull(
                ConfigurableNotaryStrategy("notary")
                        .getNotary(serviceHub)
        )
    }

    @Test
    fun `should return first notary from networkmap if notary info not found in config`() {
        val networkMapCache = Mockito.mock(NetworkMapCache::class.java)
        val publicKey = Mockito.mock(PublicKey::class.java)
        val cordappContext = Mockito.mock(CordappContext::class.java)
        val cordappConfig = Mockito.mock(CordappConfig::class.java)

        Mockito.`when`(networkMapCache.notaryIdentities)
                .thenReturn(listOf(Party(partyName, publicKey)))
        Mockito.`when`(serviceHub.networkMapCache)
                .thenReturn(networkMapCache)
        Mockito.`when`(cordappConfig.getString("notary"))
                .thenReturn("")
        Mockito.`when`(cordappContext.config)
                .thenReturn(cordappConfig)
        Mockito.`when`(serviceHub.getAppContext())
                .thenReturn(cordappContext)
        Assert.assertNotNull(
                ConfigurableNotaryStrategy("notary")
                        .getNotary(serviceHub)
        )
    }

    @Test(expected = NullPointerException::class)
    fun `should throw exception if the specified notary not found in network map`() {
        val networkMapCache = Mockito.mock(NetworkMapCache::class.java)
        val publicKey = Mockito.mock(PublicKey::class.java)
        val cordappContext = Mockito.mock(CordappContext::class.java)
        val cordappConfig = Mockito.mock(CordappConfig::class.java)

        Mockito.`when`(networkMapCache.getNotary(CordaX500Name.parse("O=Alice,L=New York,C=US")))
                .thenReturn(Party(partyName, publicKey))
        Mockito.`when`(serviceHub.networkMapCache)
                .thenReturn(networkMapCache)
        Mockito.`when`(cordappConfig.getString("notary"))
                .thenReturn("O=Bob,L=New York,C=US")
        Mockito.`when`(cordappContext.config)
                .thenReturn(cordappConfig)
        Mockito.`when`(serviceHub.getAppContext())
                .thenReturn(cordappContext)
        ConfigurableNotaryStrategy("notary").getNotary(serviceHub)

    }
}