package com.caioiox.anotaesepensamentos

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.caioiox.anotaesepensamentos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { NewNote().show(supportFragmentManager, "") }

        adapter = NoteAdapter(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun createNewNote(n: Note) {
        adapter.noteList.add(n)
        adapter.notifyItemInserted(adapter.noteList.size-1)
        saveNotes()
    }
    fun deleteNote(index: Int) {
        adapter.noteList.removeAt(index)
        adapter.notifyItemRemoved(index)
        saveNotes()
    }
    fun showNote(index: Int) {
        val dialog = ShowNote(adapter.noteList[index], index)
        dialog.show(supportFragmentManager, "")
    }

}