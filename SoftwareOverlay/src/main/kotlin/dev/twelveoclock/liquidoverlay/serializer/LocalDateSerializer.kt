package dev.twelveoclock.liquidoverlay.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate> {

    val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)


    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(dateTimeFormat))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), dateTimeFormat)
    }

}