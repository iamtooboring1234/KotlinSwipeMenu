package my.edu.tarc.kotlinswipemenu.viewModel

data class Insurance(
    val insuranceID: String?=null,
    val insuranceName: String?=null,
    val insuranceComp: String?=null,
    val insurancePlan: String?=null,
    val insuranceType: String?=null,
    val insuranceCoverage: List<String>? = null,
    val insurancePrice : Double? = null
)
