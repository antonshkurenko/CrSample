package io.github.tonyshkurenko.coroutinesexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import io.github.tonyshkurenko.coroutinesexample.examples.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass

/**
 * todos:
 *
 * todo: enable debug in android
 *
 * todo: https://proandroiddev.com/android-coroutine-recipes-33467a4302e9
 *
 * todo: probably use yield to force exceptions and cancel
 * todo: check memory
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demos = arrayOf(
            Demo("launch", LaunchActivity::class)
            , Demo("async", AsyncActivity::class)
            , Demo("coroutineScope", CrScopeActivity::class)
            , Demo("channels", ChannelsActivity::class)
            , Demo("yield", YieldActivity::class)
            , Demo("produce", ProduceActivity::class)
            , Demo("actor", ActorActivity::class)
            , Demo("select", SelectActivity::class)
            , Demo("real life", RealLifeActivity::class)
        )

        list.apply {
            adapter = ArrayAdapter(
                context, android.R.layout.simple_list_item_1, demos
            )

            setOnItemClickListener { _, _, position, _ ->
                startActivity(Intent(this@MainActivity, demos[position].actClass.java))
            }
        }
    }

    class Demo(val title: String, val actClass: KClass<out Activity>) {
        override fun toString() = title
    }
}
