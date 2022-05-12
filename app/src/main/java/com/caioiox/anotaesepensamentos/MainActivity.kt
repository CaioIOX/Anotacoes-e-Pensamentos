package com.caioiox.anotaesepensamentos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.customview.widget.ViewDragHelper
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caioiox.anotaesepensamentos.databinding.ActivityMainBinding
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val FILEPATH = "notes.json"
    }


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

        adapter.noteList = retrieveNotes()
        adapter.notifyItemRangeInserted(0, adapter.noteList.size)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


    }

    override fun onStart() {
        super.onStart()

        val nightThemeSelected = sharedPreferences.getBoolean("theme", false)
        if (nightThemeSelected) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val showDividingLines = sharedPreferences.getBoolean("dividingLines", false)
        if (showDividingLines) binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        else if (binding.recyclerView.itemDecorationCount>0) binding.recyclerView.removeItemDecorationAt(0)

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
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else ->super.onOptionsItemSelected(item)
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
    private fun saveNotes() {
//        O método saveNotes recupera a lista completa de objetos Note da classe NoteAdapter
//        e usa a classe GsonBuilder para converter a lista em uma string JSON.
        val notes = adapter.noteList
        val gson = GsonBuilder().create()
        val jsonNotes = gson.toJson(notes)

//        As classes Writer e OutputStreamWriter codificam a string JSON como um fluxo de dados que
//        pode ser gravado em um arquivo dentro do aplicativo. Os detalhes do arquivo são definidos
//        em uma variável chamada out, que armazena uma instância da classe FileOutputStream. A classe
//        FileOutputStream é inicializada usando o openFileOutput e fornecendo o nome do arquivo
//        e um modo de operação. Nesse caso, o  modo de operação é definido como privado,
//        o que significa que o arquivo notes.json só poderá ser acessado por este aplicativo.
        var writer: Writer? = null
        try {
            val out = this.openFileOutput(FILEPATH, Context.MODE_PRIVATE)

            writer = OutputStreamWriter(out)
            writer.write(jsonNotes)
        } catch (e:Exception) {
            writer?.close()
        } finally {
            writer?.close()
        }
    }
    fun retrieveNotes(): MutableList<Note> {
        var noteList = mutableListOf<Note>()

        //Checagem para ver se o arquivo FILEPATH existe
        if (this.getFileStreamPath(FILEPATH).isFile){
            var reader: BufferedReader? = null
            try {
                val fileInput = this.openFileInput(FILEPATH)
                reader = BufferedReader(InputStreamReader(fileInput))
                val stringBuilder = StringBuilder()

                for (line in reader.readLine()) stringBuilder.append(line)

                if (stringBuilder.isNotEmpty()) {
                    val listType = object : TypeToken<List<Note>>() {}.type
                    noteList = Gson().fromJson(stringBuilder.toString(), listType)
                }
            } catch (e:Exception) {
                reader?.close()
            } finally {
                reader?.close()
            }
        }
        return noteList
    }
}