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
        fun save(context: Context, name: String, dir: String, source: Any): Boolean {
            // Remove path separators
            val realName = prepareName(name)
            val masterKey = MasterKey.Builder(
                    context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
            val file = File(context.filesDir.path + dir, realName)

            // Check if file doesn't yet exist
            if (file.exists()) {
                return false
            }

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
        fun get(context: Context, dir: String): List<Any> {
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

                return list
            } else {
                return listOf()
            }
        }

        // And here
        fun recalculate(transactions: List<Transaction>): Double {
            var sum = 0.0

            transactions.forEach {
                sum += it.amount
            }

            // Round to two decimal places
            return "%.2f".format(sum).toDouble()
        }

        fun update(context: Context, name: String, dir: String, source: Any): Boolean {
            // I felt smart when I wrote this
            return rename(context, name, name, dir, source)
        }

        fun rename(context: Context, oldName: String, newName: String, dir: String, source: Any):
                Boolean {
            // Read the object for safety
            val file = File(context.filesDir.path + dir, prepareName(oldName))
            val masterKey = MasterKey.Builder(
                    context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            val encryptedFile = EncryptedFile.Builder(
                    context,
                    file,
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val encryptedInputStream = encryptedFile.openFileInput()
            val objectInputStream = ObjectInputStream(encryptedInputStream)
            val sourceObject = objectInputStream.readObject()

            // Try renaming and revert if failed
            return if (delete(context, oldName, dir)) {
                if (!save(context, newName, dir, source)) {
                    // If delete succeeded and save failed
                    save(context, oldName, dir, sourceObject)
                    // Actually I don't know if this will succeed when the previous one failed,
                    // but who knows
                } else {
                    true
                }
            } else {
                false
            }
        }

        fun delete(context: Context, name: String, dir: String): Boolean {
            val realName = prepareName(name)
            val file = File(context.filesDir.path + dir, realName)

            return file.delete()
        }

        private fun prepareName(name: String): String {
            return name
                    .replace("/", "")
                    .replace("\\", "")
        }
    }
}