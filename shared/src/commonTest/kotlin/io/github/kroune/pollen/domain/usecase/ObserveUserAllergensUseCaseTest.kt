package io.github.kroune.pollen.domain.usecase

import io.github.kroune.pollen.domain.model.AllergenSensitivityDomain
import io.github.kroune.pollen.domain.model.LevelDomain
import io.github.kroune.pollen.domain.model.PollenDomain
import io.github.kroune.pollen.domain.model.SensitivityLevel
import io.github.kroune.pollen.presentation.home.FakePollenRepository
import io.github.kroune.pollen.presentation.home.FakeSensitivityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveUserAllergensUseCaseTest {

    private val date = LocalDate(2026, 5, 18)
    private val locationId = 1

    private val birch = PollenDomain(id = 10, name = "Birch", description = "", maxLevel = 5, levels = emptyList())
    private val oak = PollenDomain(id = 20, name = "Oak", description = "", maxLevel = 4, levels = emptyList())

    private fun level(pollenId: Int, value: Int) =
        LevelDomain(id = pollenId, date = date, pollenId = pollenId, locationId = locationId, value = value)

    private fun useCaseWith(
        pollens: List<PollenDomain>,
        sensitivities: List<AllergenSensitivityDomain> = emptyList(),
        levels: List<LevelDomain> = emptyList(),
    ): ObserveUserAllergensUseCase {
        val pollenRepo = FakePollenRepository().apply {
            pollensFlow.value = pollens
            emitLevels(levels)
        }
        val sensitivityRepo = FakeSensitivityRepository().apply { emit(sensitivities) }
        return ObserveUserAllergensUseCase(pollenRepo, sensitivityRepo)
    }

    @Test
    fun allergenLevel_isTheRawValue_notNormalized() = runTest {
        val useCase = useCaseWith(
            pollens = listOf(birch, oak),
            sensitivities = listOf(AllergenSensitivityDomain(oak.id, SensitivityLevel.MODERATE)),
            levels = listOf(level(oak.id, 4)),
        )

        val allergen = useCase(locationId, date).first().allergens.single()
        assertEquals(oak.id, allergen.pollen.id)
        assertEquals(4, allergen.level)
    }

    @Test
    fun index_normalizesAgainstUniversalMax_notThePollensOwnCeiling() = runTest {
        // Oak at its own ceiling (level 4 of max 4) is "Very high" = 4/5 on the universal scale,
        // not the worst possible, so it must not score a perfect 10.
        val useCase = useCaseWith(
            pollens = listOf(oak),
            sensitivities = listOf(AllergenSensitivityDomain(oak.id, SensitivityLevel.SEVERE)),
            levels = listOf(level(oak.id, 4)),
        )

        val index = useCase(locationId, date).first().index
        assertNotNull(index)
        assertEquals(8.0, index.score, 0.0001) // (4 / 5) * 10
        assertEquals(oak.name, index.dominantAllergenName)
    }

    @Test
    fun index_isNull_whenNoActiveSensitivities() = runTest {
        val useCase = useCaseWith(pollens = listOf(birch), levels = listOf(level(birch.id, 3)))

        val profile = useCase(locationId, date).first()
        assertTrue(profile.allergens.isEmpty())
        assertNull(profile.index)
    }

    @Test
    fun index_isNull_whenNoLevelsForTheDay() = runTest {
        val useCase = useCaseWith(
            pollens = listOf(birch),
            sensitivities = listOf(AllergenSensitivityDomain(birch.id, SensitivityLevel.LIGHT)),
        )

        assertNull(useCase(locationId, date).first().index)
    }

    @Test
    fun otherPollens_areTheNonSensitiveComplement() = runTest {
        val useCase = useCaseWith(
            pollens = listOf(birch, oak),
            sensitivities = listOf(AllergenSensitivityDomain(birch.id, SensitivityLevel.LIGHT)),
            levels = listOf(level(birch.id, 1)),
        )

        val profile = useCase(locationId, date).first()
        assertEquals(listOf(birch.id), profile.allergens.map { it.pollen.id })
        assertEquals(listOf(oak.id), profile.otherPollens.map { it.id })
    }
}
