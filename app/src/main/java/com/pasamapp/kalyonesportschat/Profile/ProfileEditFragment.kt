package com.pasamapp.kalyonesportschat.Profile


import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pasamapp.kalyonesportschat.Models.Users

import com.pasamapp.kalyonesportschat.R
import com.pasamapp.kalyonesportschat.utils.EventbusDataEvents
import com.pasamapp.kalyonesportschat.utils.UniversalImageLoader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {

    lateinit var circleProfileImageFragment: CircleImageView
    lateinit var gelenKullaniciBilgileri: Users
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mStorageRef: StorageReference

    var profilPhotoUri:Uri? = null

    val RESIM_SEC = 100


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_profile_edit, container, false)

        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mStorageRef=FirebaseStorage.getInstance().reference

        setupKullaniciBilgileri(view)


        view.imgClose.setOnClickListener {

            activity!!.onBackPressed()

        }

        view.tvFotografiDegistir.setOnClickListener {

            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, RESIM_SEC)

        }

        view.imgBtnDegisiklikleriKaydet.setOnClickListener {

            if(profilPhotoUri != null){

                var dialogYukleniyor=YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager,"yukleniyorFragmenti")
                dialogYukleniyor.isCancelable=false




                val ref = mStorageRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child(profilPhotoUri!!.lastPathSegment)
                var uploadTask = ref.putFile(profilPhotoUri!!)

                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("profile_picture")
                                .setValue(downloadUri.toString())

                        dialogYukleniyor.dismiss()
                        kullaniciAdiniGuncelle(view,true)
                    } else {
                        //Log.e("HATA", p0!!.message)
                        kullaniciAdiniGuncelle(view,false)
                    }
                }


            }else {
                kullaniciAdiniGuncelle(view,null)
            }

        }


        return view
    }

    //profilresmi degisti
    //true : basarılı bir şekilde resim storage yüklenmiş ve veritabanına yazılmıstır
    //false : resim yüklenirken hata olusmustur
    //null : kullanıcı resmini değiştirmek istememiştir
    private fun kullaniciAdiniGuncelle(view: View, profilResmiDegisti: Boolean?) {

        if(!gelenKullaniciBilgileri!!.user_name!!.equals(view.etUserName.text.toString())){
            if(view.etUserName.text.toString().trim().length>5){
                mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        var userNameKullanimdaMi=false

                        for(ds in p0!!.children){

                            var okunanKullaniciAdi=ds!!.getValue(Users::class.java)!!.user_name

                            if(okunanKullaniciAdi!!.equals(view.etUserName.text.toString())){
                                userNameKullanimdaMi=true
                                profilBilgileriniGuncelle(view, profilResmiDegisti,false)
                                break
                            }


                        }

                        if(userNameKullanimdaMi==false){
                            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_name").setValue(view.etUserName.text.toString())
                            profilBilgileriniGuncelle(view, profilResmiDegisti,true)

                        }

                    }


                })
            }else {
                Toast.makeText(activity,"Kullanıcı adı en az 6 karakter olmalıdır",Toast.LENGTH_SHORT).show()
            }





        }else {
            profilBilgileriniGuncelle(view, profilResmiDegisti,null)
        }

    }

    private fun profilBilgileriniGuncelle(view: View, profilResmiDegisti: Boolean?, userNameDegisti: Boolean?) {

        var profilGuncellendiMi:Boolean?=null

        if (!gelenKullaniciBilgileri!!.adi_soyadi!!.equals(view.etProfileName.text.toString())) {
            if(!view.etProfileName.text.toString().trim().isNullOrEmpty()){
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("adi_soyadi").setValue(view.etProfileName.text.toString())
                profilGuncellendiMi=true
            }else{
                Toast.makeText(activity,"Ad Soyad boş olamaz",Toast.LENGTH_SHORT).show()
            }

        }

        if(!gelenKullaniciBilgileri!!.user_detail!!.biography!!.equals(view.etUserBio.text.toString())){
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("biography").setValue(view.etUserBio.text.toString())
            profilGuncellendiMi=true
        }

        if(!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.equals(view.etUserWebSite.text.toString())){
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("web_site").setValue(view.etUserWebSite.text.toString())
            profilGuncellendiMi=true
        }

        if(profilResmiDegisti == null && userNameDegisti == null && profilGuncellendiMi == null){
            Toast.makeText(activity,"Değişiklik Yok",Toast.LENGTH_SHORT).show()
        }
        else if(userNameDegisti== false && (profilGuncellendiMi==true || profilResmiDegisti==true)){
            Toast.makeText(activity,"Bilgiler güncelledi ama kullanıcı adı KULLANIMDA",Toast.LENGTH_SHORT).show()
        }else {

            Toast.makeText(activity,"Kullanıcı Güncellendi",Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()

        }







    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == AppCompatActivity.RESULT_OK && data!!.data != null) {

            profilPhotoUri = data!!.data!!

            circleProfileImage.setImageURI(profilPhotoUri)

        }
    }

    private fun setupKullaniciBilgileri(view: View?) {

        view!!.etProfileName.setText(gelenKullaniciBilgileri!!.adi_soyadi)
        view!!.etUserName.setText(gelenKullaniciBilgileri!!.user_name)

        if (!gelenKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()) {
            view!!.etUserBio.setText(gelenKullaniciBilgileri!!.user_detail!!.biography)
        }
        if (!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()) {
            view!!.etUserWebSite.setText(gelenKullaniciBilgileri!!.user_detail!!.web_site)
        }

        var imgUrl = gelenKullaniciBilgileri!!.user_detail!!.profile_picture
        UniversalImageLoader.setImage(imgUrl!!, view!!.circleProfileImage, view!!.progressBar, "")

    }


    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent(kullaniciBilgileri: EventbusDataEvents.KullaniciBilgileriniGonder) {

        gelenKullaniciBilgileri = kullaniciBilgileri!!.kullanici!!


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}
