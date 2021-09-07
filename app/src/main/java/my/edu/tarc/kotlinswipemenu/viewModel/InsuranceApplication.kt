package my.edu.tarc.kotlinswipemenu.viewModel

import java.util.*

data class InsuranceApplication(
    val applicationID:String?=null,
    val insuranceID:String?=null,
    val insuranceReferralID:String ?=null,
    val insuranceAppliedDate: Date? = null,
    val insuranceStatus:String?=null,
    val evidences: String? = null,
    var expandable : Boolean = false
)
