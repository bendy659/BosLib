package ru.benos.boslib

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.benos.boslib.loaders.IModLoader

object BosLibCommon {
    const val MOD_ID: String = "boslib"
    const val MOD_NAME: String = "BosLib"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    lateinit var LOADER: IModLoader

    fun init(loader: IModLoader) { LOADER = loader; LOGGER.info("Initialized $MOD_NAME on ${loader.name}") }
}
