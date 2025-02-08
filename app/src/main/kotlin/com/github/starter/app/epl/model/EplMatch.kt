package com.github.starter.modules.epl.model;

import java.time.LocalDate

data class EplMatch(
    var season: Int = 0,
    var wk: Int = 0,
    var matchDate: LocalDate? = null,
    var team: String? = null,
    var opponent: String? = null,
    var venue: String? = null,
    var result: String? = null,
    var gf: Int = 0,
    var ga: Int = 0,
    var points: Int = 0
)

