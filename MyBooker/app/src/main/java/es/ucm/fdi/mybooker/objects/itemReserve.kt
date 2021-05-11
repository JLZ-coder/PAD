package es.ucm.fdi.mybooker.objects

import java.time.LocalDateTime

data class itemReserve(val hora: LocalDateTime, val nombre_cliente: String?, val personas: Int)