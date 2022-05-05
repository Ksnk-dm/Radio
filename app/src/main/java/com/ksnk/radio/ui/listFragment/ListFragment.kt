package com.ksnk.radio.ui.listFragment

import android.content.*
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
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.main.MainViewModel
import com.ksnk.radio.ui.listFragment.adapter.ListFragmentRecyclerViewAdapter
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ListFragment : Fragment() {


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: ListFragmentRecyclerViewAdapter
    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()

    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        items = viewModel.getAll().toMutableList()
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPlayerService()
        mRecyclerView = view.findViewById(R.id.list_fragment_recycler_view)
        mGridLayoutManager = GridLayoutManager(activity, 1)
        mRecyclerView.layoutManager = mGridLayoutManager



    }

    companion object

    fun newInstance(): ListFragment {
        return ListFragment()
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            mAdapter = ListFragmentRecyclerViewAdapter(items, activity?.applicationContext, mExoPlayer!!, mPlayerService!!)
            mRecyclerView.adapter = mAdapter
            mPlayerService?.initNotification()

        }

        override fun onServiceDisconnected(className: ComponentName) {
            mPlayerService = null
            mExoPlayer = null
        }
    }

    private fun startPlayerService() {
        val intent = Intent(requireContext(), PlayerService::class.java)
        requireActivity().bindService(intent, myConnection, AppCompatActivity.BIND_AUTO_CREATE)
        requireActivity().startService(intent)
    }
}