package hr.foi.air.mshop.core.repository

import hr.foi.air.mshop.core.models.Article
import kotlinx.coroutines.flow.StateFlow

interface ArticleRepository {
    /*suspend*/ fun getAllArticles(): StateFlow<List<Article>>
    suspend fun createArticle(article: Article): Result<Article>
    suspend fun updateArticle(article: Article): Result<Article>
    /*suspend*/ fun deleteArticle(articleId: Int)
}