package group

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import common.CLAlerts

abstract class CLGroupFragment : Fragment() {
    protected lateinit var groupActivityContext: CLGroupActivity
    protected lateinit var linearLayoutManager: LinearLayoutManager
    protected val commonGroupViewModel by lazy {
        ViewModelProvider(this).get(CLFragmentsGroupViewModel::class.java)
    }
    protected lateinit var alerts: CLAlerts

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null){
            commonGroupViewModel.getFollowingsList()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        groupActivityContext = context as CLGroupActivity
        linearLayoutManager = LinearLayoutManager(groupActivityContext)
        alerts = CLAlerts(groupActivityContext)
    }
}