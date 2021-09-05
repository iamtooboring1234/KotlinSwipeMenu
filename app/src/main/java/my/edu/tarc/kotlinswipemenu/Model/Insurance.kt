package my.edu.tarc.kotlinswipemenu.Model

import java.util.*

data class Insurance(
    val insuranceID: String?=null,
    val insuranceName: String?=null,
    val insuranceComp: String?=null,
    val insurancePlan: String?=null,
    val insuranceType: String?=null,
    val insuranceCoverage: List<String>? = null
)
