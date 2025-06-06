package ru.tecon.admTools.systemParams.model.job

import java.time.LocalDateTime
import java.util.UUID

/**
 * @author Maksim Shchelkonogov
 * 06.06.2025
 */
data class JobHistory(val id: UUID, val logDate: LocalDateTime, val status: String?,
                      val actualStartDate: LocalDateTime, val runDuration: Int, val additionalInfo: String?) {

    constructor(logDate: LocalDateTime, status: String?, actualStartDate: LocalDateTime, runDuration: Int, additionalInfo: String?) :
            this(UUID.randomUUID(), logDate, status, actualStartDate, runDuration, additionalInfo)
}