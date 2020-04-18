package com.pasamapp.kalyonesportschat.Home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.instantsearch.utils.ItemClickSupport
import com.pasamapp.kalyonesportschat.Login.LoginActivity
import com.pasamapp.kalyonesportschat.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_algolis_search_mesaj.*

class AlgolisSearchMesajActivity : AppCompatActivity() {

    private val ALGOLIA_APP_ID = "60M8DCAPLT"
    private val ALGOLIA_SEARCH_API_KEY = "3c832a8c9c865059604c76bc54f44644"
    private val ALGOLIA_INDEX_NAME = "KotlinInstagram"
    lateinit var searcher: Searcher


    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algolis_search_mesaj)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()

        setupAlgoliaSearch()

    }

    private fun setupAlgoliaSearch() {
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        val helper = InstantSearch(this, searcher)
        helper.search()

        imgBack.setOnClickListener {
            onBackPressed()
        }

        listeAramaSonuclari.setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
            override fun onItemClick(recyclerView: androidx.recyclerview.widget.RecyclerView?, position: Int, v: View?) {
                var secilenUserID = listeAramaSonuclari.get(position).getString("user_id")
                //Toast.makeText(this@AlgoliaSearchActivity,"Secilen:"+secilenUserID,Toast.LENGTH_SHORT).show()

                if (!secilenUserID.equals(mAuth.currentUser!!.uid.toString())) {

                    var intent=Intent(this@AlgolisSearchMesajActivity,ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("secilenUserID",secilenUserID)
                    startActivity(intent)

                }
            }

        })
    }


    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    //Log.e("HATA", "Kullanıcı oturum açmamış, HomeActivitydesn")
                    var intent = Intent(this@AlgolisSearchMesajActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
        //Log.e("HATA", "AlgoliaSearchmesajydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}