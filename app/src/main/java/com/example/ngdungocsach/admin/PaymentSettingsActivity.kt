package com.example.ngdungocsach.admin

import android.os.Bundle
import android.widget.Toast
import com.example.ngdungocsach.R
import com.example.ngdungocsach.database.FirebaseHelper
import com.example.ngdungocsach.ui.BaseActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PaymentSettingsActivity : BaseActivity() {

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var etMomoQr: TextInputEditText
    private lateinit var etBankQr: TextInputEditText
    private lateinit var etPriceMonth: TextInputEditText
    private lateinit var etDaysMonth: TextInputEditText
    private lateinit var etPriceYear: TextInputEditText
    private lateinit var etDaysYear: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnBack: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_settings)

        firebaseHelper = FirebaseHelper()
        etMomoQr = findViewById(R.id.etMomoQr)
        etBankQr = findViewById(R.id.etBankQr)
        etPriceMonth = findViewById(R.id.etPriceMonth)
        etDaysMonth = findViewById(R.id.etDaysMonth)
        etPriceYear = findViewById(R.id.etPriceYear)
        etDaysYear = findViewById(R.id.etDaysYear)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        // Tải dữ liệu hiện tại
        firebaseHelper.getPaymentSettings { settings ->
            if (settings != null) {
                etMomoQr.setText(settings["momo_qr"]?.toString() ?: "")
                etBankQr.setText(settings["bank_qr"]?.toString() ?: "")
                etPriceMonth.setText(settings["price_month"]?.toString() ?: "1000")
                etDaysMonth.setText(settings["days_month"]?.toString() ?: "30")
                etPriceYear.setText(settings["price_year"]?.toString() ?: "5000")
                etDaysYear.setText(settings["days_year"]?.toString() ?: "365")
            }
        }

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val momoQr = etMomoQr.text.toString()
            val bankQr = etBankQr.text.toString()
            val priceMonth = etPriceMonth.text.toString().toLongOrNull() ?: 1000L
            val daysMonth = etDaysMonth.text.toString().toIntOrNull() ?: 30
            val priceYear = etPriceYear.text.toString().toLongOrNull() ?: 5000L
            val daysYear = etDaysYear.text.toString().toIntOrNull() ?: 365

            val settings = mapOf(
                "momo_qr" to momoQr,
                "bank_qr" to bankQr,
                "price_month" to priceMonth,
                "days_month" to daysMonth,
                "price_year" to priceYear,
                "days_year" to daysYear
            )

            firebaseHelper.updatePaymentSettings(settings) { success ->
                if (success) {
                    Toast.makeText(this, "Đã cập nhật cấu hình thanh toán", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Lỗi khi lưu cấu hình", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
