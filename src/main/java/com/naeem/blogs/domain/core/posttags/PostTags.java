package com.naeem.blogs.domain.core.posttags;
import javax.persistence.*;
import java.time.*;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.tags.Tags;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "postTagsEntity")
@Table(name = "post_tags")
@IdClass(PostTagsId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class PostTags extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "post_id", nullable = false)
    private Integer postId;
    
    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "tag_id", nullable = false)
    private Integer tagId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable=false, updatable=false)
    private Posts posts;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "tag_id", insertable=false, updatable=false)
    private Tags tags;


}



