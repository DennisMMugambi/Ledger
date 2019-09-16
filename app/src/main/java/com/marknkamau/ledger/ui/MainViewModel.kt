package com.marknkamau.ledger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marknkamau.ledger.data.MessagesRepository
import com.marknkamau.ledger.data.models.MessageGroup
import kotlinx.coroutines.launch

class MainViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _messages = MutableLiveData<List<MessageGroup>>()
    val messages = _messages

    fun getMessages() {
        viewModelScope.launch {
            _loading.value = true
            _messages.value = messagesRepository.getMessagesGrouped()
            _loading.value = false
        }
    }
}