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
import kotlinx.android.synthetic.main.activity_yield.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class YieldActivity : SampleCrActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yield)


        Timber.d("Welcome to synchronous coroutines :)")

        yield_s_button.setOnClickListener {

            fun yielder() = sequence<Int> {

                Timber.d("yield sequence start")

                repeat(5) {
                    Timber.d("yield sequence $it")
                    yield(it)
                }

                Timber.d("yield sequence 6..8")
                yieldAll(6..8)
            }

            scope.launch {

                Timber.d("Before transformation")
                val transformed = yielder()
                    .map { 2 * it }

                Timber.d("After transformation transformation")

                val sum = transformed.sum()

                Timber.d("After sum $sum")
            }
        }

        yield_i_button.setOnClickListener {

            fun yielder() = iterator<Int> {

                Timber.d("yield iterator start")

                repeat(5) {
                    Timber.d("yield iterator $it")
                    yield(it)
                }

                Timber.d("yield iterator 11..15")
                yieldAll(6..8)
            }


            scope.launch {

                Timber.d("Before foreach")
                yielder()
                    .forEach {
                        Timber.d("Foreach $it")
                    }

                Timber.d("After foreach")
            }

        }
    }
}
