package com.naeem.blogs.domain.core.users;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.*;
import com.naeem.blogs.domain.core.comments.Comments;
import com.naeem.blogs.domain.core.likes.Likes;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "usersEntity")
@Table(name = "users")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Users extends AbstractEntity {

    @Basic
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Basic
    @Column(name = "email", nullable = false,length =100)
    private String email;

    @Basic
    @Column(name = "password_hash", nullable = false,length =255)
    private String passwordHash;

    @Basic
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Basic
    @Column(name = "username", nullable = false,length =50)
    private String username;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comments> commentsSet = new HashSet<Comments>();
    
    public void addComments(Comments comments) {        
    	commentsSet.add(comments);

        comments.setUsers(this);
    }
    public void removeComments(Comments comments) {
        commentsSet.remove(comments);
        
        comments.setUsers(null);
    }
    
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Likes> likesSet = new HashSet<Likes>();
    
    public void addLikes(Likes likes) {        
    	likesSet.add(likes);

        likes.setUsers(this);
    }
    public void removeLikes(Likes likes) {
        likesSet.remove(likes);
        
        likes.setUsers(null);
    }
    
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Posts> postsSet = new HashSet<Posts>();
    
    public void addPosts(Posts posts) {        
    	postsSet.add(posts);

        posts.setUsers(this);
    }
    public void removePosts(Posts posts) {
        postsSet.remove(posts);
        
        posts.setUsers(null);
    }
    

}



