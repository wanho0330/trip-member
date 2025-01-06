package com.trip.user.controller;

import com.trip.auth.JwtTokenProvider;
import com.trip.user.dto.User;
import com.trip.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    String USER_API = "/apiv1/user";

    @WithMockUser(username = "test@test.com")
    @Test
    void createUser_Success() throws Exception {
        String requestBody = """
                    {
                        "email": "test@test.com",
                        "name": "test",
                        "password": "password"
                    }
                """;

        User user = User.builder()
                .idx(1L)
                .email("test@test.com")
                .name("test")
                .build();

        when(userService.createUser(any(User.class), eq("password"))).thenReturn(user);

        mockMvc.perform(post(USER_API)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "test@test.com")
    @Test
    void getUserByIdx_Success() throws Exception {
        User user = User.builder()
                .idx(1L)
                .email("test@test.com")
                .name("test")
                .build();

        when(userService.getUserByIdx(1L)).thenReturn(user);

        mockMvc.perform(get(USER_API + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.idx").value(user.getIdx()))
                .andExpect(jsonPath("$.user.name").value(user.getName()))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()));
    }

    @WithMockUser(username = "test@test.com")
    @Test
    void updateUser_Success() throws Exception {
        String requestBody = """
                    {
                        "user": {
                                    "email": "test@test.com",
                                    "name": "test",
                                    "password": "password"
                        }
                    }
                """;

        User updatedUser = User.builder()
                .idx(1L)
                .email("test@test.com")
                .name("test2")
                .build();

        when(userService.updateUser(eq(updatedUser.getIdx()), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put(USER_API + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.idx").value(updatedUser.getIdx()))
                .andExpect(jsonPath("$.user.name").value(updatedUser.getName()));
    }

    @WithMockUser(username = "test@test.com")
    @Test
    void updatePassword_Success() throws Exception {
        String requestBody = """
                    {
                        "oldPassword": "password",
                        "newPassword": "password1"
                    }
                """;

        User updatedUser = User.builder()
                .idx(1L)
                .email("test@test.com")
                .name("test2")
                .build();

        when(userService.updatePassword(eq(updatedUser.getIdx()), anyString(), anyString())).thenReturn(updatedUser);

        mockMvc.perform(put(USER_API + "/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.idx").value(updatedUser.getIdx()))
                .andExpect(jsonPath("$.user.name").value(updatedUser.getName()));
    }

    @WithMockUser(username = "test@test.com")
    @Test
    void deleteUser_Success() throws Exception {

        when(userService.deleteUser(anyLong())).thenReturn(true);

        mockMvc.perform(delete(USER_API + "/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

    }

    @WithMockUser(username = "test@test.com")
    @Test
    void testGetAllUsers_Success() throws Exception {
        List<User> mockUsers = List.of(
                User.builder().idx(1L).email("user1@test.com").name("User 1").build(),
                User.builder().idx(2L).email("user2@test.com").name("User 2").build()
        );

        when(userService.getAllUsers(0L, 10)).thenReturn(mockUsers);


        mockMvc.perform(get(USER_API)
                        .with(csrf())
                        .param("cursor", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].idx").value(1))
                .andExpect(jsonPath("$.users[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$.users[0].name").value("User 1"))
                .andExpect(jsonPath("$.users[1].idx").value(2))
                .andExpect(jsonPath("$.users[1].email").value("user2@test.com"))
                .andExpect(jsonPath("$.users[1].name").value("User 2"));

        verify(userService, times(1)).getAllUsers(0L, 10);
    }
}