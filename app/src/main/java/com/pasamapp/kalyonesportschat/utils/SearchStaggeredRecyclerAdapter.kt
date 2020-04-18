package com.pasamapp.kalyonesportschat.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasamapp.kalyonesportschat.Models.UserPosts
import com.pasamapp.kalyonesportschat.R
import android.os.Build
import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.pasamapp.kalyonesportschat.Generic.TekGonderiSearchFragment
import com.pasamapp.kalyonesportschat.Search.SearchActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.tek_sutun_staggered_resim_profil.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * Created by Emre on 3.06.2018.
 */
class SearchStaggeredRecyclerAdapter(var kullaniciPostlari:ArrayList<UserPosts>, var myContext:Context): androidx.recyclerview.widget.RecyclerView.Adapter<SearchStaggeredRecyclerAdapter.MyViewHolder>() {

    lateinit var inflater:LayoutInflater

    init {
        Collections.sort(kullaniciPostlari, object : Comparator<UserPosts> {
            override fun compare(o1: UserPosts?, o2: UserPosts?): Int {
                if (o1!!.postYuklenmeTarih!! > o2!!.postYuklenmeTarih!!) {
                    return -1
                } else return 1
            }
        })

    }

    init {
        inflater= LayoutInflater.from(myContext)
    }

    override fun getItemCount(): Int {

        return kullaniciPostlari.size

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            var tekSutunDosya=inflater.inflate(R.layout.tek_sutun_staggered_resim_profil, parent, false)
            return MyViewHolder(tekSutunDosya)

    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var dosyaYolu= kullaniciPostlari.get(position).postURL
        var noktaninGectigiIndex=dosyaYolu!!.lastIndexOf(".")
        var dosyaTuru=dosyaYolu!!.substring(noktaninGectigiIndex, noktaninGectigiIndex+4)


        if(dosyaTuru.equals(".mp4")){
            //Log.e("DOSYA TURU","DOSYA TURU"+ dosyaTuru)
            holder.videoIcon.visibility=View.VISIBLE

            VideodanThumbOlustur(holder).execute(dosyaYolu)

            holder.setData(kullaniciPostlari.get(position), true)

        }else {
            //Log.e("DOSYA TURU","DOSYA TURU"+ dosyaTuru)
            holder.videoIcon.visibility=View.GONE
            holder.dosyaProgressBar.visibility=View.VISIBLE
            UniversalImageLoader.setImage(dosyaYolu!!, holder.dosyaResim, holder.dosyaProgressBar,"")
            holder.setData(kullaniciPostlari.get(position), false)
        }

    }

    class VideodanThumbOlustur(var holder: MyViewHolder) : AsyncTask<String,Void,Bitmap>(){

        override fun onPreExecute() {
            super.onPreExecute()

            holder.dosyaProgressBar.visibility=View.VISIBLE
        }

        override fun doInBackground(vararg p0: String?): Bitmap? {

            if(p0[0] != null){
                var videoPath=p0[0]

                var bitmap: Bitmap? = null
                var mediaMetadataRetriever: MediaMetadataRetriever? = null
                try {
                    mediaMetadataRetriever = MediaMetadataRetriever()
                    if (Build.VERSION.SDK_INT >= 14)
                        mediaMetadataRetriever.setDataSource(videoPath, HashMap())
                    else
                        mediaMetadataRetriever.setDataSource(videoPath)

                    bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw Throwable(
                            "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)

                } finally {
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release()
                    }else{
                        return null
                    }
                }
                if (bitmap != null)
                return bitmap
                else return null
            }

            return null

        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            holder.dosyaProgressBar.visibility=View.GONE
            if(result != null)
            holder.dosyaResim.setImageBitmap(result)
        }

    }



     inner  class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {


        var tekSutunDosya=itemView as androidx.cardview.widget.CardView
        var videoIcon=tekSutunDosya.imgVideoIcon
        var dosyaResim=tekSutunDosya.imgTekSutunImage
        var dosyaProgressBar=tekSutunDosya.progressBar


        fun setData(oankiGonderi: UserPosts, videoMu: Boolean) {

            //bu kısım calısıyorsa bu adapter begendigim gönderiler için veya profilesetting activityde kullanılmıstır
            tekSutunDosya.setOnClickListener {
             if(myContext is SearchActivity){

                    (myContext as SearchActivity).tumLayout.visibility= View.GONE
                    (myContext as SearchActivity).frameLayout.visibility=View.VISIBLE
                    EventBus.getDefault().postSticky(EventbusDataEvents.SecilenGonderiyiGonder(oankiGonderi, videoMu))
                    var transaction=(myContext as SearchActivity).supportFragmentManager.beginTransaction()
                    transaction.add(R.id.frameLayout,TekGonderiSearchFragment(),"fra2")
                    transaction.addToBackStack("TekGonderiSearchFragment")
                    transaction.commit()


                }

            }




        }


    }




}