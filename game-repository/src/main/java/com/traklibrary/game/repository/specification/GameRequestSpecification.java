package com.traklibrary.game.repository.specification;

import com.traklibrary.game.domain.GameRequest;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "id", spec = Equal.class),
        @Spec(path = "title", spec = Like.class),
        @Spec(path = "completed", spec = Equal.class),
        @Spec(path = "completedDate", params = "completed-date", spec = Equal.class),
        @Spec(path = "userId", params = "user-id", spec = Equal.class)
})
public interface GameRequestSpecification extends Specification<GameRequest> {
}
