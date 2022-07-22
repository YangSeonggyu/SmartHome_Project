package com.seong.smarthome.Fragment

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.seong.smarthome.CCTVDialog
import com.seong.smarthome.R
import com.seong.smarthome.TempDialog
import kotlinx.android.synthetic.main.fragment_livingroom.*

class LivingroomFragment : Fragment() {
    val TAG:String = "로그"
    private var living_led: Boolean = null == true
    private var living_motor: Boolean = null == true
    private var living_lcd: Boolean = null == true
    private var living_auto: Boolean = null == true
    private var living_temp:String = ""
    private var living_auto_temp = 1
    private val datebase = Firebase.database("https://smarthome-4f0f4-default-rtdb.firebaseio.com/")
    private val ledRef = datebase.getReference("livingroom_led")
    private val motorRef = datebase.getReference("livingroom_motor")
    private val lcdRef = datebase.getReference("livingroom_lcd")
    private val autoRef = datebase.getReference("livingroom_auto")
    private val tempRef = datebase.getReference("livingroom_temp")
    private val auto_tempRef = datebase.getReference("livingroom_auto_temp")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_livingroom, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // db에서 값 가져오기
        Log.d(TAG,"LivingroomFragment - onViewCreated() called")
        autoRef.get().addOnSuccessListener {
            livingroom_auto.isOn = it.value as Boolean
            if(livingroom_auto.isOn) {
                living_auto = true
                livingroom_auto_imageview.setImageResource(R.drawable.auto_on)
            }
            else  {
                living_auto = false
                livingroom_auto_imageview.setImageResource(R.drawable.auto_off)
            }
        }.addOnFailureListener {
            Log.d(TAG,"LivingroomFragment - auto error")
        }
        ledRef.get().addOnSuccessListener {
            livingroom_led.isOn = it.value as Boolean
            if(livingroom_led.isOn)     livingroom_led_imageview.setImageResource(R.drawable.led_on)
            else                        livingroom_led_imageview.setImageResource(R.drawable.led_off)
            Log.d(TAG,"LivingroomFragment - led $living_led")
        }.addOnFailureListener {
            Log.d(TAG,"LivingroomFragment - led error")
        }
//        motorRef.get().addOnSuccessListener {
//            livingroom_motor.isOn = it.value as Boolean
//            if(livingroom_motor.isOn)   livingroom_motor_imageview.setImageResource(R.drawable.air)
//            else                        livingroom_motor_imageview.setImageResource(R.drawable.air_off)
//            Log.d(TAG,"LivingroomFragment - motor $living_motor")
//        }.addOnFailureListener {
//            Log.d(TAG,"LivingroomFragment - motor error")
//        }
        motorRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                livingroom_motor.isOn = snapshot.value as Boolean
                if(livingroom_motor.isOn)   livingroom_motor_imageview.setImageResource(R.drawable.air)
                else                        livingroom_motor_imageview.setImageResource(R.drawable.air_off)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"LivingroomFragment - temp error")
            }
        })
        lcdRef.get().addOnSuccessListener {
            livingroom_lcd.isOn = it.value as Boolean
            if(livingroom_lcd.isOn)     livingroom_lcd_imageview.setImageResource(R.drawable.tv_on)
            else                        livingroom_lcd_imageview.setImageResource(R.drawable.tv_off)
            Log.d(TAG,"LivingroomFragment - lcd $living_lcd")
        }.addOnFailureListener {
            Log.d(TAG,"LivingroomFragment - lcd error")
        }
        tempRef.get().addOnSuccessListener {
            Log.d(TAG,"LivingroomFragment - temp${it.value}")
            living_temp = it.value.toString()
            if(living_temp.toInt() >= living_auto_temp && living_auto == true){
                motorRef.setValue(true)
            }else if(living_temp.toInt() < living_auto_temp && living_auto == true)
                motorRef.setValue(false)
        }.addOnFailureListener {
            Log.d(TAG,"LivingroomFragment - temp error")
        }
        auto_tempRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                living_auto_temp = snapshot.value.toString().toInt()
                if(living_temp.toInt() >= living_auto_temp && living_auto == true){
                    motorRef.setValue(false)
                }else if(living_temp.toInt() < living_auto_temp && living_auto == true)
                    motorRef.setValue(true)
                Log.d(TAG,"auto_temp$living_auto_temp - onDataChange() called")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"LivingroomFragment - temp error")
            }
        })
        Log.d(TAG,"temp - onViewCreated() called")
        tempRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                living_temp = snapshot.value.toString()
                livingroom_motor_temp.setText(living_temp + "˚")
                if(living_temp.toInt() >= living_auto_temp && living_auto == true){
                    motorRef.setValue(true)
                }else if(living_temp.toInt() < living_auto_temp && living_auto == true)
                    motorRef.setValue(false)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"LivingroomFragment - temp error")
            }
        })
        // 스위치 버튼 리스너
        livingroom_led.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                ledRef.setValue(true)
                livingroom_led_imageview.setImageResource(R.drawable.led_on)
            }else {
                ledRef.setValue(false)
                livingroom_led_imageview.setImageResource(R.drawable.led_off)
            }
        }
        livingroom_motor.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                motorRef.setValue(true)
                livingroom_motor_imageview.setImageResource(R.drawable.air)
            }else {
                motorRef.setValue(false)
                livingroom_motor_imageview.setImageResource(R.drawable.air_off)
            }
        }
        livingroom_lcd.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                lcdRef.setValue(true)
                livingroom_lcd_imageview.setImageResource(R.drawable.tv_on)
            }else {
                lcdRef.setValue(false)
                livingroom_lcd_imageview.setImageResource(R.drawable.tv_off)
            }
        }
        livingroom_auto.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                autoRef.setValue(true)
                living_auto = true
                livingroom_auto_imageview.setImageResource(R.drawable.auto_on)
            }else {
                autoRef.setValue(false)
                living_auto = false
                livingroom_auto_imageview.setImageResource(R.drawable.auto_off)
            }
        }
        livingroom_motor_temp.setText(living_temp + "˚")
        // cctv 클릭 이벤트
        livingroom_motor_temp.setOnClickListener(View.OnClickListener {
            Log.d(TAG,"image - onViewCreated() called")
            val dialog = activity?.let { it1 -> TempDialog(it1,1) }
            dialog?.showDia()
        })
        livingroom_cctv.setOnClickListener(View.OnClickListener(){
            val dialog = activity?.let { it1 -> CCTVDialog(it1) }
            dialog?.showDia()
        })
    }
    companion object {
        fun newInstance() : LivingroomFragment {
            return LivingroomFragment()
        }
    }
}