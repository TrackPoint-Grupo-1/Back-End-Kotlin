package com.trackpoint.demo.Config.ConversorJPA

import com.trackpoint.demo.Config.Utils.CryptoUtils
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SenhaCryptoConverter : AttributeConverter<String, String> {
    override fun convertToDatabaseColumn(attribute: String?): String? {
        return CryptoUtils.encrypt(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return CryptoUtils.decrypt(dbData)
    }
}