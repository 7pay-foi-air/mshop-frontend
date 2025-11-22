package hr.foi.air.mshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.repo.MockArticleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.apply

data class ChargeAmountUIState(
    val text: String = "0,00€",
    val isFocused: Boolean = false,
    val wasVisited: Boolean = false
){
    val errorMessage: String?
        get() = if (wasVisited && text.isBlank()) "Unesite vrijednost transakcije" else null
}

class HomepageViewModel(
    private val articleRepository: ArticleRepository = MockArticleRepo()
) : ViewModel() {
    private val _selectedArticles = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val _chargeAmountUIState = MutableStateFlow(ChargeAmountUIState())
    private val _searchQuery = MutableStateFlow("")

    val selectedArticles: StateFlow<Map<Int, Int>> = _selectedArticles.asStateFlow()
    val chargeAmountUIState: StateFlow<ChargeAmountUIState> = _chargeAmountUIState.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredArticles: StateFlow<List<Article>> = _searchQuery
        .combine(articleRepository.getAllArticles()) { query, articles ->
            if (query.isBlank()) {
                articles
            } else {
                articles.filter { it.articleName.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addArticle(article: Article) {
        viewModelScope.launch {
            _selectedArticles.update { currentMap ->
                val currentQuantity = currentMap[article.id] ?: 0
                (currentMap + (article.id to currentQuantity + 1)) as Map<Int, Int>
            }
            updateChargeAmountFromPrice()
        }
    }

    fun removeArticle(article: Article) {
        viewModelScope.launch {
            _selectedArticles.update { currentMap ->
                val currentQuantity = currentMap[article.id] ?: 0
                (if (currentQuantity > 1) {
                    currentMap + (article.id to currentQuantity - 1)
                } else {
                    currentMap - article.id
                }) as Map<Int, Int>
            }
            updateChargeAmountFromPrice()
        }
    }
    fun removeArticleCompletely(article: Article){
        viewModelScope.launch {
            _selectedArticles.update { currentMap ->
                (currentMap - article.id) as Map<Int, Int>
            }
            updateChargeAmountFromPrice()
        }
    }


    fun clearSelection() {
        viewModelScope.launch {
            _selectedArticles.value = emptyMap()
            updateChargeAmountFromPrice()
        }
    }

    fun onChargeAmountChange(newText: String){
        _chargeAmountUIState.value = _chargeAmountUIState.value.copy(text = newText)
    }

    fun onChargeAmountFocusChange(isFocused: Boolean){
        val currentState = _chargeAmountUIState.value
        _chargeAmountUIState.value = currentState.copy(
            isFocused = isFocused,
            wasVisited = if (!isFocused) true else currentState.wasVisited
        )
    }

    private fun updateChargeAmountFromPrice(){
        val allArticles = articleRepository.getAllArticles().value
        val currentTotalPrice = _selectedArticles.value.entries.sumOf { ( articleId, quantity) ->
            val article = allArticles.find { it.id == articleId }
            (article?.price ?: 0.0) * quantity
        }
        _chargeAmountUIState.value = _chargeAmountUIState.value.copy(
            text = String.format("%.2f€", currentTotalPrice)
        )
    }
}