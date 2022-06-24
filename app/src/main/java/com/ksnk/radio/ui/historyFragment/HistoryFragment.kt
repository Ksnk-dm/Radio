package com.ksnk.radio.ui.historyFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.data.entity.Track
import com.ksnk.radio.enums.DisplayListType
import com.ksnk.radio.ui.favoriteFragment.FavoriteFragment
import com.ksnk.radio.ui.historyFragment.adapter.HistoryFragmentRecyclerViewAdapter
import com.ksnk.radio.ui.listFragment.adapter.ListFragmentRecyclerViewAdapter
import com.ksnk.radio.ui.main.MainViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class HistoryFragment : Fragment() {
    private var historyRecyclerView: RecyclerView?=null
    private var items: MutableList<Track> = mutableListOf<Track>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    private var mGridLayoutManager: GridLayoutManager? = null
    private  var mAdapter: HistoryFragmentRecyclerViewAdapter?=null
    private var displayListType: DisplayListType?=null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        items = viewModel.getAllTracks().toMutableList()
        super.onAttach(context)
    }

    private fun setDisplayListType() {
        mGridLayoutManager = GridLayoutManager(activity, 1)
        historyRecyclerView?.layoutManager = mGridLayoutManager
    }

    private fun initRecycler() {
        mAdapter = HistoryFragmentRecyclerViewAdapter(
            items,
            activity?.applicationContext
        )
        historyRecyclerView?.adapter = mAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setDisplayListType()
        initRecycler()

    }

    private fun init(view: View) {
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
    }

    companion object

    fun newInstance(): HistoryFragment {
        return HistoryFragment()
    }
}
