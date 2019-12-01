package com.example.callyourmother.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.callyourmother.R
import com.example.callyourmother.adapters.HomeItemAdapter
import com.example.callyourmother.utils.ContactItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * HomeFragment - this is the Fragment that lists out the current contacts you want to keep track of
 *             and also this acts as the way to add new contacts. Should come directly after the
 *             SplashScreen
 */
class HomeFragment : Fragment() {

    private lateinit var addContactFab: FloatingActionButton
    private lateinit var navController: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeItemAdapter
    private lateinit var contactList: ArrayList<ContactItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()

        if (arguments != null && arguments!!.size() != 0) {
            val args = HomeFragmentArgs.fromBundle(arguments!!)
            val contactName = args.contactName
            val contactPhotoURIStr: String? = args.contactPhotoUriStr
            val contactItem = ContactItem(contactName, contactPhotoURIStr)

            contactList.add(contactItem)
        }

        navController = view.findNavController()
        addContactFab = add_contact_fab
        addContactFab.setOnClickListener { getContact() }
        adapter = HomeItemAdapter(contactList)
        recyclerView = home_recyclerview
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
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

    override fun onStop() {
        super.onStop()

        saveData()
    }

    /**
     * toUserFragment - after successfully selecting a new contact, it will navigate to the UserFragment
     *                to set the frequency and other settings
     * @param contactName - the contact name which was selected
     * @param contactNumber - the contact's number
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

    /** saveData - save the List of contacts as a JSON String representation in SharedPreferences */
    private fun saveData() {
        val prefs = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        val json = Gson().toJson(contactList)

        editor?.putString(RECYCLER_LIST, json)
        editor?.apply()
    }

    /** loadData - on start up, load up the previous List of contacts (if one exists) */
    private fun loadData() {
        val prefs = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val json = prefs?.getString(RECYCLER_LIST, null)
        val type = object : TypeToken<ArrayList<ContactItem>>() {}.type

        if (type != null && json != null) {
            contactList = Gson().fromJson(json, type)
        } else {
            contactList = ArrayList<ContactItem>()
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
        private const val RECYCLER_LIST = "Recycler_View_Home_List"
        private const val SHARED_PREFS = "Shared_Prefs_Home"
        private var hasPermission: Boolean = false
    }
}
