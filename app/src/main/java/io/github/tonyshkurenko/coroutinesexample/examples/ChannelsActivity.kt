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
import kotlinx.android.synthetic.main.activity_channels.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class ChannelsActivity : SampleCrActivity() {

    var capacity: Int = RENDEZVOUS

    val send: Boolean
        get() = offer_or_send.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels)

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
                        1 -> capacity = UNLIMITED
                        2 -> capacity = CONFLATED
                        3 -> capacity = 5
                    }

                    capacity_button.text = "Capacity: ${values[pos]}"
                }
                .show()
        }


        send_and_receive_button.setOnClickListener {

            scope.launch {
                val chan = Channel<String>(capacity)

                val sendJob = launch {
                    repeat(10) {

                        delay(400)

                        Timber.d("Before send/offer")
                        if (send) {
                            chan.send("Send: $it")
                        } else {
                            chan.offer("Offer: $it").also {
                                Timber.d("Offered: $it")
                            }
                        }
                        Timber.d("After send/offer")
                    }

                    chan.close()

                    Timber.d("Closed for send: ${chan.isClosedForSend}")

                    try {
                        if (send) {
                            chan.send("Not happen")
                        } else {
                            chan.offer("Not happen").also {
                                Timber.d("Offered: $it")
                            }
                        }
                    } catch (e: Exception) {
                        Timber.d(e, "Closed for send!")
                    }
                }

                val receiveFastJob = launch {

                    repeat(7) {
                        Timber.d("Fast reader")
                        delay(200)

                        Timber.d("Closed for receive (fast): ${chan.isClosedForReceive}")
                        try {
                            val value = chan.receive()

                            Timber.d("Receive value (fast): $value")
                        } catch (e: Exception) {
                            Timber.d(e, "Closed for receive (fast)!")
                        }
                    }
                }

                val receiveSlowJob = launch {

                    repeat(7) {
                        Timber.d("Slow reader")
                        delay(600)

                        Timber.d("Closed for receive (slow): ${chan.isClosedForReceive}")
                        try {
                            val value = chan.receive()

                            Timber.d("Receive value (slow): $value")
                        } catch (e: Exception) {
                            Timber.d(e, "Closed for receive (slow)!")
                        }
                    }
                }
            }
        }

        send_and_receive_broadcast_button.setOnClickListener {

            scope.launch {

                val chan = try {
                    BroadcastChannel<String>(capacity)
                } catch (e: Exception) {
                    Timber.i(e, "As I said, unsupported :)")
                    return@launch
                }

                val sendJob = launch {
                    repeat(10) {

                        delay(400)

                        Timber.d("Before send/offer")
                        if (send) {
                            chan.send("Send: $it")
                        } else {
                            chan.offer("Offer: $it").also {
                                Timber.d("Offered: $it")
                            }
                        }
                        Timber.d("After send/offer")
                    }

                    chan.close()

                    Timber.d("Closed for send: ${chan.isClosedForSend}")

                    try {
                        if (send) {
                            chan.send("Not happen")
                        } else {
                            chan.offer("Not happen").also {
                                Timber.d("Offered: $it")
                            }
                        }
                    } catch (e: Exception) {
                        Timber.d(e, "Closed for send!")
                    }
                }

                val receiveFastJob = launch {

                    val recChan = chan.openSubscription()

                    repeat(10) {
                        Timber.d("Fast reader")
                        delay(200)

                        Timber.d("Closed for receive (fast): ${recChan.isClosedForReceive}")
                        try {
                            val value = recChan.receive()

                            Timber.d("Receive value (fast): $value")
                        } catch (e: Exception) {
                            Timber.d(e, "Closed for receive (fast)!")
                        }
                    }
                }

                val receiveSlowJob = launch {

                    val recChan = chan.openSubscription()

                    repeat(10) {
                        Timber.d("Slow reader")
                        delay(600)

                        Timber.d("Closed for receive (slow): ${recChan.isClosedForReceive}")
                        try {
                            val value = recChan.receive()

                            Timber.d("Receive value (slow): $value")
                        } catch (e: Exception) {
                            Timber.d(e, "Closed for receive (slow)!")
                        }
                    }
                }

                delay(1600)

                val lateSlowJob = launch {

                    val recChan = chan.openSubscription()

                    repeat(10) {
                        Timber.d("Late reader")
                        delay(400)

                        Timber.d("Closed for receive (late): ${recChan.isClosedForReceive}")
                        try {
                            val value = recChan.receive()

                            Timber.d("Receive value (late): $value")
                        } catch (e: Exception) {
                            Timber.d(e, "Closed for receive (late)!")
                        }
                    }
                }
            }
        }


    }
}
