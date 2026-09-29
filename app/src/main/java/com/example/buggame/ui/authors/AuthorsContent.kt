package com.example.buggame.ui.authors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buggame.data.model.Author

@Composable
fun AuthorsContent(
    state: AuthorsState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.authors.isEmpty() -> {
                Text(
                    text = "Список авторов пуст",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Авторы проекта",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(state.authors, key = { it.id }) { author ->
                        AuthorItem(author = author)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthorItem(author: Author) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuthorPhoto(author = author)

            Column {
                Text(
                    text = author.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = author.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AuthorPhoto(author: Author) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (author.photoResId != null) {
            Image(
                painter = painterResource(id = author.photoResId),
                contentDescription = author.fullName,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = author.fullName,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorsContentLoadingPreview() {
    AuthorsContent(state = AuthorsState(isLoading = true))
}

@Preview(showBackground = true)
@Composable
private fun AuthorsContentEmptyPreview() {
    AuthorsContent(state = AuthorsState(isLoading = false, authors = emptyList()))
}

@Preview(showBackground = true)
@Composable
private fun AuthorsContentSuccessPreview() {
    AuthorsContent(
        state = AuthorsState(
            isLoading = false,
            authors = listOf(
                Author(1, "Евгений Кривенышев", "Бизнес-логика и архитектура", null),
                Author(2, "Никита Шушаков", "Фронтенд и дизайн", null)
            )
        )
    )
}