package com.connan.kitchenassistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private sealed interface Block {
    data class Heading(val level: Int, val text: String) : Block
    data class BulletItem(val text: String, val depth: Int) : Block
    data class OrderedItem(val number: Int, val text: String) : Block
    data class CodeBlock(val code: String) : Block
    data class Paragraph(val text: String) : Block
    data object Blank : Block
}

private fun parse(markdown: String): List<Block> {
    val blocks = mutableListOf<Block>()
    val lines = markdown.lines()
    var i = 0
    while (i < lines.size) {
        val line = lines[i]

        // Fenced code block
        if (line.trimStart().startsWith("```")) {
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            blocks.add(Block.CodeBlock(codeLines.joinToString("\n")))
            i++ // consume closing ```
            continue
        }

        // Heading
        val headingMatch = Regex("^(#{1,6})\\s+(.*)").find(line)
        if (headingMatch != null) {
            blocks.add(Block.Heading(headingMatch.groupValues[1].length, headingMatch.groupValues[2]))
            i++
            continue
        }

        // Bullet list item (-, *, +)
        val bulletMatch = Regex("^(\\s*)[-*+]\\s+(.*)").find(line)
        if (bulletMatch != null) {
            val depth = bulletMatch.groupValues[1].length / 2
            blocks.add(Block.BulletItem(bulletMatch.groupValues[2], depth))
            i++
            continue
        }

        // Ordered list item
        val orderedMatch = Regex("^(\\d+)\\.\\s+(.*)").find(line)
        if (orderedMatch != null) {
            blocks.add(Block.OrderedItem(orderedMatch.groupValues[1].toInt(), orderedMatch.groupValues[2]))
            i++
            continue
        }

        // Blank line
        if (line.isBlank()) {
            blocks.add(Block.Blank)
            i++
            continue
        }

        // Paragraph (merge consecutive non-special lines)
        val paragraphLines = mutableListOf<String>()
        while (i < lines.size) {
            val l = lines[i]
            if (l.isBlank()) break
            if (Regex("^#{1,6}\\s").containsMatchIn(l)) break
            if (Regex("^\\s*[-*+]\\s").containsMatchIn(l)) break
            if (Regex("^\\d+\\.\\s").containsMatchIn(l)) break
            if (l.trimStart().startsWith("```")) break
            paragraphLines.add(l)
            i++
        }
        if (paragraphLines.isNotEmpty()) {
            blocks.add(Block.Paragraph(paragraphLines.joinToString(" ")))
        }
    }
    return blocks
}

// Renders inline spans: **bold**, *italic*, `code`, ~~strikethrough~~
private fun inlineAnnotated(text: String, baseColor: Color, codeBackground: Color) =
    buildAnnotatedString {
        val src = text
        var pos = 0
        while (pos < src.length) {
            // Bold+italic ***
            if (src.startsWith("***", pos)) {
                val end = src.indexOf("***", pos + 3)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                        append(src.substring(pos + 3, end))
                    }
                    pos = end + 3; continue
                }
            }
            // Bold **
            if (src.startsWith("**", pos)) {
                val end = src.indexOf("**", pos + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(src.substring(pos + 2, end))
                    }
                    pos = end + 2; continue
                }
            }
            // Italic *
            if (src[pos] == '*') {
                val end = src.indexOf('*', pos + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(src.substring(pos + 1, end))
                    }
                    pos = end + 1; continue
                }
            }
            // Strikethrough ~~
            if (src.startsWith("~~", pos)) {
                val end = src.indexOf("~~", pos + 2)
                if (end != -1) {
                    withStyle(SpanStyle(color = baseColor.copy(alpha = 0.5f))) {
                        append(src.substring(pos + 2, end))
                    }
                    pos = end + 2; continue
                }
            }
            // Inline code `
            if (src[pos] == '`') {
                val end = src.indexOf('`', pos + 1)
                if (end != -1) {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            background = codeBackground,
                            color = baseColor.copy(alpha = 0.85f)
                        )
                    ) {
                        append(src.substring(pos + 1, end))
                    }
                    pos = end + 1; continue
                }
            }
            append(src[pos])
            pos++
        }
    }

@Composable
fun MarkdownText(
    markdown: String,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val codeBackground = Color.Black.copy(alpha = 0.30f)
    val blocks = parse(markdown)

    Column(modifier = modifier) {
        var prevWasBlank = false
        blocks.forEachIndexed { index, block ->
            when (block) {
                is Block.Blank -> {
                    if (!prevWasBlank) Spacer(Modifier.height(6.dp))
                    prevWasBlank = true
                    return@forEachIndexed
                }
                is Block.Heading -> {
                    val (fontSize, weight) = when (block.level) {
                        1 -> 18.sp to FontWeight.Bold
                        2 -> 16.sp to FontWeight.Bold
                        else -> 14.sp to FontWeight.SemiBold
                    }
                    if (index > 0) Spacer(Modifier.height(4.dp))
                    Text(
                        text = inlineAnnotated(block.text, color, codeBackground),
                        style = TextStyle(fontSize = fontSize, fontWeight = weight, lineHeight = (fontSize.value + 6).sp),
                        color = color
                    )
                }
                is Block.BulletItem -> {
                    val indent = (block.depth * 12).dp
                    Row(modifier = Modifier.padding(start = indent)) {
                        Text(text = "• ", color = color, fontSize = 14.sp)
                        Text(
                            text = inlineAnnotated(block.text, color, codeBackground),
                            style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                            color = color
                        )
                    }
                }
                is Block.OrderedItem -> {
                    Row {
                        Text(text = "${block.number}. ", color = color, fontSize = 14.sp)
                        Text(
                            text = inlineAnnotated(block.text, color, codeBackground),
                            style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                            color = color
                        )
                    }
                }
                is Block.CodeBlock -> {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = block.code,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = color.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier
                            .background(codeBackground, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                }
                is Block.Paragraph -> {
                    Text(
                        text = inlineAnnotated(block.text, color, codeBackground),
                        style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                        color = color
                    )
                }
            }
            prevWasBlank = false
        }
    }
}
