package si.uni_lj.fe.libri.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import si.uni_lj.fe.libri.data.api.OpenLibraryWorkDetails
import kotlinx.coroutines.tasks.await

enum class BookStatus {
    READ, CURRENTLY_READING, WANT_TO_READ, NONE;

    companion object {
        fun fromString(status: String?): BookStatus {
            if (status == null) return NONE
            val upper = status.uppercase()
            return try {
                valueOf(upper)
            } catch (e: Exception) {
                when (upper) {
                    "READING", "CURRENTLY_READING" -> CURRENTLY_READING
                    "SAVED", "WANT_TO_READ" -> WANT_TO_READ
                    "FINISHED", "READ" -> READ
                    else -> NONE
                }
            }
        }
    }
}

data class LibraryBook(
    val id: String = "",
    val title: String = "",
    val authorName: String? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val authors: List<String> = emptyList(),
    val status: String = "NONE",
    val genres: List<String> = emptyList()
) {
    @get:Exclude
    val bookStatus: BookStatus get() = BookStatus.fromString(status)
}

class UserLibraryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = firestore.collection("users")

    private val _libraryBooks = MutableStateFlow<List<LibraryBook>?>(null)
    val libraryBooks: StateFlow<List<LibraryBook>?> = _libraryBooks.asStateFlow()

    // Flow to expose sync errors to the UI
    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private var currentUserId: String? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val userId = firebaseAuth.currentUser?.uid
            if (userId != currentUserId) {
                startSync()
            }
        }
        startSync()
    }

    fun startSync() {
        val userId = auth.currentUser?.uid
        if (userId == currentUserId && listenerRegistration != null) return

        stopSync()
        currentUserId = userId
        _syncError.value = null

        if (userId != null) {
            Log.i("UserLibraryRepository", "Starting sync for: $userId")
            val collection = usersCollection.document(userId).collection("books")

            listenerRegistration = collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                        _syncError.value = "Permission Denied: Please check Firestore Rules."
                    } else {
                        _syncError.value = error.message
                    }
                    _libraryBooks.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _syncError.value = null
                    val books = snapshot.toObjects(LibraryBook::class.java)
                    _libraryBooks.value = books
                }
            }
        } else {
            _libraryBooks.value = emptyList()
        }
    }

    fun stopSync() {
        listenerRegistration?.remove()
        listenerRegistration = null
        currentUserId = null
    }

    suspend fun updateBookStatus(
        bookId: String,
        details: OpenLibraryWorkDetails,
        status: BookStatus,
        authorName: String? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val collection = usersCollection.document(uid).collection("books")

        try {
            if (status == BookStatus.NONE) {
                collection.document(bookId).delete().await()
            } else {
                var finalAuthorName = if (!authorName.isNullOrBlank() && authorName != "Unknown Author") {
                    authorName
                } else {
                    details.authorNames?.joinToString(", ")
                }

                if (finalAuthorName.isNullOrBlank() || finalAuthorName == "Unknown Author") {
                    val existing = _libraryBooks.value?.find { it.id == bookId }
                    if (!existing?.authorName.isNullOrBlank()) {
                        finalAuthorName = existing?.authorName
                    }
                }

                val book = LibraryBook(
                    id = bookId,
                    title = details.title,
                    authorName = finalAuthorName,
                    coverUrl = details.coverUrl,
                    description = details.descriptionText,
                    authors = details.authors?.map {
                        it.author.key.removePrefix("/authors/").removePrefix("authors/")
                    } ?: emptyList(),
                    status = status.name,
                    genres = details.subjects?.take(5) ?: emptyList()
                )
                collection.document(bookId).set(book).await()
            }
        } catch (e: Exception) {
            Log.e("UserLibraryRepository", "Update failed: ${e.message}")
            throw e
        }
    }

    suspend fun getLibraryBook(bookId: String): LibraryBook? {
        _libraryBooks.value?.find { it.id == bookId }?.let { return it }
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = usersCollection.document(uid).collection("books").document(bookId).get().await()
            snapshot.toObject(LibraryBook::class.java)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getFavoriteGenre(): String {

        val uid = auth.currentUser?.uid ?: return "No genre yet"

        return try {

            val books = usersCollection
                .document(uid)
                .collection("books")
                .get()
                .await()
                .toObjects(LibraryBook::class.java)

            books
                .flatMap { it.genres }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
                ?: "No genre yet"

        } catch (e: Exception) {

            "No genre yet"
        }
    }



}
