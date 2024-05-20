package com.skunpham.vpn.proxy.unblock.vpnpro.ui.servers.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseHolder
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.AdapterSubServerBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.RemoteUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Locale

class SubServerAdapter(
    private val context: Context,
    private val iServerSelect: ServersAdapter.IServerSelect?,
    private val listServer: MutableList<Server>,
) : RecyclerView.Adapter<SubServerAdapter.SubServerViewHolder>() {
    private lateinit var binding: AdapterSubServerBinding
    private var disposable = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubServerViewHolder {
        binding = AdapterSubServerBinding.inflate(LayoutInflater.from(context), parent, false)
        return SubServerViewHolder(disposable)
    }

    override fun getItemCount(): Int {
        return listServer.size
    }

    override fun onBindViewHolder(holder: SubServerViewHolder, position: Int) {
        holder.onBind(listServer[position], position, null)
        holder.setIsRecyclable(false)
    }

    inner class SubServerViewHolder(
        private val disposable: CompositeDisposable,
    ) : BaseHolder<AdapterSubServerBinding>(binding) {
        override fun onBind(item: Any, position: Int, category: String?) {
            super.onBind(item, position, category)

            if (item is Server) {
                binding.rlSubServer.setOnClickListener {
                    iServerSelect?.onSelect(item)
                }

                if (RemoteUtils.isShowLabelVipOrFree) {
                    val drawableId = if (item.isVip) {
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

                val observable = Observable.create(ObservableOnSubscribe<Server> { emitter ->
                    emitter.onNext(item)
                    emitter.onComplete()
                })
                disposable.add(
                    observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe({
                            try {
                                if (item.ping!!.toInt() > 20) {
                                    binding.imgNetworkIcon.setImageDrawable(
                                        AppCompatResources.getDrawable(
                                            context,
                                            R.drawable.ic_wifi_3_bar
                                        )
                                    )
                                } else {
                                    binding.imgNetworkIcon.setImageDrawable(
                                        AppCompatResources.getDrawable(
                                            context,
                                            R.drawable.ic_wifi_4_bar
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                            }

                            Glide.with(context)
                                .asBitmap()
                                .load(
                                    Uri.parse(
                                        "file:///android_asset/flags/" + item.countryShort?.lowercase(
                                            Locale.getDefault()
                                        ) + ".png"
                                    )
                                )
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transition(GenericTransitionOptions.with(R.anim.alpha_animation))
                                .into(binding.imgSubNation)

                            binding.txtSubNationName.text =
                                if (item.isVip) item.city else if (item.city != null) item.city else item.countryLong

                            binding.txtPing.text =
                                context.getString(R.string.format_ping, item.ping)


                            binding.txtSubAds.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_shape_background_ad
                            )

//                                binding.txtSubAds.background = null


                        }, {

                        })
                )
            }
        }
    }

}