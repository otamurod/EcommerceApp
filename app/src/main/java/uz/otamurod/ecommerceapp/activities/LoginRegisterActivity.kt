package uz.otamurod.ecommerceapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.databinding.ActivityLoginRegisterBinding

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentDestination = findNavController(R.id.fragmentContainerView).currentDestination
        currentDestination?.let { destination ->
            if (destination.id == R.id.accountOptionsFragment) {
                finish()
            }
        }
    }
}