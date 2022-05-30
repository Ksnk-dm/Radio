package com.ksnk.radio.ui.favoriteFragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.ksnk.radio.listeners.MenuItemIdListener
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.enums.DisplayListType
import com.ksnk.radio.helper.PreferenceHelper
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.listFragment.adapter.ListFragmentRecyclerViewAdapter
import com.ksnk.radio.ui.main.MainViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FavoriteFragment : Fragment(), MenuItemIdListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: ListFragmentRecyclerViewAdapter
    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    private lateinit var displayListType: DisplayListType
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null

    @Inject
    lateinit var preferencesHelper: PreferenceHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        items = viewModel.getFavoriteRadioWave().toMutableList()
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_fragment, container, false);
    }

    private fun init(view: View) {
        mRecyclerView = view.findViewById(R.id.favoriteRecyclerView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPlayerService()
        init(view)
        displayListType = preferencesHelper.getDisplayListType()
        mGridLayoutManager = when (displayListType) {
            DisplayListType.List -> {
                GridLayoutManager(activity, 1)
            }
            DisplayListType.Grid -> {
                GridLayoutManager(activity, 2)
            }
        }
        mRecyclerView.layoutManager = mGridLayoutManager
    }

    private fun startPlayerService() {
        val intent = Intent(requireContext(), PlayerService::class.java)
        requireActivity().bindService(intent, myConnection, AppCompatActivity.BIND_AUTO_CREATE)
        requireActivity().startService(intent)
    }

    companion object

    fun newInstance(): FavoriteFragment {
        return FavoriteFragment()
    }

    private fun initRecycler() {
        mAdapter = ListFragmentRecyclerViewAdapter(
            items,
            activity?.applicationContext,
            mExoPlayer!!,
            mPlayerService!!,
            this@FavoriteFragment
        )
        mRecyclerView.adapter = mAdapter
        mAdapter.setDisplayListType(displayListType)
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            initRecycler()
            mPlayerService?.initNotification()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mPlayerService = null
            mExoPlayer = null
        }
    }


    override fun getItemMenu(id: Int?) {

    }

    override fun updateCountOpenItem(id: Int?) {

    }
}