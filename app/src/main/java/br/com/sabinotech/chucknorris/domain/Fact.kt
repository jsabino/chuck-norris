package br.com.sabinotech.chucknorris.domain

data class Fact(
    val id: String,
    val url: String,
    val text: String,
    val category: String?
) {
    fun getTextSize() = if (text.length < 80) 24f else 16f

    fun getCategoryName() = category ?: "UNCATEGORIZED"
}
