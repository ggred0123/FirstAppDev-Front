package com.example.mydev.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SharedViewModel : ViewModel() {
    private val _imageUpdateTrigger = MutableSharedFlow<Unit>()
    val imageUpdateTrigger = _imageUpdateTrigger.asSharedFlow()

    suspend fun notifyImageUpdated() {
        _imageUpdateTrigger.emit(Unit)
    }
}