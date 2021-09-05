package my.edu.tarc.kotlinswipemenu.Model

data class InsuranceApplication(
    val applicationID:String?=null,
    val insuranceID:String?=null,
    val insuranceReferralID:String ?=null,
    val insuranceAppliedDate: String? = null,
    val insuranceStatus:String?=null,
    val evidences: String? = null
)
