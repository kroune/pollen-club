package io.github.kroune.pollen.data.mapper

import io.github.kroune.pollen.data.remote.dto.response.VkPostDto
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedMappersTest {

    private fun vkPost(information: String) = VkPostDto(
        id = 1,
        date = "2026-05-16",
        location = "Москва",
        information = information,
        pin = 0,
    )

    @Test
    fun parsesLatinName() {
        val domain = vkPost("Marina Moskalenko: ЮЗАО. Все отлично.").toDomain()
        assertEquals("Marina Moskalenko", domain.userName)
        assertEquals("ЮЗАО. Все отлично.", domain.content)
    }

    @Test
    fun parsesCyrillicName() {
        val domain = vkPost("Марина Москаленко: ЮЗАО. Все отлично.").toDomain()
        assertEquals("Марина Москаленко", domain.userName)
        assertEquals("ЮЗАО. Все отлично.", domain.content)
    }

    @Test
    fun parsesSingleWordName() {
        val domain = vkPost("Alyona: всё хорошо").toDomain()
        assertEquals("Alyona", domain.userName)
        assertEquals("всё хорошо", domain.content)
    }

    @Test
    fun noColonYieldsEmptyName() {
        val domain = vkPost("No colon in this text at all").toDomain()
        assertEquals("", domain.userName)
        assertEquals("No colon in this text at all", domain.content)
    }

    @Test
    fun emptyStringYieldsEmptyName() {
        val domain = vkPost("").toDomain()
        assertEquals("", domain.userName)
        assertEquals("", domain.content)
    }

    @Test
    fun colonAtStartYieldsEmptyName() {
        val domain = vkPost(": some content").toDomain()
        assertEquals("", domain.userName)
        assertEquals(": some content", domain.content)
    }

    @Test
    fun colonBeyondMaxLengthYieldsEmptyName() {
        val longPrefix = "A".repeat(41)
        val domain = vkPost("$longPrefix: content").toDomain()
        assertEquals("", domain.userName)
    }

    @Test
    fun rejectsDigitsInPrefix() {
        val domain = vkPost("Данные на 15:00 - всё нормально").toDomain()
        assertEquals("", domain.userName)
    }

    @Test
    fun preservesLocationField() {
        val domain = vkPost("Nata Nata: Москва ювао, убрали базу").toDomain()
        assertEquals("Москва", domain.location)
    }

    @Test
    fun nameWithHyphen() {
        val domain = vkPost("Ekaterina Lisina-Koroleva: Нижний Новгород").toDomain()
        assertEquals("Ekaterina Lisina-Koroleva", domain.userName)
        assertEquals("Нижний Новгород", domain.content)
    }

    @Test
    fun nameWithApostrophe() {
        val domain = vkPost("O'Brien Patrick: some text").toDomain()
        assertEquals("O'Brien Patrick", domain.userName)
        assertEquals("some text", domain.content)
    }

    @Test
    fun trimsWhitespaceAroundNameAndContent() {
        val domain = vkPost("  Alyona P  :  ЮЗАО. Все отлично.  ").toDomain()
        assertEquals("Alyona P", domain.userName)
        assertEquals("ЮЗАО. Все отлично.", domain.content)
    }
}
