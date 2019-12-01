package com.example.callyourmother.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.callyourmother.R
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_layout_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contactName.text = contactList[position].contactName

        // Use the initial drawer
        if (contactList[position].contactPhotoUriStr == null) {
            holder.contactImage.setImageDrawable(InitialDrawer.getInitialDrawable(contactList[position].contactName))
        } else {
            holder.contactImage.setImageURI(Uri.parse(contactList[position].contactPhotoUriStr!!))
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    /**
     * ViewHolder - utilizing the ViewHolder pattern for each contact item
     * @param itemView - the current View of the item contact
     */
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_item_name)
        val contactImage: ImageView = itemView.findViewById(R.id.contact_item_img)
    }
}