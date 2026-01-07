package repository.repository;

import model.MediaEntry;
import model.User;

import java.util.List;
import java.util.Map;

public interface IUserRepository {
    User get(long id);
    User get(String username);
    void add(User user);
    void update(long id, User user);
    List<MediaEntry> getRecommendationsByGenre(long userId);
    List<MediaEntry> getRecommendationsByContent(long userId);
    Map<String, Double> getLeaderboard();
}
