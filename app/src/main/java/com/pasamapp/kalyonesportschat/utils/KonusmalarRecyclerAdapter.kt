package com.pasamapp.kalyonesportschat.utils

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasamapp.kalyonesportschat.Home.ChatActivity
import com.pasamapp.kalyonesportschat.Models.Konusmalar
import com.pasamapp.kalyonesportschat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_satir_konusma_item.view.*

/**
 * Created by Pasam on 18.04.2020.
 */
class KonusmalarRecyclerAdapter(var tumKonusmalar:ArrayList<Konusmalar>, var myContext:Context): androidx.recyclerview.widget.RecyclerView.Adapter<KonusmalarRecyclerAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var myView=LayoutInflater.from(myContext).inflate(R.layout.tek_satir_konusma_item, parent,false)

        return MyViewHolder(myView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setData(tumKonusmalar.get(position))
    }

    override fun getItemCount(): Int {
        return tumKonusmalar.size
    }




    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tumLayout=itemView as ConstraintLayout

        var enSonAtilanMesaj=tumLayout.tvSonMesaj
        var mesajZaman = tumLayout.tvMesajZaman
        var sohbetEdilenUserName=tumLayout.tvUserName
        var sohbetEdilenUserPic=tumLayout.imgUserProfilePicture
        var okunduBilgisi=tumLayout.imgOkunmaBilgisi



        fun setData(oankiKonusma: Konusmalar) {

            var konusmaText=oankiKonusma.son_mesaj.toString()
            if(!konusmaText.isNullOrEmpty()){
                konusmaText=konusmaText.replace("\n"," ")
                konusmaText=konusmaText.trim()

                if(konusmaText.length>25){
                    enSonAtilanMesaj.text=konusmaText.substring(0,25)+"..."
                }else{
                    enSonAtilanMesaj.text=konusmaText
                }
            }else{
                konusmaText=""
                enSonAtilanMesaj.text=konusmaText
            }



            mesajZaman.text=TimeAgo.getTimeAgoForComments(oankiKonusma.time!!.toLong())

            if(oankiKonusma.goruldu==false){

                okunduBilgisi.visibility=View.VISIBLE
                sohbetEdilenUserName.setTypeface(null,Typeface.BOLD)
                enSonAtilanMesaj.setTypeface(null,Typeface.BOLD)
                enSonAtilanMesaj.setTextColor(ContextCompat.getColor(itemView.context,R.color.siyah))
                mesajZaman.setTextColor(ContextCompat.getColor(itemView.context,R.color.siyah))

            }else {
                okunduBilgisi.visibility=View.INVISIBLE
                sohbetEdilenUserName.setTypeface(null,Typeface.NORMAL)
                enSonAtilanMesaj.setTypeface(null,Typeface.NORMAL)
                mesajZaman.setTextColor(ContextCompat.getColor(itemView.context,R.color.gri))
                enSonAtilanMesaj.setTextColor(ContextCompat.getColor(itemView.context,R.color.gri))

            }

            tumLayout.setOnClickListener {

                var intent=Intent(itemView.context,ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("secilenUserID", oankiKonusma.user_id.toString())

                FirebaseDatabase.getInstance().getReference()
                        .child("konusmalar")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(oankiKonusma.user_id.toString())
                        .child("goruldu").setValue(true)
                        .addOnCompleteListener {
                            itemView.context.startActivity(intent)
                        }



            }

            sohbetEdilenKullaniciBilgileriniGetir(oankiKonusma.user_id.toString())

        }

        private fun sohbetEdilenKullaniciBilgileriniGetir(userID: String) {

            FirebaseDatabase.getInstance().getReference().child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                   if(p0!!.getValue() != null ){


                       sohbetEdilenUserName.text=p0.child("user_name").getValue().toString()
                       var userProfilePictureURL=p0.child("user_detail").child("profile_picture").getValue().toString()
                       UniversalImageLoader.setImage(userProfilePictureURL,sohbetEdilenUserPic,null,"")



                   }
                }


            })

        }


    }


}