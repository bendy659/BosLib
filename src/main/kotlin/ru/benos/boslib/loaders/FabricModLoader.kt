//? if fabric {
package ru.benos.boslib.loaders

import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import ru.benos.boslib.BosLibCommon

object FabricModLoader : ModInitializer, IModLoader {
    override val name: String = "Fabric"
    override val isDevelopment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment
    override val isClient: Boolean
        get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT

    override fun isModLoaded(modId: String): Boolean =
        FabricLoader.getInstance().isModLoaded(modId)

    override fun onInitialize() =
        BosLibCommon.init(this)
}
//? }
