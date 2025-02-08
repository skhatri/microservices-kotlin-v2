package com.github.starter.modules.epl.model;

data class EplStanding(
    var season: Int = 0,
    var ranking: Int = 0,
    var team: String? = null,
    var played: Int = 0,
    var gf: Int = 0,
    var ga: Int = 0,
    var gd: Int = 0,
    var points: Int = 0
)
