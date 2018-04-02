package pl.edu.zut.mad.appzut.util

import android.content.Intent
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import pl.edu.zut.mad.appzut.R
import pl.edu.zut.mad.appzut.about.MISSING

private const val SEND_MAIL_SCHEME = "mailto"
private const val GITHUB_AVATAR_URL_FORMAT = "%s.png"

@BindingAdapter("githubAvatar")
fun githubAvatar(imageView: ImageView, githubProfileUrl: String) {
    val avatarUrl = String.format(GITHUB_AVATAR_URL_FORMAT, githubProfileUrl)
    Picasso.with(imageView.context)
        .load(avatarUrl)
        .placeholder(R.drawable.ic_person_black_24dp)
        .into(imageView)
}

@BindingAdapter("webOnClick")
fun webOnClick(view: View, url: String) =
    view.setOnClickListener {
        val webUri = Uri.parse(url)
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        it.context.startActivity(webIntent)
    }

@BindingAdapter("emailOnClick")
fun emailOnClick(view: View, email: String) =
    view.setOnClickListener {
        val mailUri = Uri.fromParts(SEND_MAIL_SCHEME, email, null)
        val emailIntent = Intent(Intent.ACTION_SENDTO, mailUri)
        it.context.startActivity(emailIntent)
    }

@BindingAdapter("hideIfMissingValue")
fun hideIfMissingValue(view: View, value: Int) {
    if (value == MISSING) {
        view.visibility = View.INVISIBLE
    }
}
