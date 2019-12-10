package kr.co.seoft.two_min.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_open_source.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.setupActionBar

class OpenSourceActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_open_source

    val content = "\n" +
            "\n" +
            "\n" +
            "\n" +
            "retrofit2\n" +
            "\n" +
            "Copyright 2013 Square, Inc.\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the 'License');\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an 'AS IS' BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "rxjava\n" +
            "\n" +
            "Copyright (c) 2016-present, RxJava Contributors.\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the 'License');\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an 'AS IS' BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "gson\n" +
            "\n" +
            "Copyright 2008 Google Inc.\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the 'License');\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "    http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an 'AS IS' BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "okhttp\n" +
            "\n" +
            "Copyright 2019 Square, Inc.\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the 'License');\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an 'AS IS' BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "parceler\n" +
            "\n" +
            "Copyright 2011-2015 John Ericksen\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the 'License');\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an 'AS IS' BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "tedkeyboardobserver\n" +
            "\n" +
            "Copyright 2019 Ted Park\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the 'License');\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an 'AS IS' BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\""

    companion object {
        private const val TAG = "OpenSourceActivity"

        fun startOpenSourceActivity(context: Context) {
            context.startActivity(
                Intent(context, OpenSourceActivity::class.java)
            )
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()

        actOpenSourceContext.text = content

    }

    fun initListener() {

    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.btn_back)
            setTitle("설정")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
