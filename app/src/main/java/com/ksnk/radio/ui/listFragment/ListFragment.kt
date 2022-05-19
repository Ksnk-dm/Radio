package com.ksnk.radio.ui.listFragment

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ksnk.radio.listeners.FragmentSettingListener
import com.ksnk.radio.listeners.MenuItemIdListener
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.helper.PreferenceHelper
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.listFragment.adapter.ListFragmentRecyclerViewAdapter
import com.ksnk.radio.ui.main.MainActivity
import com.ksnk.radio.ui.main.MainViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ListFragment : Fragment(), MenuItemIdListener, FragmentSettingListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: ListFragmentRecyclerViewAdapter
    private lateinit var sortImageButton: ImageButton
    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    private var matchedRadioWave: ArrayList<RadioWave> = arrayListOf()
    private lateinit var switch: SwitchMaterial

    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var sortAscImageButton: ImageButton
    private lateinit var sortNameRadioGroup: RadioGroup
    private lateinit var defaultRadioButton: RadioButton
    private var checkStateSwitch: Boolean = false

    @Inject
    lateinit var preferencesHelper: PreferenceHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    var defaultListItem: List<RadioWave> = mutableListOf<RadioWave>()
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
        switch = view.findViewById(R.id.switchMyStation)


        sortNameRadioGroup = view.findViewById(R.id.sortNameRadioGroup)
        sortNameRadioGroup.check(R.id.radioButtonDesc)
        sortNameRadioGroup.setOnCheckedChangeListener { _, i ->

            when (i) {
                R.id.radioButtonDefault -> {
                    defaultListItem = viewModel.getAll()
                    updateRecyclerView(defaultListItem)
                }
                R.id.radioButtonAsc -> {
                    if (switch.isChecked) {
                        defaultListItem = viewModel.getCustomSortAsc()
                        updateRecyclerView(defaultListItem)
                    } else {
                        defaultListItem = viewModel.getAllSortAsc()
                        updateRecyclerView(defaultListItem)
                    }
                }
                R.id.radioButtonDesc -> {
                    if (switch.isChecked) {
                        defaultListItem = viewModel.getCustomSortDesc()
                        updateRecyclerView(defaultListItem)
                    } else {
                        defaultListItem = viewModel.getAllSortDesc()
                        updateRecyclerView(defaultListItem)
                    }
                }

            }

        }
        switch.setOnClickListener {
            if (switch.isChecked) {
                preferencesHelper.setSwitchEnabled(true)
            } else {
                preferencesHelper.setSwitchEnabled(false)
            }
        }
        sortImageButton = view.findViewById(R.id.sortImageButton)
        sortImageButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        sortAscImageButton = view.findViewById(R.id.sortAscImageButton)
        sortAscImageButton.setOnClickListener {
            val sortAscList: List<RadioWave> = viewModel.getAllSortAsc()
            updateRecyclerView(sortAscList)
        }
        mRecyclerView = view.findViewById(R.id.list_fragment_recycler_view)
        bottomSheet = view.findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mGridLayoutManager = GridLayoutManager(activity, 1)
        mRecyclerView.layoutManager = mGridLayoutManager
        checkStateSwitch = preferencesHelper.getSwitchEnabled()


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.setSettingListener(this@ListFragment)

    }

    companion object

    fun newInstance(): ListFragment {
        return ListFragment()
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            mAdapter = ListFragmentRecyclerViewAdapter(
                items,
                activity?.applicationContext,
                mExoPlayer!!,
                mPlayerService!!,
                this@ListFragment
            )
            mRecyclerView.adapter = mAdapter
            mPlayerService?.initNotification()
            if (checkStateSwitch) {
                switch.isChecked = true
                defaultListItem = viewModel.getCustomAll()

                updateRecyclerView(defaultListItem)


            } else {

                switch.isChecked = false
                defaultListItem = viewModel.getAll()
                updateRecyclerView(defaultListItem)
            }

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

    override fun getItemMenu(id: Int?) {
        val radioWave: RadioWave = viewModel.getRadioWaveForId(id)
        createUpdateOrDeleteRadioWaveAlertDialog(radioWave)
    }

    override fun updateCountOpenItem(id: Int?) {
        val radioWave: RadioWave = viewModel.getRadioWaveForId(id)
        val count = radioWave.countOpen?.plus(1)
        count?.plus(1)
        radioWave.countOpen = count
        viewModel.updateRadioWave(radioWave)
    }

    private fun createUpdateOrDeleteRadioWaveAlertDialog(radioWave: RadioWave) {
        val builder = AlertDialog.Builder(requireContext())
            .create()
        val view = layoutInflater.inflate(R.layout.add_update_radio_wave_alert_dialog, null)
        val updateButton = view.findViewById<ImageButton>(R.id.saveButton)
        val nameEditText = view.findViewById<EditText>(R.id.name_edit_text)
        val delButton = view.findViewById<ImageButton>(R.id.delButton)
        delButton.visibility = View.VISIBLE
        nameEditText.setText(radioWave.name)
        val urlEditText = view.findViewById<EditText>(R.id.url_edit_text)
        urlEditText.setText(radioWave.url)
        updateButton.setOnClickListener {
            if (nameEditText.text.trim() { it <= ' ' }
                    .isEmpty() || urlEditText.text.trim() { it <= ' ' }.isEmpty()) {
                Toast.makeText(activity, "text", Toast.LENGTH_SHORT).show()
            } else {
                radioWave.name = nameEditText.text.toString()
                radioWave.image = "https://cdn-icons-png.flaticon.com/512/186/186054.png"
                radioWave.custom = true
                radioWave.url = urlEditText.text.toString()
                viewModel.updateRadioWave(radioWave)
                builder.dismiss()
                initAdapter()
            }


        }
        delButton.setOnClickListener {
            viewModel.delete(radioWave)
            builder.dismiss()
            initAdapter()
        }
        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    override fun update() {
        initAdapter()
    }

    override fun search(textSearch: String?) {
        matchedRadioWave = arrayListOf()

        textSearch?.let {
            items.forEach { radioWave ->
                if (radioWave.name!!.contains(textSearch, true) ||
                    radioWave.name.toString().contains(textSearch, true)
                ) {
                    matchedRadioWave.add(radioWave)
                }
            }
            updateRecyclerView(matchedRadioWave)
            if (matchedRadioWave.isEmpty()) {
                Toast.makeText(activity, "No match found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView(matchedRadioWave)
        }
    }

    private fun updateRecyclerView(updateList: List<RadioWave>) {
        mRecyclerView.apply {
            mAdapter.setItems(updateList)
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun initAdapter() {
        items = viewModel.getAll().toMutableList()
        mAdapter = ListFragmentRecyclerViewAdapter(
            items,
            activity?.applicationContext,
            mExoPlayer!!,
            mPlayerService!!,
            this@ListFragment
        )
        mRecyclerView.adapter = mAdapter
    }
}