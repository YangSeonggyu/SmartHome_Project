package com.seong.smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.seong.smarthome.Fragment.BedroomFragment
import com.seong.smarthome.Fragment.KitchenFragment
import com.seong.smarthome.Fragment.LivingroomFragment
import com.seong.smarthome.Weather.ApiObject
import com.seong.smarthome.Weather.ITEM
import com.seong.smarthome.Weather.ModelWeather
import com.seong.smarthome.Weather.WEATHER
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var livingroomFragment: LivingroomFragment
    private lateinit var bedroomFragment: BedroomFragment
    private lateinit var kitchenFragment: KitchenFragment
    private var base_date = "20210510"  // 발표 일자
    private var base_time = "1400"      // 발표 시각
    private var nx = "55"               // 예보지점 X 좌표
    private var ny = "127"              // 예보지점 Y 좌표
    val TAG:String = "로그"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        livingroomFragment = LivingroomFragment.newInstance()
        bedroomFragment = BedroomFragment.newInstance()
        kitchenFragment = KitchenFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_Frame, livingroomFragment).commit()
        main_radio_group.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.livingroom_btn -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Frame, livingroomFragment).commit()
                }
                R.id.bedroom_btn -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Frame, bedroomFragment).commit()
                }
                R.id.kitchen_btn -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Frame, kitchenFragment).commit()
                }
            }
        }
        setWeather(nx, ny)
    }

    private fun setWeather(nx: String, ny: String) {
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시각
        val timeM = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 분
        // API 가져오기 적당하게 변환
        base_time = getBaseTime(timeH, timeM)
        Log.d(TAG,"$base_date - $base_time")
        // 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if (timeH == "00" && base_time == "2330") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }
        val call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, nx, ny)
        Log.d(TAG,"$base_date")
        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    val it: List<ITEM> = response.body()!!.response.body.items.item

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    val weatherArr = arrayOf(ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather())
//                    var index = 0
//                    index %= 6
//                    when(it[0].category){
//                        "PTY" -> weatherArr[index].rainType = it[0].fcstValue     // 강수 형태
//                        "REH" -> weatherArr[index].humidity = it[0].fcstValue     // 습도
//                        "SKY" -> weatherArr[index].sky = it[0].fcstValue          // 하늘 상태
//                        "T1H" -> weatherArr[index].temp = it[0].fcstValue         // 기온
//                        else -> Log.d(TAG,"when called")
//                    }
//                    Log.d(TAG,"${it[0].fcstDate} ${it[0].fcstTime} ${weatherArr[0].temp} ${weatherArr[0].humidity} ${weatherArr[0].sky} ${weatherArr[0].rainType}")
                    // 배열 채우기
                    var index = 0
                    val totalCount = response.body()!!.response.body.totalCount - 1
                    for (i in 0..totalCount) {
                        index %= 6
                        when (it[i].category) {
                            "PTY" -> weatherArr[index].rainType = it[i].fcstValue     // 강수 형태
                            "REH" -> weatherArr[index].humidity = it[i].fcstValue     // 습도
                            "SKY" -> weatherArr[index].sky = it[i].fcstValue          // 하늘 상태
                            "T1H" -> weatherArr[index].temp = it[i].fcstValue         // 기온
                            else -> continue
                        }
                        index++
                    }
                    for (i in 0..5){
                        weatherArr[i].fcstTime = it[i].fcstTime

                        Log.d(TAG,"${it[i].fcstDate} ${it[i].fcstTime} ${weatherArr[i].temp} ${weatherArr[i].humidity} ${weatherArr[i].sky} ${weatherArr[i].rainType}")
                    }
                    temp_text.setText(weatherArr[1].temp + "°C")
                    humidity_text.setText(weatherArr[1].humidity + "%")
                    if(weatherArr[1].sky == "4"){
                        if(weatherArr[1].rainType == "0")
                            animation_view.setAnimation("weather_windy.json")
                        else if(weatherArr[1].rainType == "1")
                            animation_view.setAnimation("weather_blur.json")
                        else if(weatherArr[1].rainType == "2")
                            animation_view.setAnimation("weather_storm.json")
                        else
                            animation_view.setAnimation("weather_snow.json")
                    }else if(weatherArr[1].sky == "3")
                        animation_view.setAnimation("weather_cloud.json")
                    else
                        animation_view.setAnimation("weather_sun.json")
                    animation_view.loop(true)
                    animation_view.playAnimation()
                }
            }
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.d("api fail", t.message.toString())
            }
        })
    }

    private fun getBaseTime(h: String, m: String): String {
        var result = ""

        // 45분 전이면
        if (m.toInt() < 45) {
            // 0시면 2330
            if (h == "00") result = "2330"
            // 아니면 1시간 전 날씨 정보 부르기
            else {
                var resultH = h.toInt() - 1
                // 1자리면 0 붙여서 2자리로 만들기
                if (resultH < 10) result = "0" + resultH + "30"
                // 2자리면 그대로
                else result = resultH.toString() + "30"
            }
        }
        // 45분 이후면 바로 정보 받아오기
        else result = h + "30"

        return result
    }
}