package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MCAssignment1()
        }
    }
}

@Preview(heightDp = 500)
@Composable
fun PreviewScreen(){
    var progress by remember { mutableStateOf(0f) }
    var highlightedIndex by remember { mutableStateOf(-1) }
    var highlightedIcons by remember { mutableStateOf(List(getCategoryList().size) { Icons.Default.Build }) }
    var isDistanceInKilometers by remember { mutableStateOf(true) }
    var isNextStepEnabled by remember { mutableStateOf(true) }
    var totalDistanceCovered by remember { mutableStateOf(0) }
    var totalDistanceLeft by remember { mutableStateOf(getTotalDistance()) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ){
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            if(getCategoryList().size <= 10){
                getCategoryList().forEachIndexed { index, item ->
                    val accumulatedDistance = getAccumulatedDistance(index)
                    TravelDistance(
                        title = item.title,
                        subtitle = getSubtitleText(item.distance, isDistanceInKilometers),
                        isHighlighted = index == highlightedIndex,
                        icon = highlightedIcons[index],
                        onClick = {
                            totalDistanceLeft -= item.distance
                            totalDistanceCovered += item.distance
                            highlightedIndex = index
                            highlightedIcons = highlightedIcons.toMutableList().apply {
                                set(highlightedIndex, Icons.Default.CheckCircle) // Change the icon here
                            }

                            // Check for reset condition
                            if (highlightedIndex == getCategoryList().size - 1) {
                                // Disable the "Next Stop" button
                                isNextStepEnabled = false
                            }
                        }
                    )
                }
            }
            else{
                LazyColumn(content = {
                    itemsIndexed(getCategoryList()){index, item ->
                        val accumulatedDistance = getAccumulatedDistance(index)
                        TravelDistance(title = item.title,
                            subtitle = getSubtitleText(item.distance, isDistanceInKilometers),
                            isHighlighted = index == highlightedIndex,
                            icon = highlightedIcons[index],
                            onClick = {
                                totalDistanceLeft = totalDistanceLeft - item.distance
                                totalDistanceCovered = totalDistanceCovered + item.distance
                                highlightedIndex = index
                                highlightedIcons = highlightedIcons.toMutableList().apply {
                                    set(highlightedIndex, Icons.Default.CheckCircle) // Change the icon here
                                }

                                // Check for reset condition
                                if (highlightedIndex == getCategoryList().size - 1) {
                                    isNextStepEnabled = false
                                }
                            }
                        )
                    }
                })
            }
        }
        Text(
            text = "Total Distance Covered: ${getFormattedDistance(totalDistanceCovered, isDistanceInKilometers)}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Total Distance Left: ${getFormattedDistance(totalDistanceLeft, isDistanceInKilometers)}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)){
            Button(onClick = {
                progress += 1.0f / getCategoryList().size
                highlightedIndex = (highlightedIndex + 1) % getCategoryList().size
                highlightedIcons = highlightedIcons.toMutableList().apply {
                    set(highlightedIndex, Icons.Default.CheckCircle) // Change the icon here
                }
                val selectedCategory = getCategoryList()[highlightedIndex]
                totalDistanceLeft -= selectedCategory.distance
                totalDistanceCovered += selectedCategory.distance

                // Check for reset condition
                if (highlightedIndex == getCategoryList().size - 1) {
                    // Disable the "Next Step" button
                    isNextStepEnabled = false
                }
            },
                enabled = isNextStepEnabled
                ) {
                Text(text = "Next Stop")
            }
            Button(onClick = {
                progress = 0f
                highlightedIndex = -1
                highlightedIcons = List(getCategoryList().size) { Icons.Default.Build }
                totalDistanceCovered = 0
                totalDistanceLeft = getTotalDistance()
                isNextStepEnabled = true
            }) {
                Text(text = "Reset")
            }
            Button(onClick = {
                // Toggle between kilometers and miles
                isDistanceInKilometers = !isDistanceInKilometers
            }) {
                Text(text = if (isDistanceInKilometers) "Convert to Miles" else "Convert to Kilometers")
            }
        }
    }
}

@Composable
fun getSubtitleText(distance: Int, isDistanceInKilometers: Boolean): String {
    return if (isDistanceInKilometers) {
        "Distance from previous to current stop: $distance Kms"
    } else {
        "Distance from previous to current stop: ${(distance * 0.621371).toInt()} Miles"
    }
}

// Function to calculate the total distance
fun getTotalDistance(): Int {
    return getCategoryList().sumOf { it.distance }
}

// Function to get formatted distance text
fun getFormattedDistance(distance: Int, isDistanceInKilometers: Boolean): String {
    return if (isDistanceInKilometers) {
        "$distance Kms"
    } else {
        "${(distance * 0.621371).toInt()} miles"
    }
}

// Function to calculate accumulated distance at each stop
private fun getAccumulatedDistance(index: Int): Int {
    val distances = getCategoryList().subList(0, index + 1).map { it.distance }
    return distances.sum()
}

@Composable
fun TravelDistance(
    title: String,
    subtitle: String,
    isHighlighted: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
){
    Column {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }){
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)){
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    modifier = Modifier.weight(.2f))
                Column (modifier = Modifier.weight(.8f)){
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun MCAssignment1(){
    PreviewScreen()
}

data class Category(val title: String, val distance: Int)


fun getCategoryList() : MutableList<Category> {
    val list = mutableListOf<Category>()
    list.add(Category("Stop 1", 10))
    list.add(Category("Stop 2", 20))
    list.add(Category("Stop 3", 35))
    list.add(Category("Stop 4", 30))
    list.add(Category("Stop 5", 25))
    list.add(Category("Stop 6", 65))
    list.add(Category("Stop 7", 90))
    list.add(Category("Stop 8", 45))
    list.add(Category("Stop 9", 55))
    list.add(Category("Stop 10", 10))
    list.add(Category("Stop 11", 55))
    list.add(Category("Stop 12", 10))
    list.add(Category("Stop 13", 10))
    list.add(Category("Stop 14", 55))
    list.add(Category("Stop 15", 10))

    return list
}
