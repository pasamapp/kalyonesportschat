package com.pasamapp.kalyonesportschat.utils

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasamapp.kalyonesportschat.Models.Mesaj
import com.pasamapp.kalyonesportschat.Models.Users
import com.pasamapp.kalyonesportschat.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.tek_satir_mesaj_alan.view.*


class MesajRecyclerViewAdapter(var tumMesajlar:ArrayList<Mesaj>, var myContext:Context, var sohbetEdilecekUser:Users?) : androidx.recyclerview.widget.RecyclerView.Adapter<MesajRecyclerViewAdapter.MyMesajViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyMesajViewHolder {

        var myView:View?=null

        if(viewType==1){
            myView=LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_gonderen,parent,false)
            return MyMesajViewHolder(myView, null)

        }else  {
            myView=LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_alan,parent,false)
            return MyMesajViewHolder(myView, sohbetEdilecekUser!!)
        }

    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    override fun onBindViewHolder(holder:MyMesajViewHolder, position: Int) {
        holder.setData(tumMesajlar.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        if(tumMesajlar.get(position).user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            return 1
        }else return 2
    }



    class MyMesajViewHolder(itemView: View?, var sohbetEdilecekUser: Users?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tumlayout=itemView as ConstraintLayout
        var mesajText=tumlayout.tvMesaj
        var profilePicture=tumlayout.mesajUserProfilePic


        fun setData(oankiMesaj: Mesaj) {

            if(sohbetEdilecekUser!=null){
                UniversalImageLoader.setImage(sohbetEdilecekUser!!.user_detail!!.profile_picture!!,profilePicture,null,"")
            }

            mesajText.text=oankiMesaj.mesaj
        }

    }


}