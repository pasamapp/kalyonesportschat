package com.pasamapp.kalyonesportschat.Share


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pasamapp.kalyonesportschat.Home.HomeActivity
import com.pasamapp.kalyonesportschat.Models.Posts

import com.pasamapp.kalyonesportschat.R
import com.pasamapp.kalyonesportschat.utils.DosyaIslemleri
import com.pasamapp.kalyonesportschat.utils.EventbusDataEvents
import com.pasamapp.kalyonesportschat.utils.UniversalImageLoader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ShareNextFragment : Fragment() {

    var secilenDosyaYolu: String? = null
    var dosyaTuruResimMi: Boolean? = null
    lateinit var photoURI: Uri

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var mStorageReference: StorageReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_share_next, container, false)

        UniversalImageLoader.setImage(secilenDosyaYolu!!, view!!.imgSecilenResim, null, "file://")

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        mStorageReference = FirebaseStorage.getInstance().reference


        view.tvIleriButton.setOnClickListener {

            //resim dosyasını sıkıstır
            if (dosyaTuruResimMi == true) {

                DosyaIslemleri.compressResimDosya(this, secilenDosyaYolu)

            }
            //video dosyasını sıkıstır
            else if (dosyaTuruResimMi == false) {

                DosyaIslemleri.compressVideoDosya(this, secilenDosyaYolu!!)

            }


        }

        view.imgClose.setOnClickListener {

            this.activity!!.onBackPressed()

        }

        return view
    }

    private fun veritabaninaBilgileriYaz(yuklenenFileUrl: String) {

        var postID = mRef.child("posts").child(mUser.uid).push().key
        var yuklenenPost = Posts(mUser.uid, postID, 0, etPostAciklama.text.toString(), yuklenenFileUrl)


        if (postID != null) {
            mRef.child("posts").child(mUser.uid).child(postID).setValue(yuklenenPost)
        }
        if (postID != null) {
            mRef.child("posts").child(mUser.uid).child(postID).child("yuklenme_tarih").setValue(ServerValue.TIMESTAMP)
        } //2424564564

        //gönderi açıklamasını yorum düğümüne ekleyelim
        if (!etPostAciklama.text.toString().isNullOrEmpty()) {

            //var yorumKey=mRef.child("comments").child(postID).push().key
            if (postID != null) {
                mRef.child("comments").child(postID).child(postID).child("user_id").setValue(mUser.uid)
            }
            if (postID != null) {
                mRef.child("comments").child(postID).child(postID).child("yorum_tarih").setValue(ServerValue.TIMESTAMP)
            }
            if (postID != null) {
                mRef.child("comments").child(postID).child(postID).child("yorum").setValue(etPostAciklama.text.toString())
            }
            if (postID != null) {
                mRef.child("comments").child(postID).child(postID).child("yorum_begeni").setValue("0")
            }

        }

        postSayisiniGuncelle()


        var intent = Intent(activity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)

    }

    private fun postSayisiniGuncelle() {
        mRef.child("users").child(mUser.uid).child("user_detail").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var oankiGonderiSayisi = p0!!.child("post").getValue().toString().toInt()
                oankiGonderiSayisi++
                mRef.child("users").child(mUser.uid).child("user_detail").child("post").setValue(oankiGonderiSayisi.toString())
            }

        })
    }


    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onSecilenDosyaEvent(secilenResim: EventbusDataEvents.PaylasilacakResmiGonder) {
        secilenDosyaYolu = secilenResim!!.dosyaYolu!!
        dosyaTuruResimMi = secilenResim!!.dosyaTuruResimMi
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    fun uploadStorage(filePath: String?) {

        var fileUri = Uri.parse("file://" + filePath)

        var dialogYukleniyor = CompressandLoadingFragment()

        dialogYukleniyor.show(activity!!.supportFragmentManager, "compressLoadingFragmenti")
        dialogYukleniyor.isCancelable = false


        val ref = mStorageReference.child("users").child(mUser.uid).child(fileUri.lastPathSegment)
        var uploadTask = ref.putFile(fileUri)

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
                dialogYukleniyor.dismiss()
                veritabaninaBilgileriYaz(downloadUri.toString())
            } else {
                dialogYukleniyor.dismiss()
                Toast.makeText(activity, "Hata oluştu", Toast.LENGTH_SHORT).show()
            }
        }

        uploadTask.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
            override fun onProgress(p0: UploadTask.TaskSnapshot) {
                var progress = 100.0 * p0!!.bytesTransferred / p0!!.totalByteCount
                //Log.e("HATA", "ILERLEME : " + progress)
                dialogYukleniyor.tvBilgi.text = "%" + progress.toInt().toString() + " yüklendi.."

            }


        })


    }


}
