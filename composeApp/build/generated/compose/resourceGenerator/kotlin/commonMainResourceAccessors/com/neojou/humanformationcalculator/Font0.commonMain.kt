@file:OptIn(InternalResourceApi::class)

package com.neojou.humanformationcalculator

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceContentHash
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/com.neojou.humanformationcalculator/"

@delegate:ResourceContentHash(193_210_956)
internal val Res.font.notosanstc_regular: FontResource by lazy {
      FontResource("font:notosanstc_regular", setOf(
        ResourceItem(setOf(), "${MD}font/notosanstc_regular.otf", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainFont0Resources(map: MutableMap<String, FontResource>) {
  map.put("notosanstc_regular", Res.font.notosanstc_regular)
}
