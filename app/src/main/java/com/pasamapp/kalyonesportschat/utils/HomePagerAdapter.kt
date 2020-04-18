package com.pasamapp.kalyonesportschat.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * Created by Pasam on 18.04.2020.
 */
class HomePagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private var mFragmentList:ArrayList<Fragment> = ArrayList()


    //fragment pager adapterın yazmayı zorunlu kıldıgı fonksiyon
    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    //fragment pager adapterın yazmayı zorunlu kıldıgı fonksiyon
    override fun getCount(): Int {
        return mFragmentList.size
    }

    //kişisel fonksiyon
    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)
    }

    fun secilenFragmentiViewPagerdanSil(viewGroup: ViewGroup, position: Int){
        var silinecekFragment=this.instantiateItem(viewGroup,position)
        this.destroyItem(viewGroup,position,silinecekFragment)
    }

    fun secilenFragmentiViewPageraEkle(viewGroup: ViewGroup, position: Int){
        this.instantiateItem(viewGroup,position)
    }
}