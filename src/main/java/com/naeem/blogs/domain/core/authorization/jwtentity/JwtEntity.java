package com.naeem.blogs.domain.core.authorization.jwtentity;
import javax.persistence.*;
import java.time.*;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "jwtEntityEntity")
@Table(name = "jwt_entity")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class JwtEntity extends AbstractEntity {

    @Basic
    @Column(name = "authentication_token", nullable = true)
    private String authenticationToken;

    @Basic
    @Column(name = "authorization_token", nullable = false)
    private String authorizationToken;

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Basic
    @Column(name = "user_name", nullable = false,length =32)
    private String userName;


}



