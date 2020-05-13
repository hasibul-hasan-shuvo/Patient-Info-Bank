package finalyear.project.patientinfobank.View.Patient.Prescription

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import finalyear.project.patientinfobank.Adapter.Prescription.CCOEAdviceAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.ActivityShowOECCAdviceBinding

class ShowOECCAdvice : AppCompatActivity() {

    private val TAG = "ShowCCOEAdvice"

    private lateinit var binding: ActivityShowOECCAdviceBinding
    private var list = arrayListOf<String>()
    private lateinit var toolbarName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_o_e_c_c_advice)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_o_e_c_c_advice)
        // Fetching intent data
        if (intent.extras?.get(Util.CC_OE_ADVICE_LIST) != null)
            list.addAll(intent.extras?.get(Util.CC_OE_ADVICE_LIST) as Collection<String>)
        toolbarName = intent.getStringExtra(Util.TOOLBAR_NAME)
        setUpToolbar()

    }


    override fun onResume() {
        super.onResume()

        if (list.size > 0)
            setList()
    }

    private fun setList() {

        val adapter = ArrayAdapter(this, R.layout.view_prescription_cc_oe_advice_list, R.id.text, list)
        binding.list.adapter = adapter
    }


    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = toolbarName
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

}
