package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings
import finalyear.project.patientinfobank.Adapter.Prescription.CCOEAdviceAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.PrescriptionItemView
import finalyear.project.patientinfobank.databinding.ActivityWriteOeBinding

class OEWrite : AppCompatActivity(), PrescriptionItemView {
    private val TAG = "OEWrite"

    private lateinit var binding: ActivityWriteOeBinding
    private var list = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_oe)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_oe)
        setUpToolbar()

        binding.addButton.setOnClickListener {
            addValueToList()
        }
    }

    private fun addValueToList() {
        val name = binding.oe.text.toString()
        val value = binding.oeValue.text.toString()
        if (Strings.isNullOrEmpty(name))
            binding.oe.error = Util.EMPTY_ERROR_MESSAGE
        else {
            list.add("$name: $value")
            setList()
            binding.oe.setText(Util.EMPTY_VALUE)
            binding.oeValue.setText(Util.EMPTY_VALUE)
        }
    }

    override fun onStart() {
        super.onStart()
        // Fetching intent data
        if (intent.extras?.get(Util.OE_LIST) != null)
            list.addAll(intent.extras?.get(Util.OE_LIST) as Collection<String>)
    }


    override fun onResume() {
        super.onResume()

        if (list.size > 0)
            setList()
    }

    private fun setList() {

        val adapter = CCOEAdviceAdapter(this, list, this)
        binding.oeList.adapter = adapter
    }

    // Setting menu bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.OE_TITLE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.done -> sendResult()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun sendResult() {
        var resultIntent = Intent()
        resultIntent.putExtra(Util.OE_LIST, list)

        setResult(Util.RESULT_OE, resultIntent)
        finish()

        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

    override fun onItemClick(position: Int) {
        list.removeAt(position)
        setList()
    }

}
