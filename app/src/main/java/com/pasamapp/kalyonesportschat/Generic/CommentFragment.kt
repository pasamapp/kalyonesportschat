package com.pasamapp.kalyonesportschat.Generic


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasamapp.kalyonesportschat.Models.Comments
import com.pasamapp.kalyonesportschat.Models.Users
import com.pasamapp.kalyonesportschat.Profile.ProfileActivity

import com.pasamapp.kalyonesportschat.R
import com.pasamapp.kalyonesportschat.utils.EventbusDataEvents
import com.pasamapp.kalyonesportschat.utils.TimeAgo
import com.pasamapp.kalyonesportschat.utils.UniversalImageLoader
import com.firebase.ui.auth.data.model.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hendraanggrian.widget.Mention
import com.hendraanggrian.widget.MentionAdapter
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_comment.view.*
import kotlinx.android.synthetic.main.tek_satir_comment_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class CommentFragment : Fragment() {

    var yorumYapilacakGonderininID:String?=null
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var myAdapter: FirebaseRecyclerAdapter<Comments,CommentViewHolder>
    lateinit var fragmentView:View



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fragmentView =inflater.inflate(R.layout.fragment_comment, container, false)

        mAuth= FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!

        setupCommentsRecyclerview()

        setupProfilPicture()

        fragmentView.tvYorumGonderButton.setOnClickListener {

         //   var yeniYorum=Comments(mUser.uid,etYorum.text.toString(),"0",0)
            var yeniYorum= hashMapOf<String,Any>("user_id" to mUser.uid,
                    "yorum" to etMesaj.text.toString(), "yorum_begeni" to "0", "yorum_tarih" to ServerValue.TIMESTAMP)

            FirebaseDatabase.getInstance().getReference().child("comments").child(yorumYapilacakGonderininID!!).push().setValue(yeniYorum)


            etMesaj.setText("")

            fragmentView.yorumlarRecyclerView.smoothScrollToPosition(fragmentView.yorumlarRecyclerView.adapter!!.itemCount)




        }

        fragmentView.imgClose.setOnClickListener {

            activity!!.onBackPressed()
        }

        var myMentionAdapter=MentionAdapter(activity!!)

        fragmentView.etMesaj.setMentionTextChangedListener { view, s ->

            FirebaseDatabase.getInstance().getReference().child("users").orderByChild("user_name").startAt(s).endAt(s+"\uf8ff")
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0!!.getValue() != null){

                                for (user in p0!!.children){

                                    myMentionAdapter.clear()

                                    var okunanuser=user.getValue(Users::class.java)
                                    var userName=okunanuser!!.user_name.toString()
                                    var adiSoyadi=okunanuser!!.adi_soyadi.toString()
                                    var photo=okunanuser!!.user_detail!!.profile_picture!!
                                    var profilPicture=if(!photo.isNullOrEmpty()) photo else "https://pasamapp.com/wp-content/uploads/2016/10/apple-icon-72x72.png"

                                    myMentionAdapter.add(Mention(userName,adiSoyadi,profilPicture))


                                }



                            }
                        }


                    })
            //emre06
            //hasoo
            //Ironma


        }
        fragmentView.etMesaj.mentionAdapter=myMentionAdapter



        return fragmentView
    }



    private fun setupCommentsRecyclerview() {
        mRef=FirebaseDatabase.getInstance().reference.child("comments").child(yorumYapilacakGonderininID!!)


        val options = FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(mRef, Comments::class.java)
                .build()

        myAdapter=object : FirebaseRecyclerAdapter<Comments,CommentViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {


                /*
                var layoutService=LayoutInflater.from(parent!!.context)
                layoutService.inflate()
                */
                var commentViewHolder=LayoutInflater.from(parent.context).inflate(R.layout.tek_satir_comment_item, parent, false)

                return CommentViewHolder(commentViewHolder)
            }

            override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comments) {
                holder.setData(model)

                //Log.e("HATA","YORUM YAPILACAK FOTO:"+yorumYapilacakGonderininID)
                //Log.e("HATA","yorum ıd:"+getRef(0).key)
                //ilk yorum foto paylasırken yapılan acıklama ise begen iconu kaldırılır
                if(position==0 && (yorumYapilacakGonderininID!!.equals(getRef(0).key))){
                    holder.yorumBegen.visibility=View.INVISIBLE
                }

                holder.setBegenOlayi(yorumYapilacakGonderininID, getRef(position).key)

                holder.setBegenmeDurumu(yorumYapilacakGonderininID, getRef(position).key)


            }

        }

        fragmentView.yorumlarRecyclerView.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(activity, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        fragmentView.yorumlarRecyclerView.adapter=myAdapter






    }

    private fun setupProfilPicture() {
        mRef=FirebaseDatabase.getInstance().reference.child("users")
                mRef.child(mUser.uid).child("user_detail").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var profilPictureURL=p0!!.child("profile_picture").getValue().toString()
                        UniversalImageLoader.setImage(profilPictureURL,circleImageView,null,"")
                    }

                })
    }

    class CommentViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tumCommentLayoutu=itemView as ConstraintLayout
        var yorumYapanUserPhoto=tumCommentLayoutu.yorumYapanUserProfile
        var kullaniciAdiveYorum=tumCommentLayoutu.tvUsernameAndYorum
        var yorumBegen=tumCommentLayoutu.imgBegen
        var yorumSure=tumCommentLayoutu.tvYorumSure
        var yorumBegenmeSayisi=tumCommentLayoutu.tvBegenmeSayisi

        fun setData(oanOlusturulanYorum: Comments) {


            yorumSure.setText(TimeAgo.getTimeAgoForComments(oanOlusturulanYorum!!.yorum_tarih!!))
            yorumBegenmeSayisi.setText(oanOlusturulanYorum.yorum_begeni)

            kullaniciBilgileriniGetir(oanOlusturulanYorum.user_id, oanOlusturulanYorum.yorum)


        }

        private fun kullaniciBilgileriniGetir(user_id: String?, yorum: String?) {

            var mRef=FirebaseDatabase.getInstance().reference
            mRef.child("users").child(user_id!!).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userNameveYorum="<font color=#000>"+ p0!!.getValue(Users::class.java)!!.user_name!!.toString()+"</font>" + " " + yorum
                    var sonuc:Spanned?=null
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        sonuc=Html.fromHtml(userNameveYorum,Html.FROM_HTML_MODE_LEGACY)
                    }else {
                        sonuc=Html.fromHtml(userNameveYorum)
                    }
                    kullaniciAdiveYorum.setText(sonuc)

                    kullaniciAdiveYorum.setOnMentionClickListener { view, s ->

                        var tiklanilanUserName = s

                        var mRef=FirebaseDatabase.getInstance().reference
                        mRef.child("users").orderByChild("user_name").equalTo(s).limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if(p0!!.getValue() != null){

                                    for(ds in p0!!.children){
                                        var bulunanUserID = ds!!.getValue(Users::class.java)!!.user_id


                                        var oturumAcmisUserID=FirebaseAuth.getInstance().currentUser!!.uid

                                        if(!bulunanUserID.equals(oturumAcmisUserID)){
                                            var intent= Intent(itemView.context,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                            intent.putExtra("secilenUserID", bulunanUserID)
                                            itemView.context.startActivity(intent)
                                        }else {

                                            var intent=Intent(itemView.context, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                            itemView.context.startActivity(intent)
                                        }
                                    }



                                }else {
                                    //Log.e("HATA","KULLANICI BULUNAMADI")
                                }
                            }

                        })


                    }



                    UniversalImageLoader.setImage(p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture!!.toString(),yorumYapanUserPhoto
                    ,null,"")
                }


            })

        }

        fun setBegenOlayi(yorumYapilacakGonderininID: String?, begenilecekYorumID: String?) {

           var mRef=FirebaseDatabase.getInstance().reference.child("comments").child(yorumYapilacakGonderininID!!).child(begenilecekYorumID!!)

           yorumBegen.setOnClickListener {


            mRef.child("begenenler").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                   if(p0!!.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){

                       mRef.child("begenenler").child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                       yorumBegen.setImageResource(R.drawable.ic_like)

                   }else {
                       mRef.child("begenenler").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                       yorumBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                   }
                }


            })



           }




        }

        fun setBegenmeDurumu(yorumYapilacakGonderininID: String?, begenilecekYorumID: String?) {
            var mRef=FirebaseDatabase.getInstance().reference.child("comments").child(yorumYapilacakGonderininID!!).child(begenilecekYorumID!!)

            mRef.child("begenenler").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if(p0!!.exists()){

                        yorumBegenmeSayisi.visibility=View.VISIBLE
                        yorumBegenmeSayisi.text=p0!!.childrenCount.toString()+" beğenme"
                    }else {
                        yorumBegenmeSayisi.visibility=View.INVISIBLE
                    }

                    if(p0!!.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){

                        yorumBegen.setImageResource(R.drawable.ic_begen_kirmizi)

                    }else {

                        yorumBegen.setImageResource(R.drawable.ic_like)
                    }
                }


            })
        }


    }

    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onYorumYapilacakGonderi(gonderi: EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder) {
        yorumYapilacakGonderininID = gonderi!!.gonderiID!!

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }



}
