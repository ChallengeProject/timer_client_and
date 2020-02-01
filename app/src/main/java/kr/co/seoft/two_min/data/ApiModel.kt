package kr.co.seoft.two_min.data

data class ApiTimeSet(
    val title: String,
    val isRepeat: Boolean,
    val timers: List<ApiTimer>

)

data class ApiTimer(
    val comment: String,
    val alarm: Int,
    val end: Int
)