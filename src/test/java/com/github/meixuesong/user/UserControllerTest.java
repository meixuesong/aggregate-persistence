package com.github.meixuesong.user;


import com.github.meixuesong.ApiTest;
import com.github.meixuesong.common.Versionable;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerTest extends ApiTest {

    @Test
    public void should_save_new_user() throws Exception {
        User user = new User("9999", "Mei", "18823780101", "Beijing", Versionable.NEW_VERSION);

        ResponseEntity<User> userResponseEntity = this.restTemplate.postForEntity(baseUrl + "/users", user, User.class);

        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userResponseEntity.getBody().getId()).isEqualTo("9999");
    }

    @Test
    @Sql(scripts = "classpath:sql/user-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/user-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_query_user() {
        String newUserId = "NEW_USER";
        ResponseEntity<User> userResponseEntity = this.restTemplate.getForEntity(baseUrl + "/users/" + newUserId, User.class);

        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponseEntity.getBody().getId()).isEqualTo(newUserId);
    }

    @Test
    @Sql(scripts = "classpath:sql/user-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/user-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_update_user() {
        String newPhone = "010-9999";
        String userId = "TO_BE_UPDATE";
        UserChangePhoneRequest request = new UserChangePhoneRequest(userId, newPhone);

        this.restTemplate.put(baseUrl + "/users", request);

        ResponseEntity<User> userResponseEntity = this.restTemplate.getForEntity(baseUrl + "/users/" + userId, User.class);

        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponseEntity.getBody().getPhone()).isEqualTo(newPhone);
    }

    @Test
    @Sql(scripts = "classpath:sql/user-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/user-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_delete_user() {
        String userId = "TO_BE_DELETE";
        this.restTemplate.delete(baseUrl + "/users/"+userId);

        ResponseEntity<User> userResponseEntity = this.restTemplate.getForEntity(baseUrl + "/users/" + userId, User.class);
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
