package ru.benos.boslib.loaders

interface IModLoader {
    val name: String
    val isDevelopment: Boolean

    fun isModLoaded(modId: String): Boolean
}
