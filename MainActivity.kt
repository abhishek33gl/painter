package com.abhishek.simplepaint

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.abhishek.simplepaint.databinding.ActivityMainBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorListener

class MainActivity : AppCompatActivity() {
    private var mDefaultBrushSize=25
    private var mDefaultColor=Color.BLACK
    private var mBgColor=Color.WHITE
    lateinit var paintView: PaintView
    lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        paintView=binding.paintView
        //default
        run{
            setColor(mDefaultColor)
            binding.seekBarBrushSizer.setProgress(mDefaultBrushSize,true)
        }

        binding.seekBarBrushSizer.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                binding.colorViewSizeColor.setSize(p1)
                binding.paintView.setCurrentBrushSize(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
                Log.e("------------", "onStartTrackingTouch")
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                Log.e("------------", "onStopTrackingTouch")
            }
        })

        binding.colorViewColorPicker.setOnClickListener{
            var picker=ColorPickerDialog.Builder(this)
            picker.setPreferenceName("Color")
            picker.setTitle("colorPicker")
            picker.setPositiveButton("confirm",object :ColorEnvelopeListener{
                override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
                    this@MainActivity.setColor(envelope.color)
                }
            })
            picker.setNegativeButton("cancel",object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface, p1: Int) {

                }
            })
            picker.attachAlphaSlideBar(true)
            picker.attachBrightnessSlideBar(true)
            picker.setBottomSpace(12)
            picker.show()
        }

        //eraser
        //undo
        binding.imgButUndo.setOnClickListener{
            paintView.undo()
        }
        //redo
        binding.imgButRedo.setOnClickListener{
            paintView.redo()
        }
        binding.paintView.setOnUndoRedoStackChangeListener(object :PaintView.OnUndoRedoStackChangeListener{
            override fun onChange(isUndoStackEmpty: Boolean, isRedoStackEmpty: Boolean) {
                if(isUndoStackEmpty)
                    binding.imgButUndo.visibility=View.INVISIBLE
                else
                    binding.imgButUndo.visibility=View.VISIBLE

                if(isRedoStackEmpty)
                    binding.imgButRedo.visibility=View.INVISIBLE
                else
                    binding.imgButRedo.visibility=View.VISIBLE
            }

        })
        binding.checkBoxErase.setOnClickListener{
            paintView.toggleEraseMode()
        }



    }
    fun setColor(color: Int){
        binding.colorViewColorPicker.setColor(color)
        binding.paintView.setCurrentPathColor(color)
        binding.colorViewSizeColor.setColor(color)
    }
    //actionbar use to create menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       Log.e("OthersActivity", item.title.toString())
        when(item.itemId){
            R.id.ic_save->{
                return true
            }
            R.id.ic_clear->{
                paintView.clear()
                return true
            }
            R.id.ic_share->{
                return true
            }
            R.id.ic_open_file->{
                return true
            }
            R.id.ic_change_bg_color->{
                return true
            }
            R.id.id_print->{
                return true
            }
            R.id.ic_setting->{
                return true
            }
            R.id.ic_about->{
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

}