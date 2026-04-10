package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlin.math.abs

// ── data model ──────────────────────────────────────────────────────────────

private data class KitchenUnit(
    val label: String,
    val toBase: (Double) -> Double,
    val fromBase: (Double) -> Double
)

private enum class Category(val title: String, val units: List<KitchenUnit>) {
    Weight("Weight", listOf(
        KitchenUnit("mg",  { it / 1_000.0 },       { it * 1_000.0 }),
        KitchenUnit("g",   { it },                  { it }),
        KitchenUnit("kg",  { it * 1_000.0 },        { it / 1_000.0 }),
        KitchenUnit("oz",  { it * 28.3495 },        { it / 28.3495 }),
        KitchenUnit("lb",  { it * 453.592 },        { it / 453.592 }),
    )),
    Volume("Volume", listOf(
        KitchenUnit("mL",    { it },                { it }),
        KitchenUnit("L",     { it * 1_000.0 },      { it / 1_000.0 }),
        KitchenUnit("tsp",   { it * 4.92892 },      { it / 4.92892 }),
        KitchenUnit("tbsp",  { it * 14.7868 },      { it / 14.7868 }),
        KitchenUnit("cup",   { it * 236.588 },      { it / 236.588 }),
        KitchenUnit("fl oz", { it * 29.5735 },      { it / 29.5735 }),
    )),
    Temperature("Temp", listOf(
        KitchenUnit("°C", { it },                        { it }),
        KitchenUnit("°F", { (it - 32.0) * 5.0 / 9.0 },  { it * 9.0 / 5.0 + 32.0 }),
        KitchenUnit("K",  { it - 273.15 },               { it + 273.15 }),
    ))
}

private fun formatResult(value: Double): String {
    if (value.isNaN() || value.isInfinite()) return "—"
    val a = abs(value)
    val formatted = when {
        a == 0.0      -> "0"
        a >= 10_000.0 -> "%.0f".format(value)
        a >= 1_000.0  -> "%.1f".format(value)
        a >= 100.0    -> "%.2f".format(value)
        a >= 10.0     -> "%.3f".format(value)
        else          -> "%.4f".format(value)
    }
    return if (formatted.contains('.')) formatted.trimEnd('0').trimEnd('.') else formatted
}

// ── screen ──────────────────────────────────────────────────────────────────

@Composable
fun ConverterScreen(backdrop: LayerBackdrop) {
    var category by remember { mutableStateOf(Category.Weight) }
    var inputText by remember { mutableStateOf("") }
    var fromUnit  by remember { mutableStateOf(Category.Weight.units[1]) } // g
    var toUnit    by remember { mutableStateOf(Category.Weight.units[2]) } // kg

    LaunchedEffect(category) {
        fromUnit  = category.units.first()
        toUnit    = category.units.getOrElse(1) { category.units.first() }
        inputText = ""
    }

    val result = remember(inputText, fromUnit, toUnit) {
        val v = inputText.toDoubleOrNull() ?: return@remember ""
        formatResult(toUnit.fromBase(fromUnit.toBase(v)))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Category.entries.forEach { cat ->
                CategoryTab(
                    label = cat.title,
                    selected = cat == category,
                    backdrop = backdrop,
                    modifier = Modifier.weight(1f),
                    onClick = { category = cat }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // FROM panel
        ConversionPanel(
            label = "FROM",
            displayValue = inputText,
            units = category.units,
            selectedUnit = fromUnit,
            isInput = true,
            backdrop = backdrop,
            onValueChange = { new ->
                if (new.matches(Regex("^-?\\d*\\.?\\d*$"))) inputText = new
            },
            onUnitSelect = { fromUnit = it }
        )

        // Swap button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            SwapButton(backdrop = backdrop) {
                val prevResult = result
                val tmp = fromUnit
                fromUnit = toUnit
                toUnit = tmp
                if (prevResult.isNotEmpty()) inputText = prevResult
            }
        }

        // TO panel
        ConversionPanel(
            label = "TO",
            displayValue = result.ifEmpty { "—" },
            units = category.units,
            selectedUnit = toUnit,
            isInput = false,
            backdrop = backdrop,
            onValueChange = {},
            onUnitSelect = { toUnit = it }
        )
    }
}

// ── category tab ────────────────────────────────────────────────────────────

@Composable
private fun CategoryTab(
    label: String,
    selected: Boolean,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = ContinuousRoundedRectangle(14.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF2640E8).copy(alpha = 0.70f), Color(0xFF1FB4FF).copy(alpha = 0.75f))
    )

    Box(
        modifier = modifier
            .height(42.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = { blur(6.dp.toPx()); vibrancy() },
                onDrawSurface = {
                    if (selected) drawRect(brush = gradient) else drawRect(Color.White.copy(alpha = 0.10f))
                    drawRect(
                        color = if (selected) Color(0xFF1FB4FF).copy(alpha = 0.55f) else Color.White.copy(alpha = 0.18f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── conversion panel ────────────────────────────────────────────────────────

@Composable
private fun ConversionPanel(
    label: String,
    displayValue: String,
    units: List<KitchenUnit>,
    selectedUnit: KitchenUnit,
    isInput: Boolean,
    backdrop: LayerBackdrop,
    onValueChange: (String) -> Unit,
    onUnitSelect: (KitchenUnit) -> Unit
) {
    val shape = ContinuousRoundedRectangle(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(8.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 10.dp.toPx(),
                        refractionAmount = 14.dp.toPx(),
                        chromaticAberration = false
                    )
                },
                onDrawSurface = {
                    drawRect(Color.Black.copy(alpha = 0.45f))
                    drawRect(color = Color.White.copy(alpha = 0.22f), style = Stroke(width = 1.dp.toPx()))
                }
            )
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF1FB4FF).copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        if (isInput) {
            BasicTextField(
                value = displayValue,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                cursorBrush = SolidColor(Color(0xFF1FB4FF)),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    Box(Modifier.fillMaxWidth()) {
                        if (displayValue.isEmpty()) {
                            Text(
                                text = "0",
                                color = Color.White.copy(alpha = 0.20f),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        inner()
                    }
                }
            )
        } else {
            Text(
                text = displayValue,
                color = if (displayValue == "—") Color.White.copy(alpha = 0.25f) else Color(0xFF1FB4FF),
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            units.forEach { unit ->
                UnitChip(
                    label = unit.label,
                    selected = unit == selectedUnit,
                    onClick = { onUnitSelect(unit) }
                )
            }
        }
    }
}

// ── unit chip ────────────────────────────────────────────────────────────────

@Composable
private fun UnitChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFF1FB4FF).copy(alpha = 0.20f) else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White)
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color(0xFF1FB4FF) else Color.White.copy(alpha = 0.55f),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── swap button ──────────────────────────────────────────────────────────────

@Composable
private fun SwapButton(backdrop: LayerBackdrop, onClick: () -> Unit) {
    val shape = ContinuousRoundedRectangle(16.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(48.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = { blur(8.dp.toPx()); vibrancy() },
                onDrawSurface = {
                    drawRect(Color.White.copy(alpha = 0.12f))
                    drawRect(color = Color.White.copy(alpha = 0.28f), style = Stroke(width = 1.dp.toPx()))
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SwapVert,
            contentDescription = "Swap units",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
