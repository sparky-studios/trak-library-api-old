package com.sparkystudios.traklibrary.authentication.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.sparkystudios.traklibrary.authentication.domain")
public class AuthRepositoryTestConfiguration {
}
