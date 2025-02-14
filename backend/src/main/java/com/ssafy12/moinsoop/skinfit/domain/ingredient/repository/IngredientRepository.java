package com.ssafy12.moinsoop.skinfit.domain.ingredient.repository;

import com.ssafy12.moinsoop.skinfit.domain.cosmetic_ingredient.entity.CosmeticIngredient;
import com.ssafy12.moinsoop.skinfit.domain.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    // ✅ 성분 자동완성 검색 (10개 제한, 가나다순 → 영어순 정렬)
    @Query(value = """
        SELECT * FROM ingredient 
        WHERE LOWER(ingredient_name) LIKE LOWER(CONCAT('%', :query, '%'))
        AND status = 1
        ORDER BY 
            CASE WHEN ingredient_name REGEXP '^[ㄱ-ㅎ가-힣]' THEN 1 ELSE 2 END, 
            ingredient_name ASC
        LIMIT 10
    """, nativeQuery = true)
    List<Ingredient> findTop10ByIngredientNameContainingIgnoreCase(@Param("query") String query);



    // 전성분 조회
    @Query("SELECT ci FROM CosmeticIngredient ci " +
            "WHERE ci.cosmetic.cosmeticId = :cosmeticId " +
            "AND ci.ingredient.status = true " +
            "ORDER BY ci.sequence ASC")
    List<CosmeticIngredient> findIngredientsByCosmeticId(@Param("cosmeticId") Integer cosmeticId);



}