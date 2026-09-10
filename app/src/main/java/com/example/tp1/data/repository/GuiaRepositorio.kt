package com.example.tp1.data.repository

import com.example.tp1.data.model.GuiaAccion
import com.example.tp1.R

class GuiaRepositorio {

    fun obtenerGuias(): List<GuiaAccion> {

        return listOf(
            GuiaAccion(
                nombre = "Incendio",
                imagen = R.drawable.incendio,
                video = R.raw.incendio,
                antes = listOf(
                    "Mantené libres las salidas de emergencia.",
                    "Tené un extintor accesible.",
                    "No sobrecargues los enchufes eléctricos."
                ),
                durante = listOf(
                    "Mantené la calma y avisá a los demás.",
                    "Salí del lugar utilizando las rutas de evacuación.",
                    "No uses ascensores.",
                    "Si hay humo, desplazate lo más cerca posible del suelo.",
                    "Llamá a los servicios de emergencia."
                ),
                despues = listOf(
                    "No regreses al lugar hasta que las autoridades lo indiquen.",
                    "No vuelvas a conectar la electricidad por tu cuenta.",
                    "Informá sobre personas que puedan estar desaparecidas."
                )
            ),

            GuiaAccion(
                nombre = "Inundación",
                imagen = R.drawable.inundacion,
                video = R.raw.inundacion,
                antes = listOf(
                    "Prepará una mochila de emergencia.",
                    "Guardá documentos importantes en un lugar seguro.",
                    "Mantenete informado sobre las alertas meteorológicas."
                ),
                durante = listOf(
                    "Alejate de zonas inundadas.",
                    "No intentes cruzar calles o caminos con corriente de agua.",
                    "Desconectá la electricidad si el agua está entrando en tu vivienda.",
                    "Seguí las indicaciones de las autoridades."
                ),
                despues = listOf(
                    "No regreses a zonas evacuadas hasta que sea seguro.",
                    "Evitá tocar cables eléctricos caídos.",
                    "No consumas agua que pueda estar contaminada."
                )
            ),

            GuiaAccion(
                nombre = "Terremoto",
                imagen = R.drawable.terremoto,
                video = R.raw.terremoto,
                antes = listOf(
                    "Identificá lugares seguros dentro de tu vivienda.",
                    "Prepará una mochila de emergencia.",
                    "Fijá muebles y objetos que puedan caerse."
                ),
                durante = listOf(
                    "Mantené la calma.",
                    "Agachate, cubrite y sujetate.",
                    "Alejate de ventanas y objetos que puedan caer.",
                    "Si estás afuera, alejate de edificios, árboles y cables."
                ),
                despues = listOf(
                    "Verificá si hay personas heridas.",
                    "Evitá edificios dañados.",
                    "Preparáte para posibles réplicas.",
                    "Seguí las instrucciones de las autoridades."
                )
            )
        )
    }
}
