package ru.tecon.admTools.systemParams.model.job

import java.time.LocalDateTime
import java.util.UUID

/**
 * @author Maksim Shchelkonogov
 * 06.06.2025
 */
data class Jobs(val id: UUID, val jobNme: String, val comment: String?, val what: String, val lastDate: LocalDateTime, val nextDate: LocalDateTime) {

    constructor(jobNme: String, comment: String?, what: String, lastDate: LocalDateTime, nextDate: LocalDateTime) :
            this(UUID.randomUUID(), jobNme, comment, what, lastDate, nextDate)
}