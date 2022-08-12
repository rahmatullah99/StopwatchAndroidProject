package focus.timer.stopwatch.clock.pomodoro.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import focus.timer.stopwatch.clock.pomodoro.app.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var isStarted:Boolean = false

    private var time:Double = 0.0
    private lateinit var serviceIntent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)


        mainBinding.apply {

            startStopButton.setOnClickListener {
                if(isStarted){
                    stop()
                }else{
                    start()
                }
            }

            resetButton.setOnClickListener {
                reset()
            }
        }

        serviceIntent = Intent(applicationContext,StopwatchService::class.java)
        registerReceiver(updateTime(), IntentFilter(StopwatchService.UPDATED_TIME))

    }

    fun start(){
        serviceIntent.putExtra(StopwatchService.CURRENT_TIME,time)
        startService(serviceIntent)
        mainBinding.startStopButton.text="Stop"
        isStarted=true

    }

    fun stop(){
        stopService(serviceIntent)
        mainBinding.startStopButton.text="Start"
        isStarted=false
    }

    fun reset(){
        stop()
        time = 0.0
        mainBinding.stopwatchTime.text = getformattedTime(time)
    }

    fun updateTime():BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(StopwatchService.CURRENT_TIME,0.0)
            mainBinding.stopwatchTime.text=getformattedTime(time)
        }
    }

    fun getformattedTime(time:Double): String {
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60

        return String.format("%02d:%02d:%02d",hours,minutes,seconds)
    }

}