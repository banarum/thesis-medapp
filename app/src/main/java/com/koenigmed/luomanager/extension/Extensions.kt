package com.koenigmed.luomanager.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.system.IResourceManager
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun String.toUUID(): UUID? = UUID.fromString(this)

fun String.isValidJson(): Boolean {
    if (isEmpty()) return false
    var openCount = 0
    var closeCount = 0
    for (c in this.toCharArray()) {
        if (c in "{") {
            ++openCount
        } else if (c in "}") {
            ++closeCount
        }
    }
    return openCount == closeCount
}

fun Throwable.userMessage(resourceManager: IResourceManager) = when (this) {
    is HttpException -> when (this.code()) {
        304 -> resourceManager.getString(R.string.not_modified_error)
        400 -> resourceManager.getString(R.string.bad_request_error)
        401 -> resourceManager.getString(R.string.unauthorized_error)
        403 -> resourceManager.getString(R.string.forbidden_error)
        404 -> resourceManager.getString(R.string.not_found_error)
        405 -> resourceManager.getString(R.string.method_not_allowed_error)
        409 -> resourceManager.getString(R.string.conflict_error)
        422 -> resourceManager.getString(R.string.unprocessable_error)
        500 -> resourceManager.getString(R.string.server_error_error)
        else -> resourceManager.getString(R.string.unknown_error)
    }
    is IOException -> resourceManager.getString(R.string.network_error)
    else -> resourceManager.getString(R.string.unknown_error)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Date.getGraphDay(): String {
    val format = SimpleDateFormat("d LLL", Locale.getDefault())
    return format.format(date)
}

fun Fragment.getStringArray(arrayId: Int): Array<out String> {
    return resources.getStringArray(arrayId)
}

fun Fragment.getColor(colorId: Int): Int {
    return ResourcesCompat.getColor(resources, colorId, activity!!.theme)
}

fun Fragment.getDrawable(drawableId: Int): Drawable {
    return ResourcesCompat.getDrawable(resources, drawableId, activity!!.theme)!!
}

fun Fragment.showBtSettings() {
    val packageManager = activity!!.packageManager
    val intent = Intent()
    intent.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        activity!!.showToast(getString(R.string.error_device_does_not_support_bluetooth))
    }
}

fun View.visibleOrGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

inline fun View.waitForLayout(crossinline f: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            f()
        }
    })
}

fun TextView.setStrikethrough() {
    paintFlags = getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
}

fun SeekBar.onProgressChanged(onProgressChanged: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChanged.invoke(seekBar, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    })
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChanged.invoke(p0.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
inline fun TextView.onRightCompoundDrawableClick(crossinline f: () -> Unit) {
    this.setOnTouchListener(View.OnTouchListener { _, event ->
        val DRAWABLE_RIGHT = 2
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= this.right - this.paddingRight -
                    this.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                f()
                return@OnTouchListener true
            }
        }
        false
    })
}

fun String.removeMeasure() = split(" ")[0]