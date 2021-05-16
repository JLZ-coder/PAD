package es.ucm.fdi.mybooker.objects

import java.io.Serializable

class itemShift(val id_enterprise: String? = null, val start: String? =null, val end: String? =null, val period: Int?=null, val days: List<Int>?=null) : Serializable