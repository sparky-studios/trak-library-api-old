package com.sparky.trak.game.repository.specification;

import com.sparky.trak.game.domain.Company;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "id", spec = Equal.class),
        @Spec(path = "name", spec = Like.class),
        @Spec(path = "companyType", params = "company-type", spec = Equal.class)
})
public interface CompanySpecification extends Specification<Company> {
}
