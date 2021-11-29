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
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.size
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.swoopsoft.resumebuilder.data.DataObject
import com.swoopsoft.resumebuilder.data.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TemplateBuilderActivity : AppCompatActivity() {

    private lateinit var templateLayout: LinearLayout
    private lateinit var elementsLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var saveButton: Button
    private lateinit var resetButton:Button
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

        getTemplateObjects()

        templateLayout.setOnDragListener(dragListener)
        elementsLayout.setOnDragListener(dragListener)
        mainLayout.setOnDragListener(dragListener)

        saveButton.setOnClickListener(onSaveActionSelected)
        resetButton.setOnClickListener(onResetActionSelected)

    }

    private fun getTemplateObjects() {
        // connect to Firebase
        val data = FirebaseDatabase.getInstance().reference.child("users/" + FirebaseAuth.getInstance().currentUser!!.uid)

        //get references to user's database
        data.get()
            .addOnSuccessListener { dataSnapShot ->
                val userObj: User? = dataSnapShot.getValue(User::class.java)

                val dataMap: HashMap<String?, DataObject?> = userObj?.getData() as HashMap<String?, DataObject?>

                for (dataObject in dataMap) {
                    resumeElements.add(dataObject)
                }
                buildTemplateObjects(resumeElements)
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
            addLayoutToElementsLayout(createCard(value, key), layoutParams)
        }
    }

    private fun addLayoutToElementsLayout(view:View, params:LinearLayout.LayoutParams){
        elementsLayout.addView(view, params)
    }

    private fun createCard(child: DataObject?, key: String?): View {
        val card = getCard()
        val layoutParams = getLayoutParamsForCardContents()
        val imageLayoutParams = getImageLayoutParams()
        val layout = getCardLayout()
        val childLayout = getChildLayout()
        val name = getTextView(key!!, child!!.getType())
        layout.addView(name, layoutParams)
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
        val nameText = if (dataType.isNotBlank()) "$text ($dataType)" else text
        textView.setTextColor(Color.BLACK)
        textView.setPadding(5, 5, 5, 5)
        textView.text = nameText
        return textView
    }

    private fun getImageView(data:DataObject):ImageView {
        val imageView = ImageView(applicationContext)
        Picasso.get().load(data.value as String).into(imageView)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        return imageView
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

//    private val clickListener = OnClickListener {
//        val clipText = "This is our clipData text"
//        val item = ClipData.Item(clipText)
//        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
//        val data = ClipData(clipText, mimeTypes, item)
//
//        val dragShadowBuilder = DragShadowBuilder(it)
//        it.startDragAndDrop(data, dragShadowBuilder, it, 0)
//
//        it.visibility = INVISIBLE
//        true
//    }
//
//    private val touchListener = OnTouchListener { it, motionEvent ->
//        when(motionEvent.action) {
//            MotionEvent.ACTION_DOWN -> {
//                val clipText = "This is our clipData text"
//                val item = ClipData.Item(clipText)
//                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
//                val data = ClipData(clipText, mimeTypes, item)
//
//                val dragShadowBuilder = DragShadowBuilder(it)
//                it.startDragAndDrop(data, dragShadowBuilder, it, 0)
//
//                it.visibility = INVISIBLE
//            }
//            MotionEvent.ACTION_UP -> {
//                it.performClick()
//            }
//        }
//        true
//    }

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

        when {
            target is CardView -> {
                dropInCardViewParentLayout(hideContents(dropObject as CardView), target)
            }
            target.id == R.id.template_builder_layout -> {
                dropInTemplateBuilderLayout(hideContents(dropObject as CardView), target as LinearLayout, target)
            }
            target.id == R.id.layout_elements -> {
                dropInElementsLayout(dropObject, target as LinearLayout, target)
            }
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
            }
        }
        return cv
    }

    private fun dropInCardViewParentLayout(card: CardView, destination: CardView){
        removeViewFromParent(card, card.parent as ViewGroup)
        (destination.parent as ViewGroup).addView(card)
        card.setOnDragListener(dragListener)
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
                gravity = Gravity.START
            }

        layoutParams.setMargins(10, 3, 10, 10)
        removeViewFromParent(dropObject, dropObject.parent as ViewGroup)
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

    private val onSaveActionSelected = fun (_: View) {
        val sdf = SimpleDateFormat(getString(R.string.simpleDateFormatForTemplateSave))
        val key = sdf.format(Date())
        val dataMap = HashMap<String, HashMap<String, HashMap<String, String>>>()
        val rowMap = HashMap<String, HashMap<String, String>>()
        var rowCounter = 0
        for(row in templateLayout.children){
            if(row is LinearLayout){
                val cellMap = HashMap<String, String>()
                var cellCounter = 0
                row.children.forEach { cell ->
                    cellMap[cellCounter.toString()] = getKey(cell)
                    cellCounter += 1
                }
                rowMap[rowCounter.toString()] = cellMap
                rowCounter += 1
            }
        }
        dataMap[key] = rowMap
        saveRowMapToDatabase(dataMap)
    }

    private fun saveRowMapToDatabase(dataMap: HashMap<String, HashMap<String, HashMap<String, String>>>) {
        val db = Firebase.firestore
        val myAuth = FirebaseAuth.getInstance()
        db.collection("templates").document(myAuth.currentUser!!.uid)
            .set(dataMap, SetOptions.merge())
            .addOnSuccessListener {
                // go back to the TemplateChooser
                displayMessageWithToast("Template ${dataMap.keys} Created Successfully")
                finish()
            }
            .addOnFailureListener{
                displayMessageWithToast("Template was not created: ${it.message}")
            }
    }

    private fun getKey(cell: View): String {
        var returnKey = ""
        if(cell is LinearLayout){
            if(cell[0] is CardView){
                returnKey = getKeyFromCardView(cell[0] as CardView)
            }
        } else if(cell is CardView){
            returnKey = getKeyFromCardView(cell)
        }
        return returnKey
    }

    private fun getKeyFromCardView(card:CardView):String {
        var returnKey = ""
        if (card[0] is LinearLayout) {
            if ((card[0] as LinearLayout)[0] is TextView) {
                val tView = (card[0] as LinearLayout)[0] as TextView
                returnKey = tView.text.subSequence(0, tView.text.indexOf("(")).trim().toString()
            }
        }
        return returnKey
    }

    private val onResetActionSelected = fun (_: View) {
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