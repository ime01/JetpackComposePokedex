package com.plcoding.jetpackcomposepokedex.data.remote.responses

import com.plcoding.jetpackcomposepokedex.data.remote.responses.AbilityX

data class Ability(
    val ability: AbilityX,
    val is_hidden: Boolean,
    val slot: Int
)