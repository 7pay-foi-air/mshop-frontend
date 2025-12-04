package hr.foi.air.mshop.viewmodels.articleManagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.IArticleRepository
import hr.foi.air.mshop.repo.ArticleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleManagementViewModel(
    private val articleRepository: IArticleRepository = ArticleRepo()
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _deletedArticleIds = MutableStateFlow<Set<String>>(emptySet())
    private val _articleToEdit = MutableStateFlow<Article?>(null)
    val articleToEdit: StateFlow<Article?> = _articleToEdit.asStateFlow()
    private val _articleToDelete = MutableStateFlow<Article?>(null)
    val articleToDelete: StateFlow<Article?> = _articleToDelete.asStateFlow()

    val filteredArticles: StateFlow<List<Article>> = combine(
        _searchQuery,
        articleRepository.getAllArticles(),
        _deletedArticleIds
    ) { query, articles, deletedIds ->
        val articlesToShow = articles.filter { it.uuidItem !in deletedIds }
        if (query.isBlank()) {
            articlesToShow
        } else {
            articlesToShow.filter { it.articleName.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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
        _articleToDelete.update { article }
    }

    fun onDismissDeleteDialog() {
        _articleToDelete.value = null
    }

    fun deleteArticle() {
        _articleToDelete.value?.let { articleToRemove ->
            viewModelScope.launch {
                val result = articleRepository.deleteArticle(articleToRemove.uuidItem!!)
                if (result.isSuccess) {
                    Log.d("ArticleDelete", "Article deleted successfully.")
                    _deletedArticleIds.update { it + articleToRemove.uuidItem!! }
                } else {
                    Log.d("ArticleDelete", "Error deleting article: ${result.exceptionOrNull()?.message}")
                }
                onDismissDeleteDialog()
            }
        }
    }
}