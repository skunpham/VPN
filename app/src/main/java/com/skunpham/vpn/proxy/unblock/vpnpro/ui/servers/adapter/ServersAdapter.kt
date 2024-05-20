package com.skunpham.vpn.proxy.unblock.vpnpro.ui.servers.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseHolder
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.AdapterGroupServerBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.model.GroupServer
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.RemoteUtils
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.anim.SubServerViewAnim
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class ServersAdapter(private val context: Context) :
    RecyclerView.Adapter<ServersAdapter.ServersViewHolder>() {
    private var listGroupServer: MutableList<GroupServer> = mutableListOf()
    private lateinit var binding: AdapterGroupServerBinding
    private var subServerAdapter: SubServerAdapter? = null
    private var disposable = CompositeDisposable()
    var iServerSelect: IServerSelect? = null
    private var heightOfViewParam = context.resources.getDimension(R.dimen._46sdp).toInt()
    private var paddingBottomView = context.resources.getDimension(R.dimen._10sdp).toInt()
    private var animDuration = 500
    private var expandRotate = 90f
    private var lineAlpha = 0.3f
    private var currentPosition = -1

    private fun initSubServer(holder: AdapterGroupServerBinding, groupServer: GroupServer) {
        if (groupServer.subServerAdapter == null) {
            groupServer.subServerAdapter =
                SubServerAdapter(context, iServerSelect, groupServer.listServer)
            holder.rvSubServer.adapter = groupServer.subServerAdapter
        } else {
            holder.rvSubServer.adapter = groupServer.subServerAdapter
        }
//        subServerAdapter = SubServerAdapter(context, iServerSelect, groupServer.listServer)
//        holder.rvSubServer.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//        holder.rvSubServer.adapter = subServerAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServersViewHolder {
        binding = AdapterGroupServerBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.rvSubServer.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        return ServersViewHolder(disposable)
    }

    override fun getItemCount(): Int {
        return listGroupServer.size
    }

    override fun onBindViewHolder(holder: ServersViewHolder, position: Int) {
        holder.onBind(listGroupServer[position], position, null)
        holder.setIsRecyclable(false)
    }

    inner class ServersViewHolder(
        private val disposable: CompositeDisposable,
    ) : BaseHolder<AdapterGroupServerBinding>(binding) {
        override fun onBind(item: Any, position: Int, category: String?) {
            super.onBind(item, position, category)

            if (item is GroupServer) {
                if (currentPosition == position && currentPosition >= 0) {
                    binding.frmSubServer.visibility = View.VISIBLE
                    binding.imgExtend.rotation = expandRotate
                    binding.vLine.alpha = lineAlpha
                } else {
                    binding.frmSubServer.visibility = View.GONE
                    binding.imgExtend.rotation = 0f
                    binding.vLine.alpha = 0f
                }

                if (RemoteUtils.isShowLabelVipOrFree) {
                    val drawableId = if (item.listServer[0].isVip) {
                        R.drawable.ic_vip_server
                    } else {
                        R.drawable.ic_free_server
                    }
                    binding.imgServerType.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            drawableId
                        )
                    )
                    binding.imgServerType.visibility = View.VISIBLE
                }

                binding.rlServer.setOnClickListener {
                    currentPosition = position
                    if (!item.isExpand) {
                        iServerSelect?.onSelectGroupServer(position)
                        item.isExpand = !item.isExpand
                        binding.imgExtend.rotation = expandRotate
                        binding.vLine.alpha = lineAlpha
                        SubServerViewAnim.expand(
                            binding.frmSubServer,
                            animDuration,
                            heightOfViewParam * item.listServer.count() + paddingBottomView
                        )
                    } else {
                        currentPosition = -1
                        item.isExpand = !item.isExpand
                        binding.imgExtend.rotation = 0f
                        binding.vLine.alpha = 0f
                        SubServerViewAnim.expand(binding.frmSubServer, animDuration, 0)
                    }
                }

                initSubServer(binding, item)

                Glide.with(context)
                    .asBitmap()
                    .load(
                        Uri.parse(
                            "file:///android_asset/flags/" + item.listServer[0].countryShort?.lowercase(
                                Locale.getDefault()
                            ) + ".png"
                        )
                    )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(GenericTransitionOptions.with(R.anim.alpha_animation))
                    .into(binding.imgNation)

                binding.txtNationName.text = item.title

                binding.txtStatus.text = when {
                    item.isAll -> context.resources.getString(R.string.connect_to_your_choice)
                    item.listServer[0].isVip -> context.resources.getString(R.string.server_status_premium)
                    else -> context.resources.getString(R.string.server_status_free)
                }
            }
        }
    }

    fun setData(_listGroupServer: MutableList<GroupServer>) {
        currentPosition = -1
        listGroupServer.clear()
        listGroupServer.addAll(_listGroupServer)
        notifyDataSetChanged()
    }

    interface IServerSelect {
        fun onSelect(item: Server)
        fun onSelectGroupServer(position: Int)
    }
}