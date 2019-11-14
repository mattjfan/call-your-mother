package com.example.callyourmother.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.callyourmother.R
import com.github.stephenvinouze.shapetextdrawable.ShapeForm
import com.github.stephenvinouze.shapetextdrawable.ShapeTextDrawable
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment() {

    private lateinit var contactPhotoImg: ImageView
    private lateinit var contactNameTv: TextView
    private lateinit var contactNumberTv: TextView
    private lateinit var contactName: String
    private lateinit var contactNumber: String
    private var contactPhotoUriStr: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI setup
        contactPhotoImg = contact_photo_iv
        contactNameTv = contact_name_tv
        contactNumberTv = contact_number_tv

        // Should never be null otherwise we won't be able to populate this screen at all
        if (arguments != null) {
            val args = UserFragmentArgs.fromBundle(arguments!!)

            contactPhotoUriStr = args.contactPhotoUriStr
            contactNumber = args.contactNumber
            contactName = args.contactName
        }

        contactNumberTv.text = contactNumber
        contactNameTv.text = contactName
        setUpContactPhoto(contactPhotoUriStr, contactName)
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
            val nameList: List<String> = contactName.split("\\s".toRegex())
            val nameListSize = nameList.size
            val firstNameChar = nameList[0][0]
            var strToDraw = "$firstNameChar"

            if (nameListSize > 1) {
                val lastNameChar = nameList[nameListSize - 1][0]

                strToDraw += lastNameChar
            }

            val color = activity!!.getColor(R.color.materialBlue)
            val s = ShapeTextDrawable(ShapeForm.ROUND, color, text = strToDraw)

            contactPhotoImg.setImageDrawable(s)
        } else {

            contactPhotoImg.setImageURI(Uri.parse(contactPhotoUriStr))
        }
    }
}
