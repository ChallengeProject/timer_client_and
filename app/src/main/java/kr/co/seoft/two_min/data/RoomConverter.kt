package kr.co.seoft.two_min.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import kr.co.seoft.two_min.util.fromJson

class RoomConverter{

    companion object {
        val gson = Gson()

        @JvmStatic
        @TypeConverter
        fun stringToTime(str:String?): List<Time> {
            if(str.isNullOrEmpty()){
                return emptyList()
            }
            return gson.fromJson<List<Time>>(str)
        }

        @JvmStatic
        @TypeConverter
        fun timeToString(times : List<Time>): String? {
            if(times.isEmpty()){
                return null
            }
            return gson.toJson(times)
        }
    }


    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T : Enum<T>> T.toInt(): Int = this.ordinal
    private inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]

    @TypeConverter
    fun myEnumToTnt(value: Bell.Type) = value.toInt()
    @TypeConverter
    fun intToMyEnum(value: Int) = value.toEnum<Bell.Type>()
}