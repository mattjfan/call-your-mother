package com.example.callyourmother.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.callyourmother.R
import com.example.callyourmother.utils.InitialDrawer
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_user.*
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CallLog
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class UserFragment : Fragment() {

    private lateinit var contactPhotoImg: ImageView
    private lateinit var contactNameTv: TextView
    private lateinit var callButton: Button
    private lateinit var manageButton: Button
    private lateinit var reminderDialog: Button
    private lateinit var contactName: String
    private lateinit var contactNumber: String
    private lateinit var navController: NavController
    private lateinit var upButton: FloatingActionButton
    private lateinit var lastCallText: TextView // displays the last time this person was called
    private var contactPhotoUriStr: String? = null
    private var contactPhotoPresent: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI setup
        contactPhotoImg = contact_photo_iv
        contactNameTv = contact_name_tv
        upButton = user_fab
        navController = view.findNavController()
        reminderDialog = reminder_dialog
        lastCallText = lastcall_tv

        upButton.setOnClickListener { toHomeFragment() }
        reminderDialog.setOnClickListener { setUpDialog() }

        // Should never be null otherwise we won't be able to populate this screen at all
        if (arguments != null) {
            val args = UserFragmentArgs.fromBundle(arguments!!)

            contactPhotoUriStr = args.contactPhotoUriStr
            contactNumber = args.contactNumber
            contactName = args.contactName
        }

        contactNameTv.text = contactName
        setUpContactPhoto(contactPhotoUriStr, contactName)
        callButton = call_button // call the current contact
        manageButton = manage_button // schedule new timers
        callButton.setOnClickListener { callContact() }
        manageButton.setOnClickListener { }
        getLatestCall()
    }

    /** setUpDialog - sets up the AlertDialog for the reminder frequency */
    private fun setUpDialog() {
        val frequencyList = arrayOf("Daily", "Weekly", "Bi-Weekly", "Monthly")
        val dialogBuilder = AlertDialog.Builder(context!!)
        val dialogInterfaceListener = DialogInterface.OnClickListener { dialogInterface, i ->
            reminderDialog.text = frequencyList[i]
            dialogInterface.dismiss()
        }
        
        dialogBuilder.setTitle("Reminder Frequency")
        dialogBuilder.setSingleChoiceItems(frequencyList, -1, dialogInterfaceListener)
        dialogBuilder.setNeutralButton("Cancel", DialogInterface.OnClickListener {_, _ ->})
        dialogBuilder.create().show()
    }

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

    /**
     * toHomeFragment - sends the required resources back to the HomeFragment
     */
    private fun toHomeFragment() {
        val strToPass: String? = if (contactPhotoPresent) contactPhotoUriStr else null
        val action = UserFragmentDirections.actionUserFragmentToHomeFragment(contactName, strToPass, contactNumber)

        navController.navigate(action)
    }
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
    private fun getReferenceDate( daysBack: Int): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, -daysBack)
        return cal.time
    }
    private fun isLessThanXDaysBack(date: Date, daysBack: Int): Boolean {
        return date > getReferenceDate(1)
    }

    private fun isUpToDate() {

    }
    private fun getLatestCall() {
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.READ_CALL_LOG), REQUEST_READ_CALL_LOG)
        }
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            val allCalls = Uri.parse("content://call_log/calls")
            val c = context!!.contentResolver.query(allCalls, null, null, null, null)
            var mostRecentDate: Date? = null
            while(c!!.moveToNext()) {
                val num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER))// for  number
                val name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME))// for name
                val duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION))// for duration
                val date = Date(java.lang.Long.valueOf(c.getString(c.getColumnIndex(CallLog.Calls.DATE))))
                val type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)))// for call type, Incoming or out going.
                if (type == CallLog.Calls.OUTGOING_TYPE || type == CallLog.Calls.INCOMING_TYPE) {
                    if(mostRecentDate == null || (date > mostRecentDate && PhoneNumberUtils.compare(num, contactNumber))) {
                        mostRecentDate = date
                    }
                }
            }

            if (mostRecentDate == null) {
                Log.i(TAG, "No calls found")
                lastCallText.text = "Couldn't find any recent calls"
            } else {
                when {
                    (mostRecentDate > getReferenceDate(1)) -> lastCallText.text = "Last call was less than a day ago"
                    (mostRecentDate > getReferenceDate(7)) -> lastCallText.text = "Last call was earlier this week"
                    (mostRecentDate > getReferenceDate(30)) -> lastCallText.text = "Last call was earlier this month"
                    else -> lastCallText.text = "Last call was more than a month ago"
                }
                Log.i(TAG, "Most recent convo was $mostRecentDate")
//                lastCallText.text = "Last call was $mostRecentDate"
            }
        }
    }
    companion object {
        val TAG = "user-fragment"
        val REQUEST_PHONE_CALL = 2
        val REQUEST_READ_CALL_LOG = 3
    }
}
