package com.passmanager.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object PasswordsList : Screen("passwords")
    object AddPassword : Screen("add_password")
    object EditPassword : Screen("edit_password/{id}") {
        fun createRoute(id: Long) = "edit_password/$id"
    }
    object Settings : Screen("settings")
}

sealed class NavGraph(val route: String) {
    object Auth : NavGraph("auth") {
        val login = Screen.Login.route
        val register = Screen.Register.route
    }

    object Main : NavGraph("main") {
        val passwords = Screen.PasswordsList.route
        val addPassword = Screen.AddPassword.route
        val editPassword = Screen.EditPassword.route
        val settings = Screen.Settings.route
    }
} 