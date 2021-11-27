package com.swoopsoft.resumebuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.core.view.size
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class TemplateBuilderActivity : AppCompatActivity() {

    private lateinit var templateLayout: LinearLayout
    private lateinit var elementsLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var saveButton: Button
    private lateinit var resetButton:Button
    private val resumeElements: ArrayList<DataSnapshot> = ArrayList()
    private val spaceHeight = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_builder)

//        resumeElements = emptyList()

        templateLayout = findViewById(R.id.template_builder_layout)
        elementsLayout = findViewById(R.id.layout_elements)
        mainLayout = findViewById(R.id.template_builder_main_layout)
        saveButton = findViewById(R.id.btnSaveTemplate)
        resetButton = findViewById(R.id.btnResetTemplate)

        getTemplateObjects(elementsLayout)

        templateLayout.setOnDragListener(dragListener)
        elementsLayout.setOnDragListener(dragListener)
        mainLayout.setOnDragListener(dragListener)

        saveButton.setOnClickListener(onSaveActionSelected)
        resetButton.setOnClickListener(onResetActionSelected)

    }

    private fun getTemplateObjects(elementsLayout: LinearLayout?) {
        // connect to Firebase
        val dataRef = FirebaseDatabase.getInstance().reference
        val data = dataRef.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("data")
        val dataTask = data.get()
            .addOnSuccessListener { dataSnapShot ->
//                resumeElements = dataSnapShot.children
//                val result: List<String> = ArrayList()
//                dataSnapShot.children.forEach(resumeElements::add)
//                for (child in dataSnapShot.children){
//                    resumeElements.plus(child)
//                }
//                buildTemplateObjects(dataSnapShot.children.iterator())
//                while (dataSnapShot.children.iterator().hasNext()){
//                    resumeElements.plus(dataSnapShot.children.iterator().next())
//                }
                for(child in dataSnapShot.children){
//                    resumeElements.plus(child)
                    resumeElements.add(child)
                }
                buildTemplateObjects(resumeElements)
            }
            .addOnFailureListener{

            }

    }

    private fun buildTemplateObjects(elements: List<DataSnapshot>){
        // configure layout parameters
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        layoutParams.setMargins(10, 10, 10, 10)
        var numberPerRow = 2
        var counter = numberPerRow
        var layout = LinearLayout(applicationContext)
//        val elements = resumeElements.iterator()
        // iterate through documents of user
        displayMessageWithToast("before: " + resumeElements.count().toString(), false)
        for(child in elements.iterator()){
            // for each document create a simple card for display
            if(counter < 1){
                addLayoutToParentLayout(layout)
                counter = numberPerRow
            }
            if(counter % numberPerRow == 0){
                layout = LinearLayout(applicationContext)
                layout.orientation = LinearLayout.HORIZONTAL
            }
            layout.addView(createCard(child), layoutParams)
            counter -= 1
        }
        if(counter == 0 || counter % numberPerRow != 0){
            addLayoutToParentLayout(layout)
        }
        displayMessageWithToast("after: " + resumeElements.count().toString(), false)
    }

    private fun addLayoutToParentLayout(layout: LinearLayout) {
        elementsLayout.addView(layout)
    }

    private fun createCard(child: DataSnapshot): View? {
        val card = CardView(applicationContext)
        card.setPadding(3, 3, 3, 3)
        card.radius = 15f
        card.cardElevation = 25f
        card.setCardBackgroundColor(Color.WHITE)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(10, 3, 10, 3)

        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(2, 2, 2, 2)

        val name = TextView(applicationContext)
        var nameText = child.key as String
        name.setTextColor(Color.BLACK)
        name.setPadding(5, 5, 5, 5)
        layout.addView(name, layoutParams)

        for(gChild in child.children) {
            val childLayout = LinearLayout(applicationContext)
            childLayout.orientation = LinearLayout.HORIZONTAL
            childLayout.background = resources.getDrawable(R.drawable.custom_rectangular_background,null)

            if(gChild.key.toString() == "type"){
                nameText = nameText + " (" + gChild.value.toString() + ")"
            } else {

                val dataValue = TextView(applicationContext)
                dataValue.text = gChild.value as String
                dataValue.setTextColor(Color.BLACK)
                dataValue.setPadding(5, 5, 5, 5)
                childLayout.addView(dataValue, layoutParams)

                layout.addView(childLayout, layoutParams)
            }
        }

        name.text = nameText
        card.addView(layout)
        card.setOnLongClickListener(longClickListener)
        return card
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
        var originalBackgroundColor = 0
        when(event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                onActionDragStarted(v, event)
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                originalBackgroundColor = view.solidColor
                onActionDragEntered(view)
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

    private fun onActionDragStarted(v:View, event: DragEvent){
        v.invalidate()
        event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
    }

    private fun onActionDragEntered(view: View) {
        view.setBackgroundColor(resources.getColor(R.color.grey,null))
        view.invalidate()
    }

    private fun onActionDragLocation() {
    }

    private fun onActionDragExited(view: View, originalBackgroundColor: Int) {
        view.setBackgroundColor(originalBackgroundColor)
        view.invalidate()
    }

    private fun onActionDrop(target: View, dropObject: View, backgroundColor:Int) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(10, 3, 10, 10)
        dropObject.visibility = VISIBLE
        target.setBackgroundColor(backgroundColor)
        val destination = target as LinearLayout

        if (destination.id == R.id.template_builder_layout ||
                destination.id == R.id.layout_elements) {
            val owner = dropObject.parent as ViewGroup
            owner.removeView(dropObject)
            if(owner.size < 1 && (owner.id == R.id.layout_elements || owner.id == R.id.template_builder_layout)){
                val space = Space(applicationContext)
                space.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, spaceHeight)
                owner.addView(space)
            } else if (owner.size < 1){
                val parent = owner.parent as ViewGroup
                parent.removeView(owner)
            }
            if(destination.size > 0 && destination[0] is Space ){
                destination.removeViewAt(0)
            }
            destination.addView(dropObject, layoutParams)
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

//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.action_save -> {
//            onSaveActionSelected(item)
//            true
//        }
//        R.id.action_cancel -> {
//            onCancelActionSelected(item)
//            true
//        }
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }

    private val onSaveActionSelected = fun (item: View) {
        displayMessageWithToast("save for " + item.id)
    }

    private val onResetActionSelected = fun (item: View) {
        resetResumeLayout()
        clearTemplateObjects()
        buildTemplateObjects(resumeElements)
    }

    private fun resetResumeLayout() {
        templateLayout.removeAllViews()
        val space = Space(applicationContext)
        space.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, spaceHeight)
        templateLayout.addView(space)
    }

    private fun clearTemplateObjects() {
        elementsLayout.removeAllViews()
    }
}