package com.mindorks.framework.backuptotelegram.utils

private const val PREFIX_TEMPLATE = "https://t.me/"
private const val AT_SYMBOL = "@"

fun prepareChatId(chatId: String) = if (chatId.contains(PREFIX_TEMPLATE)) {
    "$AT_SYMBOL${chatId.substring(PREFIX_TEMPLATE.length)}"
} else {
    if (chatId.contains(AT_SYMBOL)) {
        "$AT_SYMBOL$chatId"
    } else {
        chatId
    }
}
