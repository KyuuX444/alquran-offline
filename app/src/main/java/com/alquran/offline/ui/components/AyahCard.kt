package com.alquran.offline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.offline.model.Ayah

@Composable
fun AyahCard(
    ayah: Ayah,
    arabicFontSize: Float,
    translationFontSize: Float,
    showTranslation: Boolean,
    onBookmarkClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Minimal Top Row: Small Verse Badge + Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Small circular verse number
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ayah.verseId.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Minimal Actions
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (ayah.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (ayah.isBookmarked) "Hapus Bookmark" else "Simpan Bookmark",
                        tint = if (ayah.isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = onCopyClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Salin Teks",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Bagikan Ayat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Arabic Text (Clear, Large, Comfortable Line Height)
        Text(
            text = ayah.textAr,
            modifier = Modifier.fillMaxWidth(),
            fontFamily = FontFamily.Serif,
            fontSize = arabicFontSize.sp,
            lineHeight = (arabicFontSize * 1.85f).sp,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Rtl
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Transliteration
        if (ayah.transliteration.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ayah.transliteration,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = (translationFontSize * 0.92f).sp,
                    lineHeight = (translationFontSize * 1.35f).sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Indonesian Translation
        if (showTranslation && ayah.textId.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ayah.textId,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = translationFontSize.sp,
                    lineHeight = (translationFontSize * 1.45f).sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Minimal separator divider
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            thickness = 0.5.dp
        )
    }
}
