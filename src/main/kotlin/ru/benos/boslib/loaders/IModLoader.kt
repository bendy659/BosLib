package ru.benos.boslib.loaders

interface IModLoader {
    val name: String
    val isDevelopment: Boolean
    val isClient: Boolean

    fun isModLoaded(modId: String): Boolean
}
