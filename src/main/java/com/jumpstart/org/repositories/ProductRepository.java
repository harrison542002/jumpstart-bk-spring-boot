package com.jumpstart.org.repositories;

import com.jumpstart.org.models.Product;
import org.hibernate.internal.util.StringHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT product FROM Product as product WHERE " +
            "(COALESCE( null, :brand ) is null or :brand LIKE CONCAT('%',product.brand.brandName,'%')) AND " +
            "(COALESCE( null, :category ) is null or :category LIKE CONCAT('%',product.category,'%')) AND" +
            "(COALESCE( null, :lowPrice ) is null or product.price >= :lowPrice) AND " +
            "(COALESCE( null, :highPrice ) is null or product.price <= :highPrice)"
    )
    Page<Product> findByPriceBetween(Pageable pageable,
                                     @Param("brand") String brand,
                                     @Param("category") String category,
                                     @Param("lowPrice") Integer lowPrice,
                                     @Param("highPrice") Integer highPrice
                                   );
}
