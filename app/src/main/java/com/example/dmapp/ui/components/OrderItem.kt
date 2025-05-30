package com.example.dmapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import com.example.dmapp.data.Order
import com.example.dmapp.data.OrderStatus
import java.time.format.DateTimeFormatter

@Composable
fun OrderItem(
    order: Order,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundColor = when (order.status) {
        OrderStatus.NEW -> Color.White
        OrderStatus.IN_PROGRESS -> Color(0xFFE8F5E9) // Light Green
        OrderStatus.COMPLETED -> Color.White // Changed from Light Gray to White
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onOrderClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Заголовок с номером заказа и статусом
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("${order.orderNumber} Заказ ")
                        val externalNumber = order.externalOrderNumber
                        if (externalNumber.length >= 4) {
                            val mainPart = externalNumber.substring(0, externalNumber.length - 4)
                            val lastFourDigits = externalNumber.substring(externalNumber.length - 4)
                            append(mainPart)
                            withStyle(style = SpanStyle(
                                color = Color(0xFF03A9F4), // Light Blue
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )) {
                                append(lastFourDigits)
                            }
                        } else {
                            append(externalNumber)
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when(order.status) {
                        OrderStatus.NEW -> "Новый"
                        OrderStatus.IN_PROGRESS -> "В работе"
                        OrderStatus.COMPLETED -> "Выполнен"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (order.status) {
                        OrderStatus.NEW -> MaterialTheme.colorScheme.primary
                        OrderStatus.IN_PROGRESS -> Color(0xFF2E7D32) // Dark Green
                        OrderStatus.COMPLETED -> Color.Gray
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Время доставки
            Text(
                text = "Доставка: ${order.deliveryTimeStart.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                        order.deliveryTimeEnd.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Адрес доставки
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.deliveryAddress,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                // Иконка навигатора
                IconButton(
                    onClick = {
                        if (order.latitude != null && order.longitude != null) {
                            // Открываем Яндекс Навигатор с маршрутом
                            val uri = Uri.parse("yandexnavi://build_route_on_map?lat_to=${order.latitude}&lon_to=${order.longitude}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            
                            // Проверяем, установлен ли Яндекс Навигатор
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                // Если не установлен, открываем Яндекс Карты в браузере
                                val webUri = Uri.parse("https://yandex.ru/maps/?mode=routes&rtext=~${order.latitude},${order.longitude}")
                                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                                context.startActivity(webIntent)
                            }
                        } else {
                            // Если координат нет, пытаемся открыть по адресу
                            val escapedAddress = Uri.encode(order.deliveryAddress)
                            val webUri = Uri.parse("https://yandex.ru/maps/?mode=search&text=$escapedAddress")
                            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                            context.startActivity(webIntent)
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = "Открыть навигатор",
                        tint = Color(0xFF2196F3) // Синий цвет для навигации
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Информация о клиенте и кнопки связи
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.clientName} • ${order.clientPhone}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                // Кнопки связи
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${order.clientPhone}")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Call,
                            contentDescription = "Позвонить",
                            tint = Color(0xFF2196F3) // Светло-синий для звонка
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${order.clientPhone}")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Message,
                            contentDescription = "Отправить SMS",
                            tint = Color.Gray
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=${order.clientPhone.replace("+", "")}")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        // Используем кастомную иконку для WhatsApp
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.dmapp.R.drawable.ic_whatsapp),
                            contentDescription = "Написать в WhatsApp",
                            tint = Color.Unspecified // Не тонируем, так как иконка уже имеет нужный цвет
                        )
                    }
                }
            }
            
            // Заметки
            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 ${order.notes}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Дополнительная информация
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.weight} кг",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "${order.orderAmount} ₽",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (order.isPrepaid) Color(0xFF2E7D32) else Color.Black
                )
            }
        }
    }
} 