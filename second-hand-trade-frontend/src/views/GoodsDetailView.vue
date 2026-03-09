<template>
  <div class="goods-detail-view">
    <!-- 导航栏 -->
    <NavBar />

    <!-- 主要内容区 -->
    <main class="main-content">
      <div class="container">
        <div v-loading="loading" class="detail-wrapper">
          <!-- 商品图片区 -->
          <div class="goods-gallery">
            <div class="main-image">
              <el-image
                :src="currentImage"
                class="image"
                fit="cover"
                :preview-src-list="imageList"
              />
            </div>
            <div
              v-if="goods.images && goods.images.length > 1"
              class="thumbnail-list"
            >
              <div
                v-for="(image, index) in goods.images"
                :key="image.id"
                :class="[
                  'thumbnail-item',
                  { active: currentImage === image.imageUrl },
                ]"
                @click="currentImage = image.imageUrl"
              >
                <el-image :src="image.imageUrl" class="thumbnail" fit="cover" />
              </div>
            </div>
          </div>

          <!-- 商品信息区 -->
          <div class="goods-info-section">
            <h1 class="goods-title">{{ goods.name }}</h1>

            <div class="goods-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ goods.viewCount }} 次浏览
              </span>
              <span class="meta-item">
                <el-icon><ShoppingBag /></el-icon>
                {{ goods.salesCount }} 件销量
              </span>
              <span class="meta-item">
                <el-icon><Star /></el-icon>
                {{ goods.favoriteCount }} 人收藏
              </span>
            </div>

            <!-- 价格区 -->
            <div class="price-section">
              <div class="price">
                <span class="price-label">价格</span>
                <span class="price-current">¥{{ goods.price }}</span>
                <span v-if="goods.originalPrice" class="price-original">
                  ¥{{ goods.originalPrice }}
                </span>
              </div>
            </div>

            <!-- 文化标签 -->
            <div
              v-if="goods.tags && goods.tags.length > 0"
              class="tags-section"
            >
              <span class="section-label">文化标签</span>
              <div class="tag-list">
                <el-tag
                  v-for="tag in goods.tags"
                  :key="tag.id"
                  size="small"
                  effect="plain"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>

            <!-- SKU 选择 -->
            <div v-if="goods.hasSku" class="sku-section">
              <span class="section-label">选择</span>
              <div class="sku-options">
                <el-button
                  v-for="sku in skus"
                  :key="sku.id"
                  :type="selectedSku?.id === sku.id ? 'primary' : ''"
                  @click="selectSku(sku)"
                >
                  {{ sku.name }}
                </el-button>
              </div>
            </div>

            <!-- 数量选择 -->
            <div class="quantity-section">
              <span class="section-label">数量</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="goods.stock"
                :step="1"
              />
              <span class="stock-info">库存：{{ goods.stock }}</span>
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button size="large" @click="handleAddCart">
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button type="primary" size="large" @click="handleBuyNow">
                立即购买
              </el-button>
              <FavoriteButton
                targetType="product"
                :targetId="goods.id"
                @change="handleFavoriteChange"
              />
              <el-button circle @click="handleShare">
                <el-icon><Share /></el-icon>
              </el-button>
            </div>

            <!-- 卖家信息 -->
            <div class="seller-info">
              <div class="seller-header">
                <el-avatar :size="40" :src="goods.shopLogo">
                  {{ goods.shopName?.charAt(0) }}
                </el-avatar>
                <div class="seller-name">
                  {{ goods.shopName || goods.sellerName }}
                </div>
              </div>
              <div class="seller-stats">
                <span class="stat-item">
                  <el-icon><User /></el-icon>
                  {{ goods.sellerName }}
                </span>
              </div>
              <el-button size="small" @click="visitShop"> 进入店铺 </el-button>
            </div>
          </div>
        </div>

        <!-- 文化信息区 -->
        <div v-if="goods.culturalInfo" class="cultural-section">
          <h2 class="section-title">文化背景</h2>
          <div class="cultural-content">
            <p v-if="goods.culturalInfo.culturalBackground">
              {{ goods.culturalInfo.culturalBackground }}
            </p>
            <p v-if="goods.culturalInfo.craftDescription">
              <strong>工艺说明：</strong
              >{{ goods.culturalInfo.craftDescription }}
            </p>
            <p v-if="goods.culturalInfo.designer">
              <strong>设计师/传承人：</strong>{{ goods.culturalInfo.designer }}
            </p>
            <p v-if="goods.culturalInfo.originPlace">
              <strong>产地：</strong>{{ goods.culturalInfo.originPlace }}
            </p>
            <p v-if="goods.culturalInfo.material">
              <strong>材质：</strong>{{ goods.culturalInfo.material }}
            </p>
            <p v-if="goods.culturalInfo.culturalIp">
              <strong>文化 IP：</strong>{{ goods.culturalInfo.culturalIp }}
            </p>
          </div>
        </div>

        <!-- 商品详情 -->
        <div class="detail-section">
          <h2 class="section-title">商品详情</h2>
          <div class="detail-content" v-html="goods.description"></div>
        </div>

        <!-- 用户评价 -->
        <div class="review-section">
          <div class="review-header">
            <h2 class="section-title">用户评价</h2>
            <div class="review-summary">
              <el-button
                v-if="isLoggedIn"
                type="primary"
                size="small"
                @click="showReviewForm = true"
              >
                发表评价
              </el-button>
            </div>
          </div>
          <ReviewList :productId="goods.id" />
        </div>

        <!-- 评价表单 -->
        <ReviewForm
          v-model="showReviewForm"
          :productId="goods.id"
          :orderId="0"
          @success="loadGoodsDetail"
        />

        <!-- 相关推荐 -->
        <div class="recommend-section">
          <h2 class="section-title">相关推荐</h2>
          <div class="recommend-list">
            <GoodsCard
              v-for="item in recommendGoods"
              :key="item.id"
              :goods="item"
            />
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  View,
  ShoppingBag,
  Star,
  ShoppingCart,
  Share,
  User,
} from "@element-plus/icons-vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import GoodsCard from "@/components/GoodsCard.vue";
import ReviewList from "@/components/ReviewList.vue";
import ReviewForm from "@/components/ReviewForm.vue";
import FavoriteButton from "@/components/FavoriteButton.vue";
import { getGoodsDetail, getRecommendGoods } from "@/api/modules/goods";
import { addToCart } from "@/api/modules/cart";
import { useUserStore } from "@/store/user";
import type { Goods, ProductImage } from "@/types";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const goods = ref<Goods>({} as Goods);
const recommendGoods = ref<Goods[]>([]);
const skus = ref<any[]>([]);
const selectedSku = ref<any>(null);
const quantity = ref(1);
const currentImage = ref("");
const showReviewForm = ref(false);

const isLoggedIn = computed(() => userStore.isLoggedIn);

const imageList = computed(() => {
  return goods.value.images?.map((img) => img.imageUrl) || [];
});

// 加载商品详情
const loadGoodsDetail = async () => {
  loading.value = true;
  try {
    const res = await getGoodsDetail(Number(route.params.id));
    goods.value = res.data;

    if (goods.value.images && goods.value.images.length > 0) {
      currentImage.value = goods.value.images[0].imageUrl;
    }

    // TODO: 加载 SKU 信息
    // if (goods.value.hasSku) {
    //   await loadSkus()
    // }
  } catch (error) {
    console.error("加载商品详情失败", error);
    ElMessage.error("商品不存在或已下架");
    router.push("/goods");
  } finally {
    loading.value = false;
  }
};

// 加载推荐商品
const loadRecommendGoods = async () => {
  try {
    const res = await getRecommendGoods(4);
    recommendGoods.value = res.data;
  } catch (error) {
    console.error("加载推荐商品失败", error);
  }
};

// 选择 SKU
const selectSku = (sku: any) => {
  selectedSku.value = sku;
};

// 加入购物车
const handleAddCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  try {
    await addToCart(goods.value.id, quantity.value);
    ElMessage.success("已添加到购物车");
  } catch (error) {
    console.error("添加购物车失败:", error);
    ElMessage.error("添加购物车失败");
  }
};

// 立即购买
const handleBuyNow = () => {
  // TODO: 跳转到订单确认页
  ElMessage.info("功能开发中...");
};

// 收藏商品
const handleFavoriteChange = (favorited: boolean, count: number) => {
  console.log("收藏状态变化:", favorited, count);
};

// 分享商品
const handleShare = () => {
  // TODO: 分享功能
  ElMessage.info("分享功能开发中...");
};

// 访问店铺
const visitShop = () => {
  router.push({
    path: "/goods",
    query: { sellerId: goods.value.sellerId },
  });
};

onMounted(() => {
  loadGoodsDetail();
  loadRecommendGoods();
});
</script>

<style lang="scss" scoped>
.goods-detail-view {
  min-height: 100vh;
  background-color: $bg-page;
}

.main-content {
  padding: $spacing-extra-large 0;
}

.detail-wrapper {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: $spacing-extra-large;
  background-color: $bg-white;
  padding: $spacing-extra-large;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow-light;
}

.goods-gallery {
  .main-image {
    width: 100%;
    aspect-ratio: 1;
    border-radius: $border-radius-large;
    overflow: hidden;
    margin-bottom: $spacing-base;

    .image {
      width: 100%;
      height: 100%;
    }
  }

  .thumbnail-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-small;

    .thumbnail-item {
      aspect-ratio: 1;
      border-radius: $border-radius-base;
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      transition: $transition-fast;

      &:hover,
      &.active {
        border-color: $primary-color;
      }

      .thumbnail {
        width: 100%;
        height: 100%;
      }
    }
  }
}

.goods-info-section {
  display: flex;
  flex-direction: column;
  gap: $spacing-large;
}

.goods-title {
  font-size: $font-size-extra-large;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.5;
}

.goods-meta {
  display: flex;
  gap: $spacing-large;
  font-size: $font-size-small;
  color: $text-secondary;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      font-size: 14px;
    }
  }
}

.price-section {
  background-color: rgba($danger-color, 0.1);
  padding: $spacing-large;
  border-radius: $border-radius-large;

  .price {
    display: flex;
    align-items: baseline;
    gap: $spacing-base;

    .price-label {
      font-size: $font-size-base;
      color: $text-regular;
    }

    .price-current {
      font-size: 32px;
      font-weight: 700;
      color: $danger-color;
    }

    .price-original {
      font-size: $font-size-base;
      color: $text-secondary;
      text-decoration: line-through;
    }
  }
}

.tags-section,
.sku-section,
.quantity-section {
  .section-label {
    display: block;
    font-size: $font-size-base;
    color: $text-regular;
    margin-bottom: $spacing-small;
  }
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-small;
}

.sku-options {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-small;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: $spacing-base;

  .stock-info {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.action-buttons {
  display: flex;
  gap: $spacing-base;
  padding: $spacing-large 0;
  border-top: 1px solid $border-lighter;
  border-bottom: 1px solid $border-lighter;
}

.seller-info {
  background-color: $bg-page;
  padding: $spacing-large;
  border-radius: $border-radius-large;

  .seller-header {
    display: flex;
    align-items: center;
    gap: $spacing-small;
    margin-bottom: $spacing-small;

    .seller-name {
      font-size: $font-size-base;
      font-weight: 500;
      color: $text-primary;
    }
  }

  .seller-stats {
    margin-bottom: $spacing-small;
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.cultural-section,
.detail-section,
.review-section,
.recommend-section {
  margin-top: $spacing-extra-large;
  background-color: $bg-white;
  padding: $spacing-extra-large;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow-light;
}

.section-title {
  font-size: $font-size-extra-large;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: $spacing-large;
}

.cultural-content {
  font-size: $font-size-base;
  line-height: 1.8;
  color: $text-regular;

  p {
    margin-bottom: $spacing-base;
  }

  strong {
    color: $text-primary;
  }
}

.detail-content {
  font-size: $font-size-base;
  line-height: 1.8;
  color: $text-regular;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-large;

  .review-summary {
    .rating {
      font-size: $font-size-base;
      color: $success-color;
    }
  }
}

.recommend-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-large;
}

// 响应式设计
@media (max-width: 1200px) {
  .detail-wrapper {
    grid-template-columns: 1fr;
  }

  .goods-gallery {
    max-width: 500px;
    margin: 0 auto;
  }

  .recommend-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .recommend-list {
    grid-template-columns: 1fr;
  }
}
</style>
