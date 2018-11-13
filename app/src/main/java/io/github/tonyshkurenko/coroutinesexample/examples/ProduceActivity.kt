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
import kotlinx.android.synthetic.main.activity_produce.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class ProduceActivity : SampleCrActivity() {

    var capacity: Int = RENDEZVOUS

    val send: Boolean
        get() = offer_or_send.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produce)

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

        fan_out_button.setOnClickListener {

            // kinda simple channel :)
            val recvChan = scope.produce<Int>(capacity = capacity) {

                repeat(9) {
                    delay(100)
                    Timber.d("Send num $it")
                    if (send) {
                        send(it)
                    } else {
                        offer(it).also {
                            Timber.d("Offer: $it")
                        }
                    }
                }
            }

            val workerJobs = List(3) { workerNumber ->
                scope.launch {
                    recvChan.consumeEach {
                        delay((workerNumber + 1) * 300L)
                        Timber.d("Consumed number: $it in worker: $workerNumber")
                    }
                }
            }
        }

        broadcast_button.setOnClickListener {

            if (capacity == RENDEZVOUS) {

                Timber.w("RENDEZVOUS is unsupported")

                return@setOnClickListener
            }

            val recvChan = scope.broadcast<Int>(capacity = capacity) {
                repeat(9) {
                    delay(100)
                    Timber.d("Send num $it")
                    if (send) {
                        send(it)
                    } else {
                        offer(it).also {
                            Timber.d("Offer: $it")
                        }
                    }
                }
            }

            val subscribers = List(3) { workerNumber ->
                scope.launch {
                    recvChan.openSubscription().consumeEach {
                        delay((workerNumber + 1) * 100L)
                        Timber.d("Consumed number: $it in subscriber: $workerNumber")
                    }
                }
            }
        }
    }
}
