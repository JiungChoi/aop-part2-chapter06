package jiung.fastcampus.aop.part2.chapter06

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val remainMinutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.remainMinutesTextView)
    }
    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById<TextView>(R.id.remainSecondsTextView)
    }

    private val soundPool = SoundPool.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            @SuppressLint("AppCompatCustomView")
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    //remainMinutesTextView.text = "%02d".format(progress)
                    if (fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    if (seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                    remainSecondsTextView.text = "00"
                }
            }
        )
    }

    private fun createCountDownTimer(initialMillis: Int): android.os.CountDownTimer {
        return object : android.os.CountDownTimer(initialMillis.toLong(), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }
    }

    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000)
        currentCountDownTimer?.start()

        tickingSoundId?.let {
            soundPool.play(it, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let {
            soundPool.play(it, 1F, 1F, 0, 0, 1F)
        }
    }

    private fun initSounds() {
        tickingSoundId = soundPool.load(
            this,
            R.raw.sunset,
            1
        ) // 프로젝트 내에 raw 폴더 내부에 있는 것을 가져옴, priority 나중에 호환성을 위해 값 1을 넣는 것을 추천
        bellSoundId = soundPool.load(this, R.raw.sunset, 1)
    }

    private fun updateRemainTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}