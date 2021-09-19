package my.edu.tarc.kotlinswipemenu.viewModel

import java.util.*

data class CancelInsurance (
    val cancelInsuranceUID : String?=null,
    val referralInsuranceUID : String ?=null,
    val reason : String?=null,
    val appliedDate : Date?=null,
    var expandable : Boolean = false
)