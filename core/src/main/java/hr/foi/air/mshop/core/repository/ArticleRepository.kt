package hr.foi.air.mshop.core.repository

import hr.foi.air.mshop.core.models.Article

interface ArticleRepository {
    suspend fun getArticles(): Result<List<Article>>
    suspend fun createArticle(article: Article): Result<Article>
}