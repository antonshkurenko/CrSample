/*
 * Copyright 2018 Anton Shkurenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tonyshkurenko.coroutinesexample.examples

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import io.github.tonyshkurenko.coroutinesexample.R
import io.github.tonyshkurenko.coroutinesexample.SampleCrActivity
import kotlinx.android.synthetic.main.activity_actor.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class ActorActivity : SampleCrActivity() {

    var capacity: Int = RENDEZVOUS

    val send: Boolean
        get() = offer_or_send.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor)

        capacity_button.setOnClickListener {

            val values = arrayOf(
                "RENDEZVOUS", "UNLIMITED", "CONFLATED", "ARRAY (5)"
            )

            AlertDialog.Builder(this)
                .setAdapter(
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        arrayOf(
                            "RENDEZVOUS", "UNLIMITED", "CONFLATED", "ARRAY (5)"
                        )
                    )
                ) { _, pos ->
                    when (pos) {
                        0 -> capacity = RENDEZVOUS
                        1 -> capacity = Channel.UNLIMITED
                        2 -> capacity = Channel.CONFLATED
                        3 -> capacity = 5
                    }

                    capacity_button.text = "Capacity: ${values[pos]}"
                }
                .show()
        }

        fan_in_button.setOnClickListener {

            // kinda simple channel :)
            val sendChan = scope.actor<Int>(capacity = capacity) {

                consumeEach {
                    delay(200L) // slow consumer
                    Timber.d("Consume number: $it")
                }
            }

            val machineGunJobs = List(3) { workerNumber ->
                scope.launch {

                    repeat(5) {

                        val num = it + 10 * workerNumber

                        delay(workerNumber * 100L)
                        Timber.d("Send number: $num from machine gun: $workerNumber")

                        if (send) {
                            sendChan.send(it)
                        } else {
                            sendChan.offer(it).also {
                                Timber.d("Offer: $it")
                            }
                        }
                    }
                }
            }
        }
    }
}
