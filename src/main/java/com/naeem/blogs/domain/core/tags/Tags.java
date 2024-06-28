package com.naeem.blogs.domain.core.tags;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.*;
import com.naeem.blogs.domain.core.posttags.PostTags;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "tagsEntity")
@Table(name = "tags")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Tags extends AbstractEntity {

    @Basic
    @Column(name = "name", nullable = false,length =100)
    private String name;

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Integer tagId;
    
    @OneToMany(mappedBy = "tags", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostTags> postTagsSet = new HashSet<PostTags>();
    
    public void addPostTags(PostTags postTags) {        
    	postTagsSet.add(postTags);

        postTags.setTags(this);
    }
    public void removePostTags(PostTags postTags) {
        postTagsSet.remove(postTags);
        
        postTags.setTags(null);
    }
    

}



