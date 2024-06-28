package com.naeem.blogs.domain.core.abstractentity;

import javax.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Version
    @EqualsAndHashCode.Include()
    @Column(name = "VERSIONO", nullable = false)
    private Long versiono;
    
}
