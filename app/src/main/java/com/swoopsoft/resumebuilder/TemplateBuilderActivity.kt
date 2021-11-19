package com.swoopsoft.resumebuilder

import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.os.Bundle
import com.swoopsoft.resumebuilder.R
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

class TemplateBuilderActivity : AppCompatActivity() {
    var templateLayout: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_builder)
        templateLayout = findViewById(R.id.template_builder_layout)
    }

    private val longClickListener = OnLongClickListener { v: View ->
        val clipText = "This is our clipData text"
        val item = ClipData.Item(clipText)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(clipText, mimeTypes, item)
        val dragShadowBuilder = DragShadowBuilder()
        v.startDragAndDrop(data, dragShadowBuilder, v, 0)
        v.visibility = View.INVISIBLE
        true
    }
    private val clickListener = View.OnClickListener { v: View ->
        val clipText = "This is our clipData Text"
        val item = ClipData.Item(clipText)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(clipText, mimeTypes, item)
        val dragShadowBuilder = DragShadowBuilder(v)
        v.startDragAndDrop(data, dragShadowBuilder, v, 0)
        v.visibility = View.INVISIBLE
    }


        private val touchListener = OnTouchListener { it, motionEvent ->
                when(motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val clipText = "This is our clipData text"
                val item = ClipData.Item(clipText)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimeTypes, item)

                val dragShadowBuilder = DragShadowBuilder(it)
                it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                it.visibility = INVISIBLE
            }
            MotionEvent.ACTION_UP -> {
                it.performClick()
            }
        }
            true
        }

        private val dragListener = OnDragListener{ view, event ->
                when(event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
//                text1.text = String.format(resources.getString(R.string.started_message),view.id) //"Started: ${view.id}"
//                text2.text = String.format(resources.getString(R.string.started_message),view.tag) //"Started: ${view.tag}"
//                text1.setTextColor(Color.BLUE)
//                text1.invalidate()

                val v = event.localState as View
                (v as? ImageView)?.setColorFilter(Color.RED)
                v.invalidate()
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
//                text1.text = String.format(resources.getString(R.string.entered_message),view.id) //"Entered: " + view.id.toString()
//                text2.text = String.format(resources.getString(R.string.entered_message),view.tag) //"Entered: " + view.tag.toString()
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
//                text1.text = String.format(resources.getString(R.string.location_message),view.id, event.x, event.y) //"Location: " + view.id.toString()
//                text2.text = String.format(resources.getString(R.string.location_message),view.tag, event.x, event.y) //"Location: " + view.tag.toString()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
//                text1.text = String.format(resources.getString(R.string.exited_message),view.id) //"Exited: " + view.id.toString()
//                text2.text = String.format(resources.getString(R.string.exited_message),view.tag) //"Exited: " + view.tag.toString()
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
//                text1.text = String.format(resources.getString(R.string.drop_message),view.id) //"Drop: " + view.id.toString()
//                text2.text = String.format(resources.getString(R.string.drop_message),view.tag) //"Drop: " + view.tag.toString()
//                text1.setTextColor(Color.GREEN)
//                text1.invalidate()
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()
                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as LinearLayout
                destination.addView(v)
                v.visibility = VISIBLE
                        (v as? ImageView)?.setColorFilter(Color.RED)
                (v as? ImageView)?.setBackgroundColor(Color.RED)

                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
                else -> false
        }
        }
}