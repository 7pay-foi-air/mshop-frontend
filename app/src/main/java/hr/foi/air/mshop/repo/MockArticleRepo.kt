package hr.foi.air.mshop.repo

import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.repo.MockArticles
import kotlinx.coroutines.flow.StateFlow

class MockArticleRepo : ArticleRepository {
    override fun getAllArticles(): StateFlow<List<Article>> {
        return MockArticles.allArticles
    }

    override suspend fun createArticle(article: Article): Result<Article> {
        TODO("Not yet implemented")
    }

    override suspend fun updateArticle(article: Article): Result<Article> {
        TODO("Not yet implemented")
    }

    override fun deleteArticle(articleId: Int) {
        MockArticles.deleteArticles(articleId)
    }
}