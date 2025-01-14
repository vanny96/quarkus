package io.quarkus.hibernate.validator.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.hibernate.validator.test.injection.TestBean;
import io.quarkus.hibernate.validator.test.injection.TestConstraint;
import io.quarkus.hibernate.validator.test.injection.TestInjectedBean;
import io.quarkus.hibernate.validator.test.injection.TestInjectionValidator;
import io.quarkus.test.QuarkusUnitTest;

public class ValidatorFromValidationTest {

    @RegisterExtension
    static final QuarkusUnitTest test = new QuarkusUnitTest().setArchiveProducer(() -> ShrinkWrap
            .create(JavaArchive.class)
            .addClasses(TestBean.class, TestConstraint.class, TestInjectedBean.class, TestInjectionValidator.class));

    @Test
    public void testValidationWithInjection() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<TestBean>> constraintViolations = validator.validate(new TestBean());

        assertThat(constraintViolations).isNotEmpty();

        TestBean bean = new TestBean();
        bean.name = "Alpha";
        constraintViolations = validator.validate(bean);
        assertThat(constraintViolations).isEmpty();
    }

}
