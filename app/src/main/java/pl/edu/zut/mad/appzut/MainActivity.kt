package pl.edu.zut.mad.appzut

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import org.joda.time.LocalDate
import pl.edu.zut.mad.schedule.DateListener
import pl.edu.zut.mad.schedule.ScheduleFragment
import pl.edu.zut.mad.schedule.search.SearchActivity

class MainActivity : AppCompatActivity(), DateListener {

    companion object {
        private val DATE_FORMAT = "LLLL"
    }

    private val scheduleFragment = ScheduleFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        initBar()
        startScheduleFragment(savedInstanceState)
        scheduleFragment.dateListener = this
    }

    private fun initBar() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.ic_logo_zut)
    }

    private fun startScheduleFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            return
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, scheduleFragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onDateChanged(date: LocalDate) {
        supportActionBar?.title = DateFormat.format(DATE_FORMAT, date.toDate()).toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_today -> scheduleFragment.moveToToday()
            R.id.action_authors -> showAboutUs()
            R.id.action_refresh -> scheduleFragment.refreshSchedule()
            R.id.action_search -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.action_logout -> scheduleFragment.logout()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showAboutUs() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, AboutUsFragment())
            .addToBackStack(null)
            .commit()
    }
}
