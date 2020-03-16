package com.americanexpress.blockchain.maranhao.workflow

import co.paralleluniverse.fibers.Suspendable
import com.americanexpress.blockchain.maranhao.NotaryStrategy
import net.corda.core.cordapp.CordappConfigException
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import java.lang.IllegalStateException

/**
 * Implementation of the notary strategy, fetching notary from cordapp config file
 * If notary info is not found in the config file, switch back to the FirstNotaryStrategy
 *
 * @property notaryConfigKey key specified for notary in the config.
 */
class ConfigurableNotaryStrategy(private val notaryConfigKey: String) : NotaryStrategy {


    /**
     * Return the notary specified in the cordapp config file
     *
     * @param serviceHub ServiceHub
     * @return Party
     */
    @Suspendable
    override fun getNotary(serviceHub: ServiceHub): Party {
        val notaryIdentity = try {
            val config = serviceHub.getAppContext().config
            config.getString(notaryConfigKey)
        } catch (ex: CordappConfigException) {
            ""
        } catch (ex1: IllegalStateException) {
            ""
        }

        return if (notaryIdentity.isBlank()) {
            FirstNotaryStrategy().getNotary(serviceHub)
        }
        else {
            val notaryX500Name = CordaX500Name.parse(notaryIdentity)
            val notaryParty = serviceHub.networkMapCache.getNotary(notaryX500Name)
            notaryParty!!
        }
    }
}