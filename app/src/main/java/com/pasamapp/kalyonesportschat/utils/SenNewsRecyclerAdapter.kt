package com.pasamapp.kalyonesportschat.utils

import android.content.Context
import android.content.Intent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasamapp.kalyonesportschat.Generic.UserProfileActivity
import com.pasamapp.kalyonesportschat.Models.BildirimModel
import com.pasamapp.kalyonesportschat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_satir_gonderi_begendi_news.view.*
import kotlinx.android.synthetic.main.tek_satir_takip_basladi_news.view.*
import kotlinx.android.synthetic.main.tek_satir_takip_istegi_news.view.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class SenNewsRecyclerAdapter(var context: Context, var tumBildirimler: ArrayList<BildirimModel>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    var yeniSiralanmisListe=ArrayList<BildirimModel>()
    init {
        Collections.sort(tumBildirimler, object : Comparator<BildirimModel> {
            override fun compare(o1: BildirimModel?, o2: BildirimModel?): Int {
                if (o1!!.time!! > o2!!.time!!) {
                    return -1
                } else return 1
            }
        })
        for (item in tumBildirimler){
            if(item.bildirim_tur==Bildirimler.YENI_TAKIP_ISTEGI){
                yeniSiralanmisListe.add(0,item)
            }else{
                yeniSiralanmisListe.add(item)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        when (yeniSiralanmisListe.get(position).bildirim_tur) {

            Bildirimler.GONDERI_BEGENILDI -> {
                return Bildirimler.GONDERI_BEGENILDI
            }
            Bildirimler.TAKIP_ETMEYE_BASLADI -> {
                return Bildirimler.TAKIP_ETMEYE_BASLADI
            }
            Bildirimler.YENI_TAKIP_ISTEGI -> {
                return Bildirimler.YENI_TAKIP_ISTEGI
            }

            Bildirimler.TAKIP_ISTEGI_ONAYLANDI -> {
                return Bildirimler.TAKIP_ISTEGI_ONAYLANDI
            }
            else -> return 0

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        if (viewType == Bildirimler.GONDERI_BEGENILDI) {

            var myView = LayoutInflater.from(context).inflate(R.layout.tek_satir_gonderi_begendi_news, parent, false)

            return GonderiBegendiViewHolder(myView)


        } else if (viewType == Bildirimler.TAKIP_ETMEYE_BASLADI || viewType == Bildirimler.TAKIP_ISTEGI_ONAYLANDI) {

            var myView = LayoutInflater.from(context).inflate(R.layout.tek_satir_takip_basladi_news, parent, false)

            return TakipBasladiViewHolder(myView)

        } else {
            var myView = LayoutInflater.from(context).inflate(R.layout.tek_satir_takip_istegi_news, parent, false)

            return TakipIstekViewHolder(myView)

        }

    }

    override fun getItemCount(): Int {
        return yeniSiralanmisListe.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        when (yeniSiralanmisListe.get(position).bildirim_tur) {

            Bildirimler.GONDERI_BEGENILDI -> {
                (holder as GonderiBegendiViewHolder).setData(yeniSiralanmisListe.get(position))
            }
            Bildirimler.TAKIP_ETMEYE_BASLADI -> {
                (holder as TakipBasladiViewHolder).setData(yeniSiralanmisListe.get(position))
            }

            Bildirimler.TAKIP_ISTEGI_ONAYLANDI -> {
                (holder as TakipBasladiViewHolder).setData(yeniSiralanmisListe.get(position))
            }

            Bildirimler.YENI_TAKIP_ISTEGI -> {
                (holder as TakipIstekViewHolder).setData(yeniSiralanmisListe.get(position))
            }


        }

    }

    inner class TakipIstekViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tumLayout = itemView as ConstraintLayout
        var takipEdenUserProfileResim = tumLayout.imgTakipEdenUserPicture
        var takipEdenUserName = tumLayout.tvTakipEdenUserName
        var takipEdenUserAdiSoyadi = tumLayout.tvTakipEdenAdiSoyadi
        var istekOnaylaButonu = tumLayout.btnOnayla
        var istekSilButonu = tumLayout.btnSil

        fun setData(oankiBildirim: BildirimModel) {

            idsiVerilenKullanicininBilgileri(oankiBildirim.user_id)
            var mRef = FirebaseDatabase.getInstance().reference
            var mUser = FirebaseAuth.getInstance().currentUser

            istekOnaylaButonu.setOnClickListener {


                mRef.child("takip_istekleri").child(mUser!!.uid).child(oankiBildirim.user_id!!).removeValue()
                Bildirimler.bildirimKaydet(oankiBildirim.user_id!!, Bildirimler.BANA_GELEN_TAKIP_ISTEGINI_SIL)

                mRef.child("following").child(oankiBildirim.user_id!!).child(mUser!!.uid).setValue(mUser!!.uid)
                mRef.child("follower").child(mUser.uid).child(oankiBildirim.user_id!!).setValue(oankiBildirim.user_id)

                Bildirimler.bildirimKaydet(oankiBildirim.user_id!!, Bildirimler.BIRI_BENI_TAKIBE_BASLADI)

                yeniSiralanmisListe.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, tumBildirimler.size)
                //notifydatasetchanged()

            }

            istekSilButonu.setOnClickListener {

                mRef.child("takip_istekleri").child(mUser!!.uid).child(oankiBildirim.user_id!!).removeValue()
                Bildirimler.bildirimKaydet(oankiBildirim.user_id!!, Bildirimler.BANA_GELEN_TAKIP_ISTEGINI_SIL)

                yeniSiralanmisListe.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, tumBildirimler.size)

            }


        }

        private fun idsiVerilenKullanicininBilgileri(user_id: String?) {

            if (user_id != null) {
                FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0!!.getValue() != null) {

                            if (!p0!!.child("user_name").getValue().toString().isNullOrEmpty())
                                takipEdenUserName.setText(p0!!.child("user_name").getValue().toString())

                            if (!p0!!.child("adi_soyadi").getValue().toString().isNullOrEmpty())
                                takipEdenUserAdiSoyadi.setText(p0!!.child("adi_soyadi").getValue().toString())

                            if (!p0!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = p0!!.child("user_detail").child("profile_picture").getValue().toString()
                                UniversalImageLoader.setImage(takipEdenPicURL, takipEdenUserProfileResim, null, "")
                            } else {
                                var takipEdenPicURL = "https://pasamapp.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                UniversalImageLoader.setImage(takipEdenPicURL, takipEdenUserProfileResim, null, "")
                            }

                            takipEdenUserProfileResim.setOnClickListener {

                                var intent=Intent(context,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra("secilenUserID",p0!!.child("user_id").getValue().toString())
                                context.startActivity(intent)

                            }


                        }


                    }

                })
            }

        }


    }

    inner class TakipBasladiViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var tumLayout = itemView as ConstraintLayout
        var takipEdenUserPicture = tumLayout.imgTakipEdenUserPic
        var bildirim = tumLayout.tvBildirimTakipBasladi


        fun setData(oankiBildirim: BildirimModel) {

            if(oankiBildirim.bildirim_tur==Bildirimler.TAKIP_ISTEGI_ONAYLANDI){
                idsiVerilenKullanicininBilgileri(oankiBildirim.user_id, oankiBildirim.time!!,1)
            }else if(oankiBildirim.bildirim_tur==Bildirimler.TAKIP_ETMEYE_BASLADI){
                idsiVerilenKullanicininBilgileri(oankiBildirim.user_id, oankiBildirim.time!!,2)
            }


        }

        private fun idsiVerilenKullanicininBilgileri(user_id: String?, bildirimZamani: Long, kontrol: Int) {

            if (user_id != null) {
                FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0!!.getValue() != null) {

                            if (!p0!!.child("user_name").getValue().toString().isNullOrEmpty()){
                                if(kontrol==1){
                                    bildirim.setText(p0!!.child("user_name").getValue().toString() + " takip isteğinizi onayladı.   " + TimeAgo.getTimeAgoForComments(bildirimZamani))

                                }else if(kontrol==2){
                                    bildirim.setText(p0!!.child("user_name").getValue().toString() + " sizi takip etmeye başladı.   " + TimeAgo.getTimeAgoForComments(bildirimZamani))
                                }
                            }



                            if (!p0!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = p0!!.child("user_detail").child("profile_picture").getValue().toString()
                                UniversalImageLoader.setImage(takipEdenPicURL, takipEdenUserPicture, null, "")
                            } else {
                                var takipEdenPicURL = "https://pasamapp.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                UniversalImageLoader.setImage(takipEdenPicURL, takipEdenUserPicture, null, "")
                            }

                            takipEdenUserPicture.setOnClickListener {
                                var intent=Intent(context,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra("secilenUserID",p0!!.child("user_id").getValue().toString())
                                context.startActivity(intent)
                            }

                        }


                    }

                })
            }

        }


    }

    inner class GonderiBegendiViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var tumLayout = itemView as ConstraintLayout
        var begenenProfilePicture = tumLayout.imgBegenenProfilePicture
        var bildirimGonderibegenildi = tumLayout.tvBildirimGonderiBegen
        var begenilenGonderiPicture = tumLayout.imgBegenilenGonderi


        fun setData(oankiBildirim: BildirimModel) {

            idsiVerilenKullanicininBilgileri(oankiBildirim.user_id, oankiBildirim.gonderi_id, oankiBildirim.time!!)

        }

        private fun idsiVerilenKullanicininBilgileri(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {

            if (user_id != null) {
                FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0!!.getValue() != null) {

                            var userName = p0!!.child("user_name").getValue().toString()
                            if (!p0!!.child("user_name").getValue().toString().isNullOrEmpty())
                                bildirimGonderibegenildi.setText(userName + " fotoğrafını beğendi.  " + TimeAgo.getTimeAgoForComments(bildirimZamani))



                            if (!p0!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = p0!!.child("user_detail").child("profile_picture").getValue().toString()
                                UniversalImageLoader.setImage(takipEdenPicURL, begenenProfilePicture, null, "")
                            } else {
                                var takipEdenPicURL = "https://pasamapp.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                UniversalImageLoader.setImage(takipEdenPicURL, begenenProfilePicture, null, "")
                            }

                            begenenProfilePicture.setOnClickListener {
                                var intent=Intent(context,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra("secilenUserID",p0!!.child("user_id").getValue().toString())
                                context.startActivity(intent)
                            }


                            if (gonderi_id != null) {
                                FirebaseDatabase.getInstance().getReference().child("posts").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(gonderi_id).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                if (p0!!.getValue() != null) {

                                                    var dosyaYolu = p0!!.child("file_url").getValue().toString()
                                                    var dosyaTuru = dosyaYolu!!.substring(dosyaYolu.lastIndexOf("."), dosyaYolu.lastIndexOf(".") + 4)

                                                    if (dosyaTuru.equals(".mp4")) {
                                                        bildirimGonderibegenildi.setText(userName + " videonu beğendi.  " + TimeAgo.getTimeAgoForComments(bildirimZamani))
                                                        begenilenGonderiPicture.visibility = View.GONE

                                                    } else {

                                                        if (!p0!!.child("file_url").getValue().toString().isNullOrEmpty()) {
                                                            begenilenGonderiPicture.visibility = View.VISIBLE
                                                            var begenilenFotoURL = p0!!.child("file_url").getValue().toString()
                                                            UniversalImageLoader.setImage(begenilenFotoURL, begenilenGonderiPicture, null, "")
                                                        } else {
                                                            begenilenGonderiPicture.visibility = View.VISIBLE
                                                            var begenilenFotoURL = "https://pasamapp.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                                                            UniversalImageLoader.setImage(begenilenFotoURL, begenilenGonderiPicture, null, "")
                                                        }

                                                    }


                                                }
                                            }


                                        })
                            }


                        }


                    }

                })
            }

        }
    }


}