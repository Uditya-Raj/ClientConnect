package com.udit.ClientConnect.utils

import java.util.UUID

data class Work(
    val id :String = UUID.randomUUID().toString(),
    val workId:String? = null,
    val workTitle:String? = null,
    val workDescription:String? = null,
    val workLastDate: String? = null,
    val workStatus:String? = null,
    var expanded:Boolean = false
)
