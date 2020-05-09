package com.koenigmed.luomanager.domain.interactor.profile

import com.koenigmed.luomanager.data.repository.profile.IUserRepository
import com.koenigmed.luomanager.presentation.mvp.profile.MyoDataPresentation
import com.koenigmed.luomanager.presentation.mvp.profile.MyoDataPresentationItem
import com.koenigmed.luomanager.presentation.ui.profile.MyoType
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class MyoDataInteractor
@Inject constructor(private val userRepository: IUserRepository) {

    fun getMyoData(): MyoDataPresentation {
        return MyoDataPresentation(
                listOf(
                        MyoDataPresentationItem(MyoType.RIGHT, LocalDateTime.now().plusHours(1), 13.0),
                        MyoDataPresentationItem(MyoType.RIGHT, LocalDateTime.now().plusHours(2), 9.0),
                        MyoDataPresentationItem(MyoType.RIGHT, LocalDateTime.now().plusHours(3), 4.0)
                ),
                listOf(
                        MyoDataPresentationItem(MyoType.LEFT, LocalDateTime.now(), 5.0),
                        MyoDataPresentationItem(MyoType.LEFT, LocalDateTime.now().minusHours(2), 7.0),
                        MyoDataPresentationItem(MyoType.LEFT, LocalDateTime.now().minusHours(3), 8.0)
                )
        )
    }

    companion object {
    }
}