package com.moore.hometabfragmentmode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_1.*

/**
 * Created by moore on 2019/11/21.
 */
class Fragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_1, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is MainActivity) {
            goTabViewPager.visibility = View.VISIBLE
        } else {
            goTabViewPager.visibility = View.GONE
        }
        goTabViewPager.setOnClickListener {
            val intent = Intent(context, ViewPagerTabActivity::class.java)
            startActivity(intent)
        }
    }
}