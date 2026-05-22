package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class TherapyDomain(
    val id: Long = 0,
    val date: LocalDate,
    val cureTypeId: Int,
    val cureName: String,
    val cureId: Int,
    val form: String,
    val dose: String,
    val frequency: String,
    val startDate: LocalDate,
)

data class CureDomain(
    val id: String,
    val name: String,
    val description: String,
    val forma: String?,
    val sortNumber: Int,
    val info: String,
    val actionType: Int,
    val items: List<CureItemDomain>,
)

data class CureItemDomain(
    val id: String,
    val name: String,
    val description: String,
    val forma: String?,
    val sortNumber: Int,
    val mark: String?,
    val activeSubstance: String?,
)

data class CureActionTypeDomain(
    val id: Int,
    val name: String,
    val sortNumber: Int,
)

data class CureFormDomain(val id: String, val name: String)
data class CureDoseDomain(val id: String, val name: String)
data class CureFrequencyDomain(val id: String, val name: String)
