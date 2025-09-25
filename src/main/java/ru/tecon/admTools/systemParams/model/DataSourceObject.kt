package ru.tecon.admTools.systemParams.model

import java.util.*

/**
 * @author Maksim Shchelkonogov
 * 25.09.2025
 */
data class DataSourceObject(val id: UUID, var name: String, var url: String, var status: String) {

    constructor() :
            this(UUID.randomUUID(), "", "", "")

    constructor(name: String, url: String, status: String) :
            this(UUID.randomUUID(), name, url, status)
}
