package com.example.callyourmother.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.callyourmother.R
import com.example.callyourmother.fragments.HomeFragmentDirections
import com.example.callyourmother.utils.ContactItem
import com.example.callyourmother.utils.InitialDrawer
import kotlinx.android.parcel.Parcelize

/**
 * HomeItemAdapter - the custom adapter for a contact item shown in the RecyclerView in the
 *                 HomeFragment
 * @param contactList - the List of contacts which will be shown in the RecyclerView
 */
class HomeItemAdapter(private val contactList: List<ContactItem>): RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactName = contactList[position].contactName
        val contactPhotoUriStr: String? = contactList[position].contactPhotoUriStr
        val contactNumber: String = contactList[position].contactNumber

        holder.contactName.text = contactName

        // Use the initial drawer
        if (contactPhotoUriStr == null) {
            holder.contactImage.setImageDrawable(InitialDrawer.getInitialDrawable(contactName))
        } else {
            holder.contactImage.setImageURI(Uri.parse(contactPhotoUriStr))
        }

        holder.itemView.setOnClickListener { toUserFragment(holder.itemView, contactName, contactPhotoUriStr, contactNumber) }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    /**
     * ViewHolder - utilizing the ViewHolder pattern for each contact item
     * @param itemView - the current View of the item contact
     */
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.card_contact_name_tv)
        val contactImage: ImageView = itemView.findViewById(R.id.card_contact_img)
    }

    /**
     * toUserFragment - when you click on a Contact it will take you to the UserFragment to edit settings etc.
     * @param itemView - the View of the current contact
     * @param contactName - the contact's name
     * @param contactPhotoUriStr - the URI String of the contact's photo if it exists
     * @param contactNumber - the contact's phone number
     */
    private fun toUserFragment(itemView: View, contactName: String, contactPhotoUriStr: String?, contactNumber: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToUserFragment(contactName, contactNumber, contactPhotoUriStr)
        val navController = itemView.findNavController()

        navController.navigate(action)
    }
}