package ru.coding_board.session_service.task.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskMapper;
import ru.coding_board.session_service.task.TaskRepository;
import ru.coding_board.session_service.task.TaskService;
import ru.coding_board.session_service.task.TaskTestDataBuilder;
import ru.coding_board.session_service.task.dto.TaskCreateRequest;
import ru.coding_board.session_service.task.dto.TaskFilterRequest;
import ru.coding_board.session_service.task.dto.TaskListItemResponse;
import ru.coding_board.session_service.task.dto.TaskPatchRequest;
import ru.coding_board.session_service.task.dto.TaskResponse;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks 
     TaskService taskService;

    @Mock
    TaskRepository taskRepository;

    @Test 
    void shouldGetTaskById() {
        // given
        Task task = TaskTestDataBuilder.aTaskWithId().build();
        UUID taskId = task.getTaskId();
        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        // when
        TaskResponse result = taskService.getTaskById(taskId);

        // then
        assertEquals(TaskMapper.buildResponse(task), result);
    }

    @Test 
    void shouldThrowWhenTaskNotFound() {
        // given
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> taskService.getTaskById(taskId));
    }

    @Test 
    void shouldFilterTasksAndReturnItResult() {
        // given
        TaskFilterRequest request = new TaskFilterRequest(
            null, null, null,
            null, null
        );
        PageRequest pageable = PageRequest.of(1, 5);
        Page<TaskListItemResponse> expectedPage = new PageImpl<>(List.of(
            new TaskListItemResponse(UUID.randomUUID(), null, null,
                null, null)
        ));

        when(taskRepository.findByFilter(
            request.searchQuery(), request.category(), request.difficulties(),
            request.programmingLanguage(), request.filteredTaskIds(), pageable))
                .thenReturn(expectedPage);

        // when
        Page<TaskListItemResponse> result = taskService.filterTasks(request, pageable);

        // then
        assertEquals(expectedPage, result);
    }

    @Test 
    void shouldSaveTaskWithRequestFieldsUnchanged() {
        // given
        TaskCreateRequest request = new TaskCreateRequest(
            "Two sum", "Test task", Difficulty.JUNIOR,
            Category.DATA_STRUCTURES, ProgrammingLanguage.JAVA);

        when(taskRepository.save(any(Task.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        taskService.createTask(request);

        // then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertEquals(request.title(), saved.getTitle());
        assertEquals(request.description(), saved.getDescription());
        assertEquals(request.difficulty(), saved.getDifficulty());
        assertEquals(request.category(), saved.getCategory());
        assertEquals(request.programmingLanguage(), saved.getProgrammingLanguage());
    }

    @Test 
    void shouldAlwaysSetNewTaskIsActive() {
        // given
        TaskCreateRequest request = new TaskCreateRequest(
            "Two sum", "Test task", Difficulty.JUNIOR,
            Category.DATA_STRUCTURES, ProgrammingLanguage.JAVA);

        when(taskRepository.save(any(Task.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        taskService.createTask(request);

        // then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        assertTrue(captor.getValue().getIsActive());
    }

    @Test 
    void shouldReturnResponseBuiltFromSavedTask() {
        // given
        TaskCreateRequest request = new TaskCreateRequest(
            "Set in request", "Test desc", Difficulty.JUNIOR,
            Category.ALGORITHMS, null);

        Task saved = TaskTestDataBuilder.aTaskWithId()
            .title("Set in DB Layer").build();

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

         // when
        TaskResponse response = taskService.createTask(request);

         // then
        assertEquals(saved.getTitle(), response.title());
        assertNotEquals(request.title(), response.title());
        assertEquals(saved.getTaskId(), response.taskId());
    }

    @Test 
    void shouldUpdateOnlyProvidedFiles() {
        // given
        Task existing = TaskTestDataBuilder.aTaskWithId().build();
        TaskPatchRequest request = new TaskPatchRequest(
            "New title", null, null,
            null, null, null);

        when(taskRepository.findById(existing.getTaskId()))
            .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        taskService.patchTask(request, existing.getTaskId());

        // then
        assertEquals(request.title(), existing.getTitle());
        assertEquals("Test desc", existing.getDescription());
        assertEquals(Difficulty.JUNIOR, existing.getDifficulty());
        assertEquals(Category.ALGORITHMS, existing.getCategory());
        assertNull(existing.getProgrammingLanguage());
        assertTrue(existing.getIsActive());
    }
    
}
