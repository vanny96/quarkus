package io.quarkus.hibernate.search.orm.elasticsearch.test.configuration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

/**
 * An indexed entity.
 */
@Entity
@Indexed(backend = "mybackend")
public class IndexedEntityInNamedBackend {

    @Id
    @GeneratedValue
    public Long id;

    @FullTextField
    public String name;

    protected IndexedEntityInNamedBackend() {
    }

    public IndexedEntityInNamedBackend(String name) {
        this.name = name;
    }

}
