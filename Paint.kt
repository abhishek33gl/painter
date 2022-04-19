
package com.abhishek.simplepaint

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
enum class PathType{NORMAL,ERASER}
data class PathObject(val path: Path,val paint: Paint,var pathType: PathType=PathType.NORMAL)
class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mIsEraseMode:Boolean=false
    private var undoPathObjStack:Stack<PathObject> = Stack()
    private var redoPathObjStack:Stack<PathObject> = Stack()
    private var bgColor:Int=Color.WHITE
    private var mPathColor:Int=Color.BLACK
    private var paint:Paint= Paint(ANTI_ALIAS_FLAG).apply {
        color=Color.RED
        strokeWidth=5f;
        style=Paint.Style.STROKE
        pathEffect=CornerPathEffect(50f)
        strokeJoin=Paint.Join.ROUND
        strokeCap=Paint.Cap.SQUARE
    }
    interface OnUndoRedoStackChangeListener{
        fun onChange(isUndoStackEmpty:Boolean,isRedoStackEmpty:Boolean)
    }
    private var path:Path= Path()
    private  var mOnUndoRedoStackChangeListener: OnUndoRedoStackChangeListener? =null
    fun setOnUndoRedoStackChangeListener(listener:OnUndoRedoStackChangeListener?){
        mOnUndoRedoStackChangeListener=listener
    }
    private fun callOnUndoRedoListener(){
        if(mOnUndoRedoStackChangeListener!=null){
            mOnUndoRedoStackChangeListener?.onChange(undoPathObjStack.isEmpty(),redoPathObjStack.isEmpty())
        }
    }
    fun setCurrentBrushSize(size:Int){
        paint.strokeWidth=size.toFloat()
    }

    fun toggleEraseMode(){
        mIsEraseMode=!mIsEraseMode
        if(mIsEraseMode){
            paint.color=bgColor
        }
        else{
            paint.color=mPathColor
        }
    }

    fun setCurrentPathColor(color:Int){
        mPathColor=color
        paint.color=mPathColor
    }
    fun setBgColor(color:Int){
        if(bgColor==color){
            return
        }
       this.bgColor=color
        updateBrushPathPaintColor()
        postInvalidate()
    }
    private fun updateBrushPathPaintColor(){
        for (p in undoPathObjStack){
            if(p.pathType==PathType.ERASER){
                p.paint.color=bgColor
            }
        }
        for (p in redoPathObjStack){
            if(p.pathType==PathType.ERASER){
                p.paint.color=bgColor
            }
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX= event.x
        val touchY= event.y
        when(event.action){
            MotionEvent.ACTION_DOWN->{

                if(!redoPathObjStack.isEmpty()){
                    redoPathObjStack.clear()
                    callOnUndoRedoListener()
                }
                path.moveTo(touchX,touchY)
            }
            MotionEvent.ACTION_MOVE->{
                path.lineTo(touchX,touchY)
            }
            MotionEvent.ACTION_UP->{

                undoPathObjStack.push(PathObject(Path().apply { set(path) },Paint().apply { set(paint) }))
                path.reset()
                callOnUndoRedoListener()
            }
            else->{
                return false
            }
        }
        postInvalidate()
        return true

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawColor(bgColor)
            for (i in undoPathObjStack){
                drawPath(i.path, i.paint)
            }
            //draw current path
            if(!path.isEmpty)
                drawPath(path, paint)
        }
    }
    fun clear(){
        Log.e("----------------",
            "height ${DeviceDimensionsHelper.getDisplayHeight(context)}" +
                    " width  ${DeviceDimensionsHelper.getDisplayWidth(context)}")
        path.reset()
        postInvalidate()
    }
    fun undo(){
        if(!undoPathObjStack.isEmpty()){
            redoPathObjStack.push(undoPathObjStack.pop())
            callOnUndoRedoListener()
            postInvalidate()
        }
    }
    fun redo(){
        if(!redoPathObjStack.isEmpty()){
            undoPathObjStack.push(redoPathObjStack.pop())
            callOnUndoRedoListener()
            postInvalidate()
        }
    }

}
