package com.github.kwasow.archipelago.utils

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.github.kwasow.archipelago.data.Transaction
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class SourceManager {

    enum class Capitalization {
        EndOfMonth, EndOfInvestment, Monthly, Yearly
    }

    companion object {
        // Different sources run these with their own parameters
        fun save(context: Context, name: String, dir: String, source: Any) : Boolean {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            val file = File(context.filesDir.path + dir, name)

            if (file.exists()) {
                return false
            }

            val encryptedFile = EncryptedFile.Builder(
                    file,
                    context,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val encryptedOutputStream = encryptedFile.openFileOutput()
            val objectOutputStream = ObjectOutputStream(encryptedOutputStream)
            objectOutputStream.writeObject(source)

            return true
        }

        // Same here
        fun get(context: Context, dir: String) : Array<Any> {
            val directory = File(context.filesDir.path + dir)

            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles()
                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                val list = mutableListOf<Any>()

                files?.forEach {
                    val encryptedFile = EncryptedFile.Builder(
                            it,
                            context,
                            masterKeyAlias,
                            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                    ).build()

                    val encryptedInputStream = encryptedFile.openFileInput()
                    val objectInputStream = ObjectInputStream(encryptedInputStream)
                    val sourceObject = objectInputStream.readObject()

                    list.add(sourceObject)
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