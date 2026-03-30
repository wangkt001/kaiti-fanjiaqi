<template>
  <div class="goods-list-view">
    <!-- 导航栏 -->
    <NavBar />

    <!-- 主要内容区 -->
    <main class="main-content">
      <div class="container">
        <div class="page-layout">
          <!-- 左侧筛选栏 -->
          <aside class="filter-sidebar">
            <div class="filter-section">
              <h3 class="filter-title">文化分类</h3>
              <div class="filter-options">
                <div
                  v-for="category in categories"
                  :key="category.id"
                  :class="[
                    'filter-option',
                    { active: filters.categoryId === category.id },
                  ]"
                  @click="selectCategory(category.id)"
                >
                  {{ category.name }}
                </div>
              </div>
            </div>

            <el-divider />

            <div class="filter-section">
              <h3 class="filter-title">价格区间</h3>
              <div class="price-inputs">
                <el-input
                  v-model="filters.minPrice"
                  placeholder="¥ 最低"
                  size="small"
                  @input="handlePriceChange"
                />
                <span class="separator">-</span>
                <el-input
                  v-model="filters.maxPrice"
                  placeholder="¥ 最高"
                  size="small"
                  @input="handlePriceChange"
                />
              </div>
            </div>

            <el-divider />

            <div class="filter-section">
              <h3 class="filter-title">热门标签</h3>
              <div class="tag-cloud">
                <el-tag
                  v-for="tag in hotTags"
                  :key="tag.id"
                  :class="{ active: filters.tagId === tag.id }"
                  size="small"
                  effect="plain"
                  @click="selectTag(tag.id)"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>
          </aside>

          <!-- 右侧商品列表 -->
          <div class="goods-section">
            <!-- 排序栏 -->
            <div class="sort-bar">
              <div class="sort-options">
                <span
                  :class="{ active: filters.sortBy === '' }"
                  @click="selectSort('')"
                >
                  综合
                </span>
                <span
                  :class="{ active: filters.sortBy === 'sales' }"
                  @click="selectSort('sales')"
                >
                  销量
                </span>
                <span
                  :class="{ active: filters.sortBy === 'price_asc' }"
                  @click="selectSort('price_asc')"
                >
                  价格 ↑
                </span>
                <span
                  :class="{ active: filters.sortBy === 'price_desc' }"
                  @click="selectSort('price_desc')"
                >
                  价格 ↓
                </span>
                <span
                  :class="{ active: filters.sortBy === 'new' }"
                  @click="selectSort('new')"
                >
                  最新
                </span>
              </div>
              <div class="result-info">共 {{ pagination.total }} 件商品</div>
            </div>

            <!-- 商品列表 -->
            <div v-loading="loading" class="goods-list">
              <div v-if="goodsList.length > 0" class="goods-grid">
                <GoodsCard
                  v-for="goods in goodsList"
                  :key="goods.id"
                  :goods="goods"
                />
              </div>
              <el-empty v-else description="暂无商品" />
            </div>

            <!-- 分页 -->
            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="pagination.current"
                v-model:page-size="pagination.size"
                :total="pagination.total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import GoodsCard from "@/components/GoodsCard.vue";
import { getGoodsList } from "@/api/modules/goods";
import { getCategoryTree } from "@/api/modules/category";
import { getHotTags } from "@/api/modules/tag";
import type { Goods, Category, CulturalTag } from "@/types";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const goodsList = ref<Goods[]>([]);
const categories = ref<Category[]>([]);
const hotTags = ref<CulturalTag[]>([]);

const filters = reactive({
  categoryId: undefined as number | undefined,
  tagId: undefined as number | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined,
  sortBy: "",
  keyword: route.query.keyword as string | undefined,
});

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0,
});

// 加载商品列表
const loadGoodsList = async () => {
  loading.value = true;
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
    };

    if (filters.categoryId) params.categoryId = filters.categoryId;
    if (filters.minPrice) params.minPrice = filters.minPrice;
    if (filters.maxPrice) params.maxPrice = filters.maxPrice;
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.keyword) params.keyword = filters.keyword;

    const res = await getGoodsList(params);
    goodsList.value = res.records;
    pagination.total = res.total;
  } catch (error) {
    console.error("加载商品列表失败", error);
  } finally {
    loading.value = false;
  }
};

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getCategoryTree();
    categories.value = Array.isArray(res) ? res : [];
  } catch (error) {
    console.error("加载分类失败", error);
    categories.value = [];
  }
};

// 加载热门标签
const loadHotTags = async () => {
  try {
    const res = await getHotTags(20);
    hotTags.value = Array.isArray(res) ? res : [];
  } catch (error) {
    console.error("加载标签失败", error);
    hotTags.value = [];
  }
};

// 选择分类
const selectCategory = (categoryId?: number) => {
  filters.categoryId =
    filters.categoryId === categoryId ? undefined : categoryId;
  pagination.current = 1;
  updateURL();
  loadGoodsList();
};

// 选择标签
const selectTag = (tagId?: number) => {
  filters.tagId = filters.tagId === tagId ? undefined : tagId;
  pagination.current = 1;
  updateURL();
  loadGoodsList();
};

// 选择排序
const selectSort = (sortBy: string) => {
  filters.sortBy = sortBy;
  pagination.current = 1;
  updateURL();
  loadGoodsList();
};

// 价格变化
const handlePriceChange = () => {
  pagination.current = 1;
  updateURL();
  loadGoodsList();
};

// 分页变化
const handleSizeChange = () => {
  pagination.current = 1;
  loadGoodsList();
};

const handleCurrentChange = () => {
  loadGoodsList();
};

// 更新 URL
const updateURL = () => {
  const query: any = {};
  if (filters.categoryId) query.categoryId = filters.categoryId;
  if (filters.minPrice) query.minPrice = filters.minPrice;
  if (filters.maxPrice) query.maxPrice = filters.maxPrice;
  if (filters.sortBy) query.sortBy = filters.sortBy;
  if (filters.keyword) query.keyword = filters.keyword;

  router.push({ path: "/goods", query });
};

// 监听路由变化
watch(
  () => route.query,
  () => {
    const query = route.query;
    if (query.categoryId) filters.categoryId = Number(query.categoryId);
    if (query.minPrice) filters.minPrice = Number(query.minPrice);
    if (query.maxPrice) filters.maxPrice = Number(query.maxPrice);
    if (query.sortBy) filters.sortBy = query.sortBy as string;
    if (query.keyword) filters.keyword = query.keyword as string;

    loadGoodsList();
  },
  { immediate: true },
);

onMounted(() => {
  loadCategories();
  loadHotTags();
});
</script>

<style lang="scss" scoped>
.goods-list-view {
  min-height: 100vh;
  background-color: $bg-page;
}

.main-content {
  padding: $spacing-extra-large 0;
}

.page-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: $spacing-extra-large;
}

.filter-sidebar {
  background-color: $bg-white;
  padding: $spacing-large;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow-light;
  height: fit-content;
  position: sticky;
  top: $spacing-large;
}

.filter-section {
  margin-bottom: $spacing-large;

  .filter-title {
    font-size: $font-size-medium;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-base;
  }
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: $spacing-small;
}

.filter-option {
  padding: $spacing-small $spacing-base;
  font-size: $font-size-small;
  color: $text-regular;
  cursor: pointer;
  border-radius: $border-radius-base;
  transition: $transition-fast;

  &:hover {
    background-color: $bg-page;
    color: $primary-color;
  }

  &.active {
    background-color: rgba($primary-color, 0.1);
    color: $primary-color;
    font-weight: 500;
  }
}

.price-inputs {
  display: flex;
  align-items: center;
  gap: $spacing-small;

  .separator {
    color: $text-secondary;
  }
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-small;

  .el-tag {
    cursor: pointer;
    transition: $transition-fast;

    &:hover {
      transform: scale(1.05);
    }

    &.active {
      background-color: $primary-color;
      color: #ffffff;
      border-color: $primary-color;
    }
  }
}

.goods-section {
  background-color: $bg-white;
  padding: $spacing-large;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow-light;
}

.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: $spacing-large;
  border-bottom: 1px solid $border-lighter;
  margin-bottom: $spacing-large;
}

.sort-options {
  display: flex;
  gap: $spacing-large;

  span {
    font-size: $font-size-base;
    color: $text-regular;
    cursor: pointer;
    transition: $transition-fast;

    &:hover {
      color: $primary-color;
    }

    &.active {
      color: $primary-color;
      font-weight: 500;
    }
  }
}

.result-info {
  font-size: $font-size-small;
  color: $text-secondary;
}

.goods-list {
  min-height: 400px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-large;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: $spacing-extra-large;
  padding-top: $spacing-large;
  border-top: 1px solid $border-lighter;
}

// 响应式设计
@media (max-width: 1200px) {
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .page-layout {
    grid-template-columns: 1fr;
  }

  .filter-sidebar {
    display: none;
  }

  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
