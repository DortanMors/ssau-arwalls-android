package ru.ssau.arwalls.common

import android.graphics.Color
import android.graphics.Paint

object Settings {
    var markerVisibility = true
    var strokeWidth = 5f
    var markerSize = 20f // размер маркера позиции
    var paintColor = Color.RED
    var markerColor = Color.GREEN
    var paintStyle = Paint.Style.STROKE
    var minConfidence = 0.45f // минимальная планка достоверности точки, иначе отбрасывеется
    var snackBackgroundColor = -0x40cdcdce
    var mapScale = 100f
    var mapOffsetX = 0f
    var mapOffsetY = 0f
    var maxNumberOfPointsToRender = 20000f

    const val MinMapScale = 1f
    const val MaxMapScale = 500f

    const val MinRenderPoints = 0f
    const val MaxRenderPoints = 50000f

    const val nameImagesDB = "tags.db.imgdb"
}
