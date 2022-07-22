package com.seong.smarthome

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

class TempDialog(context: Context, ch: Int){
    val TAG:String = "로그"
    private val dialog = Dialog(context)
    private val datebase = Firebase.database("https://smarthome-4f0f4-default-rtdb.firebaseio.com/")
    private val living_tempRef = datebase.getReference("livingroom_auto_temp")
    private val bed_tempRef = datebase.getReference("bedroom_auto_temp")
    private val kitchen_tempRef = datebase.getReference("kitchen_auto_temp")
    private var temp:Int = 2
    private val ch = ch
    fun showDia(){
        dialog.setContentView(R.layout.temp_dialog)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        Log.d(TAG,"TempDialog - showDia() called")
        val temptext = dialog.findViewById<TextView>(R.id.temp_textview_dialog)
        val subbutton = dialog.findViewById<Button>(R.id.sub_button)
        val addbutton = dialog.findViewById<Button>(R.id.add_button)
        val yesbutton = dialog.findViewById<Button>(R.id.yesButton)
        if(ch == 1){
            living_tempRef.get().addOnSuccessListener {
                temp = it.value.toString().toInt()
                temptext.setText(temp.toString() + "˚C")
                Log.d(TAG,"$temp - showDia() called")
            }.addOnFailureListener {
            }
        }else if(ch == 2){
            bed_tempRef.get().addOnSuccessListener {
                temp = it.value.toString().toInt()
                temptext.setText(temp.toString() + "˚C")
                Log.d(TAG,"$temp - showDia() called")
            }.addOnFailureListener {
            }
        }else{
            kitchen_tempRef.get().addOnSuccessListener {
                temp = it.value.toString().toInt()
                temptext.setText(temp.toString() + "˚C")
                Log.d(TAG,"$temp - showDia() called")
            }.addOnFailureListener {
            }
        }
        Log.d(TAG,"TempDialog - showDia() called")
        subbutton.setOnClickListener {
            Log.d(TAG,"subbutton$temp - showDia() called")
            temp = temp - 1
            temptext.setText(temp.toString() + "˚C")
        }
        addbutton.setOnClickListener {
            Log.d(TAG,"addbutton$temp - showDia() called")
            temp = temp + 1
            temptext.setText(temp.toString() + "˚C")
        }
        yesbutton.setOnClickListener {
            if(ch == 1)
                living_tempRef.setValue(temp.toString())
            else if(ch ==2)
                bed_tempRef.setValue(temp.toString())
            else
                kitchen_tempRef.setValue(temp.toString())
            dialog.dismiss()
        }

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