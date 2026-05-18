package si.uni_lj.fe.libri.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 14.sp
    )
)