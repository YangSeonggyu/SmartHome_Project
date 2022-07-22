package com.seong.smarthome

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.seong.smarthome.Fragment.LivingroomFragment
import com.seong.smarthome.databinding.CctvDialogBinding

//class CCTVDialog(context: Context) : Dialog(context){
//    private lateinit var binding: CctvDialogBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = CctvDialogBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        showDia()
//    }
//    private fun showDia() = with(binding){
//        setCancelable(false)
//
//        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        yesButton.setOnClickListener {
//            dismiss()
//        }
//    }
//}

class CCTVDialog(context: Context){
    private val dialog = Dialog(context)

    fun showDia(){
        dialog.setContentView(R.layout.cctv_dialog)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        val cctvweb = dialog.findViewById<WebView>(R.id.cctv_webview)
        val yesbutton = dialog.findViewById<Button>(R.id.yesButton)
        yesbutton.setOnClickListener { dialog.dismiss() }
        cctvweb.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
        }
        cctvweb.loadUrl("http://192.168.0.111:8081/")
        dialog.show()
    }
    interface ButtonClickListener{
        fun onClicked(text: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickListener(listener: ButtonClickListener){
        onClickListener = listener
    }
}