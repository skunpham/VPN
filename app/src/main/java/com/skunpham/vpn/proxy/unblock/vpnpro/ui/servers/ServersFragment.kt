package com.skunpham.vpn.proxy.unblock.vpnpro.ui.servers

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skunpham.vpn.proxy.unblock.vpnpro.MyApplication
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseFragment
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.FragmentHomeBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.FragmentServersBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.model.GroupServer
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity.Companion.isClickSelectServer
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity.Companion.isConnectFromButton
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.servers.adapter.ServersAdapter
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.RemoteUtils
import java.util.*

class ServersFragment : BaseFragment() {
    companion object {
        const val TAG = "ServersFragment"
        fun newInstance() = ServersFragment()
    }

    private lateinit var binding : FragmentServersBinding

    private val viewModel by lazy {
        ServersViewModel()
    }

    private lateinit var serversAdapter: ServersAdapter
    private var listGroupServer: ArrayList<GroupServer> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.viewModel = viewModel
    }

    private fun init() {
        Log.d(TAG, "init: ")
        initRecycler()
        initObserve()
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.getDefault()).trim()
                viewModel.queryServer(query)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.layoutParent.setOnClickListener {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

    }

    private fun initRecycler() {
        serversAdapter = ServersAdapter(requireContext())
        binding.rvServers.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvServers.adapter = serversAdapter
        serversAdapter.iServerSelect = object : ServersAdapter.IServerSelect {
            override fun onSelect(item: Server) {
                isCheckClickSelectSVConnectFrBtnSetIsDefault(item)
                navigateToHome()
            }

            override fun onSelectGroupServer(position: Int) {
                if (position + 1 == listGroupServer.size) {
                    (binding.rvServers.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        position,
                        0
                    )
                } else {
                    binding.rvServers.scrollToPosition(
                        position + 1,
                    )
                }

            }
        }
    }

    private fun isCheckClickSelectSVConnectFrBtnSetIsDefault(item: Server) {
        isClickSelectServer = true
        isConnectFromButton = false
        MyApplication.selectedServer.value = item
    }

    private fun navigateToHome() {
        navigate(R.id.action_serversFragment_to_homeFragment)
    }

    private fun initObserve() {
        viewModel.getListServer(false)

        viewModel.dataGroupServerFreeResponseLiveData.observe(this) {
            if (it.size > 0) {
                binding.tvtEmpty.visibility = View.GONE
                binding.rvServers.visibility = View.VISIBLE
                serversAdapter.setData(it)
                listGroupServer.addAll(it)
            } else {
                binding.tvtEmpty.visibility = View.VISIBLE
                binding.rvServers.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvServers.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvServers.visibility = View.VISIBLE
            }
        }
    }
}