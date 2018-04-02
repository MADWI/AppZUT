package pl.edu.zut.mad.appzut.about

const val MISSING: Int = -1

data class About(
    val titleRes: Int, val subtitleRes: Int,
    val emailRes: Int, val githubRes: Int,
    val facebookRes: Int = MISSING, val websiteRes: Int = MISSING)
