<template>
  <div class="home-view">
    <!-- 导航栏 -->
    <NavBar />

    <!-- 主要内容区 -->
    <main class="main-content">
      <div class="container">
        <!-- 轮播图区域 -->
        <section class="banner-section">
          <el-carousel
            height="400px"
            class="banner-carousel"
            :autoplay="true"
            :interval="5000"
          >
            <el-carousel-item v-for="item in banners" :key="item.id">
              <div
                class="banner-item"
                :style="{ backgroundImage: `url(${item.image})` }"
              >
                <div class="banner-content">
                  <h2 class="banner-title">{{ item.title }}</h2>
                  <p class="banner-desc">{{ item.description }}</p>
                  <el-button
                    type="primary"
                    size="large"
                    @click="handleBannerClick(item)"
                  >
                    {{ item.buttonText }}
                  </el-button>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </section>

        <!-- 文化分类导航 -->
        <section class="category-section">
          <div class="section-header">
            <h2 class="section-title">文化分类</h2>
            <router-link to="/goods" class="more-link">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="category-grid">
            <div
              v-for="category in categories"
              :key="category.id"
              class="category-item"
              @click="handleCategoryClick(category)"
            >
              <div class="category-icon">
                <el-icon :size="40">
                  <component :is="category.icon || 'Grid'" />
                </el-icon>
              </div>
              <span class="category-name">{{ category.name }}</span>
            </div>
          </div>
        </section>

        <!-- 热门推荐 -->
        <section class="recommend-section">
          <div class="section-header">
            <h2 class="section-title">热门推荐</h2>
            <router-link to="/goods" class="more-link">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div v-loading="recommendLoading" class="goods-grid">
            <GoodsCard
              v-for="goods in recommendGoods"
              :key="goods.id"
              :goods="goods"
            />
          </div>
        </section>

        <!-- 热门分类 -->
        <section class="hot-categories-section">
          <div class="section-header">
            <h2 class="section-title">热门文化标签</h2>
            <router-link to="/goods" class="more-link">
              更多标签 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div v-loading="tagsLoading" class="tag-cloud">
            <el-tag
              v-for="tag in hotTags"
              :key="tag.id"
              :size="tag.isHot ? 'large' : 'default'"
              :type="tag.isHot ? 'warning' : ''"
              effect="plain"
              class="tag-item"
              @click="handleTagClick(tag)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </section>

        <!-- 文化资讯 -->
        <section class="cultural-section">
          <div class="section-header">
            <h2 class="section-title">文化资讯</h2>
            <router-link to="/cultural" class="more-link">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div v-loading="contentLoading" class="cultural-grid">
            <div
              v-for="content in culturalContents"
              :key="content.id"
              class="cultural-item"
              @click="handleContentClick(content)"
            >
              <el-image
                :src="content.coverImage || '/placeholder.jpg'"
                class="cultural-image"
                fit="cover"
                lazy
              >
                <template #placeholder>
                  <div class="image-loading">
                    <el-icon><Loading /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="cultural-info">
                <h3 class="cultural-title">{{ content.title }}</h3>
                <p class="cultural-summary">{{ content.summary }}</p>
                <div class="cultural-meta">
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ content.viewCount }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Star /></el-icon>
                    {{ content.likeCount }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(content.createdAt) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  ArrowRight,
  View,
  Star,
  Clock,
  Loading,
} from "@element-plus/icons-vue";
import dayjs from "dayjs";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import GoodsCard from "@/components/GoodsCard.vue";
import { getRecommendGoods } from "@/api/modules/goods";
import { getCategoryTree } from "@/api/modules/category";
import { getHotTags } from "@/api/modules/tag";
import { getRecommendContents } from "@/api/modules/content";
import type { Goods, Category, CulturalTag, CulturalContent } from "@/types";

const router = useRouter();

const recommendLoading = ref(false);
const tagsLoading = ref(false);
const contentLoading = ref(false);

// 轮播图数据
const banners = ref([
  {
    id: 1,
    image: "https://picsum.photos/1920/600?random=1",
    title: "河南非遗文化",
    description: "传承千年工艺，感受中原文化魅力",
    buttonText: "立即探索",
  },
  {
    id: 2,
    image: "https://picsum.photos/1920/600?random=2",
    title: "博物馆文创",
    description: "把博物馆带回家",
    buttonText: "选购好物",
  },
  {
    id: 3,
    image: "https://picsum.photos/1920/600?random=3",
    title: "传统工艺品",
    description: "匠心独运，精品呈现",
    buttonText: "查看详情",
  },
]);

const categories = ref<Category[]>([]);
const recommendGoods = ref<Goods[]>([]);
const hotTags = ref<CulturalTag[]>([]);
const culturalContents = ref<CulturalContent[]>([]);

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format("YYYY-MM-DD");
};

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getCategoryTree();
    console.log("分类数据:", res);
    // res 已经是 Category[] 类型
    if (Array.isArray(res)) {
      categories.value = res.slice(0, 5);
    } else {
      console.warn("分类数据格式异常:", res);
      categories.value = [];
    }
  } catch (error) {
    console.error("加载分类失败", error);
    categories.value = [];
  }
};

// 加载推荐商品
const loadRecommendGoods = async () => {
  recommendLoading.value = true;
  try {
    const res = await getRecommendGoods(8);
    recommendGoods.value = res;
  } catch (error) {
    console.error("加载推荐商品失败", error);
  } finally {
    recommendLoading.value = false;
  }
};

// 加载热门标签
const loadHotTags = async () => {
  tagsLoading.value = true;
  try {
    const res = await getHotTags(15);
    hotTags.value = Array.isArray(res) ? res : [];
  } catch (error) {
    console.error("加载标签失败", error);
    hotTags.value = [];
  } finally {
    tagsLoading.value = false;
  }
};

// 加载文化资讯
const loadCulturalContents = async () => {
  contentLoading.value = true;
  try {
    const res = await getRecommendContents({ current: 1, size: 4 });
    console.log("文化资讯响应:", res);
    if (res && res.records) {
      culturalContents.value = res.records;
    }
  } catch (error) {
    console.error("加载文化资讯失败", error);
  } finally {
    contentLoading.value = false;
  }
};

// 轮播图点击
const handleBannerClick = (item: any) => {
  router.push("/goods");
};

// 分类点击
const handleCategoryClick = (category: Category) => {
  router.push({
    path: "/goods",
    query: { categoryId: category.id },
  });
};

// 标签点击
const handleTagClick = (tag: CulturalTag) => {
  router.push({
    path: "/goods",
    query: { tagId: tag.id },
  });
};

// 资讯点击
const handleContentClick = (content: CulturalContent) => {
  router.push(`/cultural-content/${content.id}`);
};

onMounted(() => {
  loadCategories();
  loadRecommendGoods();
  loadHotTags();
  loadCulturalContents();
});
</script>

<style lang="scss" scoped>
.home-view {
  min-height: 100vh;
  background-color: $bg-page;
}

.main-content {
  padding: $spacing-extra-large 0;
}

.banner-section {
  margin-bottom: $spacing-extra-large * 2;

  .banner-carousel {
    border-radius: $border-radius-large;
    overflow: hidden;
    box-shadow: $box-shadow-base;
  }

  .banner-item {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;

    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(
        to bottom,
        rgba(0, 0, 0, 0.3),
        rgba(0, 0, 0, 0.5)
      );
    }
  }

  .banner-content {
    position: relative;
    z-index: 1;
    text-align: center;
    color: #ffffff;
    max-width: 600px;
    padding: $spacing-large;
  }

  .banner-title {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: $spacing-base;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  .banner-desc {
    font-size: $font-size-large;
    margin-bottom: $spacing-large;
    opacity: 0.9;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }
}

.category-section {
  margin-bottom: $spacing-extra-large * 2;

  .category-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: $spacing-large;
  }

  .category-item {
    background-color: $bg-white;
    padding: $spacing-extra-large;
    border-radius: $border-radius-large;
    text-align: center;
    cursor: pointer;
    transition: $transition-base;
    box-shadow: $box-shadow-light;

    &:hover {
      transform: translateY(-8px);
      box-shadow: $box-shadow-base;
    }

    .category-icon {
      margin-bottom: $spacing-base;
      color: $primary-color;
    }

    .category-name {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: 500;
    }
  }
}

.recommend-section {
  margin-bottom: $spacing-extra-large * 2;

  .goods-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-large;
  }
}

.hot-categories-section {
  margin-bottom: $spacing-extra-large * 2;

  .tag-cloud {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-base;

    .tag-item {
      cursor: pointer;
      transition: $transition-fast;
      padding: $spacing-small $spacing-base;

      &:hover {
        transform: scale(1.05);
      }
    }
  }
}

.cultural-section {
  .cultural-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-large;
  }

  .cultural-item {
    background-color: $bg-white;
    border-radius: $border-radius-large;
    overflow: hidden;
    cursor: pointer;
    transition: $transition-base;
    box-shadow: $box-shadow-light;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $box-shadow-base;
    }

    .cultural-image {
      width: 100%;
      height: 200px;
      background-color: $bg-page;

      .image-loading {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        font-size: 24px;
        color: $text-secondary;
      }
    }

    .cultural-info {
      padding: $spacing-base;
    }

    .cultural-title {
      font-size: $font-size-base;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $spacing-small;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .cultural-summary {
      font-size: $font-size-small;
      color: $text-secondary;
      margin-bottom: $spacing-small;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .cultural-meta {
      display: flex;
      gap: $spacing-base;
      font-size: $font-size-extra-small;
      color: $text-secondary;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 2px;
      }
    }
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-large;

  .section-title {
    font-size: $font-size-extra-large;
    font-weight: 600;
    color: $text-primary;
    position: relative;
    padding-left: $spacing-base;

    &::before {
      content: "";
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 4px;
      background-color: $primary-color;
      border-radius: 2px;
    }
  }

  .more-link {
    font-size: $font-size-base;
    color: $primary-color;
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: $spacing-small;
    transition: $transition-fast;

    &:hover {
      gap: $spacing-small + 4px;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .category-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .goods-grid,
  .cultural-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .goods-grid,
  .cultural-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .banner-title {
    font-size: 32px !important;
  }

  .banner-desc {
    font-size: $font-size-base !important;
  }
}
</style>
