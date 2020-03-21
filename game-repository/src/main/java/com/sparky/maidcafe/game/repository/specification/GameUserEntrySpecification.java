package com.sparky.maidcafe.game.repository.specification;

import com.sparky.maidcafe.game.domain.GameUserEntry;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "id", spec = Equal.class),
        @Spec(path = "consoleId", params = "console-id", spec = Equal.class),
        @Spec(path = "gameId", params = "game-id", spec = Equal.class),
        @Spec(path = "userId", params = "user-id", spec = Equal.class),
        @Spec(path = "status", spec = Equal.class)
})
public interface GameUserEntrySpecification extends Specification<GameUserEntry> {
}
