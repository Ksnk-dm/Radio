package com.ksnk.radio.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ksnk.radio.data.dao.RadioWaveDao
import com.ksnk.radio.data.dao.TrackDao
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.data.entity.Track

@Database(
    version = 1,
    exportSchema = false,
    entities = [(RadioWave::class), (Track::class)]
)

abstract class AppDataBase : RoomDatabase() {
    abstract fun getRadioWaveDao(): RadioWaveDao?
    abstract fun getTrackDao(): TrackDao?

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getDatabase(context: Context?): AppDataBase? {
            if (INSTANCE == null) {
                synchronized(AppDataBase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context!!,
                            AppDataBase::class.java,
                            "APP_DB"
                        ).allowMainThreadQueries()
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                }

                                override fun onOpen(db: SupportSQLiteDatabase) {
                                    super.onOpen(db)
                                }
                            })
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}