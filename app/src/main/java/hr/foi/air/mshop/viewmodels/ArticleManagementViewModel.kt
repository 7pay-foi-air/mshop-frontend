package hr.foi.air.mshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.repo.ArticleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ArticleManagementViewModel(
    private val articleRepository: ArticleRepository = ArticleRepo()
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _articleToEdit = MutableStateFlow<Article?>(null)
    val articleToEdit: StateFlow<Article?> = _articleToEdit.asStateFlow()
    private val _articleToDelete = MutableStateFlow<Article?>(null)
    val articleToDelete: StateFlow<Article?> = _articleToDelete.asStateFlow()

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

    fun onStartEditArticle(article: Article) {
        _articleToEdit.value = article
    }

    fun onFinishEditArticle() {
        _articleToEdit.value = null
    }

    fun onOpenDeleteDialog(article: Article) {
        _articleToDelete.value = article
    }

    fun onDismissDeleteDialog() {
        _articleToDelete.value = null
    }

    fun deleteArticle() {
        _articleToDelete.value?.let { articleToRemove ->
            //articleRepository.deleteArticle(articleToRemove.id!!)
        }
        onDismissDeleteDialog()
    }
}