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
import kotlinx.android.synthetic.main.fragment_bedroom.*

class BedroomFragment : Fragment() {
    val TAG:String = "로그"
    private var bed_led: Boolean = null == true
    private var bed_motor: Boolean = null == true
    private var bed_lcd: Boolean = null == true
    private var bed_auto: Boolean = null == true
    private var bed_temp:String = ""
    private var bed_auto_temp = 1
    private val datebase = Firebase.database("https://smarthome-4f0f4-default-rtdb.firebaseio.com/")
    private val ledRef = datebase.getReference("bedroom_led")
    private val motorRef = datebase.getReference("bedroom_motor")
    private val lcdRef = datebase.getReference("bedroom_lcd")
    private val autoRef = datebase.getReference("bedroom_auto")
    private val tempRef = datebase.getReference("bedroom_temp")
    private val auto_tempRef = datebase.getReference("bedroom_auto_temp")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bedroom, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // db에서 값 가져오기
        Log.d(TAG,"bedroomFragment - onViewCreated() called")
        autoRef.get().addOnSuccessListener {
            bedroom_auto.isOn = it.value as Boolean
            if(bedroom_auto.isOn) {
                bed_auto = true
                bedroom_auto_imageview.setImageResource(R.drawable.auto_on)
            }
            else  {
                bed_auto = false
                bedroom_auto_imageview.setImageResource(R.drawable.auto_off)
            }
        }.addOnFailureListener {
            Log.d(TAG,"bedroomFragment - auto error")
        }
        ledRef.get().addOnSuccessListener {
            bedroom_led.isOn = it.value as Boolean
            if(bedroom_led.isOn)     bedroom_led_imageview.setImageResource(R.drawable.led_on)
            else                        bedroom_led_imageview.setImageResource(R.drawable.led_off)
            Log.d(TAG,"bedroomFragment - led $bed_led")
        }.addOnFailureListener {
            Log.d(TAG,"bedroomFragment - led error")
        }
//        motorRef.get().addOnSuccessListener {
//            bedroom_motor.isOn = it.value as Boolean
//            if(bedroom_motor.isOn)   bedroom_motor_imageview.setImageResource(R.drawable.air)
//            else                        bedroom_motor_imageview.setImageResource(R.drawable.air_off)
//            Log.d(TAG,"bedroomFragment - motor $bed_motor")
//        }.addOnFailureListener {
//            Log.d(TAG,"bedroomFragment - motor error")
//        }
        motorRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bedroom_motor.isOn = snapshot.value as Boolean
                if(bedroom_motor.isOn)   bedroom_motor_imageview.setImageResource(R.drawable.air)
                else                        bedroom_motor_imageview.setImageResource(R.drawable.air_off)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"bedroomFragment - temp error")
            }
        })
        lcdRef.get().addOnSuccessListener {
            bedroom_lcd.isOn = it.value as Boolean
            if(bedroom_lcd.isOn)     bedroom_lcd_imageview.setImageResource(R.drawable.tv_on)
            else                        bedroom_lcd_imageview.setImageResource(R.drawable.tv_off)
            Log.d(TAG,"bedroomFragment - lcd $bed_lcd")
        }.addOnFailureListener {
            Log.d(TAG,"bedroomFragment - lcd error")
        }
        tempRef.get().addOnSuccessListener {
            Log.d(TAG,"bedroomFragment - temp${it.value}")
            bed_temp = it.value.toString()
            if(bed_temp.toInt() >= bed_auto_temp && bed_auto == true){
                motorRef.setValue(true)
            }else if(bed_temp.toInt() < bed_auto_temp && bed_auto == true)
                motorRef.setValue(false)
        }.addOnFailureListener {
            Log.d(TAG,"bedroomFragment - temp error")
        }
        auto_tempRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bed_auto_temp = snapshot.value.toString().toInt()
                if(bed_temp.toInt() >= bed_auto_temp && bed_auto == true){
                    motorRef.setValue(false)
                }else if(bed_temp.toInt() < bed_auto_temp && bed_auto == true)
                    motorRef.setValue(true)
                Log.d(TAG,"auto_temp$bed_auto_temp - onDataChange() called")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"bedroomFragment - temp error")
            }
        })
        Log.d(TAG,"temp - onViewCreated() called")
        tempRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bed_temp = snapshot.value.toString()
                bedroom_motor_temp.setText(bed_temp + "˚")
                if(bed_temp.toInt() >= bed_auto_temp && bed_auto == true){
                    motorRef.setValue(true)
                }else if(bed_temp.toInt() < bed_auto_temp && bed_auto == true)
                    motorRef.setValue(false)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"bedroomFragment - temp error")
            }
        })
        // 스위치 버튼 리스너
        bedroom_led.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                ledRef.setValue(true)
                bedroom_led_imageview.setImageResource(R.drawable.led_on)
            }else {
                ledRef.setValue(false)
                bedroom_led_imageview.setImageResource(R.drawable.led_off)
            }
        }
        bedroom_motor.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                motorRef.setValue(true)
                bedroom_motor_imageview.setImageResource(R.drawable.air)
            }else {
                motorRef.setValue(false)
                bedroom_motor_imageview.setImageResource(R.drawable.air_off)
            }
        }
        bedroom_lcd.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                lcdRef.setValue(true)
                bedroom_lcd_imageview.setImageResource(R.drawable.tv_on)
            }else {
                lcdRef.setValue(false)
                bedroom_lcd_imageview.setImageResource(R.drawable.tv_off)
            }
        }
        bedroom_auto.setOnToggledListener { toggleableView, isOn ->
            if(isOn) {
                autoRef.setValue(true)
                bed_auto = true
                bedroom_auto_imageview.setImageResource(R.drawable.auto_on)
            }else {
                autoRef.setValue(false)
                bed_auto = false
                bedroom_auto_imageview.setImageResource(R.drawable.auto_off)
            }
        }
        bedroom_motor_temp.setText(bed_temp + "˚")
        // cctv 클릭 이벤트
        bedroom_motor_temp.setOnClickListener(View.OnClickListener {
            Log.d(TAG,"image - onViewCreated() called")
            val dialog = activity?.let { it1 -> TempDialog(it1,2) }
            dialog?.showDia()
        })
        bedroom_cctv.setOnClickListener(View.OnClickListener(){
            val dialog = activity?.let { it1 -> CCTVDialog(it1) }
            dialog?.showDia()
        })
    }
    companion object {
        fun newInstance() : BedroomFragment {
            return BedroomFragment()
        }
    }
}