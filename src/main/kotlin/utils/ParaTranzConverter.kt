package utils

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object ParaTranzConverter {

    fun toParaTranz(inputContent2 : String, outputFile: File) {
        try {
            var inputContent = inputContent2.replace("\\\\r", "//r")
            inputContent = inputContent.replace("\\r", "/r")
            inputContent = inputContent.replace("\\\\n", "/\\n")
            val locStringValues = JSONObject(inputContent).getJSONArray("objects").getJSONObject(0).getJSONObject("objdata").getJSONArray("LocStringValues")
            val result = JSONArray()

            for (i in 0 until locStringValues.length() step 2) {
                val key = locStringValues.getString(i)
                val original = locStringValues.getString(i + 1)
                val item = JSONObject()
                item.put("key", key)
                item.put("original", original)
                item.put("translation", "")
                result.put(item)
            }

            outputFile.writeText(result.toString(2))
        } catch (e: Exception) {
            println("Error processing toParaTranz: ${e.message}")
            throw e
        }
    }

    fun toJson(inputContent: String, outputFile: File, versionMain: String, versionFull: String, relPre: String, number: Int) {
        try {
            val orig = JSONArray(inputContent)
            val result = JSONArray()

            for (i in 0 until orig.length()) {
                val item = orig.getJSONObject(i)
                val key = item.getString("key")
                val translation = if (item.optString("translation").isNotEmpty()) {
                    item.getString("translation")
                } else {
                    item.getString("original")
                }
                result.put(key)
                result.put(translation)
            }

            var resultEncode = result.toString(2)
            resultEncode = resultEncode.replace("//r", "\\\\r")
            resultEncode = resultEncode.replace("/r", "\\r")
            resultEncode = resultEncode.replace("\\\\n", "\\n")
            resultEncode = resultEncode.replace("/\\n", "\\\\n")

            resultEncode = resultEncode.replace("【version_main】", versionMain)
            resultEncode = resultEncode.replace("【rel_pre】", relPre)
            resultEncode = resultEncode.replace("【number】", number.toString())
            resultEncode = resultEncode.replace("【version_full】", versionFull)

            resultEncode = "{\"objects\":[{\"aliases\":[\"LawnStringsData\"],\"objclass\":\"LawnStringsData\",\"objdata\":{\"LocStringValues\":$resultEncode}}],\"version\":1}"
            resultEncode = "{\"objects\":[{\"aliases\":[\"LawnStringsData\"],\"objclass\":\"LawnStringsData\",\"objdata\":{\"LocStringValues\":$resultEncode}}],\"version\":1}"

            outputFile.writeText(resultEncode)
        } catch (e: Exception) {
            println("Error processing toJson: ${e.message}")
            throw e
        }
    }
}