package ru.coding_board.session_service.task.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.task.TaskController;
import ru.coding_board.session_service.task.TaskService;
import ru.coding_board.session_service.task.dto.TaskCreateRequest;
import ru.coding_board.session_service.task.dto.TaskFilterRequest;
import ru.coding_board.session_service.task.dto.TaskPatchRequest;
import ru.coding_board.session_service.task.dto.TaskResponse;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean 
    private TaskService taskService;

    @Test
    void shouldReturnTaskById() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        when(taskService.getTaskById(id))
            .thenReturn(TaskResponse.builder().taskId(id).build());

        // when then
        mockMvc.perform(get("/tasks/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(id.toString()));
    }

    @Test
    void shouldFilterTasks() throws Exception {
        // given
        when(taskService.filterTasks(any(TaskFilterRequest.class), any(Pageable.class)))
            .thenReturn(Page.empty());

        // when then
        mockMvc.perform(get("/tasks")
            .param("category", "ALGORITHMS"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCreateTask() throws Exception {
        // given
        TaskCreateRequest request = new TaskCreateRequest(
            "Test title", "Test desc", Difficulty.JUNIOR, Category.ALGORITHMS, null);
        when(taskService.createTask(any(TaskCreateRequest.class)))
            .thenReturn(TaskResponse.builder().taskId(UUID.randomUUID()).build());

        // when then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreateTaskRequestIsInvalid() throws Exception {
        // given
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        // when then
        verify(taskService, never()).createTask(any());
    }

    @Test
    void shouldPatchTask() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        TaskPatchRequest request = new
            TaskPatchRequest("Test title", null, null, null, null, null);
        when(taskService.patchTask(any(TaskPatchRequest.class), eq(id)))
            .thenReturn(TaskResponse.builder().taskId(id).build());

        // when then
        mockMvc.perform(patch("/tasks/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }


}
