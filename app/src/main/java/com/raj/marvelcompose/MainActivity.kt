package com.raj.marvelcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.raj.marvelcompose.ui.theme.MarvelComposeTheme
import com.raj.marvelcompose.view.CharacterDetailScreen
import com.raj.marvelcompose.view.CollectionScreen
import com.raj.marvelcompose.view.LibraryScreen
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(val route: String, @StringRes val resourceId: Int, val image: Int) {
    data object Library : Screen("library", R.string.library, R.drawable.library)
    data object Collection : Screen("collection", R.string.collection, R.drawable.collection)
    data object Character : Screen("character/{characterID}", R.string.character, R.drawable.character) {
        fun createRoute(characterId: Int?) = "character/$characterId"
    }

}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarvelComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavigationBar()
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar() {
        val items = listOf(
            Screen.Library,
            Screen.Character,
            Screen.Collection
        )
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    ImageVector.vectorResource(screen.image),
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Library.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Library.route) { LibraryScreen() }
                composable(Screen.Collection.route) { CollectionScreen() }
                composable(Screen.Character.route) { CharacterDetailScreen() }
            }
        }
    }
}
