package com.example.tp1.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tp1.data.model.GuiaAccion
import com.example.tp1.data.repository.GuiaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GuiaViewModel: ViewModel() {

    private val repositorio = GuiaRepositorio()

    private val _guias = MutableStateFlow<List<GuiaAccion>>(emptyList())
    val guias: StateFlow<List<GuiaAccion>> = _guias

    init {
        cargarGuias()
    }

    private fun cargarGuias() {
        _guias.value = repositorio.obtenerGuias()
    }
}