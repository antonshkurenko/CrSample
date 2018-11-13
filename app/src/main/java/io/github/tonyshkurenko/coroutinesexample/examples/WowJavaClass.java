package io.github.tonyshkurenko.coroutinesexample.examples;

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

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/**
 * Project: CoroutinesExample
 * Follow me: @tonyshkurenko
 *
 * @author Anton Shkurenko
 * @since 11/13/18
 */
public class WowJavaClass {

    public void callFunction(RealLifeActivity activity) {
        activity.iWillBeCalledFromJava(new Continuation<String>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                Timber.d("Resumed from java: %s", o.toString());
            }
        });
    }
}
