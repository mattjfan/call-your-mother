package com.example.callyourmother.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.findNavController

import com.example.callyourmother.R
import kotlinx.android.synthetic.main.fragment_debug.*

/**
 * DebugFragment - created if you want to go to a certain part of the application quickly.
 * TODO - make sure before production release this is not the start in the Navigation Graph
 */
class DebugFragment : Fragment() {

    private lateinit var toHomeBtn: Button
    private lateinit var toManageScheduleBtn: Button
    private lateinit var toUserBtn: Button
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toHomeBtn = to_home_btn
        toManageScheduleBtn =  to_manage_schedule_btn
        toUserBtn = to_user_btn
        navController = view.findNavController()

        toHomeBtn.setOnClickListener { navController.navigate(R.id.action_debugFragment_to_homeFragment) }
        toManageScheduleBtn.setOnClickListener { navController.navigate(R.id.action_debugFragment_to_manageScheduleFragment) }
        toUserBtn.setOnClickListener { navController.navigate(R.id.action_debugFragment_to_userFragment) }
    }


}
