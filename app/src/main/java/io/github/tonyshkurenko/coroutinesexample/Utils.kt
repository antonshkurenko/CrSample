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

package io.github.tonyshkurenko.coroutinesexample

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect
import java.util.concurrent.TimeUnit

/**
 * Project: CoroutinesExample
 * Follow me: @tonyshkurenko
 *
 * @author Anton Shkurenko
 * @since 11/6/18
 */


fun TextView.onTextChanged(delegate: (String) -> Unit) {

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            delegate(s.toString())
        }
    })
}

fun <T> ReceiveChannel<T>.debounce(
    scope: CoroutineScope,
    time: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS
): ReceiveChannel<T> =
    Channel<T>(capacity = Channel.CONFLATED).also { channel ->
        scope.launch {
            var value = receive()
            whileSelect {
                onTimeout(unit.toMillis(time)) {
                    channel.offer(value)
                    value = receive()
                    true
                }
                onReceive {
                    value = it
                    true
                }
            }
        }
    }

fun <T> ActorScope<T>.debounce(
    time: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS
): ReceiveChannel<T> =
    Channel<T>(capacity = Channel.CONFLATED).also { channel ->
        launch {
            var value = receive()
            whileSelect {
                onTimeout(unit.toMillis(time)) {
                    channel.offer(value)
                    value = receive()
                    true
                }
                onReceive {
                    value = it
                    true
                }
            }
        }
    }