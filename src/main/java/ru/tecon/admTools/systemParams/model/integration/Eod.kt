package ru.tecon.admTools.systemParams.model.integration

import java.time.LocalDateTime

/**
 * @author Maksim Shchelkonogov
 * 18.11.2025
 */
data class Eod(val date: LocalDateTime, val userName: String, val viewName: String)
