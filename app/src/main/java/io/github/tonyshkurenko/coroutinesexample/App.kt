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

import android.app.Application
import timber.log.Timber

/**
 * Project: CoroutinesExample
 * Follow me: @tonyshkurenko
 *
 * @author Anton Shkurenko
 * @since 11/12/18
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        System.setProperty("kotlinx.coroutines.debug", "on")

        Timber.plant(ThreadDebugTree())
    }

    class ThreadDebugTree : Timber.DebugTree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) =
            super.log(priority, tag, "[T:${Thread.currentThread().name}] $message", t)
    }
}