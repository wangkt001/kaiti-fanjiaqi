<template>
  <div class="cultural-content-page">
    <NavBar />

    <div class="page-container">
      <!-- 顶部 Banner -->
      <div class="banner">
        <h1 class="banner-title">豫见文化</h1>
        <p class="banner-subtitle">探索河南传统文化，感受千年文明魅力</p>
      </div>

      <div class="content-wrapper">
        <!-- 左侧分类导航 -->
        <aside class="sidebar">
          <div class="category-section">
            <h3>资讯分类</h3>
            <el-menu
              :default-active="activeCategory"
              class="category-menu"
              @select="handleCategorySelect"
            >
              <el-menu-item index="">
                <el-icon><List /></el-icon>
                <span>全部</span>
              </el-menu-item>
              <el-menu-item index="intangible_heritage">
                <el-icon><Medal /></el-icon>
                <span>非物质文化遗产</span>
              </el-menu-item>
              <el-menu-item index="exhibition">
                <el-icon><Picture /></el-icon>
                <span>展览资讯</span>
              </el-menu-item>
              <el-menu-item index="activity">
                <el-icon><Present /></el-icon>
                <span>活动公告</span>
              </el-menu-item>
              <el-menu-item index="news">
                <el-icon><Document /></el-icon>
                <span>文化新闻</span>
              </el-menu-item>
              <el-menu-item index="story">
                <el-icon><Notebook /></el-icon>
                <span>文化故事</span>
              </el-menu-item>
            </el-menu>
          </div>

          <!-- 热门标签 -->
          <div class="tags-section">
            <h3>热门标签</h3>
            <div class="tag-cloud">
              <el-tag
                v-for="tag in hotTags"
                :key="tag"
                effect="plain"
                style="margin: 5px; cursor: pointer"
                @click="handleTagClick(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </aside>

        <!-- 右侧内容区 -->
        <main class="main-content">
          <!-- 搜索栏 -->
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索资讯..."
              clearable
              style="width: 300px"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>

          <!-- 资讯列表 -->
          <div v-loading="loading" class="content-list">
            <div v-if="contents.length === 0" class="empty-state">
              <el-empty description="暂无资讯" />
            </div>

            <div v-else class="content-items">
              <div
                v-for="content in contents"
                :key="content.id"
                class="content-item"
                @click="goToDetail(content.id)"
              >
                <div class="content-cover">
                  <el-image
                    :src="content.coverImage || defaultCover"
                    fit="cover"
                    style="width: 100%; height: 100%"
                  />
                </div>
                <div class="content-info">
                  <h3 class="content-title">{{ content.title }}</h3>
                  <p class="content-summary">{{ content.summary }}</p>
                  <div class="content-meta">
                    <span class="meta-item">
                      <el-icon><User /></el-icon>
                      {{ content.authorName }}
                    </span>
                    <span class="meta-item">
                      <el-icon><View /></el-icon>
                      {{ content.viewCount }}
                    </span>
                    <span class="meta-item">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ content.commentCount }}
                    </span>
                    <span class="meta-item">
                      <el-icon><Clock /></el-icon>
                      {{ formatTime(content.publishedAt) }}
                    </span>
                  </div>
                  <div class="content-tags">
                    <el-tag
                      v-for="tag in content.tags?.slice(0, 3)"
                      :key="tag"
                      size="small"
                      style="margin-right: 5px"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="total > pageSize" class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </main>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  List,
  Medal,
  Picture,
  Present,
  Document,
  Notebook,
  Search,
  User,
  View,
  ChatDotRound,
  Clock,
} from "@element-plus/icons-vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { getCulturalContents } from "@/api/modules/culturalContent";
import type { CulturalContent } from "@/api/modules/culturalContent";

const router = useRouter();

const loading = ref(false);
const contents = ref<CulturalContent[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const activeCategory = ref("");
const searchKeyword = ref("");

const hotTags = [
  "豫剧",
  "少林",
  "牡丹",
  "青铜器",
  "陶瓷",
  "剪纸",
  "年画",
  "河南博物院",
];

const defaultCover = "https://via.placeholder.com/300x200?text=Culture";

// 加载资讯列表
const loadContents = async () => {
  loading.value = true;
  try {
    const res = await getCulturalContents({
      category: activeCategory.value || undefined,
      keyword: searchKeyword.value || undefined,
      current: currentPage.value,
      size: pageSize.value,
    });
    console.log("res>>>>>>>>", res);

    if (res) {
      contents.value = res.records;
      total.value = res.total;
    }
  } catch (error) {
    console.error("加载资讯失败:", error);
  } finally {
    loading.value = false;
  }
};

// 分类选择
const handleCategorySelect = (index: string) => {
  activeCategory.value = index;
  currentPage.value = 1;
  loadContents();
};

// 标签点击
const handleTagClick = (tag: string) => {
  searchKeyword.value = tag;
  currentPage.value = 1;
  loadContents();
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadContents();
};

// 分页处理
const handleSizeChange = () => {
  currentPage.value = 1;
  loadContents();
};

const handleCurrentChange = () => {
  loadContents();
};

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr);
  return date.toLocaleDateString("zh-CN");
};

// 跳转详情
const goToDetail = (id: number) => {
  router.push(`/cultural-content/${id}`);
};

onMounted(() => {
  loadContents();
});
</script>

<style lang="scss" scoped>
.cultural-content-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    .banner {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 60px 20px;
      text-align: center;
      color: #fff;

      .banner-title {
        font-size: 36px;
        margin: 0 0 10px 0;
        font-weight: bold;
      }

      .banner-subtitle {
        font-size: 16px;
        opacity: 0.9;
        margin: 0;
      }
    }

    .content-wrapper {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
      display: grid;
      grid-template-columns: 280px 1fr;
      gap: 20px;

      .sidebar {
        .category-section {
          background: #fff;
          border-radius: 8px;
          padding: 20px;
          margin-bottom: 20px;

          h3 {
            margin: 0 0 15px 0;
            font-size: 16px;
            color: #333;
          }

          .category-menu {
            border-right: none;
          }
        }

        .tags-section {
          background: #fff;
          border-radius: 8px;
          padding: 20px;

          h3 {
            margin: 0 0 15px 0;
            font-size: 16px;
            color: #333;
          }

          .tag-cloud {
            display: flex;
            flex-wrap: wrap;
          }
        }
      }

      .main-content {
        background: #fff;
        border-radius: 8px;
        padding: 20px;

        .search-bar {
          margin-bottom: 20px;
          display: flex;
          justify-content: flex-end;
        }

        .content-list {
          min-height: 400px;

          .empty-state {
            text-align: center;
            padding: 80px 0;
          }

          .content-items {
            .content-item {
              display: grid;
              grid-template-columns: 240px 1fr;
              gap: 20px;
              padding: 20px;
              border-bottom: 1px solid #eee;
              cursor: pointer;
              transition: all 0.3s;

              &:hover {
                background: #f9f9f9;

                .content-title {
                  color: #409eff;
                }
              }

              &:last-child {
                border-bottom: none;
              }

              .content-cover {
                width: 240px;
                height: 160px;
                border-radius: 8px;
                overflow: hidden;
              }

              .content-info {
                .content-title {
                  font-size: 18px;
                  color: #333;
                  margin: 0 0 10px 0;
                  transition: color 0.3s;
                }

                .content-summary {
                  font-size: 14px;
                  color: #666;
                  margin: 0 0 15px 0;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                }

                .content-meta {
                  display: flex;
                  gap: 15px;
                  margin-bottom: 10px;
                  font-size: 13px;
                  color: #999;

                  .meta-item {
                    display: flex;
                    align-items: center;
                    gap: 4px;
                  }
                }
              }
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
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr !important;

    .content-items {
      .content-item {
        grid-template-columns: 1fr !important;

        .content-cover {
          width: 100% !important;
          height: 200px;
        }
      }
    }
  }
}
</style>
