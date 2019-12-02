package com.example.callyourmother.fragments


import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_timer_debug.*
import com.example.callyourmother.R



class TimerDebug : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        to_home_fragment.setOnClickListener {
            view.findNavController().navigate(TimerDebugDirections.actionTimerDebugToHomeFragment("Test", "Test", "Test", false))
        }
    }
}
