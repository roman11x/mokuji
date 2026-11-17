package io.github.roman11x.mokuji.data

class ExceptionFakeViewerRepository(
    private val exception: Exception
) : ViewerRepository {
    override suspend fun getViewer(): User {
        throw exception
    }
}