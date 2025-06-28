/**
 * User.java
 *
 * Represents a user entity for the Moodii application.
 * This class is mapped to the 'users' collection in MongoDB and stores user credentials and roles.
 *
 * @author [Bianca Bai]
 * @since 2025-06-28
 */

package com.moodii.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * Represents a user in the Moodii application.
 * This class is used to store user credentials and roles in the MongoDB database.
 * It includes fields for the user's ID, username, password, and role (Admin, User).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    /* Unique User ID */
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("role")
    private String role; //e.g. Admin, User
}