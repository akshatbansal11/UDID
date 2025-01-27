package com.udid.utilities.crypt

import android.os.Build
import java.util.UUID

object CryptUniqueIdGenerator {
    var DEVICE_ID = ""
    var ENCRYPTED_DEVICE_ID = ""
    val cryptUniqueId: String
        /**
         * @return Unique Device Id
         */
        get() {
            if (DEVICE_ID != null && DEVICE_ID.isNotEmpty()) {
                return DEVICE_ID
            }

            @Suppress("deprecation") val m_szDevIDShort =
                "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
            var serial: String
            try {
                serial = Build::class.java.getField("SERIAL")[null]
                    .toString()

                // Go ahead and return the serial for api => 9
                DEVICE_ID =
                    UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong())
                        .toString()
                return DEVICE_ID
            } catch (exception: Exception) {
                serial = "XdeQ2Dr4Xe6L"
            }
            DEVICE_ID =
                UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong())
                    .toString()
            return DEVICE_ID
        }
    val encryptedUniqueID: String
        /**
         * @return Encrypted Device ID
         */
        get() {
            if (ENCRYPTED_DEVICE_ID != null && ENCRYPTED_DEVICE_ID.length > 0) {
                return ENCRYPTED_DEVICE_ID
            }
            val ddid: String = cryptUniqueId
            try {
                val _crypt = CryptLib()
                ENCRYPTED_DEVICE_ID = _crypt.encryptString(ddid)!!
            } catch (e: Exception) {
                ENCRYPTED_DEVICE_ID = ddid
                //e.printStackTrace();
            }
            return ENCRYPTED_DEVICE_ID.trim { it <= ' ' }
        }
}

