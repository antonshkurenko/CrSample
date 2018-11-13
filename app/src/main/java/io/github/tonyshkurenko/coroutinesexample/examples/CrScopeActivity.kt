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
import kotlinx.android.synthetic.main.activity_cr_scope.*
import kotlinx.coroutines.*
import timber.log.Timber

class CrScopeActivity : SampleCrActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cr_scope)

        /**
         * 1) Show cancelling whole scope on exception
         */

        cancel_button.setOnClickListener {

            scope.launch {

                coroutineScope {

                    val first = launch {
                        Timber.d("Before delay #1")
                        delay(3000)
                        Timber.d("After delay #1")
                    }

                    val second = async {
                        Timber.d("Before delay #2")
                        delay(4500)
                        Timber.d("After delay #2")
                    }

                    first.cancel()

                    delay(5000)

                    val third = withContext(Dispatchers.Default) {
                        Timber.d("Before delay #3")
                        delay(4500)
                        Timber.d("After delay #3")
                    }
                }
            }
        }

        throw_button.setOnClickListener {

            scope.launch {

                coroutineScope {

                    val first = launch {
                        Timber.d("Before delay #1")
                        delay(3000)
                        throw AssertionError("throw ha-ha")
                    }

                    val second = async {
                        Timber.d("Before delay #2")
                        delay(4500)
                        Timber.d("After delay #2")
                    }

                    delay(5000)

                    val third = withContext(Dispatchers.Default) {
                        Timber.d("Before delay #3")
                        delay(4500)
                        Timber.d("After delay #3")
                    }
                }
            }
        }

        cancel_s_button.setOnClickListener {

            scope.launch {

                supervisorScope {

                    val first = launch {
                        Timber.d("Before delay #1")
                        delay(3000)
                        Timber.d("After delay #1")
                    }

                    val second = async {
                        Timber.d("Before delay #2")
                        delay(4500)
                        Timber.d("After delay #2")
                    }

                    first.cancel()

                    delay(5000)

                    val third = withContext(Dispatchers.Default) {
                        Timber.d("Before delay #3")
                        delay(4500)
                        Timber.d("After delay #3")
                    }
                }
            }
        }

        throw_s_button.setOnClickListener {

            scope.launch {

                supervisorScope {

                    val first = launch {
                        Timber.d("Before delay #1")
                        delay(3000)
                        throw AssertionError("throw ha-ha")
                    }

                    val second = async {
                        Timber.d("Before delay #2")
                        delay(4500)
                        Timber.d("After delay #2")
                    }

                    delay(5000)

                    val third = withContext(Dispatchers.Default) {
                        Timber.d("Before delay #3")
                        delay(4500)
                        Timber.d("After delay #3")
                    }
                }
            }
        }
    }
}
