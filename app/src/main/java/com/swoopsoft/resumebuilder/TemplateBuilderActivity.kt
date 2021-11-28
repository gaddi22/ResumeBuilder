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
import com.squareup.picasso.Picasso
import com.swoopsoft.resumebuilder.data.DataObject
import com.swoopsoft.resumebuilder.data.User
import java.util.HashMap

class TemplateBuilderActivity : AppCompatActivity() {

    private lateinit var templateLayout: LinearLayout
    private lateinit var elementsLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var saveButton: Button
    private lateinit var resetButton:Button
//    private val resumeElements: ArrayList<DataSnapshot> = ArrayList()
    private val resumeElements: ArrayList<MutableMap.MutableEntry<String?, DataObject?>>  = ArrayList()
    private val spaceHeight = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_builder)


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
//        val data = dataRef.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("data")
        val data = FirebaseDatabase.getInstance().reference.child("users/" + FirebaseAuth.getInstance().currentUser!!.uid)

        //get references to user's database
        val dataTask = data.get()
            .addOnSuccessListener { dataSnapShot ->
                val userObj: User? = dataSnapShot.getValue(User::class.java)

                val dataMap: HashMap<String?, DataObject?> = userObj?.getData() as HashMap<String?, DataObject?>

//                for ((key, value) in dataMap) {
                for (dataObject in dataMap) {
//                for (java.util.Map.Entry<String, DataObject> entry : dataMap.entries){
//                    val row = DataRow(activityRef, value, key, applicationContext)
//                    rows.add(row)
//                    mainLayout.addView(row.view)
                    resumeElements.add(dataObject)
//                    buildTemplateObjects(dataObject.value, dataObject.key)
                }
//                displayMessageWithToast(resumeElements.size.toString())
                buildTemplateObjects(resumeElements)
//                for(child in dataSnapShot.children){
//                    resumeElements.add(child)
//                }
//                buildTemplateObjects(resumeElements)
            }
            .addOnFailureListener{

            }

    }

    private fun buildTemplateObjects(dataList: ArrayList<MutableMap.MutableEntry<String?, DataObject?>>){
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
            .apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        layoutParams.setMargins(10, 10, 10, 10)
        for((key, value) in dataList){
//            displayMessageWithToast("$key - $value", false)
            addLayoutToParentLayout(createCard(value, key)!!, layoutParams)
        }
    }

//    private fun buildTemplateObjects(data: DataObject?, key: String?) {
////        displayMessageWithToast("buildTemplateObjects(data: DataObject?, key: String?) - " + key + "|" + data!!.getValue().toString(), false)
//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
//        )
//        .apply {
//            gravity = Gravity.CENTER_HORIZONTAL
//        }
//        layoutParams.setMargins(10, 10, 10, 10)
//        addLayoutToParentLayout(createCard(data, key)!!, layoutParams)
//    }

//    private fun buildTemplateObjects(elements: List<DataSnapshot>){
//        // configure layout parameters
//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
//        )
//            .apply {
//                gravity = Gravity.CENTER_HORIZONTAL
//            }
//        layoutParams.setMargins(10, 10, 10, 10)
////        var numberPerRow = 2
////        var counter = numberPerRow
////        var layout = LinearLayout(applicationContext)
//        // iterate through documents of user
//        for(child in elements.iterator()){
//            // for each document create a simple card for display
//            addLayoutToParentLayout(createCard(child)!!, layoutParams)
//
//        }
//    }

    private fun addLayoutToParentLayout(layout: LinearLayout) {
        elementsLayout.addView(layout)
    }

    private fun addLayoutToParentLayout(view:View, params:LinearLayout.LayoutParams){
//        displayMessageWithToast(view.id.toString(), false)
        elementsLayout.addView(view, params)
    }

    private fun createCard(child: DataObject?, key: String?): View? {
        val card = getCard()
        val layoutParams = getLayoutParamsForCardContents()
        val imageLayoutParams = getImageLayoutParams()
        val layout = getCardLayout()
        val childLayout = getChildLayout()
//        val name = getTextView(child?.getValue() as String, key!!)
        val name = getTextView(key!!, child!!.getType())
        layout.addView(name, layoutParams)
//        displayMessageWithToast(child?.getValue() as String)
        if (child.getType().lowercase() == "text") {
            childLayout.addView(getTextView(child.getValue() as String, ""))
        } else {
            childLayout.addView(getImageView(child), imageLayoutParams)
        }
        layout.addView(childLayout)
        card.addView(layout)
        return card
    }

    private fun getCard():CardView{
        val card = CardView(applicationContext)
        card.setPadding(3, 3, 3, 3)
        card.radius = 15f
        card.cardElevation = 25f
        card.setCardBackgroundColor(Color.WHITE)
        card.setOnLongClickListener(longClickListener)
//        card.setOnDragListener(dragListener)
        return card
    }

    private fun getLayoutParamsForCardContents():LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(10, 3, 10, 3)
        return layoutParams
    }

    private fun getImageLayoutParams():LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(
            150, 150
        )

        layoutParams.setMargins(3, 3, 3, 3)
        return layoutParams
    }

    private fun getCardLayout():LinearLayout {
        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(2, 2, 2, 2)
        return layout
    }

    private fun getChildLayout():LinearLayout {
        val childLayout = LinearLayout(applicationContext)
        childLayout.orientation = LinearLayout.HORIZONTAL
        childLayout.background = resources.getDrawable(R.drawable.custom_rectangular_background,null)
        return childLayout
    }

    private fun getTextView(text:String, dataType:String):TextView {
        val textView = TextView(applicationContext)
        var nameText = if (dataType.isNotBlank()) "$text ($dataType)" else text
        textView.setTextColor(Color.BLACK)
        textView.setPadding(5, 5, 5, 5)
        textView.text = nameText
        return textView
    }

    private fun getImageView(data:DataObject):ImageView {
        val imageView = ImageView(applicationContext)

//        image.setLayoutParams(params)
//        linearLayout.addView(image)
//        imageView.setMaxHeight(linearLayout.getHeight())
        Picasso.get().load(data.value as String).into(imageView)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
//        values.add(image)

//        outerParams.height = 300

        return imageView
    }

    private fun createCard(child: DataSnapshot): View? {
//        val card = CardView(applicationContext)
//        card.setPadding(3, 3, 3, 3)
//        card.radius = 15f
//        card.cardElevation = 25f
//        card.setCardBackgroundColor(Color.WHITE)
        val card = getCard()
        val layoutParams = getLayoutParamsForCardContents()
        val layout = getCardLayout()

//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//
//        layoutParams.setMargins(10, 3, 10, 3)

//        val layout = LinearLayout(applicationContext)
//        layout.orientation = LinearLayout.VERTICAL
//        layout.setPadding(2, 2, 2, 2)

//        val name = TextView(applicationContext)
//        var nameText = child.key as String
//        name.setTextColor(Color.BLACK)
//        name.setPadding(5, 5, 5, 5)
        val name = getTextView(child.key as String, "")
        layout.addView(name, layoutParams)
//displayMessageWithToast("grandchild count: " + child.children.count().toString(), false)
        for(gChild in child.children) {
            val childLayout = LinearLayout(applicationContext)
            childLayout.orientation = LinearLayout.HORIZONTAL
            childLayout.background = resources.getDrawable(R.drawable.custom_rectangular_background,null)

            if(gChild.key.toString() == "type"){
//                nameText = nameText + " (" + gChild.value.toString() + ")"
            } else {

//                val dataValue = TextView(applicationContext)
//                dataValue.text = gChild.value as String
//                dataValue.setTextColor(Color.BLACK)
//                dataValue.setPadding(5, 5, 5, 5)
                val dataValue = getTextView(gChild.value as String, "")
                childLayout.addView(dataValue, layoutParams)

                layout.addView(childLayout, layoutParams)
            }
        }

//        name.text = nameText
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

        dropObject.visibility = VISIBLE
        target.setBackgroundColor(backgroundColor)
//        val destination = target as LinearLayout

        if(target is CardView) {
            dropInCardViewParentLayout(hideContents(dropObject as CardView), target)
        } else if (target.id == R.id.template_builder_layout) {
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//                .apply {
////                gravity = Gravity.CENTER_HORIZONTAL
//                    gravity = Gravity.START
//                }
//
//            layoutParams.setMargins(10, 3, 10, 10)
//            val layout = LinearLayout(applicationContext)
//            layout.addView(dropObject, layoutParams)
//            dropInTemplateBuilderLayout(layout, destination, target)
            dropInTemplateBuilderLayout(hideContents(dropObject as CardView), target as LinearLayout, target)
        } else if (target.id == R.id.layout_elements) {
            dropInElementsLayout(dropObject, target as LinearLayout, target)
        }
    }

    private fun hideContents(cv:CardView):CardView {
        if (cv[0] is LinearLayout) {
            if ((cv[0] as LinearLayout)[0] is TextView) {
                (cv[0] as LinearLayout)[0].visibility = GONE
            }
        }
        return cv
    }

    private fun revealContents(cv:CardView):CardView {
        if (cv[0] is LinearLayout) {
            if ((cv[0] as LinearLayout)[0] is TextView) {
                (cv[0] as LinearLayout)[0].visibility = VISIBLE
            } else {
                displayMessageWithToast("TextView - " + (cv[0] as LinearLayout)[0].javaClass.simpleName)
            }
        } else {
            displayMessageWithToast("LinearLayout - " + cv[0].javaClass.simpleName)
        }
        return cv
    }

    private fun dropInCardViewParentLayout(card: CardView, destination: CardView){
//        if(dropObject is CardView) {
//            if (dropObject[0] is LinearLayout) {
//                if ((dropObject[0] as LinearLayout)[0] is TextView) {
//                    (dropObject[0] as LinearLayout)[0].visibility = GONE
//                }
//            }
        removeViewFromParent(card, card.parent as ViewGroup)
        (destination.parent as ViewGroup).addView(card)
        card.setOnDragListener(dragListener)
//        }
    }

    private fun dropInTemplateBuilderLayout(
        dropObject: View,
        destination: LinearLayout,
        target: LinearLayout
    ) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
            .apply {
//                gravity = Gravity.CENTER_HORIZONTAL
                gravity = Gravity.START
            }

        layoutParams.setMargins(10, 3, 10, 10)
//        val owner = dropObject.parent as ViewGroup
//        owner.removeView(dropObject)
//        if(owner.size < 1 && (owner.id == R.id.layout_elements || owner.id == R.id.template_builder_layout)){
//            val space = Space(applicationContext)
//            space.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, spaceHeight)
//            owner.addView(space)
//        } else if (owner.size < 1){
//            val parent = owner.parent as ViewGroup
//            parent.removeView(owner)
//        }
//        if(destination.size > 0 && destination[0] is Space ){
//            destination.removeViewAt(0)
//
//        }
        removeViewFromParent(dropObject, dropObject.parent as ViewGroup)
//        if(dropObject is CardView) {
//            if(dropObject[0] is LinearLayout){
//                if((dropObject[0] as LinearLayout)[0] is TextView){
//                    (dropObject[0] as LinearLayout)[0].visibility = GONE
//                }
////                if((dropObject[0] as LinearLayout)[1] is LinearLayout){
////                    if(((dropObject[0] as LinearLayout)[1] as LinearLayout)[0] is TextView){
////                        ((dropObject[0] as LinearLayout)[1] as LinearLayout)[0].visibility = GONE
////                    }
////                }
//            }
//            destination.addView(dropObject, destination.size-1, layoutParams)
//        } else {
//            val space = Space(applicationContext)
//            space.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, spaceHeight)
//            destination.addView(space)
//        }
        val layout = LinearLayout(applicationContext)
        layout.addView(dropObject, layoutParams)
        destination.addView(layout, destination.size-1, layoutParams)
        dropObject.setOnDragListener(dragListener)
        target.invalidate()
    }

    private fun removeViewFromParent(view: View, parent: ViewGroup){
        parent.removeView(view)
        if(parent.size < 1 && (parent.id == R.id.layout_elements || parent.id == R.id.template_builder_layout)){
            val space = Space(applicationContext)
            space.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, spaceHeight)
            parent.addView(space)
        } else if (parent.size < 1){
            val owner = parent.parent as ViewGroup
            owner.removeView(parent)
        }
    }

    private fun dropInElementsLayout(
        dropObject: View,
        destination: LinearLayout,
        target: LinearLayout
    ) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
            .apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }

        layoutParams.setMargins(10, 3, 10, 10)
        val owner = dropObject.parent as ViewGroup
        owner.removeView(dropObject)
//        if(owner.size < 1 && owner.id != R.id.template_builder_layout){
//            val ownerParent = owner.parent as ViewGroup
//            ownerParent.removeView(owner)
//        }
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
//        if(dropObject is CardView) {
//            if(dropObject[0] is LinearLayout){
//                if((dropObject[0] as LinearLayout)[0] is TextView){
//                    (dropObject[0] as LinearLayout)[0].visibility = VISIBLE
//                }
////                if((dropObject[0] as LinearLayout)[1] is LinearLayout){
////                    if(((dropObject[0] as LinearLayout)[1] as LinearLayout)[0] is TextView){
////                        ((dropObject[0] as LinearLayout)[1] as LinearLayout)[0].visibility = VISIBLE
////                    }
////                }
//            }
//            destination.addView(dropObject, layoutParams)
//        }
        displayMessageWithToast(dropObject.javaClass.simpleName)
        destination.addView(revealContents(dropObject as CardView), layoutParams)
        dropObject.setOnDragListener(null)
        target.invalidate()
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