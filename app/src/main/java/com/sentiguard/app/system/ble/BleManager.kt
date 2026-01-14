package com.sentiguard.app.system.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class BleManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = 
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    // UUIDs for the ESP32 Gas Sensor Service (Placeholder)
    private val SERVICE_UUID = UUID.fromString("0000181A-0000-1000-8000-00805f9b34fb") // Environmental Sensing
    private val CHAR_UUID = UUID.fromString("00002A58-0000-1000-8000-00805f9b34fb")    // Analog measurement (Gas)

    private var bluetoothGatt: BluetoothGatt? = null
    private var scanner: android.bluetooth.le.BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null

    @SuppressLint("MissingPermission")
    fun scanAndConnect(): Flow<Float> = callbackFlow {
        // Double check permission before scanning
        if (androidx.core.app.ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            close()
            return@callbackFlow
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            close()
            return@callbackFlow
        }

        scanner = bluetoothAdapter.bluetoothLeScanner
        
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Connected to Gas Sensor")
                    g.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Disconnected from Gas Sensor")
                    trySend(0f) 
                }
            }

            override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
                val service = g.getService(SERVICE_UUID)
                val characteristic = service?.getCharacteristic(CHAR_UUID)
                if (characteristic != null) {
                    g.setCharacteristicNotification(characteristic, true)
                }
            }

            override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val value = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 0) ?: 0f
                trySend(value)
            }
        }

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.device?.let { device ->
                    if (device.name == "Sentiguard-Gas") {
                        Log.d(TAG, "Found Sentiguard Sensor: ${device.address}")
                        bluetoothGatt = device.connectGatt(context, false, gattCallback)
                        scanner?.stopScan(this)
                    }
                }
            }
        }

        scanner?.startScan(scanCallback)

        awaitClose {
            close()
        }
    }

    @SuppressLint("MissingPermission")
    fun close() {
        try {
            bluetoothGatt?.close()
            bluetoothGatt = null
            if (scanner != null && scanCallback != null) {
                scanner?.stopScan(scanCallback)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error closing BLE", e)
        }
    }



    companion object {
        private const val TAG = "BleManager"
    }
}
