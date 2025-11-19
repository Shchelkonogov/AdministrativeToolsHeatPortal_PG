package ru.tecon.admTools.systemParams.model.integration

import java.time.LocalDateTime

/**
 * @author Maksim Shchelkonogov
 * 18.11.2025
 */
data class Assd(val objId: Int, val parId: Int, val value: String, val date: LocalDateTime, val updated: LocalDateTime)
