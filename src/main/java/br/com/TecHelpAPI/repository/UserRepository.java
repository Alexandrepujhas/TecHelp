package br.com.TecHelpAPI.repository;

import br.com.TecHelpAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNameUser(String nameUser);

    // Nova consulta otimizada para verificação rápida de existência
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

    // Nova consulta para login seguro
    @Query("SELECT u.password FROM User u WHERE u.nameUser = :nameUser")
    Optional<String> findPasswordByNameUser(String nameUser);
}