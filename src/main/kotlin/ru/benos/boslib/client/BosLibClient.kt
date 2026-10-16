//? if fabric {
package ru.benos.boslib.client

import net.fabricmc.api.ClientModInitializer
import ru.benos.boslib.BosLibCommon

object BosLibClient : ClientModInitializer {
    override fun onInitializeClient() =
        BosLibCommon.LOGGER.info("Initialized ${BosLibCommon.MOD_NAME} client on Fabric")
}
//? }
