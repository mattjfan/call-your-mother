package com.example.callyourmother.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.callyourmother.R
import com.example.callyourmother.utils.InitialDrawer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_user.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.provider.CallLog
import android.telephony.PhoneNumberUtils
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import com.example.callyourmother.notification.Notifications
class UserFragment : Fragment() {
    val DEFAULT_FREQUENCY = 7
    private lateinit var contactPhotoImg: ImageView
    private lateinit var contactNameTv: TextView
    private lateinit var contactNumberTv: TextView
    private lateinit var callButton: Button
    private lateinit var deleteButton: Button
    private lateinit var saveButton: Button
//    private lateinit var reminderDialog: Button
    private lateinit var contactName: String
    private lateinit var contactNumber: String
    private lateinit var navController: NavController
    private lateinit var upButton: FloatingActionButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var reminderFrequencySpinner: Spinner
    private lateinit var lastCallText: TextView // displays the last time this person was called
    private var contactPhotoUriStr: String? = null
    private var contactPhotoPresent: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // UI setup
        contactPhotoImg = card_contact_photo
        contactNumberTv = card_contact_number
        contactNameTv = card_contact_name_tv
        upButton = user_fab
        saveButton = save_contact
        deleteButton = delete_contact
        navController = view.findNavController()
//        reminderDialog = reminder_dialog
        lastCallText = card_last_call_tv

        upButton.setOnClickListener {
            Toast.makeText(context, "Contact Saved!", Toast.LENGTH_SHORT).show()
            toHomeFragment(true)
        }
        deleteButton.setOnClickListener {
            Toast.makeText(context,"Contact Deleted!", Toast.LENGTH_SHORT).show()
            toHomeFragment(false)
        }
        saveButton.setOnClickListener {
            Toast.makeText(context, "Contact Saved!", Toast.LENGTH_SHORT).show()
            toHomeFragment(true)
        }
//        reminderDialog.setOnClickListener { setUpDialog() }

        // Should never be null otherwise we won't be able to populate this screen at all
        if (arguments != null) {
            val args = UserFragmentArgs.fromBundle(arguments!!)

            contactPhotoUriStr = args.contactPhotoUriStr
            contactNumber = args.contactNumber
            contactName = args.contactName
        }

        contactNameTv.text = contactName
        contactNumberTv.text = contactNumber
        setUpContactPhoto(contactPhotoUriStr, contactName)
        callButton = call_button // call the current contact
        callButton.setOnClickListener { callContact() }
        reminderFrequencySpinner = reminder_frequency_spinner
        sharedPreferences = this.activity!!.getPreferences(MODE_PRIVATE)
        reminderFrequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i(TAG, "Nothing Selected")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.i(TAG, "${reminderFrequencySpinner.selectedItem} selected")
                when (position) {
                    0 -> setScheduleFrequency(1)
                    1 -> setScheduleFrequency(7)
                    2 -> setScheduleFrequency(14)
                    3 -> setScheduleFrequency(30)
                    4 -> setScheduleFrequency(365)
                }

            }

        }
    }

//    /** setUpDialog - sets up the AlertDialog for the reminder frequency */
//    private fun setUpDialog() {
//        val frequencyList = arrayOf("Daily", "Weekly", "Bi-Weekly", "Monthly")
//        val dialogBuilder = AlertDialog.Builder(context!!)
//        val dialogInterfaceListener = DialogInterface.OnClickListener { dialogInterface, i ->
//            reminderDialog.text = frequencyList[i]
//            dialogInterface.dismiss()
//        }
//
//        dialogBuilder.setTitle("Reminder Frequency")
//        dialogBuilder.setSingleChoiceItems(frequencyList, -1, dialogInterfaceListener)
//        dialogBuilder.setNeutralButton("Cancel", DialogInterface.OnClickListener {_, _ ->})
//        dialogBuilder.create().show()
//    }

    /**
     * setUpContactPhoto - this sets up the contact's photo. If one exists, use that one. Otherwise,
     *                   use the TextDrawable
     * @param contactPhotoUriStr - the Uri String of where the contact photo is if it exists
     * @param contactName - the name of the contact
     */
    private fun setUpContactPhoto(contactPhotoUriStr: String?, contactName: String) {
        // Use the ShapeTextDrawable
        if (contactPhotoUriStr == null) {
            val initials = InitialDrawer.getInitialDrawable(contactName)

            contactPhotoPresent = false
            contactPhotoImg.setImageDrawable(initials)
        } else {

            contactPhotoPresent = true
            contactPhotoImg.setImageURI(Uri.parse(contactPhotoUriStr))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }
    /**
     * toHomeFragment - sends the required resources back to the HomeFragment
     */
    private fun toHomeFragment(shouldSave: Boolean) {
        val strToPass: String? = if (contactPhotoPresent) contactPhotoUriStr else null
        val action = UserFragmentDirections.actionUserFragmentToHomeFragment(contactName, strToPass, contactNumber, shouldSave)

        navController.navigate(action)
    }

    /** callContact - call the Contact on the screen */
    private fun callContact() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$contactNumber")
        // if permission is granted, request it
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
        }
        // if permission has been granted, call the number
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        }
    }

    /**
     * getReferenceDate - gets the reference date
     * @param daysBack - number of days back
     * @return the last date back
     */
    private fun getReferenceDate( daysBack: Int): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, -daysBack)
        return cal.time
    }

    /**
     * isLessThanXDaysBacks - checks to see how far back the date is from the last call
     * @param date - the previous date
     * @param daysBack - the number of days back
     * @return true if the date is greater than the number of days back. False otherwise
     */
    private fun isLessThanXDaysBack(date: Date, daysBack: Int): Boolean {
        return date > getReferenceDate(1)
    }

    /** isUpToDate - checks to see if the timer is up to date? */
    private fun isUpToDate() {

    }
    private fun setScheduleFrequency(freq: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(Notifications.phoneNumbertoID(contactNumber), freq)
        editor.apply()
        val mostRecentCallDate = getMostRecentCallDate() ?: getReferenceDate(0)
        val notificationManager : Notifications = Notifications()
        notificationManager.createNotificationChannel(this.requireContext(),
                "0", "main", "")

        val mostRecentDate : Calendar = Calendar.getInstance()
        mostRecentDate.time = mostRecentCallDate

        notificationManager.setScheduledNotification(this.requireContext(),
                freq, mostRecentDate, contactNumber, "Call Your Mother!",
                "Don't forget to call " + contactName + "!")
    }
    private fun getScheduleFrequency(): Int {
        var freq = sharedPreferences.getInt(Notifications.phoneNumbertoID(contactNumber), -1)
        if (freq == -1) {
            setScheduleFrequency(DEFAULT_FREQUENCY)
            freq = DEFAULT_FREQUENCY
        }
        when {
            freq <= 1 -> reminderFrequencySpinner.setSelection(0)
            freq <= 7 -> reminderFrequencySpinner.setSelection(1)
            freq <= 14 -> reminderFrequencySpinner.setSelection(2)
            freq <= 31 -> reminderFrequencySpinner.setSelection(3)
            freq <= 366 -> reminderFrequencySpinner.setSelection(4)
        }
        return freq
    }
    private fun getMostRecentCallDate(): Date? {
        if (ContextCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                REQUEST_READ_CALL_LOG
            )
        }
        if (ContextCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val allCalls = Uri.parse("content://call_log/calls")
            val c = context!!.contentResolver.query(allCalls, null, null, null, null)
            var mostRecentDate: Date? = null
            while (c!!.moveToNext()) {
                val num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER))// for  number
                val name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME))// for name
                val duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION))// for duration
                val date =
                    Date(java.lang.Long.valueOf(c.getString(c.getColumnIndex(CallLog.Calls.DATE))))
                val type =
                    Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)))// for call type, Incoming or out going.
                if (type == CallLog.Calls.OUTGOING_TYPE || type == CallLog.Calls.INCOMING_TYPE) {
                    if ((mostRecentDate == null || date > mostRecentDate) && PhoneNumberUtils.compare(
                            num,
                            contactNumber
                        )
                    ) {
                        mostRecentDate = date
                    }
                }
            }

            c.close()
            return mostRecentDate
        }
        return null
    }

    private fun getStatusMessage(colorID: Int, status: String, moreInfo: String ): Spanned {
        return Html.fromHtml("<b><font color=${resources.getColor(colorID)}>$status</font></b> <br/> $moreInfo")
    }
    private fun refreshUI() {
        val mostRecentDate: Date? = getMostRecentCallDate()
        val freq: Int = getScheduleFrequency()

        Log.i(TAG, "ColorStateList: ${this.context!!.resources.getColorStateList(R.color.colorNeutral, null)}")
        if (mostRecentDate != null && mostRecentDate > getReferenceDate(freq)) {
            callButton.backgroundTintList = ContextCompat.getColorStateList(this.context!!, R.color.colorNeutral)
            callButton.setBackgroundColor(resources.getColor(R.color.colorNeutral, null))
            callButton.invalidate()
            Log.i(TAG, "Call button should be grey")
        } else {
            callButton.backgroundTintList = ContextCompat.getColorStateList(this.context!!, R.color.accentGreen)
            callButton.setBackgroundColor(resources.getColor(R.color.accentGreen, null))
            callButton.invalidate()
            Log.i(TAG, "Call button should be green")
        }

        if (mostRecentDate == null) {
            Log.i(TAG, "No calls found")
            lastCallText.text = getStatusMessage(R.color.accentYellow, resources.getString(R.string.status_behind), "Could not find any recent calls")
        } else {
            var message: String
            var colorID: Int
            when {
                mostRecentDate > getReferenceDate(freq) -> {
                    message = resources.getString(R.string.status_caught_up)
                    colorID = R.color.accentGreen
                }
                else -> {
                    message = resources.getString(R.string.status_behind)
                    colorID = R.color.accentYellow
                }
            }
            val moreInfo: String = when {
                (mostRecentDate > getReferenceDate(1)) -> "Last call was less than a day ago"
                (mostRecentDate > getReferenceDate(7)) -> "Last call was earlier this week"
                (mostRecentDate > getReferenceDate(30)) -> "Last call was earlier this month"
                else -> "Last call was more than a month ago"
            }

            lastCallText.text = getStatusMessage(colorID, message, moreInfo)
            Log.i(TAG, "Most recent convo was $mostRecentDate")
//                lastCallText.text = "Last call was $mostRecentDate"
        }
    }

    companion object {
        val TAG = "user-fragment"
        val REQUEST_PHONE_CALL = 2
        val REQUEST_READ_CALL_LOG = 3
    }
}
