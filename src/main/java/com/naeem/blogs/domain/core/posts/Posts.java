package com.naeem.blogs.domain.core.posts;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.*;
import com.naeem.blogs.domain.core.comments.Comments;
import com.naeem.blogs.domain.core.likes.Likes;
import com.naeem.blogs.domain.core.postcategories.PostCategories;
import com.naeem.blogs.domain.core.posttags.PostTags;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "postsEntity")
@Table(name = "posts")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Posts extends AbstractEntity {

    @Basic
    @Column(name = "content", nullable = false)
    private String content;

    @Basic
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Integer postId;
    
    @Basic
    @Column(name = "title", nullable = false,length =255)
    private String title;

    @Basic
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comments> commentsSet = new HashSet<Comments>();
    
    public void addComments(Comments comments) {        
    	commentsSet.add(comments);

        comments.setPosts(this);
    }
    public void removeComments(Comments comments) {
        commentsSet.remove(comments);
        
        comments.setPosts(null);
    }
    
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Likes> likesSet = new HashSet<Likes>();
    
    public void addLikes(Likes likes) {        
    	likesSet.add(likes);

        likes.setPosts(this);
    }
    public void removeLikes(Likes likes) {
        likesSet.remove(likes);
        
        likes.setPosts(null);
    }
    
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostCategories> postCategoriesSet = new HashSet<PostCategories>();
    
    public void addPostCategories(PostCategories postCategories) {        
    	postCategoriesSet.add(postCategories);

        postCategories.setPosts(this);
    }
    public void removePostCategories(PostCategories postCategories) {
        postCategoriesSet.remove(postCategories);
        
        postCategories.setPosts(null);
    }
    
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostTags> postTagsSet = new HashSet<PostTags>();
    
    public void addPostTags(PostTags postTags) {        
    	postTagsSet.add(postTags);

        postTags.setPosts(this);
    }
    public void removePostTags(PostTags postTags) {
        postTagsSet.remove(postTags);
        
        postTags.setPosts(null);
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Users users;


}



