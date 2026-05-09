package io.github.kroune.pollen.domain.model

data class UserForecastDomain(
    val info: List<UserForecastInfoDomain>,
    val forecasts: List<UserForecastEntryDomain>,
)

data class UserForecastInfoDomain(
    val id: String,
    val description: String,
)

data class UserForecastEntryDomain(
    val id: String,
    val date: String,
    val value: String,
)
