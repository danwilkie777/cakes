@file:OptIn(ExperimentalLifecycleComposeApi::class)

package dan.wilkie.cakes.cakelist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dan.wilkie.cakes.R
import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.cakelist.domain.CakeListViewModel
import dan.wilkie.cakes.common.domain.Lce
import dan.wilkie.cakes.common.domain.Lce.*
import dan.wilkie.cakes.common.ui.FullScreenErrorState
import dan.wilkie.cakes.common.ui.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CakeListScreen() {
    val displayedCake = remember { mutableStateOf<Cake?>(null) }

    Column {
        TopAppBar {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start= dimensionResource(R.dimen.space_2x))
            )
        }
        CakeList(
            onCakeClick = { displayedCake.value = it }
        )
        CakeDialog(displayedCake)
    }
}

@Composable
private fun CakeDialog(
    displayedCake: MutableState<Cake?>
) {
    displayedCake.value?.let { cake ->
        AlertDialog(onDismissRequest = { displayedCake.value = null },
            title = { Text(cake.title) },
            text = { Text(cake.description) },
            confirmButton = {
                Button(onClick = { displayedCake.value = null }) {
                    Text(stringResource(R.string.ok))
                }
            })
    }
}

@Composable
private fun CakeList(viewModel: CakeListViewModel = koinViewModel(), onCakeClick: (Cake) -> Unit) {
    val data: Lce<List<Cake>> =
        viewModel.data.collectAsStateWithLifecycle(initialValue = Loading).value
    when (data) {
        Loading -> LoadingState()
        is Error -> FullScreenErrorState {
            viewModel.retry()
        }
        is Content -> CakeListContent(
            cakes = data.value,
            onCakeClick = onCakeClick
        )
    }
}

@Composable
private fun CakeListContent(cakes: List<Cake>, onCakeClick: (Cake) -> Unit) {
    LazyColumn {
        items(cakes) { cake ->
            CakeRow(cake, onCakeClick)
        }
    }
}

@Composable
private fun CakeRow(cake: Cake, onCakeClick: (Cake) -> Unit) {
    Column(Modifier.clickable { onCakeClick(cake) }) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.space_2x))
                .fillMaxWidth(),
            verticalAlignment = CenterVertically,
        ) {
            AsyncImage(
                model = cake.image,
                contentDescription = cake.title,
                modifier = Modifier.size(dimensionResource(R.dimen.cake_image_size)),
                contentScale = Crop,
                alignment = Center
            )
            Text(
                text = cake.title,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_2x)),
                style = MaterialTheme.typography.subtitle1
            )
        }
        Divider()
    }
}

@Preview
@Composable
fun CakeListContentPreview() {
    CakeListContent(
        listOf(
            Cake("Lemon Drizzle", "A tangy option", "lemon_drizzle.jpg"),
            Cake("Chocolate", "Old fashioned chocolate cake", "chocolate.jpg"),
        )
    ) {}
}