package com.example.callyourmother.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactInformation (val contactName: String, val contactNumber: String, val reminderFrequency: Int): Parcelable