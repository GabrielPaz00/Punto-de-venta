package com.example.pointofsale.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pointofsale.R
import com.example.pointofsale.core.theme.PointOfSaleTheme

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentPadding: Dp = 16.dp
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(24.dp))
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = stringResource(id = R.string.app_logo_content_description),
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(tint)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppLogoPreview() {
    PointOfSaleTheme(darkTheme = true) {
        AppLogo(
            modifier = Modifier.size(200.dp)
        )
    }
}
