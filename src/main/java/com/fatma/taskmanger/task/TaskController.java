package com.fatma.taskmanger.task;

import com.fatma.taskmanger.task.dto.CreateTaskRequest;
import com.fatma.taskmanger.task.dto.TaskResponse;
import com.fatma.taskmanger.task.dto.UpdateTaskRequest;
import com.fatma.taskmanger.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The JWT filter already put an Authentication in the SecurityContext
 * (see JwtAuthenticationFilter) with the user's email as the principal
 * name - here we resolve that email to a user id so TaskService can
 * enforce ownership. In a larger project this lookup is usually wrapped
 * in a small @AuthenticationPrincipal-based helper instead of being
 * repeated per controller.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public TaskResponse createTask(Authentication authentication, @Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(currentUserId(authentication), request);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(Authentication authentication, @PathVariable Long id) {
        return taskService.getTask(id, currentUserId(authentication));
    }

    @GetMapping
    public List<TaskResponse> getMyTasks(Authentication authentication) {
        return taskService.getTasksForUser(currentUserId(authentication));
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(Authentication authentication, @PathVariable Long id,
                                    @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, currentUserId(authentication), request);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(Authentication authentication, @PathVariable Long id) {
        taskService.deleteTask(id, currentUserId(authentication));
    }

    private Long currentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
    }
}
