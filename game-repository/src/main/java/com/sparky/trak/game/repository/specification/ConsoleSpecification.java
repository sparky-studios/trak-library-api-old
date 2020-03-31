package com.sparky.trak.game.repository.specification;

import com.sparky.trak.game.domain.Console;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "id", spec = Equal.class),
        @Spec(path = "name", spec = Like.class),
        @Spec(path = "releaseDate", params = "release-date", spec = Equal.class)
})
public interface ConsoleSpecification extends Specification<Console> {
}
