package com.ksnk.radio.ui.playerFragment

import android.animation.Animator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.main.MainViewModel
import com.squareup.picasso.Picasso
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import kotlin.properties.Delegates


class PlayerFragment : Fragment() {
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null
    private lateinit var mPlayerView: PlayerControlView
    private lateinit var mPosterImageView: ImageView
    private lateinit var mNameTextView: TextView
    private lateinit var mFmFrequencyTextView: TextView
    private lateinit var radioWave: RadioWave
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var mVisualizer: BarVisualizer
    private var audioSessionId by Delegates.notNull<Int>()
    private lateinit var favoriteImageButton: ImageButton

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            mPlayerView.player = mExoPlayer
            Picasso.get()
                .load(mPlayerService?.getRadioWave()?.image)
                .into(mPosterImageView)
            mNameTextView.text = mPlayerService?.getRadioWave()?.name
            mFmFrequencyTextView.text = mPlayerService?.getRadioWave()?.fmFrequency

            radioWave = mPlayerService?.getRadioWave()!!

            if (mPlayerService?.getRadioWave()?.favorite == true) {
                favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
            } else {
                favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            if (mPlayerService == null) return
            audioSessionId = mExoPlayer!!.audioSessionId


            try {
                mVisualizer.setAudioSessionId(audioSessionId)
            } catch (e: Exception) {
                mVisualizer.release()
                mVisualizer.setAudioSessionId(audioSessionId)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPlayerService()
        mPlayerView = view.findViewById(R.id.playerView)
        mPosterImageView = view.findViewById(R.id.imageViewPoster)
        mNameTextView = view.findViewById(R.id.nameTextView)
        mFmFrequencyTextView = view.findViewById(R.id.fmFrequencyTextView)
        mVisualizer = view.findViewById(R.id.bar)
        lottieAnimationView = view.findViewById(R.id.favAnimationView)
        favoriteImageButton = view.findViewById(R.id.favoriteImageButton)
        favoriteImageButton.setOnClickListener {

            if (radioWave.favorite == false) {
                radioWave.favorite = true
                viewModel.updateRadioWave(radioWave)
                favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
                lottieAnimationView.visibility = View.VISIBLE
                lottieAnimationView.playAnimation()
            } else {
                radioWave.favorite = false
                viewModel.updateRadioWave(radioWave)
                favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
            lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    lottieAnimationView.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }


    }

    companion object

    fun newInstance(): PlayerFragment {
        return PlayerFragment()
    }
}