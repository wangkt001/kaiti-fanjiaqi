<template>
  <header class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <div class="navbar-logo" @click="$router.push('/')">
        <span class="logo-text">豫见好物</span>
      </div>

      <!-- 搜索框 -->
      <div class="navbar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文创商品"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 导航菜单 -->
      <nav class="navbar-menu">
        <router-link to="/" class="menu-item">首页</router-link>
        <router-link to="/goods" class="menu-item">商品</router-link>
        <router-link to="/cultural" class="menu-item">文化资讯</router-link>
        <router-link to="/about" class="menu-item">关于我们</router-link>
      </nav>

      <!-- 用户操作区 -->
      <div class="navbar-actions">
        <template v-if="userStore.isLoggedIn">
          <!-- 购物车（仅买家可见） -->
          <el-badge
            v-if="!userStore.isSeller"
            :value="cartCount"
            :hidden="cartCount === 0"
            class="action-item"
          >
            <el-icon @click="$router.push('/cart')"><ShoppingCart /></el-icon>
          </el-badge>

          <!-- 消息通知 -->
          <!-- <el-badge
            :value="messageCount"
            :hidden="messageCount === 0"
            class="action-item"
          >
            <el-icon><Bell /></el-icon>
          </el-badge> -->

          <!-- 用户下拉菜单 -->
          <el-dropdown class="user-dropdown">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user')"
                  >个人中心</el-dropdown-item
                >
                <el-dropdown-item
                  v-if="userStore.isSeller"
                  @click="$router.push('/seller')"
                >
                  卖家中心
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="userStore.isAdmin"
                  @click="$router.push('/admin')"
                >
                  后台管理
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout"
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')"
            >登录</el-button
          >
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, ShoppingCart, Bell } from "@element-plus/icons-vue";
import { getCartCount, logout as logoutApi } from "@/api/modules/user";

const router = useRouter();
const userStore = useUserStore();
const searchKeyword = ref("");
const cartCount = ref(0);
const messageCount = ref(0);

// 加载购物车数量
const loadCartCount = async () => {
  if (!userStore.isLoggedIn) {
    cartCount.value = 0;
    return;
  }

  try {
    const res = await getCartCount();
    cartCount.value = typeof res === "number" ? res : 0;
  } catch (error) {
    console.error("加载购物车数量失败:", error);
  }
};

// 搜索处理
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning("请输入搜索关键词");
    return;
  }
  router.push({
    path: "/goods",
    query: { keyword: searchKeyword.value },
  });
};

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 调用后端登出接口
    await logoutApi();

    // 清理本地缓存
    userStore.logout();
    ElMessage.success("已退出登录");
    router.push("/");
  } catch {
    // 取消退出
  }
};

onMounted(() => {
  loadCartCount();
});
</script>

<style lang="scss" scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: $z-index-top;
  background-color: $bg-white;
  box-shadow: $box-shadow-light;
  height: $header-height;
}

.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: $page-width;
  margin: 0 auto;
  padding: 0 $spacing-base;
  height: 100%;
}

.navbar-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: $spacing-extra-large;

  .logo-image {
    height: 40px;
    width: auto;
  }

  .logo-text {
    font-size: $font-size-extra-large;
    font-weight: 600;
    color: $primary-color;
    margin-left: $spacing-small;
  }
}

.navbar-search {
  flex: 1;
  max-width: 500px;
  margin: 0 $spacing-large;

  .search-input {
    width: 100%;
  }
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: $spacing-extra-large;

  .menu-item {
    font-size: $font-size-medium;
    color: $text-regular;
    text-decoration: none;
    transition: $transition-fast;

    &:hover {
      color: $primary-color;
    }

    &.router-link-active {
      color: $primary-color;
      font-weight: 500;
    }
  }
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: $spacing-large;

  .action-item {
    font-size: 20px;
    color: $text-regular;
    cursor: pointer;
    transition: $transition-fast;

    &:hover {
      color: $primary-color;
    }
  }

  .user-dropdown {
    margin-left: $spacing-small;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: $spacing-small;
    cursor: pointer;

    .user-name {
      font-size: $font-size-small;
      color: $text-regular;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }

  .navbar-search {
    display: none;
  }
}
</style>
