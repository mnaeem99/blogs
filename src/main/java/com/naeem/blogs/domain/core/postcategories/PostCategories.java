package com.naeem.blogs.domain.core.postcategories;
import javax.persistence.*;
import java.time.*;
import com.naeem.blogs.domain.core.categories.Categories;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "postCategoriesEntity")
@Table(name = "post_categories")
@IdClass(PostCategoriesId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class PostCategories extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    
    @Id
    @EqualsAndHashCode.Include() 
    @Column(name = "post_id", nullable = false)
    private Integer postId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable=false, updatable=false)
    private Categories categories;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable=false, updatable=false)
    private Posts posts;


}



