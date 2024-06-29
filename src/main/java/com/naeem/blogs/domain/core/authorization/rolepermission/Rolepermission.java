package com.naeem.blogs.domain.core.authorization.rolepermission;
import javax.persistence.*;
import java.time.*;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "rolepermissionEntity")
@Table(name = "rolepermission")
@IdClass(RolepermissionId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Rolepermission extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;
    
    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "role_id", nullable = false)
    private Long roleId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable=false, updatable=false)
    private Permission permission;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable=false, updatable=false)
    private Role role;


}



