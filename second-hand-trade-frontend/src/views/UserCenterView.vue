<template>
  <div class="user-center-page">
    <div class="page-container">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="user-profile">
          <el-avatar :size="80" :src="userInfo.avatar" />
          <h3 class="nickname">{{ userInfo.nickname }}</h3>
          <p class="username">{{ userInfo.username }}</p>
        </div>

        <el-menu
          :default-active="activeTab"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="reviews">
            <el-icon><ChatDotRound /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
          <el-menu-item index="orders" v-if="isBuyer">
            <el-icon><ShoppingBag /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="seller-apply" v-if="isBuyer">
            <el-icon><Shop /></el-icon>
            <span>申请入驻</span>
          </el-menu-item>
          <el-menu-item index="seller" v-if="isSeller">
            <el-icon><Shop /></el-icon>
            <span>卖家中心</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 个人中心 -->
        <div v-if="activeTab === 'profile'" class="profile-section">
          <h2 class="section-title">个人信息</h2>
          <el-form :model="userInfo" label-width="100px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="userInfo.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile"
                >保存修改</el-button
              >
            </el-form-item>
          </el-form>
        </div>

        <!-- 我的收藏 -->
        <div v-else-if="activeTab === 'favorites'" class="favorites-section">
          <h2 class="section-title">我的收藏</h2>
          <div v-loading="loading" class="content">
            <div v-if="favorites.length === 0" class="empty-state">
              <el-empty description="暂无收藏商品">
                <el-button type="primary" @click="$router.push('/goods')"
                  >去逛逛</el-button
                >
              </el-empty>
            </div>
            <div v-else class="goods-grid">
              <GoodsCard
                v-for="goods in favorites"
                :key="goods.id"
                :goods="goods"
              />
            </div>
          </div>
        </div>

        <!-- 我的评价 -->
        <div v-else-if="activeTab === 'reviews'" class="reviews-section">
          <h2 class="section-title">我的评价</h2>
          <div v-loading="loading" class="content">
            <div v-if="userReviews.length === 0" class="empty-state">
              <el-empty description="暂无评价" />
            </div>
            <div v-else class="review-list">
              <div
                v-for="review in userReviews"
                :key="review.id"
                class="review-item"
              >
                <div class="review-header">
                  <div class="goods-info">
                    <el-avatar
                      :size="50"
                      :src="review.productImage"
                      shape="square"
                    />
                    <div class="goods-detail">
                      <div class="goods-name">{{ review.productName }}</div>
                      <div class="rating">
                        <el-rate
                          v-model="review.rating"
                          disabled
                          show-text
                          text-color="#ff9900"
                        />
                      </div>
                    </div>
                  </div>
                  <div class="review-time">
                    {{ formatTime(review.createdAt) }}
                  </div>
                </div>
                <div class="review-content">
                  <p>{{ review.content }}</p>
                  <div
                    v-if="review.images && review.images.length > 0"
                    class="review-images"
                  >
                    <el-image
                      v-for="(img, index) in review.images"
                      :key="index"
                      :src="img"
                      :preview-src-list="review.images"
                      fit="cover"
                      style="
                        width: 80px;
                        height: 80px;
                        margin-right: 8px;
                        border-radius: 4px;
                      "
                    />
                  </div>
                </div>
                <div class="review-actions">
                  <el-button size="small" @click="handleDeleteReview(review.id)"
                    >删除</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 我的订单 -->
        <div v-else-if="activeTab === 'orders'" class="orders-section">
          <h2 class="section-title">我的订单</h2>
          <div class="content">
            <el-empty description="暂无订单" />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import {
  User,
  Star,
  ChatDotRound,
  ShoppingBag,
  Shop,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import GoodsCard from "@/components/GoodsCard.vue";
import { useUserStore } from "@/store/user";
import { getUserFavoriteProducts } from "@/api/modules/favorite";
import type { Product } from "@/types";

const userStore = useUserStore();
const router = useRouter();

// 计算属性：是否为卖家
const isSeller = computed(() => userStore.userInfo?.role === "seller");

// 计算属性：是否为买家
const isBuyer = computed(() => userStore.userInfo?.role === "buyer");

const activeTab = ref("profile");
const loading = ref(false);

const userInfo = reactive({
  id: 0,
  username: "",
  nickname: "",
  phone: "",
  email: "",
  gender: 0,
  avatar: "",
});

const favorites = ref<Product[]>([]);
const userReviews = ref<any[]>([]);

// 加载用户信息
const loadUserInfo = () => {
  if (userStore.userInfo) {
    Object.assign(userInfo, userStore.userInfo);
    console.log("用户中心加载的用户信息:", userStore.userInfo);
    console.log("用户角色:", userStore.userInfo.role);
    console.log("是否为买家:", isBuyer.value);
    console.log("是否为卖家:", isSeller.value);
  }
};

// 加载收藏列表
const loadFavorites = async () => {
  if (activeTab.value !== "favorites") return;

  loading.value = true;
  try {
    const res = await getUserFavoriteProducts({
      current: 1,
      size: 12,
    });

    console.log("收藏列表响应:", res);
    if (res && res.records) {
      favorites.value = res.records;
    }
  } catch (error) {
    console.error("加载收藏失败:", error);
  } finally {
    loading.value = false;
  }
};

// 加载我的评价
const loadUserReviews = async () => {
  if (activeTab.value !== "reviews") return;

  loading.value = true;
  try {
    // TODO: 调用获取用户评价的 API
    userReviews.value = [];
  } catch (error) {
    console.error("加载评价失败:", error);
  } finally {
    loading.value = false;
  }
};

// 更新个人信息
const handleUpdateProfile = async () => {
  try {
    // TODO: 调用更新用户信息 API
    ElMessage.success("保存成功");
  } catch (error) {
    console.error("更新失败:", error);
    ElMessage.error("更新失败");
  }
};

// 删除评价
const handleDeleteReview = async (reviewId: number) => {
  try {
    await ElMessageBox.confirm("确定要删除该评价吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // TODO: 调用删除评价 API
    ElMessage.success("删除成功");
    loadUserReviews();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

// 菜单选择
const handleMenuSelect = (index: string) => {
  if (index === "seller-apply") {
    router.push("/seller/apply");
    return;
  }
  if (index === "seller") {
    router.push("/seller");
    return;
  }

  activeTab.value = index;
  if (index === "favorites") {
    loadFavorites();
  } else if (index === "reviews") {
    loadUserReviews();
  }
};

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr);
  return date.toLocaleDateString("zh-CN");
};

onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  loadUserInfo();
});
</script>

<style lang="scss" scoped>
.user-center-page {
  min-height: calc(100vh - 120px);
  background: #f5f5f5;
  padding: 20px;

  .page-container {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 280px 1fr;
    gap: 20px;

    .sidebar {
      background: #fff;
      border-radius: 8px;
      padding: 20px;

      .user-profile {
        text-align: center;
        padding: 20px 0;
        border-bottom: 1px solid #eee;
        margin-bottom: 20px;

        .nickname {
          margin: 15px 0 5px;
          font-size: 18px;
          color: #333;
        }

        .username {
          font-size: 14px;
          color: #999;
        }
      }

      .sidebar-menu {
        border-right: none;

        .el-menu-item {
          height: 50px;
          line-height: 50px;
          border-radius: 4px;
          margin-bottom: 5px;

          &:hover {
            background-color: #f5f7fa;
          }

          &.is-active {
            background-color: #ecf5ff;
            color: #409eff;
          }
        }
      }
    }

    .main-content {
      background: #fff;
      border-radius: 8px;
      padding: 30px;
      min-height: 500px;

      .section-title {
        font-size: 20px;
        color: #333;
        margin-bottom: 25px;
        padding-bottom: 15px;
        border-bottom: 2px solid #f0f0f0;
      }

      .content {
        .empty-state {
          text-align: center;
          padding: 60px 0;
        }

        .goods-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 20px;
        }

        .review-list {
          .review-item {
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 8px;
            margin-bottom: 20px;

            .review-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 15px;

              .goods-info {
                display: flex;
                align-items: center;

                .goods-detail {
                  margin-left: 12px;

                  .goods-name {
                    font-size: 14px;
                    color: #333;
                    margin-bottom: 5px;
                  }
                }
              }

              .review-time {
                font-size: 13px;
                color: #999;
              }
            }

            .review-content {
              margin-bottom: 15px;

              p {
                margin: 0 0 10px 0;
                font-size: 14px;
                color: #333;
                line-height: 1.6;
              }

              .review-images {
                display: flex;
                flex-wrap: wrap;
              }
            }

            .review-actions {
              text-align: right;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .page-container {
    grid-template-columns: 1fr !important;
  }
}
</style>
