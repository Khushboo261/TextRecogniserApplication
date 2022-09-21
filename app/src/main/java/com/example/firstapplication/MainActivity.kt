package com.example.firstapplication

import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.RecognizedLanguage
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    var imageView:ImageView?=null;
    var resultTextView:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.textView);
        var chooseButton:Button = findViewById(R.id.button);
            title="Text Recogniser"
        chooseButton.setOnClickListener {
            var i:Intent = Intent();
            i.type = "image/"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Choose image"), 121)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 121){
            imageView?.setImageURI(data?.data)
            val image: FirebaseVisionImage
            try {
                image = FirebaseVisionImage.fromFilePath(applicationContext, data?.data!!)
                val textRecognizer = FirebaseVision.getInstance()
                    .onDeviceTextRecognizer

                textRecognizer.processImage(image)
                    .addOnSuccessListener {
                        // Task completed successfully
                        // ...
                        //resultTextView?.text = it.text
                        val resultText: String = it.getText()
                        for (block in it.getTextBlocks()) {
                            val blockText: String = block.getText()
                            val blockConfidence: Float? = block.getConfidence()
                            val blockLanguages: List<RecognizedLanguage> =
                                block.getRecognizedLanguages()
                            val blockCornerPoints: Array<Point> = block.getCornerPoints() as Array<Point>
                            val blockFrame: Rect? = block.getBoundingBox()
                            for (line in block.getLines()) {
                                val lineText: String = line.getText()
                                val lineConfidence: Float? = line.getConfidence()
                                val lineLanguages: List<RecognizedLanguage> =
                                    line.getRecognizedLanguages()
                                val lineCornerPoints: Array<Point> = line.getCornerPoints() as Array<Point>
                                val lineFrame: Rect? = line.getBoundingBox()
                                for (element in line.getElements()) {
                                    val elementText: String = element.getText()
                                    val elementConfidence: Float? = element.getConfidence()
                                    val elementLanguages: List<RecognizedLanguage> =
                                        element.getRecognizedLanguages()
                                    val elementCornerPoints: Array<Point> =
                                        element.getCornerPoints() as Array<Point>
                                    val elementFrame: Rect? = element.getBoundingBox()
                                    resultTextView?.append(element.text + " ")
                                }
                                resultTextView?.append("\n")
                            }
                            resultTextView?.append("\n\n")
                        }
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                    }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}