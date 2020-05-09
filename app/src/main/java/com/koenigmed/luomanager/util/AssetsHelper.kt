package com.koenigmed.luomanager.util

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object AssetsHelper {

    fun <T, V> objectFromJsonFile(testClass: Class<T>, objectClass: Class<V>, filePath: String): V {
        return GsonUtil.gson().fromJson(getJson(testClass, filePath), objectClass)
    }

    fun <T, V> listFromJsonFile(testClass: Class<T>, objectClass: Class<Array<V>>, filePath: String): List<V> {
        return GsonUtil.gson().fromJson(getJson(testClass, filePath), objectClass).toList()
    }

    private fun <T> getJson(testClass: Class<T>, filePath: String): String {
        val stream = testClass.classLoader.getResourceAsStream(filePath)
        return convertStreamToString(stream)
    }

    private fun convertStreamToString(input: InputStream): String {
        val reader = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            sb.append(line).append("\n")
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

}

