package si.uni_lj.fe.libri.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import si.uni_lj.fe.libri.data.api.OpenLibraryWorkDetails

enum class BookStatus {
    READ, CURRENTLY_READING, WANT_TO_READ, NONE
}

data class LibraryBook(
    val id: String = "",
    val title: String = "",
    val coverUrl: String? = null,
    val description: String? = null,
    val authors: List<String> = emptyList(),
    val status: String = BookStatus.NONE.name
)

class UserLibraryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val usersCollection = firestore.collection("users")

    private fun getUserBooksCollection() = auth.currentUser?.uid?.let { uid ->
        usersCollection.document(uid).collection("books")
    }

    suspend fun updateBookStatus(bookId: String, details: OpenLibraryWorkDetails, status: BookStatus) {
        val collection = getUserBooksCollection() ?: return
        
        if (status == BookStatus.NONE) {
            collection.document(bookId).delete().await()
        } else {
            val book = LibraryBook(
                id = bookId,
                title = details.title,
                coverUrl = details.coverUrl,
                description = details.descriptionText,
                authors = details.authors?.map { it.author.key.removePrefix("/authors/") } ?: emptyList(),
                status = status.name
            )
            collection.document(bookId).set(book).await()
        }
    }

    suspend fun getLibraryBook(bookId: String): LibraryBook? {
        val collection = getUserBooksCollection() ?: return null
        return try {
            val snapshot = collection.document(bookId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(LibraryBook::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getBookStatus(bookId: String): BookStatus {
        val book = getLibraryBook(bookId)
        return book?.let { BookStatus.valueOf(it.status) } ?: BookStatus.NONE
    }

    suspend fun getBooksByStatus(status: BookStatus): List<LibraryBook> {
        val collection = getUserBooksCollection() ?: return emptyList()
        return try {
            val snapshot = collection.whereEqualTo("status", status.name).get().await()
            snapshot.toObjects(LibraryBook::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
