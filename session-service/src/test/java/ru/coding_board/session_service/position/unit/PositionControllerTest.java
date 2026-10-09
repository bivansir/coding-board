package ru.coding_board.session_service.position.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.coding_board.session_service.position.PositionController;
import ru.coding_board.session_service.position.PositionService;
import ru.coding_board.session_service.position.dto.PositionCreateRequest;
import ru.coding_board.session_service.position.dto.PositionResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PositionController.class)
class PositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean 
    private PositionService positionService;
    

    @Test
    void shouldReturnPositionById() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        PositionResponse response = PositionResponse.builder().positionId(id).title("Java Developer").build();
        when(positionService.getPositionById(id)).thenReturn(response);

        // when then
        mockMvc.perform(get("/positions/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.positionId").value(id.toString()));
    }

    @Test
    void shouldFindPositionsByTitle() throws Exception {
        // given
        when(positionService.findPositionsByTitle("Java")).thenReturn(
            List.of(PositionResponse.builder().title("Java Developer").build()));

        // when then
        mockMvc.perform(get("/positions").param("title", "Java"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn400WhenTitleParamMissing() throws Exception {
        // when then
        mockMvc.perform(get("/positions"))
            .andExpect(status().isBadRequest());

        verify(positionService, never()).findPositionsByTitle(any());
    }

    @Test
    void shouldCreatePosition() throws Exception {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Java Developer");
        
        when(positionService.createPosition(any(PositionCreateRequest.class)))
            .thenReturn(PositionResponse.builder().positionId(UUID.randomUUID()).build());

        // when then
        mockMvc.perform(post("/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreatePositionRequestIsInvalid() throws Exception {
        // given
        String invalidJson = "{}";

        // when then
        mockMvc.perform(post("/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(positionService, never()).createPosition(any());
    }

    @Test
    void shouldDeactivatePosition() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        // when then
        mockMvc.perform(patch("/positions/{id}/deactivate", id))
            .andExpect(status().isNoContent());

        verify(positionService).deactivatePosition(id);
    }
}
