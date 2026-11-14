package io.github.roman11x.mokuji.data

interface ViewerRepository {
    suspend fun getViewer(): User
}