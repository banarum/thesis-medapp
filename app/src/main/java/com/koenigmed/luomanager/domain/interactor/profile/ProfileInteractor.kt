package com.koenigmed.luomanager.domain.interactor.profile

import com.koenigmed.luomanager.data.mapper.user.UserMapper
import com.koenigmed.luomanager.data.repository.prefs.PrefsRepository
import com.koenigmed.luomanager.data.repository.profile.IUserRepository
import com.koenigmed.luomanager.extension.removeMeasure
import com.koenigmed.luomanager.presentation.mvp.profile.ProfileFeelsState
import com.koenigmed.luomanager.presentation.mvp.profile.UserProfilePresentation
import com.koenigmed.luomanager.presentation.mvp.profile_edit.UserDataPresentation
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

class ProfileInteractor
@Inject constructor(
        private val userRepository: IUserRepository,
        private val userMapper: UserMapper,
        private val prefsRepository: PrefsRepository
) {

    fun getUserData() = userRepository.getUserData()
            .map { userMapper.mapToPresentation(it) }

    fun getUserProfileData() : Single<UserProfilePresentation> {
        val showFeels = Duration.between(Instant.ofEpochSecond(prefsRepository.feelsAnsweredDate), Instant.now()) > Duration.ofDays(1)
        val showPainLevel = Duration.between(Instant.ofEpochSecond(prefsRepository.painLevelAnsweredDate), Instant.now()) > Duration.ofDays(1)
        return userRepository.getUserData()
                .map {
                    UserProfilePresentation(
                        userMapper.mapToPresentation(it),
                        showFeels,
                        showPainLevel
                        )
                }
    }

    fun getOfflineUserProfileData() : Single<UserProfilePresentation> {
        val showFeels = Duration.between(Instant.ofEpochSecond(prefsRepository.feelsAnsweredDate), Instant.now()) > Duration.ofDays(1)
        val showPainLevel = Duration.between(Instant.ofEpochSecond(prefsRepository.painLevelAnsweredDate), Instant.now()) > Duration.ofDays(1)
        return Single.just(UserProfilePresentation(UserDataPresentation("johndoe@gmail.com", "John", "Doe", LocalDate.MIN, "180", "60"), true, true))
        //     .map {
        //         UserProfilePresentation(
        //             userMapper.mapToPresentation(it),
        //             showFeels,
        //             showPainLevel
        //             )
        //     }
    }

    fun saveUserData(userData: UserDataPresentation) =
            Completable.defer {
                Timber.d("(userData.height.removeMeasure()) " + (userData.height.removeMeasure()))
                Timber.d("(userData.height.removeMeasure() double) " + ((userData.height.removeMeasure()).toDouble()))
                userRepository.save(userMapper.mapToModel(userData))
                        .ignoreElement()
            }

    fun onPainLevelChosen() {
        prefsRepository.painLevelAnsweredDate = Instant.now().epochSecond
    }

    fun onFeelsChosen(state: ProfileFeelsState) {
        prefsRepository.feelsAnsweredDate = Instant.now().epochSecond
    }
}