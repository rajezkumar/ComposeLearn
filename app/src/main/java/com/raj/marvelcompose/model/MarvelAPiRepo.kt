package com.raj.marvelcompose.model

import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class MarvelAPiRepo(private val api: MarvelApi) {
    val characters = MutableStateFlow<NetworkResult<CharactersApiResponse>>(NetworkResult.Loading())

    fun query(query: String) {
        characters.value = NetworkResult.Loading()
        api.getCharacters(query).enqueue(object : retrofit2.Callback<CharactersApiResponse> {
            override fun onResponse(
                call: Call<CharactersApiResponse>,
                response: Response<CharactersApiResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        characters.value = NetworkResult.Success(it)
                    }
                } else {
                    characters.value = NetworkResult.Error(response.message())
                }
            }

            override fun onFailure(call: Call<CharactersApiResponse>, t: Throwable) {
                t.localizedMessage?.let {
                    characters.value = NetworkResult.Error(it)
                }
            }

        })
    }
}