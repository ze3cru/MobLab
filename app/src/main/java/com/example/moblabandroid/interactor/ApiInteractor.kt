package com.example.moblabandroid.interactor

import android.util.Log
import com.example.moblabandroid.db.CharacterDao
import com.example.moblabandroid.db.entities.RoomCharacter
import com.example.moblabandroid.db.toResult
import com.example.moblabandroid.db.toRoomModel
import com.example.moblabandroid.interactor.event.GetCharacterEvent
import com.example.moblabandroid.model.CharacterX
import com.example.moblabandroid.network.RnMApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ApiInteractor @Inject constructor(
    private var RnMApi: RnMApi,
    private var characterDao: CharacterDao
) {
    fun getAllCharacter() {
        val event = GetCharacterEvent()

        try {
            val characterQueryCall = RnMApi.getAllChars()
            val charactersFromLocalDb = characterDao.getAllCharacters()

            val response = characterQueryCall.execute()
            Log.d("getAllCharacter Reponse", response.body().toString())
            Log.d("getAllCharacter DB", charactersFromLocalDb.toString())
            if (response.code() != 200) {
                throw Exception("CharacterX code is not 200: ${response.code()}")
            }
            event.code = response.code()
            event.characters = response.body()?.results?.plus(charactersFromLocalDb.map(RoomCharacter::toResult))
            EventBus.getDefault().post(event)
        } catch (e: Exception) {
            event.throwable = e
            EventBus.getDefault().post(event)
        }
    }

    fun getCharacterById(characterId: Long) {
        val event = GetCharacterEvent()

        try {
            val characterQueryCall = RnMApi.getCharacterById(characterId)
            val characterFromLocalDb = characterDao.getCharacterById(characterId)

            val response = characterQueryCall.execute()
            Log.d("getAllCharacter Reponse", response.body().toString())
            if (response.code() != 200 && response.code() != 404) {
                throw Exception("CharacterX code is not 200")
            }
            event.code = response.code()
            event.characters = listOf(response.body() ?: characterFromLocalDb.toResult())
            EventBus.getDefault().post(event)
        } catch (e: Exception) {
            event.throwable = e
            EventBus.getDefault().post(event)
        }
    }

    fun createCharacter(character: CharacterX) {
        characterDao.insertCharacter(character.toRoomModel())
    }

    fun updateCharacter(character: CharacterX) {
        characterDao.insertCharacter(character.toRoomModel())
    }

    fun deleteCharacter(character: CharacterX) {
        characterDao.deleteChallenge(character.id.toLong())
    }
}