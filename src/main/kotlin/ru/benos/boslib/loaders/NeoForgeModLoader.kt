//? if neoforge {
/*package ru.benos.boslib.loaders

import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.ModList
import ru.benos.boslib.BosLibCommon

@Mod(BosLibCommon.MOD_ID)
object NeoForgeModLoader : IModLoader {
    override val name: String = "NeoForge"
    override val isDevelopment: Boolean
        get() = !FMLEnvironment.isProduction()
    override val isClient: Boolean
        get() = FMLEnvironment.getDist().isClient

    override fun isModLoaded(modId: String): Boolean =
        ModList.get().isLoaded(modId)

    init { BosLibCommon.init(this) }
}
*///? }
