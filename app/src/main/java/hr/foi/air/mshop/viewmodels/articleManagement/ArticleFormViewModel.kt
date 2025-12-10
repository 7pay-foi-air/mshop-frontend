package hr.foi.air.mshop.viewmodels.articleManagement

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.data.UIState
import hr.foi.air.ws.repository.ArticleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleFormViewModel(
    private val repo: ArticleRepo = ArticleRepo()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState

    var isImagePickerVisible by mutableStateOf(false)
        private set
    var ean by mutableStateOf("")
    var articleName by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)
    var imageUrl by mutableStateOf<String?>(null)
    var imagePath by mutableStateOf("")

    private var articleToEdit: Article? = null

    val isEditMode: Boolean
        get() = articleToEdit != null

    var eanVisited by mutableStateOf(false)
    var articleNameVisited by mutableStateOf(false)
    var priceVisited by mutableStateOf(false)

    val eanEmpty: Boolean get() = ean.isBlank()
    val articleNameEmpty: Boolean get() = articleName.isBlank()
    val priceEmpty: Boolean get() = price.isBlank()
    val eanNotNumeric: Boolean get() = !eanEmpty && ean.toLongOrNull() == null
    val priceNotNumeric: Boolean get() = !priceEmpty && price.toDoubleOrNull() == null

    val eanError: Boolean get() = eanVisited && (eanEmpty || eanNotNumeric)
    val nameError: Boolean get() = articleNameVisited && articleNameEmpty
    val priceError: Boolean get() = priceVisited && (priceEmpty || priceNotNumeric)

    val isFormValid: Boolean
        get() = ean.isNotBlank() && articleName.isNotBlank() && price.isNotBlank() && price.toDoubleOrNull() != null

    fun initializeState(article: Article?) {
        articleToEdit = article
        if (article != null) {
            ean = article.ean
            articleName = article.articleName
            description = article.description ?: ""
            price = article.price.toString()
            imageUrl = article.imageUrl
            imageUri = article.imageUri?.let { Uri.parse(it) }
            imagePath = article.imageUrl?.substringAfterLast('/') ?: ""
        }
    }

    fun onImageSelected(uri: Uri) {
        imageUri = uri
        imageUrl = null
        imagePath = uri.lastPathSegment?.substringAfterLast('/') ?: "slika"
    }

    fun saveArticle(context: Context) {
        val article = Article(
            id = articleToEdit?.id,
            uuidItem = articleToEdit?.uuidItem,
            ean = ean.trim(),
            articleName = articleName.trim(),
            description = description.trim(),
            price = price.toDouble(),
            currency = articleToEdit?.currency ?: "EUR",
            imageUrl = imageUrl,
            imageUri = imageUri?.toString(),
            stockQuantity = articleToEdit?.stockQuantity ?: 1
        )

        if (article.uuidItem.isNullOrBlank()) {
            createArticle(article, context)
        } else {
            updateArticle(article, context)
        }
    }

    private fun createArticle(article: Article, context: Context) {
        viewModelScope.launch {
            _uiState.value = UIState(loading = true)
            val result = repo.createArticle(article, context)
            _uiState.value = if (result.isSuccess) {
                UIState(successMessage = result.getOrNull() ?: "Article created successfully!")
            } else {
                UIState(errorMessage = result.exceptionOrNull()?.message ?: "Error creating article.")
            }
        }
    }

    private fun updateArticle(article: Article, context: Context) {
        viewModelScope.launch {
            _uiState.value = UIState(loading = true)
            val result = repo.updateArticle(article, context)
            _uiState.value = if (result.isSuccess) {
                UIState(successMessage = result.getOrNull() ?: "Article updated successfully!")
            } else {
                UIState(errorMessage = result.exceptionOrNull()?.message ?: "Error updating article.")
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }

    fun showImagePicker() {
        isImagePickerVisible = true
    }

    fun hideImagePicker() {
        isImagePickerVisible = false
    }

}