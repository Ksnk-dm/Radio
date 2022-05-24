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
import com.ksnk.radio.enums.DisplayListType
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
    private lateinit var sortNameRadioGroup: RadioGroup
    private lateinit var hideBottomSheetImageButton: ImageButton
    private var checkStateSwitch: Boolean = false

    @Inject
    lateinit var preferencesHelper: PreferenceHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var displayListType:DisplayListType
    @Inject
    lateinit var viewModel: MainViewModel

    private var defaultRadioButtonStatus: Boolean = true
    private var ascRadioButtonStatus: Boolean = false
    private var descRadioButtonStatus: Boolean = false
    private var popularRadioButtonStatus: Boolean = false
    private var notPopularRadioButtonStatus: Boolean = false

    var defaultListItem: List<RadioWave> = mutableListOf<RadioWave>()
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPlayerService()
        init(view)
        initListeners()
        loadPrefsAndUpdateRadioButton()
        initRecycler()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    private fun init(view: View) {
        switch = view.findViewById(R.id.switchMyStation)
        hideBottomSheetImageButton = view.findViewById(R.id.hideBottomSheetImageButton)
        sortNameRadioGroup = view.findViewById(R.id.sortNameRadioGroup)
        sortImageButton = view.findViewById(R.id.sortImageButton)
        mRecyclerView = view.findViewById(R.id.list_fragment_recycler_view)
        bottomSheet = view.findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    }

    private fun initListeners() {
        hideBottomSheetImageButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        switch.setOnClickListener {
            switchIsChecked()
        }
        sortNameRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.radioButtonDefault -> {
                    defaultSetPrefsAndUpdateRv()
                }
                R.id.radioButtonAsc -> {
                    ascSetPrefsAndUpdateRv()
                }
                R.id.radioButtonDesc -> {
                    descSetPrefsAndUpdateRv()
                }
                R.id.popularRadioButton -> {
                    popularSetPrefsAndUpdateRv()
                }
                R.id.notPopularRadioButton -> {
                    notPopularSetPrefsAndUpdateRv()
                }
            }
        }
        sortImageButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun switchIsChecked() {
        if (switch.isChecked) {
            preferencesHelper.setSwitchEnabled(true)
            defaultListItem = viewModel.getCustomAll()
            updateRecyclerView(defaultListItem)
        } else {
            preferencesHelper.setSwitchEnabled(false)
            defaultListItem = viewModel.getAll()
            updateRecyclerView(defaultListItem)
        }
    }

    private fun defaultSetPrefsAndUpdateRv() {
        preferencesHelper.setDefaultSortStatus(true)
        preferencesHelper.setSortAscStatus(false)
        preferencesHelper.setSortDescStatus(false)
        preferencesHelper.setSortPopularStatus(false)
        preferencesHelper.setSortNotPopularStatus(false)
    }

    private fun ascSetPrefsAndUpdateRv() {
        preferencesHelper.setDefaultSortStatus(false)
        preferencesHelper.setSortAscStatus(true)
        preferencesHelper.setSortDescStatus(false)
        preferencesHelper.setSortPopularStatus(false)
        preferencesHelper.setSortNotPopularStatus(false)
    }

    private fun descSetPrefsAndUpdateRv() {
        preferencesHelper.setDefaultSortStatus(false)
        preferencesHelper.setSortAscStatus(false)
        preferencesHelper.setSortDescStatus(true)
        preferencesHelper.setSortPopularStatus(false)
        preferencesHelper.setSortNotPopularStatus(false)
    }

    private fun popularSetPrefsAndUpdateRv() {
        preferencesHelper.setSortPopularStatus(true)
        preferencesHelper.setSortNotPopularStatus(false)
        preferencesHelper.setDefaultSortStatus(false)
        preferencesHelper.setSortAscStatus(false)
        preferencesHelper.setSortDescStatus(false)
    }

    private fun notPopularSetPrefsAndUpdateRv() {
        preferencesHelper.setSortPopularStatus(false)
        preferencesHelper.setSortNotPopularStatus(true)
        preferencesHelper.setDefaultSortStatus(false)
        preferencesHelper.setSortAscStatus(false)
        preferencesHelper.setSortDescStatus(false)
    }

    private fun setDefaultStatusAndUpdateUI() {
        defaultRadioButtonStatus = preferencesHelper.getDefaultSortStatus()
        if (defaultRadioButtonStatus) {
            sortNameRadioGroup.check(R.id.radioButtonDefault)
            items = viewModel.getAll().toMutableList()
        }
    }

    private fun setAscStatusAndUpdateUI() {
        ascRadioButtonStatus = preferencesHelper.getSortAscStatus()
        if (ascRadioButtonStatus) {
            sortNameRadioGroup.check(R.id.radioButtonAsc)
            items = viewModel.getAllSortAsc().toMutableList()
        }
    }

    private fun setDescStatusAndUpdateUI() {
        descRadioButtonStatus = preferencesHelper.getSortDescStatus()
        if (descRadioButtonStatus) {
            sortNameRadioGroup.check(R.id.radioButtonDesc)
            items = viewModel.getAllSortDesc().toMutableList()
        }
    }

    private fun setPopularStatusAndUpdateUI() {
        popularRadioButtonStatus = preferencesHelper.getSortPopularStatus()
        if (popularRadioButtonStatus) {
            sortNameRadioGroup.check(R.id.popularRadioButton)
            items = viewModel.getPopularDesc().toMutableList()
        }
    }

    private fun setNotPopularStatusAndUpdateUI() {
        notPopularRadioButtonStatus = preferencesHelper.getSortNotPopularStatus()
        if (notPopularRadioButtonStatus) {
            sortNameRadioGroup.check(R.id.notPopularRadioButton)
            items = viewModel.getPopularAsc().toMutableList()
        }
    }

    private fun loadPrefsAndUpdateRadioButton() {
        setDefaultStatusAndUpdateUI()
        setAscStatusAndUpdateUI()
        setDescStatusAndUpdateUI()
        setPopularStatusAndUpdateUI()
        setNotPopularStatusAndUpdateUI()
    }

    private fun initRecycler() {
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
            initAdapter()
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

    private fun initListenersAlertDialog(
        updateButton: ImageButton, nameEditText: EditText,
        urlEditText: EditText, radioWave: RadioWave, delButton: ImageButton, builder: AlertDialog
    ) {
        updateButton.setOnClickListener {
            if (nameEditText.text.trim() { it <= ' ' }
                    .isEmpty() || urlEditText.text.trim() { it <= ' ' }.isEmpty()) {
                Toast.makeText(activity, getText(R.string.empty_edit_text), Toast.LENGTH_SHORT)
                    .show()
            } else {
                radioWave.name = nameEditText.text.toString()
                radioWave.image = getString(R.string.default_logo_url)
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
        initListenersAlertDialog(
            updateButton,
            nameEditText,
            urlEditText,
            radioWave,
            delButton,
            builder
        )
        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    override fun update() {
        initAdapter()
    }

    override fun search(textSearch: String?) {
        textSearch?.let {
            items.forEach { radioWave ->
                if (radioWave.name!!.contains(textSearch, true) ||
                    radioWave.name.toString().contains(textSearch, true)
                ) {
                    matchedRadioWave.add(radioWave)
                }
            }
            updateRecyclerViewSearch(matchedRadioWave)
            if (matchedRadioWave.isEmpty()) {
                Toast.makeText(activity, getText(R.string.no_match), Toast.LENGTH_SHORT).show()
            }
            updateRecyclerViewSearch(matchedRadioWave)
        }
    }

    private fun updateRecyclerView(updateList: List<RadioWave>) {
        mRecyclerView.apply {
            mAdapter.setItems(updateList)
            mAdapter.notifyDataSetChanged()
        }
    }
    private fun updateRecyclerViewSearch(updateList: List<RadioWave>) {
        mAdapter.clearItems()
        mRecyclerView.apply {
            mAdapter.setItems(updateList)
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun initAdapter() {
        mAdapter = ListFragmentRecyclerViewAdapter(
            items,
            activity?.applicationContext,
            mExoPlayer!!,
            mPlayerService!!,
            this@ListFragment
        )
        mRecyclerView.adapter = mAdapter
        mAdapter.setDisplayListType(displayListType)
    }
}