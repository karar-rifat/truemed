/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.pillreminder.activities

import android.os.Bundle
//import android.support.v7.app.AppCompatActivity
//import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillreminder.R
import com.example.pillreminder.adaptors.PostAdapter
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.interfaces.RetrofitInterface
import com.example.pillreminder.models.Post
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.post_layout.*
import kotlin.jvm.java


class ViewPostsActivity : AppCompatActivity(){
    internal  lateinit var jsonApi: RetrofitInterface
    internal lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_layout)
        //init api
        val retrofit = RetrofitClient.getRetrofitInstance()
        jsonApi = retrofit!!.create(RetrofitInterface::class.java)
        //view
        recycler_posts.setHasFixedSize(true)
        recycler_posts.layoutManager = LinearLayoutManager(this)
        Log.e("view post activity","entered onCreate")
//        jsonApi = RetrofitInterface()
        compositeDisposable = CompositeDisposable()
        fetchData()

//        recycler_posts.
    }

    private fun fetchData() {
        Log.e("view post activity","entered fetchData")
//        compositeDisposable =
        compositeDisposable.add(jsonApi.posts
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ posts -> displayData(posts) }
        )


    }

    private fun displayData(posts: List<Post>?) {
        Log.e("view post activity","entered displayData")
        val adapter = PostAdapter(this,posts!!)
        recycler_posts.adapter = adapter
    }
}