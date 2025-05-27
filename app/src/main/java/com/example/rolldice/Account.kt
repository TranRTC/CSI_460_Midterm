package com.example.rolldice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*

need to config in build.gradle.kts (Module :app)
and syn to make parcelable work here

    plugins {
        id ("kotlin-android")
        id ("kotlin-parcelize")
    }
 */


@Parcelize
data class Account(


    val accountNumber: String,
    var balance: Double,
    val bankName: String


): Parcelable
