package com.example.callyourmother.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * ContactItem - data class to hold the information passed to the contact items
 * @param contactName - the contact's name to be shown
 * @param contactPhotoUriStr - the contact photo's URI String (null if use the initial drawer
 * @param contactNumber - the contact's phone number
 */
@Parcelize
data class ContactItem(val contactName: String, val contactPhotoUriStr: String?, val contactNumber: String): Parcelable