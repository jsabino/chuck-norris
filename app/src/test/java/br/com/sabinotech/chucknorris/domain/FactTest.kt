package br.com.sabinotech.chucknorris.domain

import org.junit.Assert
import org.junit.Test

class FactTest {

    @Test
    fun `when facts category is null return UNCATEGORIZED`() {
        val fact = Fact("a1", "http://google.com", "Fact 1", null)

        Assert.assertEquals("UNCATEGORIZED", fact.getCategoryName())
    }

    @Test
    fun `when facts category is not null return it`() {
        val fact = Fact("a1", "http://google.com", "Fact 1", "DEV")

        Assert.assertEquals("DEV", fact.getCategoryName())
    }

    @Test
    fun `when the number of characters in the text is less than 80 return 24f as text size`() {
        val factText = "".padEnd(79, 'a')
        val fact = Fact("a1", "http://google.com", factText, "DEV")

        Assert.assertEquals(24f, fact.getTextSize())
    }

    @Test
    fun `when the number of characters in the text is greater than or equal to 80 return 16f as text size`() {
        val factText = "".padEnd(80, 'a')
        val fact = Fact("a1", "http://google.com", factText, "DEV")

        Assert.assertEquals(16f, fact.getTextSize())
    }
}