//? if fabric {
package ru.benos.boslib.loaders

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import ru.benos.boslib.BosLibCommon

class FabricModLoader : ModInitializer, IModLoader {
    override val name: String = "Fabric"
    override val isDevelopment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment

    override fun isModLoaded(modId: String): Boolean =
        FabricLoader.getInstance().isModLoaded(modId)

    override fun onInitialize() =
        BosLibCommon.init(this)
}
//? }
