package pl.edu.zut.mad.appzut.util

import android.content.Intent
import android.databinding.BindingAdapter
import android.net.Uri
import android.support.annotation.StringRes
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import pl.edu.zut.mad.appzut.R
import pl.edu.zut.mad.appzut.about.MISSING

private const val SEND_MAIL_SCHEME = "mailto"
private const val GITHUB_AVATAR_URL_FORMAT = "%s.png"

@BindingAdapter("githubAvatar")
fun githubAvatar(imageView: ImageView, @StringRes githubProfileRes: Int) =
    with(imageView.context) {
        val imageUrl = String.format(GITHUB_AVATAR_URL_FORMAT, getString(githubProfileRes))
        Picasso.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_black_24dp)
            .into(imageView)
    }

@BindingAdapter("webOnClick")
fun webOnClick(view: View, @StringRes urlRes: Int) =
    view.setOnClickListener {
        with(it.context) {
            val webUri = Uri.parse(getString(urlRes))
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

@BindingAdapter("emailOnClick")
fun emailOnClick(view: View, @StringRes emailRes: Int) =
    view.setOnClickListener {
        with(it.context) {
            val mailUri = Uri.fromParts(SEND_MAIL_SCHEME, getString(emailRes), null)
            startActivity(Intent(Intent.ACTION_SENDTO, mailUri))
        }
    }

@BindingAdapter("hideIfMissingValue")
fun hideIfMissingValue(view: View, value: Int) {
    if (value == MISSING) {
        view.visibility = View.INVISIBLE
    }
}
