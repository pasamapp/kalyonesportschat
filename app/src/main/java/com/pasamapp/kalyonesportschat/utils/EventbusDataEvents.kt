package com.pasamapp.kalyonesportschat.utils

import com.pasamapp.kalyonesportschat.Models.UserPosts
import com.pasamapp.kalyonesportschat.Models.Users

/**
 * Created by Emre on 1.05.2018.
 */

class EventbusDataEvents {

    internal class KayitBilgileriniGonder(var telNo:String?, var email:String?, var verificationID:String?, var code:String?, var emailkayit:Boolean )

    internal class KullaniciBilgileriniGonder(var kullanici:Users?)

    internal class PaylasilacakResmiGonder(var dosyaYolu:String?, var dosyaTuruResimMi:Boolean?)

    internal class GalerySecilenDosyaYolunuGonder(var dosyaYolu:String?)

    internal class KameraIzinBilgisiGonder(var kameraIzniVerildiMi: Boolean?)

    internal class YorumYapilacakGonderininIDsiniGonder(var gonderiID:String?)

    internal class SecilenGonderiyiGonder(var secilenGonderi:UserPosts?,var videoMu:Boolean?)

}
