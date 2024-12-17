package com.popytka.popytka.repos;

import com.popytka.popytka.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User getById(Long id);
    @Query("SELECT u.isadmin FROM User u WHERE u.id = ?1")
    boolean getRole(Long id);
    @Modifying(clearAutomatically = true)
    @Query("insert into User (first_name, last_name, phone, email, password) VALUES (:first_name, :last_name, :phone, :email, :password)")
    void addUser(@Param("first_name") String first_name, @Param("last_name") String last_name,
                 @Param("phone") String phone, @Param("email") String email, @Param("password") String password);

    @Query("select case when count(u) > 0 then true else false end from User as u where email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("select userId from User as u where email = :email")
    Long getUsersIdByEmail(@Param("email") String email);

    @Query("select u from User as u")
    List<User> getAllUser();
    @Query("select userId from User as u where phone = :phone")
    Long getUsersIdByPhone(@Param("phone") String phone);
    @Query("select password from User as u where email = :email")
    String getPasswordUserByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("update User set first_name = :first_name, last_name = :last_name, phone = :phone, email = :email where userId= :id")
    void updateUser(@Param("id") long id, @Param("first_name") String first_name, @Param("last_name") String last_name, @Param("phone") String phone, @Param("email") String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User set password = :password where userId = :id")
    void setPasswordById(@Param("id") Long id, @Param("password") String password);

    @Query("select u from User as u where phone = :phone")
    User getUserByPhone(@Param("phone") String phone);
//    @Query("SELECT u FROM User u JOIN Orders o ON u.user_id = o.user_id WHERE o.order_id = :order_id")
//    User getUserByOrderId(@Param("order_id") Long order_id);
}
