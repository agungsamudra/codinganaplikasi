package com.pemesananlapanganfutsal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CekJadwalActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/lapangan?id=";
    private static final String url_jadwal = "/api/jadwal?id=";
    private String id_lapangan, nama_lapangan;
    private RadioGroup rg_jadwal;
    private EditText et_tanggal;
    private DatePickerDialog picker;
    private int harga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_jadwal);
        setTitle("Jadwal Lapangan");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_lapangan = extras.getString("ID_LAPANGAN");
        }

        rg_jadwal = findViewById(R.id.rg_jadwal);
        et_tanggal = findViewById(R.id.et_tanggal);
        Button btn_booking = findViewById(R.id.btn_booking);

        et_tanggal.setInputType(InputType.TYPE_NULL);
        et_tanggal.setOnClickListener(v -> {
            int initialDay = Integer.parseInt(et_tanggal.getText().toString().substring(0, 2));
            int initialMonth = Integer.parseInt(et_tanggal.getText().toString().substring(3, 5)) - 1;
            int initialYear = Integer.parseInt(et_tanggal.getText().toString().substring(6, 10));

            DatePickerDialog picker = new DatePickerDialog(CekJadwalActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedFormattedDate = String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year1);
                        et_tanggal.setText(selectedFormattedDate);
                        getJadwal(et_tanggal.getText().toString(), id_lapangan);
                    }, initialYear, initialMonth, initialDay);

            picker.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            long now = System.currentTimeMillis() - 1000;
            picker.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * 14)); // max 14 days
            picker.show();
        });

        btn_booking.setOnClickListener(view -> {
            if (et_tanggal.getText().toString().equals("")) {
                Toast.makeText(this, "Pilih dulu tanggal", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rg_jadwal.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Pilih dulu jadwal", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton rb_jadwal = findViewById(rg_jadwal.getCheckedRadioButtonId());
            Intent intent = new Intent(CekJadwalActivity.this, CheckoutActivity.class);
            intent.putExtra("ID_LAPANGAN", id_lapangan);
            intent.putExtra("HARGA_SEWA", harga);
            intent.putExtra("TGL_BOOKING", et_tanggal.getText().toString());
            intent.putExtra("JAM_BOOKING", rb_jadwal.getText().toString());
            intent.putExtra("NAMA_LAPANGAN", nama_lapangan);
            startActivity(intent);
        });

        getData();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(CekJadwalActivity.this);
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void getData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url + id_lapangan,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            JSONObject res = jObj.getJSONObject("data");
                            harga = res.getInt("harga_sewa");
                            nama_lapangan = res.getString("nama_lapangan");

                            setDefaultDate();
                        } else {
                            Toast.makeText(CekJadwalActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CekJadwalActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(CekJadwalActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(smr);
    }

    private void getJadwal(String tanggal, String id_lapangan) {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url_jadwal + id_lapangan + "&tanggal=" + tanggal,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            JSONArray result_pemesanan = jObj.getJSONArray("pemesanan");
                            String[] pemesanan = new String[result_pemesanan.length()];
                            for (int i = 0; i < result_pemesanan.length(); i++) {
                                JSONObject jsonObject = result_pemesanan.getJSONObject(i);
                                pemesanan[i] = jsonObject.getString("jam_booking");
                            }

                            rg_jadwal.removeAllViews();
                            JSONArray result2 = jObj.getJSONArray("jadwal");
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                            for (int i = 0; i < result2.length(); i++) {
                                JSONObject jsonObject = result2.getJSONObject(i);
                                RadioButton radioButton = new RadioButton(this);
                                radioButton.setText(jsonObject.getString("jam"));
                                radioButton.setId(jsonObject.getInt("id_jadwal"));
                                radioButton.setGravity(Gravity.CENTER);
                                radioButton.setPadding(20, 20, 20, 20);
                                radioButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));
                                radioButton.setButtonDrawable(android.R.color.transparent);
                                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 0, 15, 0);
                                radioButton.setLayoutParams(params);

                                if (Arrays.asList(pemesanan).contains(jsonObject.getString("jam"))) {
                                    radioButton.setEnabled(false);
                                }

                                if (currentDate.equals(et_tanggal.getText().toString()) &&
                                        isTimePassedToday(jsonObject.getString("jam"))) {
                                    radioButton.setEnabled(false);
                                }

                                rg_jadwal.addView(radioButton, params);
                            }
                        } else {
                            Toast.makeText(CekJadwalActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CekJadwalActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(CekJadwalActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(smr);
    }

    private void setDefaultDate() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String formattedDate = String.format("%02d-%02d-%04d", day, (month + 1), year);
        et_tanggal.setText(formattedDate);
        getJadwal(et_tanggal.getText().toString(), id_lapangan);
    }

    private boolean isTimePassedToday(String timeString) {
        String[] separated = timeString.split(":");
        int hour = Integer.parseInt(separated[0]);
        int minute = Integer.parseInt(separated[1]);

        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        if (hour < currentHour) {
            return true;
        } else return hour == currentHour && minute <= currentMinute;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}