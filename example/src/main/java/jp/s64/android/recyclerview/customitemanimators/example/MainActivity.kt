package jp.s64.android.recyclerview.customitemanimators.example

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        findViewById<Button>(R.id.button_default)
                .setOnClickListener {
                    startActivity(Intent(this@MainActivity, CustomizableDefaultExampleActivity::class.java))
                }

        findViewById<Button>(R.id.button_liftup)
                .setOnClickListener {
                    startActivity(Intent(this@MainActivity, LiftUpExampleActivity::class.java))
                }

        findViewById<Button>(R.id.button_fade_liftup)
                .setOnClickListener {
                    startActivity(Intent(this@MainActivity, FadeLiftUpExampleActivity::class.java))
                }

        findViewById<Button>(R.id.button_place_fade_liftup)
                .setOnClickListener {
                    startActivity(Intent(this@MainActivity, PlaceAndFadeLiftUpExampleActivity::class.java))
                }
    }

}
