package com.naeem.blogs.domain.core.comments;
import javax.persistence.*;
import java.time.*;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "commentsEntity")
@Table(name = "comments")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Comments extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Integer commentId;
    
    @Basic
    @Column(name = "content", nullable = false)
    private String content;

    @Basic
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts posts;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Users users;


}



