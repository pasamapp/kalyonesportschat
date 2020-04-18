package com.pasamapp.kalyonesportschat.Profile


import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pasamapp.kalyonesportschat.R
import kotlinx.android.synthetic.main.fragment_yukleniyor.view.*


/**
 * A simple [Fragment] subclass.
 */
class YukleniyorFragment : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_yukleniyor, container, false)

        view.progressBar2.indeterminateDrawable.setColorFilter(ContextCompat.getColor(activity!!,R.color.siyah), PorterDuff.Mode.SRC_IN)

        return view
    }

}// Required empty public constructor
