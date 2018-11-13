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
import io.github.tonyshkurenko.coroutinesexample.R
import io.github.tonyshkurenko.coroutinesexample.SampleCrActivity
import io.github.tonyshkurenko.coroutinesexample.debounce
import io.github.tonyshkurenko.coroutinesexample.onTextChanged
import kotlinx.android.synthetic.main.activity_real_life.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.consumeEach
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class RealLifeActivity : SampleCrActivity() {

    val presenter = KindaPresenter(scope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_life)

        load_smth_button.setOnClickListener {

            scope.launch(Dispatchers.Main) {
                presenter.loadSomeCoolStuff()
                    .openSubscription()
                    .consumeEach {
                        Timber.d("New state: $it")
                    }
            }
        }

        some_edit_text.onTextChanged {
            presenter.onSomeTextChanged(it)
        }

        some_dialog_button.setOnClickListener {
            presenter.onSomeDialogButtonClick {
                showDialogWithResult()
            }
        }

        call_from_java_button.setOnClickListener {
            WowJavaClass().callFunction(this)
        }
    }

    private suspend fun showDialogWithResult(): String {
        return suspendCoroutine<String> {
            AlertDialog.Builder(this)
                .setTitle("Some title")
                .setPositiveButton("Hi, success") { _, _ ->
                    it.resume("Success")
                }
                .setNegativeButton("Hi, negative") { _, _ ->
                    it.resumeWithException(RuntimeException("Exception"))
                }
                .show()
        }
    }

    suspend fun iWillBeCalledFromJava(): String {
        return withContext(Dispatchers.Default) {
            "Hello from kotlin!"
        }
    }

    class KindaPresenter(val scope: CoroutineScope) {

        enum class State {
            LOADING, SUCCESS, ERROR
        }

        fun loadSomeCoolStuff() = scope.broadcast<State> {

            Timber.d("Start loading")

            send(State.LOADING)

            val resultString = withContext(Dispatchers.Default) {
                delay(1000)
                "Some String".also { Timber.d("Loading result string: $it") }
            }

            val firstDef = async {
                delay(500)
                resultString.toLowerCase()
            }

            val secondDef = async {
                delay(750)
                resultString.toUpperCase()
            }

            val realResult = "${firstDef.await()} ${secondDef.await()}".also {
                Timber.d("Real result: $it")
            }

            if (Random.nextBoolean()) {
                send(State.SUCCESS)
            } else {
                send(State.ERROR)
            }
        }

        /**
         *
         *
         */

        val textWatcherActor by lazy {
            scope.actor<String>(capacity = Channel.CONFLATED) {
                debounce(700, TimeUnit.MILLISECONDS)
                    .consumeEach {
                        Timber.d("Text consumed: $it")
                    }
            }
        }

        fun onSomeTextChanged(newText: String) {
            textWatcherActor.offer(newText).also {
                Timber.d("Text \"$newText\" offered: $it")
            }
        }

        fun onSomeDialogButtonClick(block: suspend () -> String) {

            scope.launch {
                val data = try {
                    block()
                } catch (e: Exception) {
                    ":("
                }

                Timber.d("I wrapped dialog! Data: $data")
            }
        }
    }
}
