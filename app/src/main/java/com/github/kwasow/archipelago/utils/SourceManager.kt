package com.github.kwasow.archipelago.utils

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import org.json.JSONObject
import java.io.File

class SourceManager {

    companion object {

        // TODO: Read about abstract functions and maybe add this to source class
        fun save(context: Context, name: String, dir: String, jsonObject: JSONObject): Boolean {
            // Remove path separators
            val realName = prepareName(name)
            val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
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
            val jsonString = jsonObject.toString()
            encryptedOutputStream.write(
                jsonString.encodeToByteArray()
            )

            // Close streams
            encryptedOutputStream.flush()
            encryptedOutputStream.close()

            return true
        }

        fun get(context: Context, dir: String): List<JSONObject> {
            val directory = File(context.filesDir.path + dir)

            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles()
                val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                val list = mutableListOf<JSONObject>()

                files?.forEach { it ->
                    val encryptedFile = EncryptedFile.Builder(
                        context,
                        it,
                        masterKey,
                        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                    ).build()

                    val encryptedInputStream = encryptedFile.openFileInput()
                    val jsonString = encryptedInputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }

                    // Add object to list
                    list.add(JSONObject(jsonString))

                    // Close streams
                    encryptedInputStream.close()
                }

                return list
            } else {
                return listOf()
            }
        }

        fun update(context: Context, name: String, dir: String, jsonObject: JSONObject): Boolean {
            // I felt smart when I wrote this
            return rename(context, name, name, dir, jsonObject)
        }

        fun rename(context: Context, oldName: String, newName: String, dir: String, jsonObject: JSONObject):
            Boolean {
                // Read the object for safety
                val file = File(context.filesDir.path + dir, prepareName(oldName))
                val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val encryptedFile = EncryptedFile.Builder(
                    context,
                    file,
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()

                val encryptedInputStream = encryptedFile.openFileInput()
                val jsonString = encryptedInputStream.bufferedReader().use { reader ->
                    reader.readText()
                }

                // Try renaming and revert if failed
                return if (delete(context, oldName, dir)) {
                    if (!save(context, newName, dir, jsonObject)) {
                        // If delete succeeded and save failed
                        save(context, oldName, dir, JSONObject(jsonString))
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
