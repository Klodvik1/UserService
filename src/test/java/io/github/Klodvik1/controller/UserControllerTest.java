package io.github.Klodvik1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.exception.DuplicateEmailException;
import io.github.Klodvik1.exception.GlobalExceptionHandler;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
    private static final LocalDateTime TEST_CREATED_AT =
            LocalDateTime.of(2026, 3, 21, 12, 30, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /users/{id} should return 200 and user when user exists")
    void getUserById_ShouldReturn200AndUser_WhenUserExists() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "Denis",
                "denis@gmail.com",
                23,
                TEST_CREATED_AT
        );

        given(userService.getUserById(1L)).willReturn(responseDto);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Denis"))
                .andExpect(jsonPath("$.email").value("denis@gmail.com"))
                .andExpect(jsonPath("$.age").value(23));
    }

    @Test
    @DisplayName("GET /users/{id} should return 404 when user does not exist")
    void getUserById_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        given(userService.getUserById(1L))
                .willThrow(new UserNotFoundException("Пользователь с id 1 не найден"));

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id 1 не найден"));
    }

    @Test
    @DisplayName("GET /users should return 200 and users list")
    void getAllUsers_ShouldReturn200AndUsers() throws Exception {
        List<UserResponseDto> users = List.of(
                new UserResponseDto(
                        2L,
                        "Alice",
                        "alice@gmail.com",
                        25,
                        TEST_CREATED_AT
                ),
                new UserResponseDto(
                        1L,
                        "Denis",
                        "denis@gmail.com",
                        23,
                        TEST_CREATED_AT
                )
        );

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].name").value("Denis"));
    }

    @Test
    @DisplayName("POST /users should return 201 and created user")
    void createUser_ShouldReturn201AndCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Denis",
                "denis@gmail.com",
                23
        );

        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "Denis",
                "denis@gmail.com",
                23,
                TEST_CREATED_AT
        );

        given(userService.createUser(requestDto)).willReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Denis"))
                .andExpect(jsonPath("$.email").value("denis@gmail.com"))
                .andExpect(jsonPath("$.age").value(23));
    }

    @Test
    @DisplayName("POST /users should return 400 when request body is invalid")
    void createUser_ShouldReturn400_WhenRequestBodyIsInvalid() throws Exception {
        String invalidJson = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "age": 200
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации тела запроса."))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /users should return 409 when email already exists")
    void createUser_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Denis",
                "denis@gmail.com",
                23
        );

        given(userService.createUser(requestDto))
                .willThrow(new DuplicateEmailException("Пользователь с таким email уже существует."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Пользователь с таким email уже существует."));
    }

    @Test
    @DisplayName("PUT /users/{id} should return 200 and updated user")
    void updateUser_ShouldReturn200AndUpdatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Denis Updated",
                "denis.updated@gmail.com",
                22
        );

        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "Denis Updated",
                "denis.updated@gmail.com",
                22,
                TEST_CREATED_AT
        );

        given(userService.updateUser(eq(1L), eq(requestDto))).willReturn(responseDto);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Denis Updated"))
                .andExpect(jsonPath("$.email").value("denis.updated@gmail.com"))
                .andExpect(jsonPath("$.age").value(22));
    }

    @Test
    @DisplayName("PUT /users/{id} should return 404 when user does not exist")
    void updateUser_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Denis Updated",
                "denis.updated@gmail.com",
                22
        );

        given(userService.updateUser(eq(1L), eq(requestDto)))
                .willThrow(new UserNotFoundException("Пользователь с id=1 не найден."));

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));
    }

    @Test
    @DisplayName("DELETE /users/{id} should return 204 when user is deleted")
    void deleteUserById_ShouldReturn204_WhenUserDeleted() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("DELETE /users/{id} should return 404 when user does not exist")
    void deleteUserById_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        doThrow(new UserNotFoundException("Пользователь с id=1 не найден."))
                .when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));
    }
}