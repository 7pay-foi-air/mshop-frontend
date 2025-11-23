package hr.foi.air.mshop.core.repository

import android.content.Context
import hr.foi.air.mshop.core.models.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getAllArticles(): Flow<List<Article>>

    suspend fun createArticle(article: Article, context: Context): Result<String>
    suspend fun updateArticle(article: Article, context: Context): Result<String>
    suspend fun deleteArticle(articleId: Int): Result<Unit>
}