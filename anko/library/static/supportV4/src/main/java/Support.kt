/*
 * Copyright 2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")
package org.jetbrains.anko.support.v4

import android.view.View
import androidx.fragment.app.Fragment
import org.jetbrains.anko.bundleOf

inline fun <reified T : View> Fragment.find(id: Int): T = view?.findViewById(id) as T
inline fun <reified T : View> Fragment.findOptional(id: Int): T? = view?.findViewById(id) as? T

fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any?>): T {
    arguments = bundleOf(*params)
    return this
}