package hr.foi.air.mshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.models.TransactionItem
import hr.foi.air.mshop.core.repository.IArticleRepository
import hr.foi.air.mshop.repo.ArticleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChargeAmountUIState(
    val text: String = "0,00€",
    val isFocused: Boolean = false,
    val wasVisited: Boolean = false
){
    val errorMessage: String?
        get() = if (wasVisited && text.isBlank()) "Unesite vrijednost transakcije" else null
}

class HomepageViewModel(
    articleRepository: IArticleRepository = ArticleRepo()
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
                val articleId = article.id ?: return@update currentMap
                val currentQuantity = currentMap[articleId] ?: 0
                currentMap + (articleId to currentQuantity + 1)
            }
            updateChargeAmountFromPrice()
        }
    }


    fun removeArticle(article: Article) {
        viewModelScope.launch {
            _selectedArticles.update { currentMap ->
                val articleId = article.id ?: return@update currentMap
                val currentQuantity = currentMap[articleId] ?: 0
                if (currentQuantity > 1) {
                    currentMap + (articleId to currentQuantity - 1)
                } else {
                    currentMap - articleId
                }
            }
            updateChargeAmountFromPrice()
        }
    }

    fun removeArticleCompletely(article: Article){
        viewModelScope.launch {
            _selectedArticles.update { currentMap ->
                val articleId = article.id ?: return@update currentMap
                currentMap - articleId
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
        val allArticles = filteredArticles.value
        val currentTotalPrice = _selectedArticles.value.entries.sumOf { ( articleId, quantity) ->
            val article = allArticles.find { it.id == articleId }
            (article?.price ?: 0.0) * quantity
        }
        _chargeAmountUIState.value = _chargeAmountUIState.value.copy(
            text = String.format("%.2f€", currentTotalPrice)
        )
    }

    fun buildTransaction(): Transaction? {
        val selected = _selectedArticles.value
        if(selected.isEmpty()) return null

        val allArticles = filteredArticles.value
        if(allArticles.isEmpty()) return null

        val items = selected.mapNotNull { (articleId, quantity) ->
            val article = allArticles.find { it.id == articleId }
            val uuid = article?.uuidItem
            if (article != null && uuid != null) {
                TransactionItem(
                    uuidItem = uuid,
                    name = article.articleName,
                    price = article.price,
                    quantity = quantity
                )
            } else {
                null
            }
        }

        if (items.isEmpty()) return null
        val total = items.sumOf { it.price * it.quantity }

        return Transaction(
            description = "Kupnja u mShopu",
            items = items,
            totalAmount = total,
            currency = "EUR"
        )
    }
}