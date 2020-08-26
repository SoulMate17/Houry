package tech.kotelnikov.houry

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private var lastSmokedTime: LocalDateTime? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val infoFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @RequiresApi(Build.VERSION_CODES.O)
    private val toastFormatter = DateTimeFormatter.ofPattern("mm:ss")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start.setOnClickListener {
            when {
                lastSmokedTime == null -> {
                    lastSmokedTime = LocalDateTime.now()
                    setCantSmokeView()
                }
                LocalDateTime.now().hour - lastSmokedTime!!.hour == 0 -> {
                    lastSmokedTime?.let {
                        val minutesLeft = 60 - LocalDateTime.now().minute
                        val secondsLeft = 60 - LocalDateTime.now().second
                        Toast.makeText(
                            this,
                            "Need to wait ${minutesLeft}m:${secondsLeft}s",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    setCantSmokeView()
                }
                else -> {
                    lastSmokedTime = LocalDateTime.now()
                    setCantSmokeView()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWelcomeSmokeView() {
        layout.background = ContextCompat.getDrawable(this, android.R.color.holo_green_light)
        tv_inform.text = resources.getString(R.string.inform_accept)
        tv_last_time.text = resources.getString(R.string.first_message)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCantSmokeView() {
        layout.background = ContextCompat.getDrawable(this, android.R.color.holo_red_light)
        tv_inform.text = resources.getString(R.string.inform_deny)
        tv_last_time.text = "Last time you smoked at ${lastSmokedTime?.format(infoFormatter)}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCanSmokeView() {
        layout.background = ContextCompat.getDrawable(this, android.R.color.holo_green_light)
        tv_inform.text = resources.getString(R.string.inform_accept)
        tv_last_time.text = "Last time you smoked at ${lastSmokedTime?.format(infoFormatter)}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        recoverState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        recoverState()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        saveState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        saveState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveState() {
        val sharedPref = this.getPreferences(MODE_PRIVATE) ?: return
        val milliseconds = lastSmokedTime?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli() ?: 0

        with(sharedPref.edit()) {
            putString(resources.getString(R.string.last_cigarette), "$milliseconds")
            commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun recoverState() {
        val sharedPref = this.getPreferences(MODE_PRIVATE) ?: return

        val millisecondsInString =
            sharedPref.getString(resources.getString(R.string.last_cigarette), "0")
        if (millisecondsInString == "0") {
            setWelcomeSmokeView()
        } else {
            val milliseconds = millisecondsInString!!.toLong()
            lastSmokedTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC
            )

            if (LocalDateTime.now().hour - lastSmokedTime!!.hour == 0) {
                setCantSmokeView()
            } else setCanSmokeView()
        }
    }
}