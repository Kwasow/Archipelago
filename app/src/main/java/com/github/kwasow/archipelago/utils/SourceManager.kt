package com.github.kwasow.archipelago.utils

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.data.Transaction
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class SourceManager {

    enum class Capitalization(val value: Int) {
        EndOfMonth(R.string.end_of_month),
        EndOfInvestment(R.string.end_of_investment),
        Monthly(R.string.monthly),
        Yearly(R.string.yearly)
    }

    companion object {
        // Different sources run these with their own parameters
        fun save(context: Context, name: String, dir: String, source: Any) : Boolean {
            // Remove path separators
            var realName = name
                    .replace("/", "")
                    .replace("\\", "")
            val masterKey = MasterKey.Builder(
                    context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
            var file = File(context.filesDir.path + dir, realName)

            // Check if file doesn't yet exist and change filename if necessary
            var changes: Boolean
            do {
                changes = false
                if (file.exists()) {
                    realName += ".1"
                    file = File(context.filesDir.path + dir, realName)
                    changes = true
                }
            } while (changes)

            // Create missing directories
            if (!file.parentFile!!.exists()) {
                file.parentFile!!.mkdirs()
            }

            val encryptedFile = EncryptedFile.Builder(
                    context,
                    file,
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val encryptedOutputStream = encryptedFile.openFileOutput()
            val objectOutputStream = ObjectOutputStream(encryptedOutputStream)
            objectOutputStream.writeObject(source)

            // Added this
            // Close streams
            objectOutputStream.close()
            encryptedOutputStream.flush()
            encryptedOutputStream.close()

            return true
        }

        // Same here
        fun get(context: Context, dir: String) : Array<Any> {
            val directory = File(context.filesDir.path + dir)

            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles()
                val masterKey = MasterKey.Builder(
                        context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()
                val list = mutableListOf<Any>()

                files?.forEach {
                    val encryptedFile = EncryptedFile.Builder(
                            context,
                            it,
                            masterKey,
                            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                    ).build()

                    val encryptedInputStream = encryptedFile.openFileInput()
                    val objectInputStream = ObjectInputStream(encryptedInputStream)
                    val sourceObject = objectInputStream.readObject()

                    list.add(sourceObject)

                    // Close streams
                    objectInputStream.close()
                    encryptedInputStream.close()
                }

                return list.toTypedArray()
            } else {
                return arrayOf()
            }
        }

        // And here
        fun recalculate(transactions: Array<Transaction>) : Double {
            var sum = 0.0

            transactions.forEach {
                sum += it.amount
            }

            return sum
        }
    }
}