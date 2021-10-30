package ni.edu.uca.camara

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import ni.edu.uca.camara.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val REQUEST_PERMISSION_CAMERA =100
    private val REQUEST_IMAGE_CAMERA =101

    private var ruta = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamara.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    goToCamera()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        REQUEST_PERMISSION_CAMERA
                    )
                }
            } else {
                goToCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==REQUEST_PERMISSION_CAMERA){
            if(permissions.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                goToCamera()
            }else{
                Toast.makeText(this,"Necesita habilitar",Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK) {
            //val imgBitmap = data?.extras?.get("data") as Bitmap
            binding.ivImagen.setImageURI(Uri.parse(ruta))
        }
    }

    private fun goToCamera(){
        val cameraintent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(cameraintent.resolveActivity(packageManager)!=null){
            var imagenArchivo:File? = null

            try {
                imagenArchivo = crearImagen()
            }catch (e:IOException){
                Log.e("Error: ",e.toString())
            }

            if(imagenArchivo!=null){
                val foto: Uri = FileProvider.getUriForFile(this,"ni.edu.uca.camara",imagenArchivo)
                cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,foto)
            }
            startActivityForResult(cameraintent, REQUEST_IMAGE_CAMERA)
        }
    }



    private fun crearImagen():File{
        val nmbImagen = "foto_"
        val directorio: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imagen = File.createTempFile(nmbImagen,".jpg",directorio)
        ruta = imagen.absolutePath
        return imagen
    }
}

//new String[]{Manifest.permission.CAMERA}