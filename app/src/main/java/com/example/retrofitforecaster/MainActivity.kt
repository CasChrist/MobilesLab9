package com.example.retrofitforecaster

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import timber.log.Timber

object WeatherStore {
    var weathers: List<WeatherItem>? = null

    fun updateWeather(newWeather: List<WeatherItem>) {
        weathers = newWeather
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: WeatherAdapter
    private lateinit var cityNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.setBackgroundColor(Color.parseColor("#FFCC99"))

        Timber.plant(Timber.DebugTree())
        cityNameTextView = findViewById(R.id.city_name)
        adapter = WeatherAdapter()
        findViewById<RecyclerView>(R.id.r_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        if (WeatherStore.weathers == null) {
            fetchWeather()
        } else {
            adapter.submitList(WeatherStore.weathers)
            Timber.d("MainActivity: Loaded weather data from WeatherStore: ${WeatherStore.weathers}")
        }
    }

    private fun fetchWeather() {
        val apiKey = "29a24e313af4439bd524fa07c6421932"
        val city = "Shklov"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getWeatherForecast(city, apiKey)

                // log response body to timber
                Timber.d("Response: $response.list")

                WeatherStore.updateWeather(response.list.map { weather ->
                    val iconCode = weather.weather[0].icon
                    val temperature = "${(weather.main.temp - 273.15).toInt()}°C"

                    val dateTime = java.util.Date(weather.dt * 1000L)
                    val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                    val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

                    val date = dateFormat.format(dateTime)
                    val time = timeFormat.format(dateTime)

                    val iconUrl = "http://openweathermap.org/img/wn/$iconCode@2x.png"
                    WeatherItem(temperature, iconUrl, time, date)
                })
                adapter.submitList(WeatherStore.weathers)

                cityNameTextView.text = city

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // override onSaveInstanceState(outState: Bundle) and save response data to bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        WeatherStore.weathers?.let {
            val weatherItemsJson = Gson().toJson(it)
            outState.putString("weather_data", weatherItemsJson)
            Timber.d("MainActivity: Saved weather data")
        }
    }

}