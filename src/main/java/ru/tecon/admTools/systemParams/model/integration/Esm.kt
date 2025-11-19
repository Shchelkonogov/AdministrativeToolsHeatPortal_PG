package ru.tecon.admTools.systemParams.model.integration

import java.time.LocalDateTime

/**
 * @author Maksim Shchelkonogov
 * 18.11.2025
 */
data class Esm(val login: String,
               val ip: String,
               val date: LocalDateTime,
               val action: String,
               val objName: String,
               val propName: String,
               val fileName: String?,
               val esmGuid: String)
