package hr.foi.air.mshop.viewmodels.articleManagement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.repo.ArticleRepo
import hr.foi.air.mshop.data.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddArticleViewModel(
    private val repo: ArticleRepo = ArticleRepo()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState

    fun createArticle(article: Article, context: Context) {
        viewModelScope.launch {
            _uiState.value = UIState(loading = true)

            val result = repo.createArticle(article, context)

            _uiState.value =
                if (result.isSuccess) {
                    UIState(successMessage = result.getOrNull() ?: "Uspješno dodano!")
                } else {
                    UIState(
                        errorMessage = result.exceptionOrNull()?.message ?: "Greška pri dodavanju."
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
