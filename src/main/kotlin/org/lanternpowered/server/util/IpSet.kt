/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.function.Predicate

class IpSet private constructor(private val address: InetAddress, private val prefixLength: Int) : Predicate<InetAddress?> {

    override fun test(input: InetAddress?): Boolean {
        if (input == null)
            return false
        val addressBytes = input.address
        val checkAddressBytes = this.address.address
        if (addressBytes.size != checkAddressBytes.size)
            return false
        val completeSegments = this.prefixLength shr 3
        val overlap = this.prefixLength and 7
        for (i in 0 until completeSegments) {
            if (addressBytes[i] != checkAddressBytes[i])
                return false
        }
        for (i in 0 until overlap) {
            fun value(address: ByteArray) = (address[completeSegments + 1].toInt() shr (7 - i)) and 0x1
            if (value(checkAddressBytes) != value(addressBytes))
                return false
        }
        return true
    }

    override fun toString(): String = this.address.hostAddress + "/" + this.prefixLength

    object IpSetSerializer : TypeSerializer<IpSet> {

        override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): IpSet? {
            return try {
                val string = value.string
                if (string == null) null else fromCidr(string)
            } catch (e: IllegalArgumentException) {
                throw ObjectMappingException(e)
            }
        }

        override fun serialize(type: TypeToken<*>, obj: IpSet?, value: ConfigurationNode) {
            value.value = obj.toString()
        }
    }

    companion object {

        fun fromAddressPrefix(address: InetAddress, prefixLength: Int): IpSet {
            validatePrefixLength(address, prefixLength)
            return IpSet(address, prefixLength)
        }

        fun fromCidr(spec: String): IpSet {
            val addressString: String
            val prefixLength: Int
            val slashIndex = spec.lastIndexOf('/')
            if (slashIndex == -1) {
                prefixLength = 32
                addressString = spec
            } else {
                prefixLength = spec.substring(slashIndex + 1).toInt()
                addressString = spec.substring(0, slashIndex)
            }
            val address = try {
                InetAddress.getByName(addressString)
            } catch (e: UnknownHostException) {
                throw IllegalArgumentException("$addressString does not contain a valid IP address")
            }
            return fromAddressPrefix(address, prefixLength)
        }

        private fun validatePrefixLength(address: InetAddress, prefixLen: Int) {
            require(prefixLen >= 0) { "Minimum prefix length for an IP address is 0!" }
            val maxLength = getMaxPrefixLength(address)
            require(prefixLen <= maxLength) { "Maximum prefix length for a ${address.javaClass.simpleName} is $maxLength" }
        }

        private fun getMaxPrefixLength(address: InetAddress): Int = when (address) {
            is Inet4Address -> 32
            is Inet6Address -> 128
            else -> throw IllegalArgumentException("Unknown IP address type $address")
        }
    }
}
