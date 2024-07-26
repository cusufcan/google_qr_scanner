package com.cusufcan.googleqrscanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cusufcan.googleqrscanner.databinding.ActivityMainBinding
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var scanner: GmsBarcodeScanner

    private var isScannerInstalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVariables()
        installGoogleScanner()
        registerUIListener()
    }

    private fun initVariables() {
        val options = initializeGoogleScanner()
        scanner = GmsBarcodeScanning.getClient(this, options)
    }

    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom().build()
    }

    private fun installGoogleScanner() {
        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest =
            ModuleInstallRequest.newBuilder().addApi(GmsBarcodeScanning.getClient(this)).build()

        moduleInstall.installModules(moduleInstallRequest).addOnSuccessListener {
            isScannerInstalled = true
        }.addOnFailureListener {
            isScannerInstalled = false
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUIListener() {
        binding.scanQrBtn.setOnClickListener {
            if (isScannerInstalled) {
                startScanning()
            } else {
                Toast.makeText(this, "Please try again...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScanning() {
        scanner.startScan().addOnSuccessListener {
            val result = it.rawValue
            result?.let {
                binding.scannedValueTv.text = "${R.string.scanned_value} $it"
            }
        }.addOnCanceledListener {
            Toast.makeText(this, "Canceled...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}