<template>
  <div class="goods-card" @click="handleClick">
    <!-- 商品图片 -->
    <div class="goods-image-wrapper">
      <el-image
        :src="goods.imageUrl || goods.images?.[0]?.imageUrl || '/placeholder.png'"
        :alt="goods.name"
        class="goods-image"
        fit="cover"
        lazy
      >
        <template #placeholder>
          <div class="image-loading">
            <el-icon><Loading /></el-icon>
          </div>
        </template>
      </el-image>

      <!-- 商品标签 -->
      <div v-if="goods.tags?.length" class="goods-tags">
        <el-tag
          v-for="tag in goods.tags.slice(0, 2)"
          :key="tag.id"
          size="small"
          class="goods-tag"
        >
          {{ tag.name }}
        </el-tag>
      </div>

      <!-- 操作按钮 -->
      <div class="goods-actions">
        <div @click.stop>
          <FavoriteButton
            targetType="product"
            :targetId="goods.id"
            :initialCount="goods.favoriteCount"
            circle
            size="small"
          />
        </div>
        <el-button circle size="small" @click.stop="handleAddCart">
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="goods-info">
      <h3 class="goods-name" :title="goods.name">
        {{ goods.name }}
      </h3>

      <div class="goods-meta">
        <div class="goods-price">
          <span class="price-current">¥{{ goods.price }}</span>
          <span v-if="goods.originalPrice" class="price-original">
            ¥{{ goods.originalPrice }}
          </span>
        </div>

        <div class="goods-stats">
          <span class="stat-item">
            <el-icon><ShoppingBag /></el-icon>
            {{ goods.salesCount }}
          </span>
          <span v-if="goods.viewCount" class="stat-item">
            <el-icon><View /></el-icon>
            {{ goods.viewCount }}
          </span>
          <span class="stat-item">
            <el-icon><Star /></el-icon>
            {{ goods.favoriteCount }}
          </span>
        </div>
      </div>

      <div class="goods-seller">
        <el-avatar :size="20" :src="goods.shopLogo">
          {{ (goods.shopName || goods.sellerName)?.charAt(0) }}
        </el-avatar>
        <span class="seller-name">{{
          goods.shopName || goods.sellerName
        }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  Loading,
  Star,
  ShoppingCart,
  ShoppingBag,
  View,
} from "@element-plus/icons-vue";
import FavoriteButton from "@/components/FavoriteButton.vue";
import type { Goods } from "@/types";

interface Props {
  goods: Goods;
}

const props = defineProps<Props>();
const router = useRouter();

// 点击商品卡片
const handleClick = () => {
  router.push(`/goods/${props.goods.id}`);
};

// 加入购物车
const handleAddCart = () => {
  ElMessage.success("已添加到购物车");
  // TODO: 调用购物车 API
};
</script>

<style lang="scss" scoped>
.goods-card {
  background-color: $bg-white;
  border-radius: $border-radius-large;
  overflow: hidden;
  transition: $transition-base;
  cursor: pointer;
  box-shadow: $box-shadow-light;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $box-shadow-base;
  }
}

.goods-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%; // 1:1 比例
  overflow: hidden;
  background-color: $bg-page;

  .goods-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  .image-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    font-size: 24px;
    color: $text-secondary;
  }

  .goods-tags {
    position: absolute;
    top: $spacing-small;
    left: $spacing-small;
    display: flex;
    gap: $spacing-small;
  }

  .goods-tag {
    background-color: rgba($primary-color, 0.8);
    color: #ffffff;
    border: none;
  }

  .goods-actions {
    position: absolute;
    top: $spacing-small;
    right: $spacing-small;
    display: flex;
    flex-direction: column;
    gap: $spacing-small;
    opacity: 0;
    transition: $transition-fast;

    .el-button {
      background-color: rgba(255, 255, 255, 0.9);
      border: none;
      box-shadow: $box-shadow-light;

      &:hover {
        background-color: $primary-color;
        color: #ffffff;
      }
    }
  }
}

.goods-image-wrapper:hover .goods-actions {
  opacity: 1;
}

.goods-info {
  padding: $spacing-base;
}

.goods-name {
  font-size: $font-size-base;
  font-weight: 500;
  color: $text-primary;
  margin-bottom: $spacing-small;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  min-height: 42px;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-small;
}

.goods-price {
  .price-current {
    font-size: $font-size-large;
    font-weight: 600;
    color: $danger-color;
  }

  .price-original {
    font-size: $font-size-small;
    color: $text-secondary;
    text-decoration: line-through;
    margin-left: $spacing-small;
  }
}

.goods-stats {
  display: flex;
  gap: $spacing-small;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: $font-size-extra-small;
    color: $text-secondary;

    .el-icon {
      font-size: 12px;
    }
  }
}

.goods-seller {
  display: flex;
  align-items: center;
  gap: $spacing-small;
  padding-top: $spacing-small;
  border-top: 1px solid $border-lighter;

  .seller-name {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}
</style>
