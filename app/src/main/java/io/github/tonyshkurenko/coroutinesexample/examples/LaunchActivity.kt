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
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.system.measureTimeMillis

class LaunchActivity : SampleCrActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        just_launch_button.setOnClickListener {
            scope.launch {
                Timber.d(suspendedFunc())
            }
        }

        var cancelJob: Job? = null
        cancel_button.setOnClickListener {

            if (cancelJob != null) {
                Timber.d("Canceling (cancel)")
                cancelJob!!.cancel()
                cancelJob = null
            } else {
                cancelJob = scope.launch {

                    Timber.d("Before delay (cancel)")
                    delay(7500)
                    Timber.d("After delay (cancel)")
                }
            }
        }

        join_button.setOnClickListener {

            scope.launch {

                val time = measureTimeMillis {

                    val first = launch {

                        Timber.d("Before delay #1 (join)")
                        delay(3000)
                        Timber.d("After delay #1 (join)")
                    }

                    val second = launch {

                        Timber.d("Before delay #2 (cancel join)")
                        delay(4500)
                        Timber.d("After delay #2 (cancel join)")
                    }

                    first.join()
                    second.cancelAndJoin()

                    Timber.d("After join and cancel and join")
                }

                Timber.d("Measured $time ms")
            }
        }

        lazy_button.setOnClickListener {

            Timber.d("Before launch (lazy)")
            val lazy = scope.launch(start = CoroutineStart.LAZY) {

                Timber.d("Before delay (lazy)")
                delay(3000)
                Timber.d("After delay (lazy)")
            }
            Timber.d("After launch (lazy)")
            lazy.start()
            Timber.d("After start (lazy)")
        }

        cancel_parent_button.setOnClickListener {

            scope.launch {

                val time = measureTimeMillis {

                    val parent = launch {

                        launch {
                            Timber.d("Before parent cancel #1")
                            delay(4000)
                            Timber.d("After parent cancel #1")
                        }

                        launch {
                            Timber.d("Before parent cancel #2")
                            delay(4000)
                            Timber.d("After parent cancel #2")
                        }
                    }

                    Timber.d("Before delay parent-parent")
                    delay(2000)
                    Timber.d("After delay parent-parent")
                    parent.cancelAndJoin()
                    Timber.d("After cancel and join")
                }

                Timber.d("Measured $time ms")
            }
        }

        throw_and_catch_button.setOnClickListener {

            scope.launch {
                Timber.d("Before throw")
                throw AssertionError("throw!")
            }
        }

        join_throw_button.setOnClickListener {

            scope.launch {

                val time = measureTimeMillis {

                    val first = launch {

                        Timber.d("Before throw #1 (throw join)")
                        delay(3000)
                        throw java.lang.AssertionError("throw!")
                    }

                    val second = launch {

                        Timber.d("Before throw #2 (join)")
                        delay(4500)
                        Timber.d("After throw #2 (join)")
                    }

                    first.join()
                    second.join()
                    Timber.d("After join and throw and join")
                }

                Timber.d("Measured $time ms")
            }
        }

        throw_catch_button.setOnClickListener {

            scope.launch {
                Timber.d("Before throw")
                throw AssertionError("throw!")
            }.invokeOnCompletion { t ->
                Timber.d(t, "Invoke on completion :), caught an exception!")
            }
        }
    }

    suspend fun suspendedFunc(): String {
        return async { "suspended func" }.await()
    }
}
