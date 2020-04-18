package com.pasamapp.kalyonesportschat.Search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import com.pasamapp.kalyonesportschat.Login.LoginActivity
import com.pasamapp.kalyonesportschat.Models.Posts
import com.pasamapp.kalyonesportschat.Models.UserPosts
import com.pasamapp.kalyonesportschat.Models.Users
import com.pasamapp.kalyonesportschat.R
import com.pasamapp.kalyonesportschat.utils.BottomnavigationViewHelper
import com.pasamapp.kalyonesportschat.utils.SearchStaggeredRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.otaliastudios.cameraview.Grid
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private val ACTIVITY_NO=1
    private val TAG="SearchActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var takipEttigimUserIDleri = ArrayList<String>()
    var gosterilecekTumGonderiler=ArrayList<UserPosts>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()

        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {

                var backStacktekiElemanSayisi= supportFragmentManager.backStackEntryCount
                if(backStacktekiElemanSayisi==0){
                    //Log.e("MMM","Back stackte eleman yok")
                    tumLayout.visibility= View.VISIBLE
                    frameLayout.visibility=View.GONE
                }else{
                    tumLayout.visibility=View.GONE
                    frameLayout.visibility=View.VISIBLE
                    //Log.e("MMM","*****************************************")
                    //for(i in 0..backStacktekiElemanSayisi-1)
                        //Log.e("MMM",""+supportFragmentManager.getBackStackEntryAt(i).name)

                }

            }

        })

        takipEttigimUserIDleriGetir()




        searchview.setOnClickListener {

            var intent=Intent(this,AlgoliaSearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)

        }
    }


    private fun takipEttigimUserIDleriGetir() {

        progressBar7.visibility=View.VISIBLE

        var myUserID=FirebaseAuth.getInstance().currentUser!!.uid
        var mRef=FirebaseDatabase.getInstance().reference

        mRef.child("following").child(myUserID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progressBar7.visibility=View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.getValue() != null){

                    for (id in p0!!.children){
                        takipEttigimUserIDleri.add(id.key!!)
                    }
                    //Log.e("kkk","takip ettiğim user idleri "+takipEttigimUserIDleri)

                    if(takipEttigimUserIDleri.size>0){
                        takipEttikleriminTakipEttigiKisiIDleriniGetir()
                    }else{
                        progressBar7.visibility=View.GONE
                    }


                }else{
                    progressBar7.visibility=View.GONE
                }
            }

        })




    }

    private fun takipEttikleriminTakipEttigiKisiIDleriniGetir() {
        var myUserID=FirebaseAuth.getInstance().currentUser!!.uid
        var mRef=FirebaseDatabase.getInstance().reference
        var toplamTakipciSayisi=takipEttigimUserIDleri.size

        for (i in 0..toplamTakipciSayisi-1){

            mRef.child("following").child(takipEttigimUserIDleri.get(i)).orderByKey().limitToLast(5).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    progressBar7.visibility=View.GONE
                }

                override fun onDataChange(p0: DataSnapshot) {


                   if(p0!!.getValue()!=null){

                       for (id in p0!!.children){
                          if(!takipEttigimUserIDleri.contains(id.key)){

                              takipEttigimUserIDleri.add(id.key!!)

                          }
                          else{
                              //Log.e("kkk","bu zaten listede diye eklenmedi:"+id.key)

                          }
                       }

                       if(i==toplamTakipciSayisi-1){
                           //Log.e("kkk"," i değeri: "+i+"takip edilen user id sayısı:"+takipEttigimUserIDleri.size+" "+takipEttigimUserIDleri)
                           takipEdilenlerinSonGonderileriniGetir()
                       }

                   }else{
                       if(i==toplamTakipciSayisi-1){
                           //Log.e("kkk"," i değeri else kısmı: "+i+"takip edilen user id sayısı:"+takipEttigimUserIDleri.size+" "+takipEttigimUserIDleri)
                           takipEdilenlerinSonGonderileriniGetir()
                       }
                       progressBar7.visibility=View.GONE
                   }
                }

            })



        }
    }

    private fun takipEdilenlerinSonGonderileriniGetir() {
        var myUserID=FirebaseAuth.getInstance().currentUser!!.uid
        var mRef=FirebaseDatabase.getInstance().reference

        if(takipEttigimUserIDleri.contains(myUserID)){
            takipEttigimUserIDleri.remove(myUserID)
        }

        //Log.e("kkk","i maximum değeri :"+(takipEttigimUserIDleri.size-1))


        for (i in 0..takipEttigimUserIDleri.size-1){

        mRef.child("users").child(takipEttigimUserIDleri.get(i)).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progressBar7.visibility=View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0!!.getValue() != null){

                   var okunanUser=p0!!.getValue(Users::class.java)
                    //Log.e("kkk","şuan gönderileri getirilen user name:"+okunanUser!!.user_name)



                   mRef.child("posts").child(takipEttigimUserIDleri.get(i)).orderByChild("yuklenme_tarih").limitToLast(10).addListenerForSingleValueEvent(object : ValueEventListener{
                       override fun onCancelled(p0: DatabaseError) {

                       }

                       override fun onDataChange(p0: DataSnapshot) {
                           if(p0!!.getValue()!=null){


                               for(post in p0!!.children){

                                   var okunanPost=post!!.getValue(Posts::class.java)
                                   var eklenecekUserPost=UserPosts()
                                   eklenecekUserPost.userID=okunanUser!!.user_id
                                   eklenecekUserPost.userName=okunanUser!!.user_name
                                   eklenecekUserPost.userPhotoURL=okunanUser!!.user_detail!!.profile_picture
                                   eklenecekUserPost.postID=okunanPost!!.post_id
                                   eklenecekUserPost.postAciklama=okunanPost!!.aciklama
                                   eklenecekUserPost.postYuklenmeTarih=okunanPost!!.yuklenme_tarih
                                   eklenecekUserPost.postURL=okunanPost!!.file_url

                                   gosterilecekTumGonderiler.add(eklenecekUserPost)
                                   //Log.e("kkk","toplam post sayısı:"+gosterilecekTumGonderiler.size)

                               }
                               //Log.e("kkk","i değeri:"+i)
                               if(i==takipEttigimUserIDleri.size-1){
                                   listeyiHazirla()
                                   //Log.e("kkk","liste hazırlanacak size:"+gosterilecekTumGonderiler)
                               }else if(gosterilecekTumGonderiler.size>=50){
                                   listeyiHazirla()
                               }

                           }else{
                               //Log.e("kkk","i değeri:"+i)
                               if(i==takipEttigimUserIDleri.size-1){
                                   listeyiHazirla()
                                   //Log.e("kkk","liste hazırlanacak else içerdeki size:"+gosterilecekTumGonderiler)
                               }else if(gosterilecekTumGonderiler.size>=50){
                                   listeyiHazirla()
                               }

                           }

                       }

                   })


                }else{

                    progressBar7.visibility=View.GONE
                    if(i==takipEttigimUserIDleri.size-1){
                        listeyiHazirla()
                        //Log.e("kkk","liste hazırlanacak else dışardaki size:"+gosterilecekTumGonderiler)
                    }else if(gosterilecekTumGonderiler.size>=50){
                        listeyiHazirla()
                    }
                }

            }

        })






        }


    }

    private fun listeyiHazirla() {

        if(gosterilecekTumGonderiler.size==0){
            progressBar7.visibility=View.GONE
        }


        var myRecyclerView=recyclerSonGonderiler

        var myLayoutManager= androidx.recyclerview.widget.StaggeredGridLayoutManager(3, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
        myRecyclerView.layoutManager=myLayoutManager

        var myAdapter=SearchStaggeredRecyclerAdapter(gosterilecekTumGonderiler,this)
        myRecyclerView.adapter=myAdapter
        progressBar7.visibility=View.GONE


    }


    override fun onResume() {
        super.onResume()
        setupNavigationView()
    }

    fun setupNavigationView(){

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView,ACTIVITY_NO)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    override fun onBackPressed() {

        if(supportFragmentManager.backStackEntryCount>0){
            tumLayout.visibility=View.GONE
            frameLayout.visibility=View.VISIBLE
            supportFragmentManager.popBackStack()
        }else{
            tumLayout.visibility=View.VISIBLE
            frameLayout.visibility=View.GONE
            super.onBackPressed()
            overridePendingTransition(0,0)
        }


    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    //Log.e("HATA", "Kullanıcı oturum açmamış, HomeActivitydesn")
                    var intent = Intent(this@SearchActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {


                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        //Log.e("HATA", "HomeActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
