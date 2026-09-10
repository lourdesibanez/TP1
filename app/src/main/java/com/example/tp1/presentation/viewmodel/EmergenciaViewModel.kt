package com.example.tp1.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.tp1.data.model.Emergencia
import com.google.firebase.firestore.FirebaseFirestore

class EmergenciaViewModel : ViewModel() {
    //la lista de tipo Emergencia que guardará lo que venga de firebase
    val emergencias = mutableStateListOf<Emergencia>()

    init {
        //apenas se crea el ViewModel, empezamos a escuchar Firebase
        obtenerEmergencias()
    }

    private fun obtenerEmergencias() {
        FirebaseFirestore.getInstance().collection("emergencias")
            .addSnapshotListener { snapshot, error -> //quedate escuchando esta coleccion y avisame cada vez q algo cambie
                if (error != null) return@addSnapshotListener

                snapshot?.let {
                    emergencias.clear()
                    for (documento in it.documents) {
                        val emergencia = documento.toObject(Emergencia::class.java)
                        emergencia?.let { emergencias.add(it) } //vuelve a agregar los datos actualizados q llegan de firebase
                    }
                }
            }
    }
}