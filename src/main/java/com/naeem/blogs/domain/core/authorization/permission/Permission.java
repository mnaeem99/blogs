package com.naeem.blogs.domain.core.authorization.permission;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.*;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;
import com.naeem.blogs.domain.core.authorization.userspermission.Userspermission;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "permissionEntity")
@Table(name = "permission")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Permission extends AbstractEntity {

    @Basic
    @Column(name = "display_name", nullable = false,length =255)
    private String displayName;

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Basic
    @Column(name = "name", nullable = false,length =255)
    private String name;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rolepermission> rolepermissionsSet = new HashSet<Rolepermission>();
    
    public void addRolepermissions(Rolepermission rolepermissions) {        
    	rolepermissionsSet.add(rolepermissions);

        rolepermissions.setPermission(this);
    }
    public void removeRolepermissions(Rolepermission rolepermissions) {
        rolepermissionsSet.remove(rolepermissions);
        
        rolepermissions.setPermission(null);

    }
    
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Userspermission> userspermissionsSet = new HashSet<Userspermission>();
    
    public void addUserspermissions(Userspermission userspermissions) {        
    	userspermissionsSet.add(userspermissions);

        userspermissions.setPermission(this);
    }
    public void removeUserspermissions(Userspermission userspermissions) {
        userspermissionsSet.remove(userspermissions);
        
        userspermissions.setPermission(null);

    }
    

}



