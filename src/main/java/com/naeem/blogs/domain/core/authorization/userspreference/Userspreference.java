package com.naeem.blogs.domain.core.authorization.userspreference;
import javax.persistence.*;
import java.time.*;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "userspreferenceEntity")
@Table(name = "userspreference")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Userspreference extends AbstractEntity {

    @Basic
    @Column(name = "language", nullable = false,length =256)
    private String language;

    @Basic
    @Column(name = "theme", nullable = false,length =256)
    private String theme;

    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;


}



