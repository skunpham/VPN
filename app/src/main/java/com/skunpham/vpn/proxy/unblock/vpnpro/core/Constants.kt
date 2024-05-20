package com.skunpham.vpn.proxy.unblock.vpnpro.core

object Constants {
    const val SET_PASS_WORD_APP_LOCK = "set_pass_word_app_lock"
    const val PASS_WORD_APP_LOCK = "pass_word_app_lock"
    const val ANSWER = "answer"
    const val QUESTION_NUMBER = "question_number"
    const val DATA_APP_LOCK = "DATA_APP_LOCK"
    const val MyPREFERENCES = "MyPreferences"
    const val BASE_URL = "http://www.vpngate.net/"
    const val EMAIL_FEEDBACK = "trustedapp.help@gmail.com"
    const val URL_POLICY =
        "https://firebasestorage.googleapis.com/v0/b/trusted-vpn.appspot.com/o/Privacy_Policy.html?alt=media&token=4e44565d-ef87-4841-bead-4472485cc0fb"
    const val RATED = "RATED"
    const val COUNT_SHOW_RATE = "COUNT_SHOW_RATE"

    const val IS_DEFAULT_SERVER = "is_default_server"

    const val MORE_TIME_60 = 3600L
    const val DEFAULT_TIME = 7200L
    var remainTime: Long = 0


    //check trạng thái connnect vpn trong 1 vòng đời của app
    var isConnectVPNInApp = false

    var isFromClickServer = false
}
