package uz.otamurod.ecommerceapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.util.Constants.INTRODUCTION_BUTTON_CLICKED_KEY
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _navigateState = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigateState

    init {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_BUTTON_CLICKED_KEY, false)
        val user = firebaseAuth.currentUser
        if (user != null) {
            viewModelScope.launch {
                _navigateState.emit(NAVIGATE_TO_SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) {
            viewModelScope.launch {
                _navigateState.emit(NAVIGATE_TO_ACCOUNT_OPTIONS_FRAGMENT)
            }
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(INTRODUCTION_BUTTON_CLICKED_KEY, true).apply()
    }

    companion object {
        const val NAVIGATE_TO_SHOPPING_ACTIVITY = 20
        const val NAVIGATE_TO_ACCOUNT_OPTIONS_FRAGMENT =
            R.id.action_introductionFragment_to_accountOptionsFragment
    }
}