package io.github.kwasow.archipelago.data

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import org.javamoney.moneta.Money
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

interface Source : Serializable {
    var name: String
    var country: String
    var countryCode: String
    var currencyCode: String
    var sum: Money
    var transactions: MutableList<Transaction>

    companion object {
        const val INTENT_PUT_NAME = "intentSourceObjectSerializable"

        const val JSON_NAME = "name"
        const val JSON_COUNTRY = "country"
        const val JSON_COUNTRY_CODE = "countryCode"
        const val JSON_CURRENCY = "currency"
        const val JSON_AMOUNT = "amount"
        const val JSON_TRANSACTIONS = "transactions"

        fun getMonthChange(transactions: List<Transaction>, currencyCode: String): Money {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            // Set calendar to beginning of this month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val beginning = calendar.time

            // Calculate change
            var change = Money.of(0, currencyCode)
            transactions.forEach {
                if (it.date.after(beginning)) {
                    change = change.add(it.amount)
                }
            }

            return change
        }

        fun fromJsonObject(jsonObject: JSONObject): Source {
            // Get common details
            val jsonName = jsonObject.getString(JSON_NAME)
            val jsonCountry = jsonObject.getString(JSON_COUNTRY)
            val jsonCountryCode = jsonObject.getString(JSON_COUNTRY_CODE)
            val jsonCurrencyCode = jsonObject.getString(JSON_CURRENCY)
            val jsonAmount = Money.of(
                BigDecimal(jsonObject.getString(JSON_AMOUNT)),
                jsonCurrencyCode
            )

            // Get transactions
            val jsonTransactions = mutableListOf<Transaction>()
            for (i in 0 until jsonObject.getJSONArray(JSON_TRANSACTIONS).length()) {
                val transactionObject = jsonObject.getJSONArray(JSON_TRANSACTIONS)[i] as JSONObject
                jsonTransactions.add(
                    Transaction.fromJsonObject(transactionObject, jsonCurrencyCode)
                )
            }

            return object : Source {
                override var name = jsonName
                override var country = jsonCountry
                override var countryCode = jsonCountryCode
                override var currencyCode = jsonCurrencyCode
                override var sum = jsonAmount
                override var transactions = jsonTransactions
            }
        }

        fun get(context: Context, dir: String): List<JSONObject> {
            val directory = File(context.filesDir.path + dir)

            return if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles()
                val list = mutableListOf<JSONObject>()

                files?.forEach { it ->
                    val jsonObject = readFileAsJsonObject(context, it)
                    // Add object to list
                    list.add(jsonObject)
                }

                list
            } else {
                listOf()
            }
        }

        private fun readFileAsJsonObject(context: Context, file: File): JSONObject {
            val encryptedFile = buildEncryptedFile(context, file)

            val encryptedInputStream = encryptedFile.openFileInput()
            val jsonString = encryptedInputStream.bufferedReader().use { reader ->
                reader.readText()
            }

            encryptedInputStream.close()

            return JSONObject(jsonString)
        }

        private fun buildEncryptedFile(context: Context, file: File): EncryptedFile {
            return EncryptedFile.Builder(
                context,
                file,
                getMasterEncryptionKey(context),
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }

        private fun getMasterEncryptionKey(context: Context): MasterKey {
            return MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
    }

    fun recalculate() {
        sum = Money.of(0, currencyCode)

        transactions.forEach {
            sum = sum.add(it.amount)
        }
    }

    fun toJsonObject(): JSONObject {
        val returnObject = JSONObject()

        // Put common details
        returnObject.put(JSON_NAME, name)
        returnObject.put(JSON_COUNTRY, country)
        returnObject.put(JSON_COUNTRY_CODE, countryCode)
        returnObject.put(JSON_CURRENCY, currencyCode)
        returnObject.put(JSON_AMOUNT, sum.number)

        // Generate transactions array
        val transactionsArray = JSONArray()
        transactions.forEach {
            transactionsArray.put(it.toJsonObject())
        }

        returnObject.put(JSON_TRANSACTIONS, transactionsArray)

        // Return the common stuff
        return returnObject
    }

    fun save(context: Context, jsonObject: JSONObject): Boolean {
        val file = File(
            context.filesDir.path + getDir(),
            removeIllegalFileSymbolsFromName()
        )

        // Check if file doesn't yet exist
        if (file.exists()) {
            return false
        }

        saveJsonObjectToFile(context, file, jsonObject)

        return true
    }

    fun update(context: Context, jsonObject: JSONObject): Boolean {
        // I felt smart when I wrote this
        return rename(context, name, jsonObject)
    }

    fun rename(context: Context, newName: String, jsonObject: JSONObject): Boolean {
        // Read the object for safety
        val file = File(
            context.filesDir.path + getDir(),
            removeIllegalFileSymbolsFromName()
        )
        val backupJsonObject = readFileAsJsonObject(context, file)

        val oldName = name
        delete(context)
        name = newName
        return if (save(context, jsonObject)) {
            true
        } else {
            name = oldName
            save(context, backupJsonObject)
        }
    }

    fun delete(context: Context): Boolean {
        val file = File(
            context.filesDir.path + getDir(),
            removeIllegalFileSymbolsFromName()
        )

        return file.delete()
    }

    private fun removeIllegalFileSymbolsFromName(): String {
        return name
            .replace("/", "")
            .replace("\\", "")
    }

    private fun saveJsonObjectToFile(context: Context, file: File, jsonObject: JSONObject) {
        // Create missing directories
        if (!file.parentFile!!.exists()) {
            file.parentFile!!.mkdirs()
        }

        val encryptedFile = buildEncryptedFile(context, file)

        val encryptedOutputStream = encryptedFile.openFileOutput()
        val jsonString = jsonObject.toString()
        encryptedOutputStream.write(
            jsonString.encodeToByteArray()
        )

        // Close streams
        encryptedOutputStream.flush()
        encryptedOutputStream.close()
    }

    private fun getDir(): String {
        return when (this) {
            is SourceAccount -> "/account"
            is SourceInvestment -> "/investment"
            else -> ""
        }
    }
}
