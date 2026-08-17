package com.fatma.taskmanger.task;

import com.fatma.taskmanger.common.exception.TaskNotFoundException;
import com.fatma.taskmanger.common.exception.UserNotFoundException;
import com.fatma.taskmanger.task.dto.CreateTaskRequest;
import com.fatma.taskmanger.task.dto.TaskResponse;
import com.fatma.taskmanger.task.dto.UpdateTaskRequest;
import com.fatma.taskmanger.user.User;
import com.fatma.taskmanger.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Follows the exact same layering the course used for User: Controller ->
 * Service -> Repository, DTOs in/out, mapping via a dedicated Mapper.
 *
 * The ownership check in requireOwnership() is the kind of authorization
 * rule that belongs in the service layer, not in SecurityConfig -
 * SecurityConfig only knows "is this request authenticated at all", it
 * has no idea which task belongs to which user.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository,
                        TagRepository tagRepository,
                        UserRepository userRepository,
                        TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Task task = new Task(request.title(), request.description(), user);
        task.getTags().addAll(resolveTags(request.tags()));

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse getTask(Long id, Long requesterId) {
        Task task = findTaskOrThrow(id);
        requireOwnership(task, requesterId);
        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> getTasksForUser(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public TaskResponse updateTask(Long id, Long requesterId, UpdateTaskRequest request) {
        Task task = findTaskOrThrow(id);
        requireOwnership(task, requesterId);

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());

        task.getTags().clear();
        task.getTags().addAll(resolveTags(request.tags()));

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id, Long requesterId) {
        Task task = findTaskOrThrow(id);
        requireOwnership(task, requesterId);
        taskRepository.delete(task);
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void requireOwnership(Task task, Long requesterId) {
        if (!task.getUser().getId().equals(requesterId)) {
            throw new AccessDeniedException("You do not have access to this task.");
        }
    }

    /** get-or-create: unknown tag names become new Tag rows automatically. */
    private Set<Tag> resolveTags(Set<String> tagNames) {
        if (tagNames == null) {
            return new HashSet<>();
        }
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));
            tags.add(tag);
        }
        return tags;
    }
}
