package pl.edu.zut.mad.appzut.about

import android.content.res.Resources
import android.support.annotation.ArrayRes
import android.support.annotation.RawRes
import com.google.gson.Gson

class AboutProvider(val resources: Resources) {

    private val parser = Gson()

    fun getFromArray(@ArrayRes id: Int): MutableList<About> {
        val aboutList = mutableListOf<About>()
        val aboutIds = resources.obtainTypedArray(id)
        for (i in 0 until aboutIds.length()) {
            val aboutRawId = aboutIds.getResourceId(i, 0)
            openReaderForId(aboutRawId).use {
                val about = parser.fromJson<About>(it.readText(), About::class.java)
                aboutList.add(about)
            }
        }
        return aboutList
    }

    private fun openReaderForId(@RawRes id: Int) = resources.openRawResource(id).bufferedReader()
}
