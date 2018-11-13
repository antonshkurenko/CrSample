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
import kotlinx.android.synthetic.main.activity_async.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.system.measureTimeMillis

class AsyncActivity : SampleCrActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)

        two_awaits_seq_button.setOnClickListener {

            scope.async {

                val time = measureTimeMillis {

                    Timber.d("before first")

                    val first = async {
                        Timber.d("before async #1")
                        delay(3000)
                        Timber.d("after async #1")
                        "Heyo, first"
                    }.await()

                    Timber.d("after first")

                    Timber.d("before second")

                    val second = async {
                        Timber.d("before async #2")
                        delay(3000)
                        Timber.d("after async #2")
                        "Heyo, second"
                    }.await()

                    Timber.d("after second")

                    Timber.d("$first - $second")
                }

                Timber.d("Measured $time ms")
            }
        }

        two_awaits_par_button.setOnClickListener {

            scope.async {

                val time = measureTimeMillis {

                    Timber.d("before first")

                    val first = async {
                        Timber.d("before async #1")
                        delay(3000)
                        Timber.d("after async #1")
                        "Heyo, first"
                    }

                    Timber.d("after first")

                    Timber.d("before second")

                    val second = async {
                        Timber.d("before async #2")
                        delay(3000)
                        Timber.d("after async #2")
                        "Heyo, second"
                    }

                    Timber.d("after second")

                    Timber.d("${first.await()} - ${second.await()}")
                }

                Timber.d("Measured $time ms")
            }
        }

        cancel_button.setOnClickListener {

            scope.async {

                Timber.d("Before async (cancel)")

                val def = async {
                    Timber.d("before cancel async")
                    delay(3000)
                    Timber.d("after cancel async")
                    "Heyo"
                }

                Timber.d("After async (cancel)")
                def.cancel()

                Timber.d("After async cancel (cancel)")
                def.await()
                Timber.d("After async await (cancel)")
            }
        }

        lazy_button.setOnClickListener {

            scope.async {

                Timber.d("Before async (lazy)")

                val lazy = async(start = CoroutineStart.LAZY) {
                    Timber.d("before lazy async")
                    delay(3000)
                    Timber.d("after lazy async")
                    "Heyo"
                }

                Timber.d("After async (lazy)")
                lazy.start()
                Timber.d("After async start (lazy)")
            }
        }

        throw_button.setOnClickListener {

            scope.async {

                Timber.d("Before async (throw)")

                async {
                    Timber.d("before throw")
                    throw AssertionError("err ha-ha!")
                }

                delay(1000)
                Timber.d("After async and delay (throw)")
            }
        }

        throw_and_catch_button.setOnClickListener {

            scope.async {

                Timber.d("Before async (throw)")

                val err = async {
                    Timber.d("before async")
                    delay(3000)
                    throw AssertionError("err ha-ha!")
                }
                try {
                    err.await()
                } catch (t: Throwable) {
                    Timber.d(t, "Catch await!")
                }
            }
        }


    }
}
