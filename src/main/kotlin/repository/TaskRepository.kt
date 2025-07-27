package com.tkjen.repository

import com.tkjen.model.Priority
import com.tkjen.model.Task


val tasks = mutableListOf(
    Task("cleaning", "Clean the house", Priority.Low),
    Task("gardening", "Mow the lawn", Priority.Medium),
    Task("shopping", "Buy the groceries", Priority.High),
    Task("painting", "Paint the fence", Priority.Medium)
)