package pl.edu.zut.mad.appzut.about

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import kotlinx.android.synthetic.main.activity_about.*
import pl.edu.zut.mad.appzut.R
import pl.edu.zut.mad.appzut.databinding.AboutCardBinding
import pl.edu.zut.mad.appzut.util.CollapsingToolbarColorAnimator

class AboutActivity : AppCompatActivity() {

    companion object {
        private const val VERSION_FORMAT = "v%s"
    }

    private lateinit var collapsingToolbarColorAnimator: CollapsingToolbarColorAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_version).title = getVersionText()
        return super.onPrepareOptionsMenu(menu)
    }

    private fun getVersionText(): String {
        val versionText = packageManager.getPackageInfo(packageName, 0).versionName
        return String.format(VERSION_FORMAT, versionText)
    }

    private fun init() {
        setupBar()
        setupAnimator()
        setupAboutViews()
    }

    private fun setupBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupAnimator() {
        collapsingToolbarColorAnimator = CollapsingToolbarColorAnimator(collapsingToolbarLayout)
        collapsingToolbarColorAnimator.addAnimatorForView(cardsContentView)
    }

    private fun setupAboutViews() {
        AboutProvider(resources).getFromArray(R.array.about)
            .forEach { setupAboutView(it) }
    }

    private fun setupAboutView(about: About) {
        DataBindingUtil
            .inflate<AboutCardBinding>(layoutInflater, R.layout.about_card, aboutContainerView, true)
            .about = about
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        collapsingToolbarColorAnimator.removeAllAnimators()
    }
}
