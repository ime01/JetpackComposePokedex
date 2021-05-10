package com.plcoding.jetpackcomposepokedex.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.palette.graphics.Palette
import com.plcoding.jetpackcomposepokedex.data.models.PokeDexListEntry
import com.plcoding.jetpackcomposepokedex.util.Constants.PAGE_SIZE
import com.plcoding.jetpackcomposepokedex.util.Resource
import kotlinx.coroutines.launch
import java.util.*

@HiltViewModel
class PokeMonListViewModel @Inject constructor (private val repository: PokemonRepository): ViewModel() {


    private var  curPage = 0

    var pokemonList = mutableStateOf<List<PokeDexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)


    init {
        loadPokemonPaginated()
    }


    fun loadPokemonPaginated(){

        isLoading.value = true

        viewModelScope.launch {

        val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)

        when(result){
           is Resource.Success->{
               endReached.value = curPage * PAGE_SIZE >= result.data!!.count

               val pokedexEntries = result.data.results.mapIndexed { index, entry ->

                   val number = if(entry.url.endsWith("/")){
                       entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                   }else{
                       entry.url.takeLastWhile { it.isDigit() }
                   }

                   val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                   PokeDexListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
               }

               curPage++

               loadError.value = ""
               isLoading.value = false
               pokemonList.value += pokedexEntries
           }
            is Resource.Error->{

                loadError.value = result.message!!
                isLoading.value = false
            }

        }

        }

    }




    fun calDominantColor(drawable: Drawable, onFinish: (Color)-> Unit){

        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate{ palette->
            palette?.dominantSwatch?.rgb?.let { colorValue->
                onFinish(Color(colorValue))
            }

        }
    }

}