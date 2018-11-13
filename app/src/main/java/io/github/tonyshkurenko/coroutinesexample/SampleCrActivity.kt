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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Project: CoroutinesExample
 * Follow me: @tonyshkurenko
 *
 * @author Anton Shkurenko
 * @since 11/6/18
 */
abstract class SampleCrActivity : AppCompatActivity(), CoroutineScope {

    private var _ctx: CoroutineContext? = null
    override val coroutineContext: CoroutineContext
        get() = _ctx ?: throw IllegalStateException("Scope isn't started")

    val scope: CoroutineScope
        get() = this

    private fun startScope() {
        _ctx =
                Dispatchers.Main +
                SupervisorJob() +
                CoroutineName("samplecr") +
                CoroutineExceptionHandler { _, throwable ->
                    Timber.w(throwable, "Caught in exception handler!")
                }
    }

    private fun endScope() {
        _ctx!![Job]!!.cancel()  // throws only when misused, e.g. no startScope()
        _ctx = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScope()
    }

    override fun onDestroy() {
        endScope()
        super.onDestroy()
    }
}