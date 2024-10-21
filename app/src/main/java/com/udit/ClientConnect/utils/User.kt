package com.udit.ClientConnect.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize   // for making data parcelable between navigation
data class User(
    val id :String = UUID.randomUUID().toString(),
    val userId:String? = null,
    val userName : String? = null,
    val userEmail : String? = null,
    val userPassword: String? = null,
    val userImage:String? = null
):Parcelable
