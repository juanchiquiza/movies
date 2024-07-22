package com.users.onboarding.koin

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.users.onboarding.data.database.AppDatabase
import com.users.onboarding.data.network.RetrofitHelper
import com.users.onboarding.data.network.RetrofitHelperImpl
import com.users.onboarding.data.repository.FirebaseRepository
import com.users.onboarding.data.repository.MovieRepository
import com.users.onboarding.data.network.TmApi
import com.users.onboarding.data.repository.MovieDbRepository
import com.users.onboarding.utils.UtilsWithContextRequired
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun <S> provideApiService(retrofitHelper: RetrofitHelper, service: Class<S>): S {
    return retrofitHelper.buildRetrofitInstance(
        baseURL = "https://api.themoviedb.org/3/",
    ).create(service)
}

fun provideFirebaseStorageInstance(): FirebaseStorage {
    return Firebase.storage
}

fun provideFirebaseFireStoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}

val firebase = module {
    single { provideFirebaseStorageInstance() }
    single { provideFirebaseFireStoreInstance() }
}

val serviceModule = module {
    single<RetrofitHelper> { RetrofitHelperImpl() }
    single { provideApiService(get(), TmApi::class.java) }
}

val repositoryModule = module {
    single { MovieRepository(service = get()) }
    single { MovieDbRepository(movieDao = get()) }
    single { FirebaseRepository(firebaseStorage = get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database")
            .build()
    }
    single { get<AppDatabase>().movieDao() }
}

val commonModule = module {
    single { UtilsWithContextRequired(context = androidApplication()) }
    single { LocationServices.getFusedLocationProviderClient(get<Context>()) }
}

val coreModule = serviceModule + repositoryModule + commonModule + firebase + databaseModule