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
import kotlinx.android.synthetic.main.fragment_kitchen.*

class KitchenFragment : Fragment() {
    val TAG:String = "로그"
    private var kit_led: Boolean = null == true
    private var kit_motor: Boolean = null == true
    private var kit_lcd: Boolean = null == true
    private var kit_auto: Boolean = null == true
    private var kit_temp:String = ""
    private var kit_auto_temp = 1
    private val datebase = Firebase.database("https://smarthome-4f0f4-default-rtdb.firebaseio.com/")
    private val ledRef = datebase.getReference("kitchen_led")
    private val motorRef = datebase.getReference("kitchen_motor")
    private val lcdRef = datebase.getReference("kitchen_lcd")
    private val autoRef = datebase.getReference("kitchen_auto")
    private val tempRef = datebase.getReference("kitchen_temp")
    private val auto_tempRef = datebase.getReference("kitchen_auto_temp")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kitchen, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // db에서 값 가져오기
        Log.d(TAG,"kitchenFragment - onViewCreated() called")
        autoRef.get().addOnSuccessListener {
            kitchen_auto.isOn = it.value as Boolean
            if(kitchen_auto.isOn) {
                kit_auto = true
                kitchen_auto_imageview.setImageResource(R.drawable.auto_on)
            }
            else  {
                kit_auto = false
                kitchen_auto_imageview.setImageResource(R.drawable.auto_off)
            }
        }.addOnFailureListener {
            Log.d(TAG,"kitchenFragment - auto error")
        }
        ledRef.get().addOnSuccessListener {
            kitchen_led.isOn = it.value as Boolean
            if(kitchen_led.isOn)     kitchen_led_imageview.setImageResource(R.drawable.led_on)
            else                        kitchen_led_imageview.setImageResource(R.drawable.led_off)
            Log.d(TAG,"kitchenFragment - led $kit_led")
        }.addOnFailureListener {
            Log.d(TAG,"kitchenFragment - led error")
        }
//        motorRef.get().addOnSuccessListener {
//            kitchen_motor.isOn = it.value as Boolean
//            if(kitchen_motor.isOn)   kitchen_motor_imageview.setImageResource(R.drawable.air)
//            else                        kitchen_motor_imageview.setImageResource(R.drawable.air_off)
//            Log.d(TAG,"kitchenFragment - motor $kit_motor")
//        }.addOnFailureListener {
//            Log.d(TAG,"kitchenFragment - motor error")
//        }
        motorRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kitchen_motor.isOn = snapshot.value as Boolean
                if(kitchen_motor.isOn)   kitchen_motor_imageview.setImageResource(R.drawable.air)
                else                        kitchen_motor_imageview.setImageResource(R.drawable.air_off)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"kitchenFragment - temp error")
            }
        })
        lcdRef.get().addOnSuccessListener {
            kitchen_lcd.isOn = it.value as Boolean
            if(kitchen_lcd.isOn)     kitchen_lcd_imageview.setImageResource(R.drawable.tv_on)
            else                        kitchen_lcd_imageview.setImageResource(R.drawable.tv_off)
            Log.d(TAG,"kitchenFragment - lcd $kit_lcd")
        }.addOnFailureListener {
            Log.d(TAG,"kitchenFragment - lcd error")
        }
        tempRef.get().addOnSuccessListener {
            Log.d(TAG,"kitchenFragment - temp${it.value}")
            kit_temp = it.value.toString()
            if(kit_temp.toInt() >= kit_auto_temp && kit_auto == true){
                motorRef.setValue(true)
            }else if(kit_temp.toInt() < kit_auto_temp && kit_auto == true)
                motorRef.setValue(false)
        }.addOnFailureListener {
            Log.d(TAG,"kitchenFragment - temp error")
        }
        auto_tempRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kit_auto_temp = snapshot.value.toString().toInt()
                if(kit_temp.toInt() >= kit_auto_temp && kit_auto == true){
                    motorRef.setValue(false)
                }else if(kit_temp.toInt() < kit_auto_temp && kit_auto == true)
                    motorRef.setValue(true)
                Log.d(TAG,"auto_temp$kit_auto_temp - onDataChange() called")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"kitchenFragment - temp error")
            }
        })
        Log.d(TAG,"temp - onViewCreated() called")
        tempRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kit_temp = snapshot.value.toString()
                kitchen_motor_temp.setText(kit_temp + "˚")
                if(kit_temp.toInt() >= kit_auto_temp && kit_auto == true){
                    motorRef.setValue(true)
                }else if(kit_temp.toInt() < kit_auto_temp && kit_auto == true)
                    motorRef.setValue(false)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"kitchenFragment - temp error")
            }
        })
        // 스위치 버튼 리스너
        kitchen_led.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                ledRef.setValue(true)
                kitchen_led_imageview.setImageResource(R.drawable.led_on)
            }else {
                ledRef.setValue(false)
                kitchen_led_imageview.setImageResource(R.drawable.led_off)
            }
        }
        kitchen_motor.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                motorRef.setValue(true)
                kitchen_motor_imageview.setImageResource(R.drawable.air)
            }else {
                motorRef.setValue(false)
                kitchen_motor_imageview.setImageResource(R.drawable.air_off)
            }
        }
        kitchen_lcd.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                lcdRef.setValue(true)
                kitchen_lcd_imageview.setImageResource(R.drawable.tv_on)
            }else {
                lcdRef.setValue(false)
                kitchen_lcd_imageview.setImageResource(R.drawable.tv_off)
            }
        }
        kitchen_auto.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                autoRef.setValue(true)
                kit_auto = true
                kitchen_auto_imageview.setImageResource(R.drawable.auto_on)
            }else {
                autoRef.setValue(false)
                kit_auto = false
                kitchen_auto_imageview.setImageResource(R.drawable.auto_off)
            }
        }
        kitchen_motor_temp.setText(kit_temp + "˚")
        // cctv 클릭 이벤트
        kitchen_motor_temp.setOnClickListener(View.OnClickListener {
            Log.d(TAG,"image - onViewCreated() called")
            val dialog = activity?.let { it1 -> TempDialog(it1,3) }
            dialog?.showDia()
        })
        kitchen_cctv.setOnClickListener(View.OnClickListener(){
            val dialog = activity?.let { it1 -> CCTVDialog(it1) }
            dialog?.showDia()
        })
    }
    companion object {
        fun newInstance() : KitchenFragment {
            return KitchenFragment()
        }
    }
}