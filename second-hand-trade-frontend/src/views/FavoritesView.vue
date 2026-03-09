<template>
  <div class="favorites-page">
    <div class="page-container">
      <h2 class="page-title">我的收藏</h2>

      <div class="content">
        <!-- 收藏列表 -->
        <div v-loading="loading" class="goods-list">
          <div v-if="favorites.length === 0" class="empty-state">
            <el-empty description="暂无收藏商品">
              <el-button type="primary" @click="$router.push('/goods')">
                去逛逛
              </el-button>
            </el-empty>
          </div>

          <div v-else class="goods-grid">
            <GoodsCard
              v-for="goods in favorites"
              :key="goods.id"
              :goods="goods"
              show-delete
              @delete="handleRemoveFavorite"
            />
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import GoodsCard from "@/components/GoodsCard.vue";
import { getUserFavoriteProducts, unfavorite } from "@/api/modules/favorite";
import type { Product } from "@/types";

const loading = ref(false);
const favorites = ref<Product[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true;
  try {
    const res = await getUserFavoriteProducts({
      current: currentPage.value,
      size: pageSize.value,
    });

    if (res.data.code === 200 && res.data.data) {
      favorites.value = res.data.data.records;
      total.value = res.data.data.total;
    }
  } catch (error) {
    console.error("加载收藏失败:", error);
    ElMessage.error("加载收藏失败");
  } finally {
    loading.value = false;
  }
};

// 移除收藏
const handleRemoveFavorite = async (productId: number) => {
  try {
    await unfavorite("product", productId);
    ElMessage.success("已取消收藏");
    loadFavorites();
  } catch (error) {
    console.error("取消收藏失败:", error);
    ElMessage.error("操作失败");
  }
};

// 分页处理
const handleSizeChange = () => {
  currentPage.value = 1;
  loadFavorites();
};

const handleCurrentChange = () => {
  loadFavorites();
};

onMounted(() => {
  loadFavorites();
});
</script>

<style lang="scss" scoped>
.favorites-page {
  min-height: calc(100vh - 120px);
  background: #f5f5f5;
  padding: 20px;

  .page-container {
    max-width: 1200px;
    margin: 0 auto;

    .page-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 20px;
      font-weight: 600;
    }

    .content {
      background: #fff;
      border-radius: 8px;
      padding: 20px;

      .goods-list {
        min-height: 400px;

        .empty-state {
          text-align: center;
          padding: 80px 0;
        }

        .goods-grid {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 20px;

          @media (max-width: 1200px) {
            grid-template-columns: repeat(3, 1fr);
          }

          @media (max-width: 768px) {
            grid-template-columns: repeat(2, 1fr);
          }
        }
      }

      .pagination {
        margin-top: 30px;
        text-align: center;
      }
    }
  }
}
</style>
