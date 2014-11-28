/*
 * Copyright 2014 JetBrains s.r.o.
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

package org.jetbrains.android.dsl

import java.util.HashSet
import java.util.HashMap
import java.util.ArrayList
import java.io.File

open class GeneratorConfiguration(outputDirectory: String = "gen/") : BaseGeneratorConfiguration() {

    override val outputDirectory = outputDirectory
    override val outputPackage = "kotlinx.android.koan"

    override val excludedClasses = HashSet(readLines("props/excluded_classes.txt"))

    override val excludedMethods = HashSet(readLines("props/excluded_methods.txt"))

    override fun getOutputFile(subsystem: KoanFile): File {
        return File(outputDirectory + "src/main/kotlin/", subsystem.filename)
    }

    override val imports = listOf(
        "layouts" to "props/imports_layouts.txt",
        "views" to "props/imports_views.txt",
        "services" to "props/imports_services.txt"
    ).fold(HashMap<String, String>()) { hashMap, t ->
        hashMap.put(t.first, readFile(t.second)); hashMap
    }

    override val helperConstructors: Map<String, List<List<Variable>>>
        get() {
            val res = HashMap<String, ArrayList<List<Variable>>>()
            for (line in readLines("props/helper_constructors.txt").filter { it.isNotEmpty() && !it.startsWith('#') }) {
                try {
                    val separator = line.indexOf(' ')
                    val className = line.substring(0, separator)
                    val props = line.substring(separator + 1).split(',').map {
                        val nameType = it.split(":")
                        Variable(nameType[0].trim(), nameType[1].trim())
                    }.toList()
                    val constructors = res.getOrElse(className, { ArrayList<List<Variable>>() })
                    constructors.add(props)
                    res.put(className, constructors)
                } catch (e: ArrayIndexOutOfBoundsException) {
                    throw RuntimeException("Failed to tokenize string, malformed helper_constructors.txt")
                }
            }
            return res
        }

    override val customMethodParameters: Map<String, String>
        get() {
            fun parseLine(s: String): Pair<String, String>? {
                val trimmed = s.trim()
                if (trimmed.length == 0) return null
                val paren = trimmed.indexOf('(')
                return Pair(trimmed.substring(0, paren), trimmed.substring(paren + 1, trimmed.length - 1))
            }
            return readLines("props/custom_method_parameters.txt").fold(HashMap<String, String>()) { r, t ->
                val parsed = parseLine(t)
                if (parsed != null) r.put(parsed.first, parsed.second)
                r
            }
        }
}