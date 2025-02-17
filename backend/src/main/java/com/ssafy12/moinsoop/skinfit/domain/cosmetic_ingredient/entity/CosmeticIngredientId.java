package com.ssafy12.moinsoop.skinfit.domain.cosmetic_ingredient.entity;

import java.io.Serializable;
import java.util.Objects;

public class CosmeticIngredientId implements Serializable {
    private Integer cosmeticId;
    private Integer ingredientId;

    // 기본 생성자
    public CosmeticIngredientId() {}

    // 인자 2개를 받는 생성자 추가
    public CosmeticIngredientId(Integer cosmeticId, Integer ingredientId) {
        this.cosmeticId = cosmeticId;
        this.ingredientId = ingredientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CosmeticIngredientId that = (CosmeticIngredientId) o;
        return Objects.equals(cosmeticId, that.cosmeticId) &&
                Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cosmeticId, ingredientId);
    }
}
