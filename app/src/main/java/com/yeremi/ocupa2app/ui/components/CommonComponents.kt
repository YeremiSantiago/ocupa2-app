package com.yeremi.ocupa2app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.ui.theme.*

@Composable
fun Ocupa2Logo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AcidLime, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(3.dp, Obsidian, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .padding(1.dp)
                        .background(SolarOrange, RoundedCornerShape(100.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "OCUPA2",
            color = IceWhite,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        Text(
            text = "NIGHT SHIFT",
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun Ocupa2TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    helperText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label.uppercase(),
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Border, RoundedCornerShape(12.dp)),
            placeholder = { Text(placeholder, color = MutedGray, fontFamily = WorkSansFamily) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = MutedGray) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = GraphiteRaise,
                unfocusedContainerColor = GraphiteRaise,
                disabledContainerColor = GraphiteRaise,
                focusedTextColor = IceWhite,
                unfocusedTextColor = IceWhite,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AcidLime
            )
        )
        if (helperText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = helperText,
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun Ocupa2Button(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = AcidLime,
    contentColor: Color = Obsidian
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text.uppercase(),
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
