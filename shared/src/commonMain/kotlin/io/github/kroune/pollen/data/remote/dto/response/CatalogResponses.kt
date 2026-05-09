package io.github.kroune.pollen.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PollenDataResponse(
    @SerialName("pollens") val pollens: List<PollenDto> = emptyList(),
)

@Serializable
data class PollenDto(
    @SerialName("id") val id: Int,
    @SerialName("desc") val desc: String = "",
    @SerialName("desc_eng") val descEng: String = "",
    @SerialName("info") val info: String = "",
    @SerialName("info_eng") val infoEng: String = "",
    @SerialName("max_level") val maxLevel: Int = 0,
    @SerialName("levels") val levels: List<PollenLevelDto> = emptyList(),
)

@Serializable
data class PollenLevelDto(
    @SerialName("level") val level: Int,
    @SerialName("name") val name: String = "",
    @SerialName("name_eng") val nameEng: String = "",
    @SerialName("info") val info: String = "",
    @SerialName("info_eng") val infoEng: String = "",
    @SerialName("color") val color: Int = 0,
)

@Serializable
data class LocationDataResponse(
    @SerialName("locations") val locations: List<LocationDto> = emptyList(),
)

@Serializable
data class LocationDto(
    @SerialName("id") val id: Int,
    @SerialName("desc") val desc: String = "",
    @SerialName("comment") val comment: String = "",
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("eng_name") val engName: String = "",
    @SerialName("eng_desc") val engDesc: String = "",
)

@Serializable
data class HashTagDataResponse(
    @SerialName("hashtags") val hashtags: List<HashTagDto> = emptyList(),
)

@Serializable
data class HashTagDto(
    @SerialName("id") val id: String = "",
    @SerialName("value") val value: String = "",
    @SerialName("name") val name: String? = null,
)

@Serializable
data class CuresDataResponse(
    @SerialName("action_types") val actionTypes: List<CureActionTypeDto> = emptyList(),
    @SerialName("cures") val cures: List<CureDto> = emptyList(),
    @SerialName("forms") val forms: List<CureFormDto> = emptyList(),
    @SerialName("doses") val doses: List<CureDoseDto> = emptyList(),
    @SerialName("frequency") val frequency: List<CureFrequencyDto> = emptyList(),
)

@Serializable
data class CureActionTypeDto(
    @SerialName("id") val id: Int,
    @SerialName("name_rus") val nameRus: String? = null,
    @SerialName("name_eng") val nameEng: String? = null,
    @SerialName("sort_number") val sortNumber: Int = 0,
)

@Serializable
data class CureDto(
    @SerialName("id") val id: String = "",
    @SerialName("name_rus") val nameRus: String? = null,
    @SerialName("name_eng") val nameEng: String? = null,
    @SerialName("desc_rus") val descRus: String? = null,
    @SerialName("desc_eng") val descEng: String? = null,
    @SerialName("forma") val forma: String? = null,
    @SerialName("sort_number") val sortNumber: Int = 0,
    @SerialName("info_rus") val infoRus: String? = null,
    @SerialName("info_eng") val infoEng: String? = null,
    @SerialName("action_type") val actionType: Int = 0,
    @SerialName("items") val items: List<CureItemDto> = emptyList(),
)

@Serializable
data class CureItemDto(
    @SerialName("id") val id: String = "",
    @SerialName("name_rus") val nameRus: String? = null,
    @SerialName("name_eng") val nameEng: String? = null,
    @SerialName("desc_rus") val descRus: String? = null,
    @SerialName("desc_eng") val descEng: String? = null,
    @SerialName("forma") val forma: String? = null,
    @SerialName("sort_number") val sortNumber: Int = 0,
    @SerialName("mark") val mark: String? = null,
    @SerialName("active_substance") val activeSubstance: String? = null,
)

@Serializable
data class CureFormDto(
    @SerialName("id") val id: String = "",
    @SerialName("name_rus") val nameRus: String? = null,
    @SerialName("name_eng") val nameEng: String? = null,
)

@Serializable
data class CureDoseDto(
    @SerialName("id") val id: String = "",
    @SerialName("name_rus") val nameRus: String? = null,
    @SerialName("name_eng") val nameEng: String? = null,
)

@Serializable
data class CureFrequencyDto(
    @SerialName("id") val id: String = "",
    @SerialName("name_rus") val nameRus: String? = null,
    @SerialName("name_eng") val nameEng: String? = null,
)
