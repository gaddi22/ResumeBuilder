package com.swoopsoft.resumebuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*

class TemplateBuilderActivity : AppCompatActivity() {

    private lateinit var templateLayout: LinearLayout
    private lateinit var elementsLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var textView1: TextView
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var button10: Button
    private lateinit var button11: Button
    private lateinit var button12: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_builder)

        templateLayout = findViewById(R.id.template_builder_layout)
        elementsLayout = findViewById(R.id.layout_elements)
        mainLayout = findViewById(R.id.template_builder_main_layout)
        textView1 = findViewById(R.id.textView1)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        button9 = findViewById(R.id.button9)
        button10 = findViewById(R.id.button10)
        button11 = findViewById(R.id.button11)
        button12 = findViewById(R.id.button12)

        templateLayout.setOnDragListener(dragListener)
        elementsLayout.setOnDragListener(dragListener)
        mainLayout.setOnDragListener(dragListener)

        textView1.setOnLongClickListener(longClickListener)

        button1.setOnLongClickListener(longClickListener)
        button2.setOnLongClickListener(longClickListener)
        button3.setOnLongClickListener(longClickListener)
        button4.setOnLongClickListener(longClickListener)
        button5.setOnLongClickListener(longClickListener)
        button6.setOnLongClickListener(longClickListener)
        button7.setOnLongClickListener(longClickListener)
        button8.setOnLongClickListener(longClickListener)
        button9.setOnLongClickListener(longClickListener)
        button10.setOnLongClickListener(longClickListener)
        button11.setOnLongClickListener(longClickListener)
        button12.setOnLongClickListener(longClickListener)

    }

    private val longClickListener = OnLongClickListener {
        val clipText = "This is our clipData text"
        val item = ClipData.Item(clipText)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(clipText, mimeTypes, item)

        val dragShadowBuilder = DragShadowBuilder(it)
        it.startDragAndDrop(data, dragShadowBuilder, it, 0)

        it.visibility = INVISIBLE

        true
    }

    private val clickListener = OnClickListener {
        val clipText = "This is our clipData text"
        val item = ClipData.Item(clipText)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(clipText, mimeTypes, item)

        val dragShadowBuilder = DragShadowBuilder(it)
        it.startDragAndDrop(data, dragShadowBuilder, it, 0)

        it.visibility = INVISIBLE
        true
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
        val v = event.localState as View
        var originalBackgroundColor: Int = 0
        when(event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                (v as? ImageView)?.setColorFilter(Color.RED)
                (v as? Button)?.setBackgroundColor(Color.BLUE)
                v.invalidate()
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                originalBackgroundColor = view.solidColor
                view.setBackgroundColor(resources.getColor(R.color.grey,null))
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                onActionDragLocation()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                onActionDragExited(view, originalBackgroundColor)
                true
            }
            DragEvent.ACTION_DROP -> {
                onActionDrop(view, v, originalBackgroundColor)
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                onActionDragEnded(view, v)
                true
            }
            else -> false
        }
    }

    private fun onActionDragLocation() {
        TODO("Not yet implemented")
    }

    private fun onActionDragExited(view: View, originalBackgroundColor: Int) {
        view.setBackgroundColor(originalBackgroundColor)
        view.invalidate()
    }

    private fun onActionDrop(target: View, dropObject: View, backgroundColor:Int) {
        dropObject.visibility = VISIBLE
//        val item = event.clipData.getItemAt(0)
//        val dragData = item.text
        target.setBackgroundColor(backgroundColor)
        val destination = target as LinearLayout

        displayMessageWithToast("destination_id: ${destination.id}", false)
        if (destination.id == R.id.template_builder_layout) {
            displayMessageWithToast("destination_id: ${destination.id}")
            val owner = dropObject.parent as ViewGroup
            owner.removeView(dropObject)
            destination.addView(dropObject)
            target.invalidate()
        }
    }

    private fun onActionDragEnded(target:View, dropObject:View) {
        target.invalidate()
        dropObject.visibility = VISIBLE
    }

    private fun displayMessageWithToast(message:String, long:Boolean=true){
        if (long) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}