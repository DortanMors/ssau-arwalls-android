package ru.ssau.arwalls.common

import android.graphics.Color
import android.graphics.Paint
import ru.ssau.arwalls.rawdepth.R

object Settings {
    var markerVisibility = true
    var strokeWidth = 5f
    var markerSize = 20f // размер маркера позиции
    var paintColor = Color.RED
    var markerColor = Color.GREEN
    var paintStyle = Paint.Style.FILL
    var minConfidence = 0.45f // минимальная планка достоверности точки, иначе отбрасывеется
    var snackBackgroundColor = -0x40cdcdce
    var mapScale = 100
    var mapOffsetX = 0f
    var mapOffsetY = 0f
    var scanVerticalRadius = 0.15f // область вертикального сканирования метрах, чтобы не захватывать горизонтальные поверхности
    var heightOffset = 0f // повышает или понижает область вертикального сканирования в runtime
    var maxNumberOfPointsToRender = 20000f

    const val MinAccuracy = 0f
    const val MaxAccuracy = 1f

    const val MinVerticalRadius = 0f
    const val MaxVerticalRadius = 1.5f

    const val MinHeightOffset = -3f
    const val MaxHeightOffset = 3f

    const val MinRenderPoints = 1f
    const val MaxRenderPoints = 50000f

    val NoTrackingWarnings = listOf(R.string.waiting_depth, R.string.no_tracking)
    val HeightWarnings = listOf(R.string.hold_above, R.string.hold_below)
}
