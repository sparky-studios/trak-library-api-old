package com.traklibrary.game.repository.specification;

import com.traklibrary.game.domain.Genre;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "id", spec = Equal.class),
        @Spec(path = "name", spec = Like.class)
})
public interface GenreSpecification extends Specification<Genre> {
}
