package ru.tecon.admTools.systemParams.model.integration

import java.time.LocalDateTime

/**
 * @author Maksim Shchelkonogov
 * 18.11.2025
 */
data class Asot(val tableName: String,
                val muid: String,
                val createDate: LocalDateTime,
                val operation: String,
                val status: String)
