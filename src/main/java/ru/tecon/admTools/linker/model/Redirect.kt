package ru.tecon.admTools.linker.model

import java.io.Serializable

/**
 * @author Maksim Shchelkonogov
 * 17.10.2023
 */
data class Redirect(val name: String, val value: String) : Serializable
