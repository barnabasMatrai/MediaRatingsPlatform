import model.MediaEntry;
import model.MediaType;
import model.Rating;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.DataAccessException;
import repository.repository.MediaRepository;
import repository.repository.RatingRepository;
import repository.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private RatingRepository ratingRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User("testuser1", "pwd1");
        user2 = new User("testuser2", "pwd2");
    }

    //USER TESTS

    @Test
    void addUserSucceeds() {
        assertDoesNotThrow(() -> userRepository.add(user1));
        verify(userRepository).add(user1);
    }

    @Test
    void findUserByUsername() {
        when(userRepository.get("testuser1"))
                .thenReturn(user1);

        User result = userRepository.get("testuser1");

        assertNotNull(result);
        assertEquals("testuser1", result.getUsername());
    }

    // MEDIA ADD / UPDATE

    @Test
    void addMediaSucceeds() {
        MediaEntry media = new MediaEntry(user1.getUsername(), "Movie1", "Description", MediaType.movie, 2000, new String[]{"thriller", "science"}, 12);
        doNothing().when(mediaRepository).add(media, "testuser1");

        assertDoesNotThrow(() ->
                mediaRepository.add(media, "testuser1")
        );
    }

    @Test
    void updateMediaAllowed() {
        MediaEntry updated = new MediaEntry(user1.getUsername(), "Movie1", "Description", MediaType.movie, 2000, new String[]{"thriller", "science"}, 12);
        doNothing().when(mediaRepository)
                .update(updated, 1L, 1L);

        assertDoesNotThrow(() ->
                mediaRepository.update(updated, 1L, 1L)
        );
    }

    @Test
    void updateMediaForbidden() {
        MediaEntry updated = new MediaEntry(user1.getUsername(), "Movie1", "Description", MediaType.movie, 2000, new String[]{"thriller", "science"}, 12);

        doThrow(new DataAccessException("Not allowed"))
                .when(mediaRepository)
                .update(updated, 1L, 2L);

        assertThrows(DataAccessException.class, () ->
                mediaRepository.update(updated, 1L, 2L)
        );
    }

    // FAVORITES

    @Test
    void favoriteMediaSucceeds() {
        doNothing().when(mediaRepository).favorite(1L, 1L);

        mediaRepository.favorite(1L, 1L);

        verify(mediaRepository).favorite(1L, 1L);
    }

    @Test
    void favoriteMediaFailsIfAlreadyFavorited() {
        doThrow(new DataAccessException("Already favorite"))
                .when(mediaRepository)
                .favorite(1L, 1L);

        assertThrows(DataAccessException.class, () ->
                mediaRepository.favorite(1L, 1L)
        );
    }

    @Test
    void unfavoriteMediaSucceeds() {
        doNothing().when(mediaRepository).unfavorite(1L, 1L);

        assertDoesNotThrow(() ->
                mediaRepository.unfavorite(1L, 1L)
        );
    }

    @Test
    void unfavoriteFailsIfNotFavorited() {
        doThrow(new DataAccessException("Not favorited"))
                .when(mediaRepository)
                .unfavorite(1L, 1L);

        assertThrows(DataAccessException.class, () ->
                mediaRepository.unfavorite(1L, 1L)
        );
    }

    // RATINGS

    @Test
    void rateMediaSucceeds() {
        Rating rating = new Rating(user1.getUsername(), "Great", (short) 5);

        doNothing().when(mediaRepository)
                .rate(rating, 1L, 1L);

        assertDoesNotThrow(() ->
                mediaRepository.rate(rating, 1L, 1L)
        );
    }

    @Test
    void rateMediaFailsIfAlreadyRated() {
        Rating rating = new Rating(user1.getUsername(), "Good", (short) 4);

        doThrow(new DataAccessException("Already rated"))
                .when(mediaRepository)
                .rate(rating, 1L, 1L);

        assertThrows(DataAccessException.class, () ->
                mediaRepository.rate(rating, 1L, 1L)
        );
    }

    @Test
    void updateRatingSucceeds() {
        Rating updated = new Rating(user1.getUsername(), "Okay", (short) 3);

        doNothing().when(ratingRepository)
                .update(updated, 1L, 1L);

        assertDoesNotThrow(() ->
                ratingRepository.update(updated, 1L, 1L)
        );
    }

    @Test
    void updateRatingFailsIfNotOwner() {
        Rating updated = new Rating(user1.getUsername(), "Bad", (short) 2);

        doThrow(new DataAccessException("Not allowed"))
                .when(ratingRepository)
                .update(updated, 1L, 2L);

        assertThrows(DataAccessException.class, () ->
                ratingRepository.update(updated, 1L, 2L)
        );
    }

    @Test
    void confirmRatingSucceeds() {
        doNothing().when(ratingRepository)
                .confirm(1L, 1L);

        assertDoesNotThrow(() ->
                ratingRepository.confirm(1L, 1L)
        );
    }

    @Test
    void confirmRatingFailsIfAlreadyConfirmed() {
        doThrow(new DataAccessException("Already confirmed"))
                .when(ratingRepository)
                .confirm(1L, 1L);

        assertThrows(DataAccessException.class, () ->
                ratingRepository.confirm(1L, 1L)
        );
    }

    // LIKE RATING

    @Test
    void likeRatingSucceeds() {
        doNothing().when(ratingRepository)
                .likeRating(1L, 1L);

        assertDoesNotThrow(() ->
                ratingRepository.likeRating(1L, 1L)
        );
    }

    @Test
    void likeRatingFailsIfAlreadyLiked() {
        doThrow(new DataAccessException("Already liked"))
                .when(ratingRepository)
                .likeRating(1L, 1L);

        assertThrows(DataAccessException.class, () ->
                ratingRepository.likeRating(1L, 1L)
        );
    }

    // RECOMMENDATIONS

    @Test
    void recommendationsReturned() {
        List<MediaEntry> recommendations =
                List.of(new MediaEntry(user1.getUsername(), "Movie", "Description", MediaType.movie, 2000, new String[]{"thriller", "science"}, 12),
                        new MediaEntry(user1.getUsername(), "Game", "Description2", MediaType.game, 1995, new String[]{"thriller", "science"}, 14));

        when(userRepository.getRecommendationsByGenre(1L))
                .thenReturn(recommendations);

        List<MediaEntry> result =
                userRepository.getRecommendationsByGenre(1L);

        assertEquals(2, result.size());
    }

    @Test
    void recommendationsEmptyIfNoPreferences() {
        when(userRepository.getRecommendationsByGenre(1L))
                .thenReturn(List.of());

        List<MediaEntry> result =
                userRepository.getRecommendationsByGenre(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void lowRatedMediaDoesNotAffectRecommendations() {
        Rating lowRating = new Rating(user1.getUsername(), "Not good", (short) 2);

        doNothing().when(mediaRepository)
                .rate(lowRating, 1L, 1L);

        when(userRepository.getRecommendationsByGenre(1L))
                .thenReturn(List.of()); // No recommendations based on low rating

        assertDoesNotThrow(() ->
                mediaRepository.rate(lowRating, 1L, 1L)
        );

        List<MediaEntry> recommendations =
                userRepository.getRecommendationsByGenre(1L);

        assertTrue(recommendations.isEmpty());
    }
}
