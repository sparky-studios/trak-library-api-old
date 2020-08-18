package com.traklibrary.authentication.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.traklibrary.authentication.domain")
public class AuthRepositoryTestConfiguration {
}
