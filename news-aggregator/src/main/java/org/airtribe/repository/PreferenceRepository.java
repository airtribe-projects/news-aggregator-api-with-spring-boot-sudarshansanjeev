package org.airtribe.repository;

import org.airtribe.model.Preferences;
import org.airtribe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PreferenceRepository extends JpaRepository<Preferences, Long> {

    Optional<Preferences> findByUser(User user);
}
