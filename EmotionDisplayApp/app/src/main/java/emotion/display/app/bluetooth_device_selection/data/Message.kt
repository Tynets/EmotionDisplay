package emotion.display.app.bluetooth_device_selection.data

data class Message(
    val command: Byte = 0,
    val category: Byte? = null,
    val position: Byte? = null,
    val sizeLSB: Byte? = null,
    val sizeMSB: Byte? = null,
    val pixels: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (pixels != null) {
            if (other.pixels == null) return false
            if (!pixels.contentEquals(other.pixels)) return false
        } else if (other.pixels != null) return false

        return true
    }

    override fun hashCode(): Int {
        return pixels?.contentHashCode() ?: 0
    }
}

fun Message.toStr(): String {
    val builder = StringBuilder()
    builder.append(this.command.toUnsignedInt()).append(" ")
        .append(category?.toUnsignedInt()).append(" ")
        .append(position?.toUnsignedInt()).append(" ")
        .append(sizeLSB?.toUnsignedInt()).append(" ")
        .append(this.sizeMSB?.toUnsignedInt()).append(" ")
        .append("[ ")
    this.pixels?.forEach { builder.append(it.toUnsignedInt()).append(" ") }
    builder.append("]")
    return builder.toString()
}

fun Message.toByteArray() : ByteArray {
    var output: ByteArray = byteArrayOf()
    command.let { output += it }
    category?.let { output += it }
    position?.let { output += it }
    sizeLSB?.let { output += it }
    sizeMSB?.let { output += it }
    pixels?.let { output += it }
    return output
}

fun ByteArray.toMessage() : Message {
    return if (this.isEmpty()) Message()
    else if (this.size == 1) Message(this[0])
    else if (this.size < 6) throw IllegalArgumentException("Must contain at least 6 bytes")
    else Message(this[0], this[1], this[2], this[3], this[4], this.sliceArray(5..<this.size))
}

fun Int.toSignedByte() : Byte {
    return if (this > 255) throw IllegalArgumentException("Must be lower that 256")
    else if (this < 128) this.toByte()
    else (-(this.inv() + 1)).toByte()
}

fun Byte.toUnsignedInt() : Int {
    return (this.toInt() shl 24) ushr 24
}