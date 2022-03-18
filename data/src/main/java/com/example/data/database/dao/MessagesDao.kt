package com.example.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.data.database.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharactersDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT id, cursor, name, species, homeworld, favorite FROM character_table
    """)
    fun getAllOnce(): List<CharacterEntity>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT cursor FROM character_table
    """)
    fun getCursors(): List<String>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT id FROM character_table WHERE favorite = 1
    """)
    fun getFavoritesIds(): Flow<List<String>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT id FROM character_table WHERE favorite = 1
    """)
    fun getFavoritesIdsOnce(): List<String>

    @Query("""
        INSERT INTO character_table (id, cursor, name, species, homeworld, favorite) VALUES (:id, :cursor, :name, :species, :homeworld, 0)
    """)
    fun insertCharacter(
        id: String,
        cursor: String,
        name: String?,
        species: String?,
        homeworld: String?
    )

    @Query("""
        UPDATE character_table SET name = :name, species = :species, homeworld = :homeworld WHERE id = :id
    """)
    fun updateCharacter(
        id: String,
        name: String?,
        species: String?,
        homeworld: String?
    )

    @Query(
        """ 
        UPDATE character_table SET eyeColor = :eyeColor, hairColor = :hairColor, skinColor = :skinColor,
        birthYear = :birthYear, vehicles = :vehicles  WHERE id = :id
    """)
    fun updateCharacterDetails(
        id: String,
        eyeColor: String?,
        hairColor: String?,
        skinColor: String?,
        birthYear: String?,
        vehicles: List<String>?
    )

    @Query("UPDATE character_table SET favorite = 1 WHERE id = :id")
    fun favoriteTrue(id: String)

    @Query("UPDATE character_table SET favorite = 0 WHERE id = :id")
    fun favoriteFalse(id: String)

    @Query("SELECT id, cursor, name, species, homeworld, favorite FROM character_table WHERE name LIKE '%' || :query || '%'")
    fun getAllCharactersPaging(query: String): PagingSource<Int, CharacterEntity>
}
