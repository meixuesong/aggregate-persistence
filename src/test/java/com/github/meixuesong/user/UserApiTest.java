package com.github.meixuesong.user;


import com.github.meixuesong.ApiTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class UserApiTest extends ApiTest {

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        User user = new User("9999", "Mei", "18823780101", "Beijing");

        ResponseEntity<User> userResponseEntity = this.restTemplate.postForEntity(baseUrl + "/users", user, User.class);

        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userResponseEntity.getBody().getId()).isEqualTo("9999");
    }


}
