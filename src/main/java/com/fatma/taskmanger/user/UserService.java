package com.fatma.taskmanger.user;

import com.fatma.taskmanger.common.exception.UserNotFoundException;
import com.fatma.taskmanger.user.dto.UpdateUserRequest;
import com.fatma.taskmanger.user.dto.UserResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caching lives here, on the Service, never on the Controller (too high
 * level - misses the point of caching) or the Repository (mixes concerns:
 * a repository should only talk to the database, and tomorrow the cache
 * might not even be backed by the same data source).
 *
 * TTL / eviction policy itself is configured once, centrally, in
 * CacheConfig - the annotations here only say WHAT to cache and WHICH key
 * to use.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * @CachePut ALWAYS executes the method (unlike @Cacheable) and then
     * replaces whatever was in the cache for this key with the fresh
     * result - exactly what we want after an update.
     */
    @CachePut(value = "users", key = "#id")
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Modify the existing entity instead of building a new one, so we
        // never lose fields the request doesn't carry (id, password, role).
        user.setName(request.name());
        user.setEmail(request.email());

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
