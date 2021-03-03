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
            val file = File(
                context.filesDir.path + dir,
                removeIllegalFileSymbols(name)
            )

            // Check if file doesn't yet exist
            if (file.exists()) {
                return false
            }

            saveJsonObjectToFile(context, file, jsonObject)

            return true
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

        fun update(context: Context, name: String, dir: String, jsonObject: JSONObject): Boolean {
            // I felt smart when I wrote this
            return rename(context, name, name, dir, jsonObject)
        }

        fun rename(context: Context, oldName: String, newName: String, dir: String, jsonObject: JSONObject):
            Boolean {
                // Read the object for safety
                val file = File(
                    context.filesDir.path + dir,
                    removeIllegalFileSymbols(oldName)
                )
                val backupJsonObject = readFileAsJsonObject(context, file)

                // Try renaming and revert if failed
                return if (delete(context, oldName, dir)) {
                    if (!save(context, newName, dir, jsonObject)) {
                        // If delete succeeded and save failed
                        save(context, oldName, dir, backupJsonObject)
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
            val file = File(
                context.filesDir.path + dir,
                removeIllegalFileSymbols(name)
            )

            return file.delete()
        }

        private fun removeIllegalFileSymbols(name: String): String {
            return name
                .replace("/", "")
                .replace("\\", "")
        }

        private fun getMasterEncryptionKey(context: Context): MasterKey {
            return MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
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
    }
}
