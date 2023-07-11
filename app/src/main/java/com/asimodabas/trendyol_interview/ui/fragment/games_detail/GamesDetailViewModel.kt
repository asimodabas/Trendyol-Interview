package com.asimodabas.trendyol_interview.ui.fragment.games_detail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asimodabas.trendyol_interview.common.NetworkCheck
import com.asimodabas.trendyol_interview.common.state.DetailState
import com.asimodabas.trendyol_interview.domain.mapper.detail_local.DetailLocalMapper
import com.asimodabas.trendyol_interview.domain.model.Detail
import com.asimodabas.trendyol_interview.domain.model.DetailLocal
import com.asimodabas.trendyol_interview.domain.usecase.delete_detail.DeleteDetailUseCase
import com.asimodabas.trendyol_interview.domain.usecase.get_game_detail.GetGameDetailUseCase
import com.asimodabas.trendyol_interview.domain.usecase.insert_details.InsertDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesDetailViewModel @Inject constructor(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val insertDetailsUseCase: InsertDetailsUseCase,
    private val deleteDetailUseCase: DeleteDetailUseCase,
    private val detailLocalMapper: DetailLocalMapper,
    private var sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _detailState = MutableLiveData<DetailState>()
    val detailState: LiveData<DetailState> = _detailState

    private val _wishlistState = MutableLiveData<Boolean>()
    val wishlistState: LiveData<Boolean> = _wishlistState

    fun getDetail(id: Int) = viewModelScope.launch {
        when (val request = getGameDetailUseCase.invoke(id)) {
            is NetworkCheck.Success -> {
                _detailState.postValue(
                    DetailState(
                        success = request.data
                    )
                )
            }

            is NetworkCheck.Error -> {
                DetailState(error = request.message)
            }
        }
    }

    fun checkWishlist(detail: Detail) {
        if (sharedPreferences.getBoolean(detail.name, false)) {
            _wishlistState.postValue(true)
            detail.wishlist = true
        } else {
            _wishlistState.postValue(false)
            detail.wishlist = false
        }
    }

    fun wishlistClickButton(detail: Detail) {
        val detailLocal = detailLocalMapper.map(detail)
        if (detailLocal.wishlist) {
            detailLocal.wishlist = false
            _wishlistState.postValue(false)
            deleteWishList(detailLocal)
            sharedPreferences.edit().remove(detailLocal.name).apply()

        } else {
            detailLocal.wishlist = true
            _wishlistState.postValue(true)
            addWishList(detailLocal)
            sharedPreferences.edit().putBoolean(detailLocal.name, true).apply()
        }
    }

    private fun addWishList(detail: DetailLocal) = viewModelScope.launch {
        insertDetailsUseCase.invoke(detail)
    }

    private fun deleteWishList(detail: DetailLocal) = viewModelScope.launch {
        deleteDetailUseCase.invoke(detail)
    }
}