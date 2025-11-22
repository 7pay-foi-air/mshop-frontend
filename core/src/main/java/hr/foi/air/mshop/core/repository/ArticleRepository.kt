package hr.foi.air.mshop.core.repository

import hr.foi.air.mshop.core.models.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getAllArticles(): Flow<List<Article>>

    suspend fun createArticle(article: Article): Result<String>
    suspend fun updateArticle(article: Article): Result<String>
    suspend fun deleteArticle(articleId: Int): Result<Unit>
}