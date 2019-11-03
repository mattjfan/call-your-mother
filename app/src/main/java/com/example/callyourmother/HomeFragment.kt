package com.example.callyourmother

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var toUserBtn: Button
    private lateinit var toManageBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toUserBtn = tmp_to_user_btn
        toManageBtn = tmp_to_schedule_btn

        toManageBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_manageScheduleFragment)
        }

        toUserBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_userFragment)
        }
    }

}
