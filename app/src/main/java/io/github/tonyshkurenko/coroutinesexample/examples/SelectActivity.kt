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
import io.github.tonyshkurenko.coroutinesexample.R
import io.github.tonyshkurenko.coroutinesexample.SampleCrActivity
import kotlinx.android.synthetic.main.activity_select.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.whileSelect
import timber.log.Timber
import kotlin.random.Random

class SelectActivity : SampleCrActivity() {

    val receiveOrNull: Boolean
        get() = receive_or_null.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        who_first.setOnClickListener {

            scope.launch {

                val first = async {
                    delay(Random.nextLong(300, 500))
                    "first!"
                }

                val second = async {
                    delay(Random.nextLong(300, 500))
                    "second!"
                }

                val whoFirst = select<String> {
                    first.onAwait { it }
                    second.onAwait { it }
                    onTimeout(450) { "losers" }
                }

                Timber.d("who first: $whoFirst")
            }
        }

        select_receive_button.setOnClickListener {

            scope.launch {

                val first = produce<Int> {
                    repeat(5) {
                        delay(500)
                        send(it)
                    }
                }

                val second = produce<Int> {
                    repeat(5) {
                        delay(250)
                        send(it)
                    }
                }

                whileSelect {

                    if (receiveOrNull) {
                        first.onReceiveOrNull {
                            Timber.d("first receive $it")

                            it != null
                        }
                    } else {
                        first.onReceive {
                            Timber.d("first receive $it")
                            true // exception on close
                        }
                    }

                    if (receiveOrNull) {
                        second.onReceiveOrNull {
                            Timber.d("second receive $it")
                            it != null
                        }
                    } else {
                        second.onReceive {
                            Timber.d("second receive $it")
                            true // exception on close
                        }
                    }

                    onTimeout(100) {
                        Timber.d("loop timeout :)")
                        true
                    }
                }
            }
        }

        select_send_button.setOnClickListener {

            scope.launch {
                val first = actor<String> {
                    consumeEach {
                        delay(300)
                        Timber.d("first: $it")
                    }
                }

                val second = actor<String> {
                    consumeEach {
                        delay(500)
                        Timber.d("second: $it")
                    }
                }

                var firstCounter = 0
                var secCounter = 0
                whileSelect {
                    Timber.d("Called every loop")
                    first.onSend("first test") {
                        firstCounter++

                        Timber.d("firstCounter $firstCounter")

                        (firstCounter < 5)
                    }

                    second.onSend("second test") {
                        secCounter++

                        Timber.d("secondCounter $secCounter")

                        secCounter < 5
                    }

                    onTimeout(100) {
                        Timber.d("in between :)")
                        true
                    }
                }
            }
        }
    }
}
