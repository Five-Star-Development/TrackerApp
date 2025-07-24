package dev.five_star.trackingapp

sealed interface ExampleAction {
    object OnButtonClicked: ExampleAction
}