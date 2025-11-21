package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.navigation.AppRoutes

@Composable
fun ManageArticlesPage(navController: NavHostController) {

    val fakeArticles = listOf(
        Article(
            id = 1,
            ean = "123456",
            articleName = "Coca Cola",
            description = "Piće",
            price = 2.5
        ),
        Article(
            id = 2,
            ean = "123456",  // isti EAN → zato id!
            articleName = "Coca Cola Zero",
            description = "Piće bez šećera",
            price = 2.6
        )
    )

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Upravljanje artiklima")

        Spacer(Modifier.height(12.dp))

        Button(onClick = { navController.navigate(AppRoutes.ADD_ARTICLE) }) {
            Text("Dodaj novi artikal")
        }

        Spacer(Modifier.height(12.dp))

        fakeArticles.forEach { article ->
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(article.articleName)
                TextButton(onClick = {
                    navController.navigate(AppRoutes.editArticleRoute(article.id))
                }) {
                    Text("Uredi")
                }
            }
        }
    }
}
