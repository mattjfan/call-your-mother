package com.example.callyourmother.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.callyourmother.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*
import java.net.URI

/**
 * HomeFragment - this is the Fragment that lists out the current contacts you want to keep track of
 *             and also this acts as the way to add new contacts. Should come directly after the
 *             SplashScreen
 */
class HomeFragment : Fragment() {

    private lateinit var addContactFab: FloatingActionButton
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()
        addContactFab = add_contact_fab
        add_contact_fab.setOnClickListener { getContact() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PICK_CONTACT_REQUEST_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true
                startContactsApp()
            } else {
                Toast.makeText(context, getString(R.string.requires_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            val contactUri = data?.data ?: return
            val projection = arrayOf(FORMATTED_NAME, FORMATTED_NUMBER, FORMATTED_PHOTO)
            val cr = activity?.contentResolver
            val cursor = cr?.query(contactUri, projection, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val contactNameColIdx = cursor.getColumnIndex(FORMATTED_NAME)
                val contactPhotoURIColIdx = cursor.getColumnIndex(FORMATTED_PHOTO)
                val phoneNumberColIdx = cursor.getColumnIndex(FORMATTED_NUMBER)
                val contactName = cursor.getString(contactNameColIdx)
                val contactNumber = cursor.getString(phoneNumberColIdx)
                val contactPhotoURIStr: String? = cursor.getString(contactPhotoURIColIdx)

                cursor.close()
                toUserFragment(contactName, contactNumber, contactPhotoURIStr)
            }

            cursor?.close()
        }
    }

    /**
     * toUserFragment - after successfully selecting a new contact, it will navigate to the UserFragment
     *                to set the frequency and other settings
     * @param contactName - the contact name which was selected
     * @param contactNumber - the contact number which was selected
     * @param contactPhotoURIStr - the String of the URI of the contact's photo (if it exists)
     */
    private fun toUserFragment(contactName: String, contactNumber: String, contactPhotoURIStr: String?) {
        val action = HomeFragmentDirections.actionHomeFragmentToUserFragment(contactName, contactNumber, contactPhotoURIStr)
        navController.navigate(action)
    }

    /**
     * startContactsApp - starts the contacts application with a PICK_CONTACT_REQUEST
     */
    private fun startContactsApp() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = CONTENT_PHONE_ITEM_TYPE

        if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
            startActivityForResult(intent, PICK_CONTACT_REQUEST)
        }
    }

    /**
     * needsRuntimePermission - checks to see at runtime if the READ_CONTACTS permission has been granted
     *                       or not
     * @return true if the READ_CONTACT permission has been granted. Otherwise return false
     */
    private fun needsRuntimePermission(): Boolean {
        return activity?.checkSelfPermission(READ_CONTACTS_PERM) != PackageManager.PERMISSION_GRANTED
    }

    /** getContact - if the correct permissions are granted, this will start the contacts application */
    private fun getContact() {
        if (!hasPermission && needsRuntimePermission()) {
            requestPermissions(arrayOf(READ_CONTACTS_PERM), PICK_CONTACT_REQUEST_PERM)
        } else {
            startContactsApp()
        }
    }

    companion object {
        private const val READ_CONTACTS_PERM = Manifest.permission.READ_CONTACTS
        private const val PICK_CONTACT_REQUEST = 0
        private const val PICK_CONTACT_REQUEST_PERM = 1
        private const val CONTENT_PHONE_ITEM_TYPE = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        private const val FORMATTED_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        private const val FORMATTED_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER
        private const val FORMATTED_PHOTO = ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        private var hasPermission: Boolean = false
    }
}
