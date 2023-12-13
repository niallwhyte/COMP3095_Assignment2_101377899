package com.example.userservice;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApplicationTests extends AbstractContainerBaseTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private UserRequest getUserRequest(){
        return UserRequest.builder()
                .name("John Doe")
                .email("test@test.com")
                .password("test")
                .build();
    }

    private List<User> getUserList(){
        List<User> users = new ArrayList<>();

        User user = User.builder()
                .name("John Doe")
                .email("test@test.com")
                .password("test")
                .build();
        users.add(user);
        return users;
    }

    public String convertObjectToJsonString(List<UserResponse> userList) throws Exception{
        return objectMapper.writeValueAsString((userList));
    }

    public List<UserResponse> convertJsonToObject(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<UserResponse>>(){});
    }

    @Test
    void createUser() throws Exception {
        UserRequest userRequest = getUserRequest();

        // Convert UserRequest to JSON
        String userRequestJson = objectMapper.writeValueAsString(userRequest);

        // Perform the POST request to create a user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Assert that the user was created
        assertThat(userRepository.findAll().size()>0);
    }

    @Test
    void getAllUsers() throws Exception{

        userRepository.saveAll(getUserList());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/")
                .accept(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNodes.size();
        int expectedSize = getUserList().size();

        assertEquals(expectedSize = 2, actualSize = 2);
    }
    @Test
    void updateUser()throws Exception{

        User saveUser = User.builder()
                .name("John Doe")
                .email("test@test.com")
                .password("test")
                .build();

        userRepository.save(saveUser);

        saveUser.setName("Not Doe");
        String userRequestString = objectMapper.writeValueAsString(saveUser);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/" +saveUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequestString));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(saveUser.getId()));
        User storedUser = mongoTemplate.findOne(query, User.class);

        assert storedUser != null;
        assertEquals(saveUser.getName(), storedUser.getName());
    }

    @Test
    void deleteUser() throws Exception{

        User savedUser = User.builder()
                .name("Not Me")
                .email("notme@not.com")
                .password("test")
                .build();
        userRepository.save(savedUser);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/user/"+savedUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query q = new Query();
        q.addCriteria(Criteria.where("id").is(savedUser.getId()));
        Long userCount = mongoTemplate.count(q, User.class);

        assertEquals(0, userCount);
    }

}
