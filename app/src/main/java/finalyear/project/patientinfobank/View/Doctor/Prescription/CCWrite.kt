package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings
import finalyear.project.patientinfobank.Adapter.Prescription.CCOEAdviceAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.databinding.ActivityWriteCcBinding

class CCWrite : AppCompatActivity(), ItemView {

    private val TAG = "CCWrite"

    private lateinit var binding: ActivityWriteCcBinding
    private var list = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_cc)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_cc)
        setUpToolbar()

        binding.addButton.setOnClickListener {
            addValueToList()
        }
    }

    private fun addValueToList() {
        val cc = binding.cc.text.toString()
        if (Strings.isNullOrEmpty(cc))
            binding.cc.error = Util.EMPTY_ERROR_MESSAGE
        else {
            list.add(cc)
            setList()
            binding.cc.setText(Util.EMPTY_VALUE)
        }
    }

    override fun onStart() {
        super.onStart()
        // Fetching intent data
        if (intent.extras?.get(Util.CC_LIST) != null)
            list.addAll(intent.extras?.get(Util.CC_LIST) as Collection<String>)
    }

    override fun onResume() {
        super.onResume()

        if (list.size > 0)
            setList()
    }

    private fun setList() {

        val adapter = CCOEAdviceAdapter(this, list, this)
        binding.ccList.adapter = adapter
    }

    // Setting menu bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.CC_TITLE
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
        resultIntent.putExtra(Util.CC_LIST, list)

        Log.d(TAG, list.size.toString())

        setResult(Util.RESULT_CC, resultIntent)
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
