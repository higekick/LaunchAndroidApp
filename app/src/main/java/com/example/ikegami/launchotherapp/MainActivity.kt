package com.example.ikegami.launchotherapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.*
import android.widget.*


class MainActivity : AppCompatActivity() {
    private val launchItemsAdapter: ArrayAdapter<LaunchItem>? = null
    private val launchItems = ArrayList<LaunchItem>()
    private var mListAdapter: LaunchListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pm = packageManager
        val pckInfoList = pm.getInstalledPackages(
                PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
        for (pckInfo in pckInfoList) {
            var oLaunchItem: LaunchItem? = null
            if (pm.getLaunchIntentForPackage(pckInfo.packageName) != null) {
                val packageName = pckInfo.packageName
                val className = pm.getLaunchIntentForPackage(pckInfo.packageName)!!.component!!.className + ""
                Log.i("起動可能なパッケージ名", packageName)
                Log.i("起動可能なクラス名", className)
                oLaunchItem = LaunchItem(true, packageName, className)
            } else {
                Log.i("----------起動不可能なパッケージ名", pckInfo.packageName)
                oLaunchItem = LaunchItem(false, pckInfo.packageName, null)
            }
            launchItems.add(oLaunchItem)
        }

        mListAdapter = LaunchListAdapter(this.applicationContext)
        mListAdapter!!.mArrayList = launchItems
        val listener = object : LaunchAppListener {
            override fun onLaunch(intent: Intent) {
                startActivity(intent)
            }
        }
        mListAdapter!!.setLaunchAppListener(listener)

        val launchListLayout = findViewById<ListView>(R.id.launchListLayout)
        launchListLayout.setAdapter(mListAdapter)
    }

    interface LaunchAppListener {
        fun onLaunch(intent: Intent)
    }

    internal inner class LaunchListAdapter(var context: Context) : BaseAdapter() {
        var layoutInflater: LayoutInflater? = null
        var mArrayList: ArrayList<LaunchItem>? = null

        var mLaunchAppListener: LaunchAppListener? = null

        fun setLaunchAppListener(_mLaunchAppListener: LaunchAppListener) {
            this.mLaunchAppListener = _mLaunchAppListener
        }

        init {
            this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return mArrayList!!.size
        }

        override fun getItem(position: Int): Any {
            return mArrayList!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override
        fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            convertView = layoutInflater!!.inflate(R.layout.activity_launch_other_app_sample_list, parent, false)
            val packageNameText = convertView.findViewById<TextView>(R.id.packageName)
            val classNameText = convertView.findViewById<TextView>(R.id.className)
            val button = convertView.findViewById<Button>(R.id.launch)
            val packageName = mArrayList!![position].packageName
            val className = mArrayList!![position].className

//            button.setOnClickListener(object : View.OnClickListener {
//                override
//                fun onClick(v: View) {
//                    if (mArrayList!![position].isLaunchble) {
//                        val intent = Intent()
//                        if (packageName != null && className != null && mLaunchAppListener != null) {
//                            intent.setClassName(packageName!!, className!!)
//                            mLaunchAppListener!!.onLaunch(intent)
//                        }
//                    }
//                }
//            })

            button.setOnClickListener{ _ ->
                if (mArrayList!![position].isLaunchble) {
                    val intent = Intent()
                    if (packageName != null && className != null && mLaunchAppListener != null) {
                        intent.setClassName(packageName!!, className!!)
                        mLaunchAppListener!!.onLaunch(intent)
                    }
                }
            }

            if (mArrayList!![position].isLaunchble) {
                packageNameText.setText(packageName)
                classNameText.setText(className)
            } else {
                packageNameText.setText(packageName)
                button.setVisibility(View.INVISIBLE)
            }

            return convertView
        }

        fun setmArrayList(mArrayList: ArrayList<LaunchItem>) {
            this.mArrayList = mArrayList
        }

    }

    internal inner class LaunchItem(isLaunchble: Boolean, val packageName: String, val className: String?) {
        var isLaunchble = false
        init {
            this.isLaunchble = isLaunchble
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        createList(item!!.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun createList(selection: Int) : ArrayList<LaunchItem> {
        launchItems.clear()
        val pm = packageManager
        val pckInfoList = pm.getInstalledPackages(
                PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
        for (pckInfo in pckInfoList) {
            var oLaunchItem: LaunchItem? = null
            if (pm.getLaunchIntentForPackage(pckInfo.packageName) != null) {
                val packageName = pckInfo.packageName
                val className = pm.getLaunchIntentForPackage(pckInfo.packageName)!!.component!!.className + ""
                Log.i("起動可能なパッケージ名", packageName)
                Log.i("起動可能なクラス名", className)
                oLaunchItem = LaunchItem(true, packageName, className)
            } else {
                Log.i("----------起動不可能なパッケージ名", pckInfo.packageName)
                oLaunchItem = LaunchItem(false, pckInfo.packageName, null)
            }
            when (selection) {
                R.id.all -> {
                    launchItems.add(oLaunchItem)
                }
                R.id.canLaunch -> {
                    if (oLaunchItem.isLaunchble) {
                        launchItems.add(oLaunchItem)
                    }
                }
                R.id.cannotLaunch -> {
                    if (!oLaunchItem.isLaunchble) {
                        launchItems.add(oLaunchItem)
                    }
                }
            }
        }

        Toast.makeText(baseContext, "application number: " + launchItems.size, Toast.LENGTH_SHORT).show()

//        mListAdapter = LaunchListAdapter(this.applicationContext)
//        mListAdapter!!.mArrayList = launchItems
        mListAdapter!!.notifyDataSetChanged()
        return launchItems
    }
}
