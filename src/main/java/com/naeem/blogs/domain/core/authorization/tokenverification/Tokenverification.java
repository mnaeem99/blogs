package com.naeem.blogs.domain.core.authorization.tokenverification;
import javax.persistence.*;
import java.time.*;
import java.util.Date;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "tokenverificationEntity")
@Table(name = "tokenverification")
@IdClass(TokenverificationId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Tokenverification extends AbstractEntity {

    @Basic
    @Column(name = "expiration_time", nullable = true)
    private Date expirationTime;

    @Basic
    @Column(name = "token", nullable = true,length =512)
    private String token;

    @Id
    @EqualsAndHashCode.Include()
    @Column(name = "token_type", nullable = false,length =256)
    private String tokenType;

    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "users_user_id", nullable = false)
    private Long usersUserId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "users_user_id", insertable=false, updatable=false)
    private Users users;


}



