package com.example.sashakt

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import java.util.*
import kotlin.math.hypot

abstract class BaseActivity : AppCompatActivity(), OnLocaleChangedListener {
    private var localizationAgent: LocalizationAgent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        localizationAgent = LocalizationAgent(this)
        localizationAgent?.addOnLocaleChangedListener(this)
        localizationAgent?.onCreate()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        localizationAgent?.onResume()
    }

    fun setLanguage(language: String) {
        localizationAgent?.language = language
    }

    fun setLanguage(locale: Locale) {
        localizationAgent?.setLanguage(locale)
    }

    fun setDefaultLanguage(language: String) {
        localizationAgent?.setDefaultLanguage(language)
    }

    fun setDefaultLanguage(locale: Locale) {
        localizationAgent?.setDefaultLanguage(locale)
    }

    fun getLanguage(): String {
        return localizationAgent!!.language
    }

    fun getLocale(): Locale {
        return localizationAgent!!.locale
    }

    override fun beforeLocaleChanged() = Unit
    override fun afterLocaleChanged() = Unit

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showChangeLangDialog(imgButton:ImageButton) {

       val dialogView = View.inflate(this, R.layout.language_dialog, null)
        val dialog = Dialog(this, R.style.MyAlertDialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)

        dialog.setOnShowListener { revealShow(dialogView, true, null, imgButton) }

        dialog.setOnKeyListener(DialogInterface.OnKeyListener { dialogInterface:DialogInterface, i:Int, keyEvent:KeyEvent ->
            if (i == KeyEvent.KEYCODE_BACK) {
                revealShow(dialogView, false, dialog, imgButton)
                return@OnKeyListener true
            }
            false
        })

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val imageView = dialog.findViewById<View>(R.id.closeDialogImg) as ImageView
        imageView.setOnClickListener { revealShow(dialogView, false, dialog, imgButton) }

        val spinner1: AppCompatSpinner = dialogView!!.findViewById(R.id.spinner1)
        val positiveButton=dialog.findViewById<View>(R.id.change) as Button
        val negativeButton=dialog.findViewById<View>(R.id.cancel) as Button
        negativeButton.setOnClickListener { revealShow(dialogView, false, dialog, imgButton) }

        positiveButton.setOnClickListener {
            when (spinner1.selectedItemPosition) {
                1 //Hindi
                -> {
                    dialog.dismiss()
                    setLanguage(LanguageSetting.LANGUAGE_HINDI)
                    return@setOnClickListener
                }
                else //By default set to english
                -> {
                    dialog.dismiss()
                    setLanguage(LanguageSetting.LANGUAGE_ENGLISH)
                    return@setOnClickListener
                }
            }
        }
    }

    //Animation for ImageButton
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun revealShow(dialogView:View, b:Boolean, dialog:Dialog?, imgButton: ImageButton) {
        val view = dialogView.findViewById<View>(R.id.dialog)
        val w = view.width
        val h = view.height
        val endRadius = hypot(w.toDouble(), h.toDouble()).toInt()
        val cx = (imgButton.x + imgButton.width / 2).toInt()
        val cy: Int = imgButton.y.toInt() + imgButton.height + 56
        if (b) {
            val revealAnimator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, endRadius.toFloat())
            view.visibility = View.VISIBLE
            revealAnimator.duration = 700
            revealAnimator.start()
        } else {
            val anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius.toFloat(), 0f)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    dialog?.dismiss()
                    view.visibility = View.INVISIBLE
                }
            })
            anim.duration = 700
            anim.start()
        }
    }
}