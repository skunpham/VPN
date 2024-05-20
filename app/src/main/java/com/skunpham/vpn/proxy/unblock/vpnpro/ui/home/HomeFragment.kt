package com.skunpham.vpn.proxy.unblock.vpnpro.ui.home

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.skunpham.vpn.proxy.unblock.vpnpro.MyApplication
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseFragment
import com.skunpham.vpn.proxy.unblock.vpnpro.core.Constants
import com.skunpham.vpn.proxy.unblock.vpnpro.core.Constants.isFromClickServer
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.FragmentHomeBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.service.CountDownService
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.AUTH
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.GET_CONFIG
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_AUTHENTICATION_CHECK
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_CONNECT
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_CONNECTED
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_CONNECTING
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_CONNECTING_TIMEOUT
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_DISCONNECTED
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_DISCONNECTING
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_INVALID_DEVICE
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_LOADING
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_NO_NETWORK
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_TRY_DIFFERENT_SERVER
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.TCP_CONNECT
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity.Companion.isClickSelectServer
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity.Companion.isConnectFromButton
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.AppDataUtils
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.CheckInternetConnection
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.RemoteUtils
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.VpnStatus
import java.io.IOException
import java.util.Locale
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

class HomeFragment : BaseFragment() {

    var isDestroyView = true

    companion object {
        const val TAG = "HomeFragment"
        fun newInstance() = HomeFragment()
        var isLogEventConnectedServer = false
        const val TIME_DELAY_RECONNECT = 1000L
        private const val START_TIME_IN_MILLIS = 300000L
    }

    private var isConnectedVPN = false //trạng thái connect vpn
    private var mBottomSheetDialog: Dialog? = null
    private var timer: Handler? = null
    private var timerRunnable: Runnable? = null
    private var isDisconnectFromButton = false
    private var timeOut = 30

    //Check timeout connect server
    private var timerOut: Timer? = null
    private var remainingTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0
    private var endTime: Long = 0
    private var onUserEarnedReward = false

    //Limit time bonus
    private var countDownTimer: CountDownTimer? = null

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CountDownService.ACTION_DISCONNECT_FROM_SERVICE) {
                MyApplication.isDisconnectServerFromTimeCountService = true
                Toast.makeText(requireContext(), getText(R.string.time_up), Toast.LENGTH_SHORT)
                    .show()
                stopVpn()
            } else {
                try {
                    intent.getStringExtra("state")?.let { status(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    var downloadSpeed = intent.getStringExtra(OpenVPNService.KEY_DOWNLOAD_SPEED)
                    var uploadSpeed = intent.getStringExtra(OpenVPNService.KEY_UPLOAD_SPEED)
                    if (downloadSpeed == null) downloadSpeed =
                        myContext.getString(R.string.txt_kbytes_per_second, 00.0f)
                    if (uploadSpeed == null) uploadSpeed =
                        myContext.getString(R.string.txt_kbytes_per_second, 00.0f)
                    if (viewModel.connectVpnStatus.get() == STATUS_CONNECTED) {
                        binding.txtDownloadSpeed.text = getTextFromHtml(downloadSpeed)
                        binding.txtUploadSpeed.text = getTextFromHtml(uploadSpeed)
                    } else {
                        binding.txtDownloadSpeed.text =
                            getTextFromHtml(
                                myContext.getString(
                                    R.string.txt_kbytes_per_second,
                                    00.0f
                                )
                            )
                        binding.txtUploadSpeed.text =
                            getTextFromHtml(
                                myContext.getString(
                                    R.string.txt_kbytes_per_second,
                                    00.0f
                                )
                            )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getTextFromHtml(text: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

    private val viewModel by lazy {
        HomeViewModel()
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (AppDataUtils.getServerSelected() == null) {
//            val freeServerList = AppDataUtils.getListServerFree()
//            if (freeServerList.size <= 0)
//                return
//            val randomServerPosition = Random().nextInt(freeServerList.size)
//            AppDataUtils.setSelectedServer(freeServerList[randomServerPosition])
//        }
        MyApplication.listServerFree.observe(this) {
            if (it.isNotEmpty() && AppDataUtils.getServerSelected() == null) {
                val randomServerPosition = Random().nextInt(it.size)
                AppDataUtils.setSelectedServer(it[randomServerPosition])
                viewModel.selectedServer.set(AppDataUtils.getServerSelected())
            }
        }
        if (AppDataUtils.getIsClickBtnTimeBonus()) {
            timeLeftInMillis = AppDataUtils.getTimeLeftInMillis()
            endTime = AppDataUtils.getEndTime()
            if (endTime == 0L) {
                remainingTimeInMillis = timeLeftInMillis
            } else {
                var timeDiff = endTime - System.currentTimeMillis()
                //to convert into positive number
                timeDiff = abs(timeDiff)
                val timeDiffInSeconds = timeDiff / 1000 % 60
                val timeDiffInMillis = timeDiffInSeconds * 1000
                var timeDiffInMillisPlusTimerRemaining = timeLeftInMillis - timeDiffInMillis
                remainingTimeInMillis = timeLeftInMillis - timeDiffInMillis
                if (timeDiffInMillisPlusTimerRemaining < 0) {
                    timeDiffInMillisPlusTimerRemaining = abs(timeDiffInMillisPlusTimerRemaining)
                    remainingTimeInMillis =
                        START_TIME_IN_MILLIS - timeDiffInMillisPlusTimerRemaining
                }
            }
            startTimerLimitBonus()
        }
    }

    private fun init() {
        status(OpenVPNService.getStatus())
        isDestroyView = false

        /*if (getStatus().lowercase() == STATUS_CONNECTED) {
            if (!AppPurchase.getInstance().isPurchased(myContext)) {
                loadInterOff()
            }
        }*/

        VpnStatus.initLogCache(myContext.cacheDir)
        initView()
        initEvent()
        if (AppDataUtils.getServerSelected() != null) {
            viewModel.selectedServer.set(AppDataUtils.getServerSelected())
        }

        MyApplication.selectedServer.observe(viewLifecycleOwner) { server ->
            if (server != null) {
                AppDataUtils.setSelectedServer(server)
                viewModel.selectedServer.set(server)
                if (isConnectedVPN && isClickSelectServer && !isConnectFromButton) {
                    /*loadAdsNativeConnected()*/
                    binding.txtDownloadSpeed.text =
                        getTextFromHtml(myContext.getString(R.string.txt_kbytes_per_second, 00.0f))
                    binding.txtUploadSpeed.text =
                        getTextFromHtml(myContext.getString(R.string.txt_kbytes_per_second, 00.0f))
                    isDisconnectFromButton = false
                    stopVpn()
                    isClickSelectServer = false
                    isLogEventConnectedServer = false
                    Handler(Looper.myLooper()!!).postDelayed({ prepareVpn() }, TIME_DELAY_RECONNECT)
                    isFromClickServer = true
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (mBottomSheetDialog != null) {
            if (!mBottomSheetDialog!!.isShowing) {
                AppDataUtils.enableAdsResume(requireContext())
            }
        } else {
            AppDataUtils.enableAdsResume(requireContext())
        }
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        val filter = IntentFilter(CountDownService.ACTION_DISCONNECT_FROM_SERVICE)
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, filter)

        when (OpenVPNService.getStatus().lowercase(Locale.getDefault())) {
            STATUS_CONNECTED -> {
                setConnectButtonVisibility(View.GONE)
            }

            STATUS_DISCONNECTED -> {
                setConnectButtonVisibility(View.VISIBLE)
            }
        }

        Log.e(TAG, "onResume: ${AppDataUtils.getIsClickBtnTimeBonus()}")
        if (AppDataUtils.getIsClickBtnTimeBonus()) {
            binding.layoutTimeBonus.setBackgroundResource(R.drawable.bg_grey)
            binding.imgGift.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_gift_grey
                )
            )
            binding.tvtTimeBonus.setTextColor(requireContext().resources.getColor(R.color.grey68))
            binding.tvtAD.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.greyCE)
        }

        if (OpenVPNService.getStatus().lowercase() == STATUS_CONNECTED) {
            if (isFromClickServer) {
                Constants.isConnectVPNInApp = false
                isFromClickServer = false
            } else {
                Constants.isConnectVPNInApp = true
            }
        } else {
            Constants.isConnectVPNInApp = false
        }
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    private fun initView() {
//        if (AppPurchase.getInstance().isPurchased(context))
//        {
        binding.btnUpgrade.visibility = View.GONE
        binding.layoutTimeBonus.visibility = View.GONE
        binding.tvTimeConnected.visibility = View.GONE
//        }
    }

    private fun initEvent() {
        binding.btnToggleConnect.setOnClickListener {
            if (AppDataUtils.getServerSelected() == null) {
                navigate(R.id.action_homeFragment_to_serversFragment)
            } else {
                when (viewModel.connectVpnStatus.get()) {
                    STATUS_CONNECT -> {
                        isConnectFromButton = true
                        viewModel.connectVpnStatus.set(STATUS_CONNECTING)
                        prepareVpn()
                        binding.btnToggleConnect.playAnimation()
                    }

                    STATUS_CONNECTING ->
                        Toast.makeText(
                            myContext,
                            myContext.getString(R.string.txt_home_connecting),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }
        binding.btnUpgrade.setOnClickListener {
            //showPurchase()
        }
        binding.layoutTimeBonus.setOnClickListener {
            if (AppDataUtils.getIsClickBtnTimeBonus())
                return@setOnClickListener
            if (RemoteUtils.isShowAdsReward60min) {
                /*AppOpenManager.getInstance().disableAppResume()*/
                /*Admod.getInstance().showRewardAds(activity, object : RewardCallback {
                    override fun onUserEarnedReward(var1: RewardItem?) {
                        onUserEarnedReward = true
                    }

                    override fun onRewardedAdClosed() {
                        Admod.getInstance()
                            .initRewardAds(context, BuildConfig.ads_reward_60_min)
                        *//*AppOpenManager.getInstance().enableAppResume()*//*
                            if (onUserEarnedReward) {
                                binding.layoutTimeBonus.setBackgroundResource(R.drawable.bg_grey)
                                binding.imgGift.setImageDrawable(
                                    AppCompatResources.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_gift_grey
                                    )
                                )
                                binding.tvtTimeBonus.setTextColor(
                                    requireContext().resources.getColor(
                                        R.color.grey68
                                    )
                                )
                                binding.tvtAD.backgroundTintList =
                                    ContextCompat.getColorStateList(
                                        requireContext(),
                                        R.color.greyCE
                                    )
                                Constants.remainTime += Constants.MORE_TIME_60
                                startTimerLimitBonus()
                                AppDataUtils.setIsClickBtnTimeBonus(true)
                                onUserEarnedReward = false
                            }
                        }

                        override fun onRewardedAdFailedToShow(codeError: Int) {
                            Log.e(TAG, "onRewardedAdFailedToShow: $codeError")
                            Toast.makeText(
                                context,
                                getString(R.string.please_wait_for_the_ad_to_finish_loading),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })*/
                binding.layoutTimeBonus.setBackgroundResource(R.drawable.bg_grey)
                binding.imgGift.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_gift_grey
                    )
                )
                binding.tvtTimeBonus.setTextColor(
                    requireContext().resources.getColor(
                        R.color.grey68
                    )
                )
                binding.tvtAD.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.greyCE
                    )
                Constants.remainTime += Constants.MORE_TIME_60
                startTimerLimitBonus()
                AppDataUtils.setIsClickBtnTimeBonus(true)
                onUserEarnedReward = false
            } else {
                binding.layoutTimeBonus.setBackgroundResource(R.drawable.bg_grey)
                binding.imgGift.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_gift_grey
                    )
                )
                binding.tvtTimeBonus.setTextColor(requireContext().resources.getColor(R.color.grey68))
                binding.tvtAD.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.greyCE)
                startTimerLimitBonus()
                Constants.remainTime += Constants.MORE_TIME_60
                AppDataUtils.setIsClickBtnTimeBonus(true)
            }
        }

        binding.layoutChangeSever.setOnClickListener {
            if (viewModel.connectVpnStatus.get() != STATUS_CONNECTING) {

                findNavController().navigate(R.id.action_homeFragment_to_serversFragment)

            } else {
                Toast.makeText(
                    requireContext(),
                    getText(R.string.can_not_select_server),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSetting.setOnClickListener {
            //navigate(R.id.action_homeFragment_to_settingFragment)
        }

        binding.btnDisconnect.setOnClickListener {
            when (viewModel.connectVpnStatus.get()) {
                STATUS_CONNECTED -> {
                    binding.layoutTimeBonus.visibility = View.GONE
                    isDisconnectFromButton = true
                    MyApplication.isResetTimeCount = false
                    stopVpn()
//                    if (!AppPurchase.getInstance().isPurchased(context)) {
                    stopTimeCounter()
                    resetTimerLimitBonus()
                    AppDataUtils.setIsClickBtnTimeBonus(false)
//                    }
                }

                STATUS_DISCONNECTING ->
                    Toast.makeText(
                        myContext,
                        myContext.getString(R.string.txt_home_disconnecting),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun startTimerLimitBonus() {
        if (remainingTimeInMillis == 0L) remainingTimeInMillis = START_TIME_IN_MILLIS
        Log.e(TAG, "startTimerLimitBonus: $remainingTimeInMillis")
        countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                AppDataUtils.setIsClickBtnTimeBonus(false)
                resetTimerLimitBonus()
                binding.layoutTimeBonus.setBackgroundResource(R.drawable.background_time_connect)
                binding.imgGift.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_gift
                    )
                )
                binding.tvtTimeBonus.setTextColor(requireContext().resources.getColor(R.color.black))
                binding.tvtTimeBonus.text = getText(R.string.time_bonus)
                binding.tvtAD.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorTitle)
            }
        }.start()
    }

    private fun resetTimerLimitBonus() {
        countDownTimer?.cancel()
        remainingTimeInMillis = START_TIME_IN_MILLIS
        updateCountDownText()
    }

    private fun updateCountDownText() {
        val second = remainingTimeInMillis / 1000 % 60
        val minutes = remainingTimeInMillis / (1000 * 60) % 60
        val timeSecond = if (second < 10) {
            "0$second"
        } else {
            second
        }
        val timeMinutes = if (minutes < 10) {
            "0$minutes"
        } else {
            minutes
        }
        Log.e(TAG, "onTick: $timeMinutes : $timeSecond")
        try {
            binding.tvtTimeBonus.text =
                getString(R.string.time_bonus_value, timeMinutes, timeSecond)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        AppDataUtils.setTimeLeftInMillis(timeLeftInMillis)
        AppDataUtils.setEndTime(System.currentTimeMillis())
        super.onStop()
    }

    //View.VISIBLE = 0, View.GONE = 8
    private fun setConnectButtonVisibility(status: Int) {
        binding.connect.visibility = status
        binding.disconnect.visibility = abs(status - 8)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //Permission granted, start the VPN
                startVpn()
            } else {
                Toast.makeText(
                    myContext,
                    myContext.getString(R.string.txt_home_permission_deny),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // start connect vpn to server
    private fun prepareVpn() {
        if (CheckInternetConnection.netCheck(myContext)) {
            // Checking permission for network monitor
            Log.i(TAG, "start connect vpn to server ")
            val intent = VpnService.prepare(myContext)
            if (intent != null) {
                resultLauncher.launch(intent)
            } else startVpn() //have already permission

            // Update confection status
            status(STATUS_CONNECTING)
        } else {
            // No internet connection available
            (activity as MainActivity).showDialogNoInternet()
        }
    }

    private fun startVpn() {
        try {
            OpenVpnApi.startVpn(
                myContext,
                AppDataUtils.getServerSelected()?.oVpnConfigData,
                AppDataUtils.getServerSelected()?.countryLong,
                "vpn",
                "vpn"
            )
            Log.i(TAG, "startVpn: Connecting...")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
            Log.e(TAG, "startVpn: ${e.message}")
        } finally {
            var time = 0
            timerOut = Timer()
            timerOut!!.schedule(object : TimerTask() {
                override fun run() {
                    time++
                    Log.e(TAG, "run: $time")
                    if (time > timeOut && !isConnectedVPN) {
                        Log.e(TAG, "onCreate: timeOutRunnable")
                        try {
                            requireActivity().runOnUiThread {
                                status(STATUS_CONNECTING_TIMEOUT)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        cancelTimerOut()
                    }
                }

            }, 1000, 1000)
        }
    }

    fun cancelTimerOut() {
        if (timerOut != null) {
            timerOut!!.cancel()
            timerOut = null
        }
    }

    private fun stopVpn() {
        status(STATUS_DISCONNECTING)
        try {
            OpenVPNThread.stop()
            status(STATUS_CONNECT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startTimeCounter() {
        timer = Handler(Looper.myLooper()!!)
        timerRunnable = Runnable {
            activity?.runOnUiThread(object : Runnable {
                override fun run() {
                    //set giá trị cho tv time = Constants.remainTime
                    binding.tvTimeConnected.text = convertSecondsToHMmSs(Constants.remainTime)
                    timer?.postDelayed(this, 1000)
                }
            })
        }
        timer!!.postDelayed(timerRunnable!!, 0)
        val intent = Intent(requireContext(), CountDownService::class.java)
        requireContext().startService(intent)
    }

    private fun convertSecondsToHMmSs(seconds: Long): String {
        val s = seconds % 60
        val m = seconds / 60 % 60
        val h = seconds / (60 * 60) % 24
        return if (h < 10) {
            String.format("0%d:%02d:%02d", h, m, s)
        } else {
            String.format("%d:%02d:%02d", h, m, s)
        }
    }

    private fun stopTimeCounter() {
        if (timer != null) {
            timer!!.removeCallbacks(timerRunnable!!)
            timer = null
            timerRunnable = null
        }
        val intent = Intent(requireContext(), CountDownService::class.java)
        requireContext().stopService(intent)
    }

    private fun status(status: String) {
        when (status.lowercase()) {
            STATUS_DISCONNECTING -> {
                Log.i(TAG, "status: disconnecting")
                if (MyApplication.isDisconnectServerFromTimeCountService) {
                    MyApplication.isResetTimeCount = false
                    MyApplication.isDisconnectServerFromTimeCountService = false
                }
                isConnectedVPN = false
                viewModel.connectVpnStatus.set(STATUS_DISCONNECTING)
            }

            STATUS_DISCONNECTED -> {
                Log.i(TAG, "status: disconnected")
                isConnectedVPN = false
                OpenVPNService.setDefaultStatus()
                viewModel.connectVpnStatus.set(STATUS_CONNECT)

            }

            STATUS_CONNECT -> {
                //khi đã disconnect
                Log.i(TAG, "status: connect")
                isConnectedVPN = false
                isLogEventConnectedServer = false
                viewModel.connectVpnStatus.set(STATUS_CONNECT)
                if (isDisconnectFromButton) {

                    val direction =
                        HomeFragmentDirections.actionHomeFragmentToConnectResultFragment(
                            AppDataUtils.getServerSelected()!!,
                            false
                        )
                    navigate(direction)
                }
            }

            STATUS_CONNECTING, AUTH, GET_CONFIG, TCP_CONNECT -> {
                Log.i(TAG, "status: connecting")
                viewModel.connectVpnStatus.set(STATUS_CONNECTING)
            }

            STATUS_CONNECTED -> {
                Log.i(TAG, "status: connected")
                binding.layoutTimeBonus.visibility = View.VISIBLE
                cancelTimerOut()
                isConnectedVPN = true
                viewModel.connectVpnStatus.set(STATUS_CONNECTED)

                if (AppDataUtils.getServerSelected() != null) {
                    if (!MyApplication.isResetTimeCount) {
                        Constants.remainTime = Constants.DEFAULT_TIME
                    }

                    startTimeCounter()

                    MyApplication.isResetTimeCount = true
                    val direction =
                        HomeFragmentDirections.actionHomeFragmentToConnectResultFragment(
                            AppDataUtils.getServerSelected()!!,
                            true
                        )
                    navigate(direction)
                    if (isLogEventConnectedServer)
                        return
                    AppDataUtils.getServerSelected()?.countryLong?.let {
                        isLogEventConnectedServer = true
                    }
                }
            }

            STATUS_TRY_DIFFERENT_SERVER -> {
                Log.i(TAG, "status: tryDifferentServer")
                viewModel.connectVpnStatus.set(STATUS_TRY_DIFFERENT_SERVER)
            }

            STATUS_LOADING -> {
                Log.i(TAG, "Loading Server..")
                viewModel.connectVpnStatus.set(STATUS_LOADING)
            }

            STATUS_INVALID_DEVICE -> {
                Log.i(TAG, "Invalid Device")
                viewModel.connectVpnStatus.set(STATUS_INVALID_DEVICE)
            }

            STATUS_AUTHENTICATION_CHECK -> {
                Log.i(TAG, "Authentication \n Checking...")
                viewModel.connectVpnStatus.set(STATUS_AUTHENTICATION_CHECK)
            }

            STATUS_CONNECTING_TIMEOUT -> {
                Log.i(TAG, "connecting timeout")
                MyApplication.isResetTimeCount = false
                stopVpn()
                viewModel.connectVpnStatus.set(STATUS_CONNECT)
                binding.btnToggleConnect.setAnimation(R.raw.anim_connect)
                binding.btnToggleConnect.frame = 0
                Toast.makeText(
                    myContext,
                    getText(R.string.toast_time_out),
                    Toast.LENGTH_SHORT
                ).show()
            }

            STATUS_NO_NETWORK -> {
                if (!CheckInternetConnection.netCheck(myContext)) {
                    Log.i(TAG, "No network")
                    stopVpn()
                    (activity as MainActivity).showDialogNoInternet()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isDestroyView = true
    }

}