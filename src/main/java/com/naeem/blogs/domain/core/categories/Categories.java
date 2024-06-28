package com.naeem.blogs.domain.core.categories;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.*;
import com.naeem.blogs.domain.core.postcategories.PostCategories;
import com.naeem.blogs.domain.core.abstractentity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.Config;
import org.hibernate.annotations.TypeDefs;


@Entity
@Config(defaultVariableName = "categoriesEntity")
@Table(name = "categories")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({
}) 
public class Categories extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include() 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    
    @Basic
    @Column(name = "description", nullable = true)
    private String description;

    @Basic
    @Column(name = "name", nullable = false,length =100)
    private String name;

    @OneToMany(mappedBy = "categories", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostCategories> postCategoriesSet = new HashSet<PostCategories>();
    
    public void addPostCategories(PostCategories postCategories) {        
    	postCategoriesSet.add(postCategories);

        postCategories.setCategories(this);
    }
    public void removePostCategories(PostCategories postCategories) {
        postCategoriesSet.remove(postCategories);
        
        postCategories.setCategories(null);
    }
    

}



