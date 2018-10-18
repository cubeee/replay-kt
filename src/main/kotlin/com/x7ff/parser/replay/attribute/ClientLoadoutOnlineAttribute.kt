package com.x7ff.parser.replay.attribute

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.attribute.ProductAttribute.Companion.readProductAttribute

data class ClientLoadoutOnlineAttribute(
    val productAttributes: List<List<ProductAttribute>>
) {
    companion object {
        fun BitBuffer.readClientLoadoutOnline(
            versions: Versions,
            objectReferences: List<ObjectReference>
        ): ClientLoadoutOnlineAttribute {
            val productAttributeLists = mutableListOf<List<ProductAttribute>>()
            val listCount = getByte()

            for (i in 0 until listCount) {
                val productAttributes = mutableListOf<ProductAttribute>()
                val productCount = getByte()

                for (j in 0 until productCount) {
                    productAttributes.add(readProductAttribute(versions, objectReferences))
                }
                productAttributeLists.add(productAttributes)
            }
            return ClientLoadoutOnlineAttribute(productAttributeLists)
        }
    }
}