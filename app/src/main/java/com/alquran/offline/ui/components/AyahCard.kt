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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Ayah Header Bar (inspired by com.andi.alquran.id / Al Quran Indonesia)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verse Number Badge: e.g. [1:5]
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "${ayah.surahId}:${ayah.verseId}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Direct action icons: Bookmark, Copy, Share
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (ayah.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (ayah.isBookmarked) "Hapus Bookmark" else "Simpan Bookmark",
                            tint = if (ayah.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Salin Ayat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Bagikan Ayat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Arabic Verse Text - Elegant typography with RTL alignment
        Text(
            text = ayah.textAr,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            fontFamily = FontFamily.Serif,
            fontSize = arabicFontSize.sp,
            lineHeight = (arabicFontSize * 2.1f).sp,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Rtl
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Latin Transliteration - Standard Indonesian format
        if (ayah.transliteration.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = ayah.transliteration,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = (translationFontSize * 0.95f).sp,
                    lineHeight = (translationFontSize * 1.45f).sp
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }

        // Indonesian Translation - Standard Kemenag RI
        if (showTranslation && ayah.textId.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ayah.textId,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = translationFontSize.sp,
                    lineHeight = (translationFontSize * 1.55f).sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtle divider between ayahs
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}
