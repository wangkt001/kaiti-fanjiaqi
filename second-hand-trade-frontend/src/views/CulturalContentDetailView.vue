<template>
  <div class="content-detail-page">
    <NavBar />

    <div class="page-container">
      <article class="article">
        <!-- 文章头部 -->
        <header class="article-header">
          <h1 class="article-title">{{ content.title }}</h1>
          <div class="article-meta">
            <div class="meta-left">
              <el-avatar :size="40" :src="defaultAvatar" />
              <div class="author-info">
                <span class="author-name">{{
                  content.authorName || "管理员"
                }}</span>
                <span class="publish-time">{{
                  formatTime(content.publishedAt)
                }}</span>
              </div>
            </div>
            <div class="meta-right">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ content.viewCount }} 阅读
              </span>
              <span class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ content.commentCount }} 评论
              </span>
              <span class="meta-item">
                <el-icon><Star /></el-icon>
                {{ content.favoriteCount }} 收藏
              </span>
            </div>
          </div>
        </header>

        <!-- 封面图 -->
        <div v-if="content.coverImage" class="cover-image">
          <el-image
            :src="content.coverImage"
            fit="cover"
            style="width: 100%; height: 400px"
          />
        </div>

        <!-- 标签 -->
        <div
          v-if="content.tags && content.tags.length > 0"
          class="tags-section"
        >
          <el-tag
            v-for="tag in content.tags"
            :key="tag"
            effect="plain"
            style="margin-right: 10px"
          >
            {{ tag }}
          </el-tag>
        </div>

        <!-- 文章内容 -->
        <div class="article-content">
          <div v-html="content.content" class="content-html"></div>
        </div>

        <!-- 互动区域 -->
        <div class="interaction-section">
          <div class="action-buttons">
            <LikeButton
              targetType="content"
              :targetId="content.id"
              :initialCount="content.likeCount"
              show-count
            >
              点赞
            </LikeButton>
            <FavoriteButton
              targetType="content"
              :targetId="content.id"
              :initialCount="content.favoriteCount"
              show-count
            >
              收藏
            </FavoriteButton>
            <el-button @click="handleShare">
              <el-icon><Share /></el-icon>
              分享
            </el-button>
          </div>
        </div>

        <!-- 评论区域 -->
        <div class="comment-section">
          <h2 class="section-title">评论 ({{ content.commentCount }})</h2>
          <ContentCommentList :contentId="content.id" />
        </div>

        <!-- 推荐阅读 -->
        <div v-if="recommendContents.length > 0" class="recommend-section">
          <h2 class="section-title">推荐阅读</h2>
          <div class="recommend-list">
            <div
              v-for="item in recommendContents"
              :key="item.id"
              class="recommend-item"
              @click="goToDetail(item.id)"
            >
              <div class="recommend-cover">
                <el-image
                  :src="item.coverImage || defaultCover"
                  fit="cover"
                  style="width: 100%; height: 100%"
                />
              </div>
              <div class="recommend-info">
                <h3 class="recommend-title">{{ item.title }}</h3>
                <p class="recommend-summary">{{ item.summary }}</p>
                <div class="recommend-meta">
                  <span>{{ item.viewCount }} 阅读</span>
                  <span>{{ formatTime(item.publishedAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </article>

      <!-- 侧边栏 -->
      <aside class="sidebar">
        <!-- 分类目录 -->
        <div class="sidebar-section">
          <h3>分类目录</h3>
          <el-menu class="sidebar-menu" @select="handleCategorySelect">
            <el-menu-item index="">全部</el-menu-item>
            <el-menu-item index="intangible_heritage"
              >非物质文化遗产</el-menu-item
            >
            <el-menu-item index="exhibition">展览资讯</el-menu-item>
            <el-menu-item index="activity">活动公告</el-menu-item>
            <el-menu-item index="news">文化新闻</el-menu-item>
            <el-menu-item index="story">文化故事</el-menu-item>
          </el-menu>
        </div>

        <!-- 热门文章 -->
        <div class="sidebar-section">
          <h3>热门资讯</h3>
          <div class="hot-list">
            <div
              v-for="(item, index) in hotContents"
              :key="item.id"
              class="hot-item"
              @click="goToDetail(item.id)"
            >
              <span class="hot-index" :class="{ top3: index < 3 }">{{
                index + 1
              }}</span>
              <div class="hot-info">
                <div class="hot-title">{{ item.title }}</div>
                <div class="hot-meta">{{ item.viewCount }} 阅读</div>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { View, ChatDotRound, Star, Share } from "@element-plus/icons-vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import LikeButton from "@/components/LikeButton.vue";
import FavoriteButton from "@/components/FavoriteButton.vue";
import ContentCommentList from "@/components/ContentCommentList.vue";
import {
  getContentDetail,
  getRecommendContents,
} from "@/api/modules/culturalContent";
import type { CulturalContent } from "@/api/modules/culturalContent";

const defaultAvatar = "https://placeholder.co/100x100?text=Avatar";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const content = ref<CulturalContent>({} as CulturalContent);
const recommendContents = ref<CulturalContent[]>([]);
const hotContents = ref<CulturalContent[]>([]);

const defaultCover = "https://via.placeholder.com/300x200?text=Culture";

// 加载资讯详情
const loadContentDetail = async () => {
  loading.value = true;
  try {
    const id = Number(route.params.id);
    const res = await getContentDetail(id);

    if (res) {
      content.value = res;
    }
  } catch (error) {
    console.error("加载详情失败:", error);
    ElMessage.error("加载资讯失败");
  } finally {
    loading.value = false;
  }
};

// 加载推荐内容
const loadRecommendContents = async () => {
  try {
    const res = await getRecommendContents({
      current: 1,
      size: 4,
    });

    if (res) {
      recommendContents.value = res.records;
    }
  } catch (error) {
    console.error("加载推荐失败:", error);
  }
};

// 加载热门内容
const loadHotContents = async () => {
  try {
    const res = await getRecommendContents({
      current: 1,
      size: 10,
    });

    if (res) {
      hotContents.value = res.records.slice(0, 10);
    }
  } catch (error) {
    console.error("加载热门失败:", error);
  }
};

// 分享
const handleShare = () => {
  // TODO: 分享功能
  ElMessage.info("分享功能开发中...");
};

// 跳转详情
const goToDetail = (id: number) => {
  router.push(`/cultural-content/${id}`);
  window.scrollTo(0, 0);
};

// 分类选择
const handleCategorySelect = (index: string) => {
  router.push({
    path: "/cultural-contents",
    query: { category: index },
  });
};

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr);
  return date.toLocaleDateString("zh-CN");
};

onMounted(() => {
  loadContentDetail();
  loadRecommendContents();
  loadHotContents();
});
</script>

<style lang="scss" scoped>
.content-detail-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    display: grid;
    grid-template-columns: 1fr 300px;
    gap: 20px;

    .article {
      background: #fff;
      border-radius: 8px;
      padding: 30px;

      .article-header {
        margin-bottom: 30px;

        .article-title {
          font-size: 28px;
          color: #333;
          margin: 0 0 20px 0;
          line-height: 1.4;
        }

        .article-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 15px 0;
          border-top: 1px solid #eee;
          border-bottom: 1px solid #eee;

          .meta-left {
            display: flex;
            align-items: center;
            gap: 12px;

            .author-info {
              display: flex;
              flex-direction: column;

              .author-name {
                font-size: 14px;
                color: #333;
                font-weight: 500;
              }

              .publish-time {
                font-size: 12px;
                color: #999;
                margin-top: 3px;
              }
            }
          }

          .meta-right {
            display: flex;
            gap: 20px;
            font-size: 13px;
            color: #666;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }

      .cover-image {
        margin: 20px 0;
        border-radius: 8px;
        overflow: hidden;
      }

      .tags-section {
        margin: 20px 0;
        padding: 15px 0;
        border-top: 1px solid #eee;
        border-bottom: 1px solid #eee;
      }

      .article-content {
        margin: 30px 0;

        .content-html {
          font-size: 16px;
          line-height: 1.8;
          color: #333;

          :deep(p) {
            margin: 15px 0;
          }

          :deep(img) {
            max-width: 100%;
            height: auto;
            margin: 20px 0;
            border-radius: 8px;
          }

          :deep(h2),
          :deep(h3),
          :deep(h4) {
            margin: 25px 0 15px;
            color: #333;
          }
        }
      }

      .interaction-section {
        margin: 40px 0;
        padding: 20px;
        background: #f9f9f9;
        border-radius: 8px;

        .action-buttons {
          display: flex;
          gap: 15px;
          justify-content: center;
        }
      }

      .comment-section {
        margin-top: 40px;

        .section-title {
          font-size: 20px;
          color: #333;
          margin-bottom: 20px;
          padding-bottom: 15px;
          border-bottom: 2px solid #f0f0f0;
        }
      }

      .recommend-section {
        margin-top: 40px;
        padding-top: 30px;
        border-top: 2px solid #f0f0f0;

        .section-title {
          font-size: 20px;
          color: #333;
          margin-bottom: 20px;
        }

        .recommend-list {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 20px;

          .recommend-item {
            display: grid;
            grid-template-columns: 140px 1fr;
            gap: 15px;
            padding: 15px;
            border: 1px solid #eee;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
              border-color: #409eff;
              box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);

              .recommend-title {
                color: #409eff;
              }
            }

            .recommend-cover {
              width: 140px;
              height: 100px;
              border-radius: 6px;
              overflow: hidden;
            }

            .recommend-info {
              .recommend-title {
                font-size: 15px;
                color: #333;
                margin: 0 0 8px 0;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                transition: color 0.3s;
              }

              .recommend-summary {
                font-size: 13px;
                color: #666;
                margin: 0 0 8px 0;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .recommend-meta {
                font-size: 12px;
                color: #999;
                display: flex;
                gap: 10px;
              }
            }
          }
        }
      }
    }

    .sidebar {
      .sidebar-section {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 20px;

        h3 {
          margin: 0 0 15px 0;
          font-size: 16px;
          color: #333;
          padding-bottom: 10px;
          border-bottom: 2px solid #f0f0f0;
        }

        .sidebar-menu {
          border-right: none;

          .el-menu-item {
            height: 40px;
            line-height: 40px;
            font-size: 14px;
          }
        }

        .hot-list {
          .hot-item {
            display: flex;
            align-items: flex-start;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
            cursor: pointer;
            transition: all 0.3s;

            &:last-child {
              border-bottom: none;
            }

            &:hover {
              .hot-title {
                color: #409eff;
              }
            }

            .hot-index {
              width: 24px;
              height: 24px;
              line-height: 24px;
              text-align: center;
              background: #eee;
              color: #666;
              border-radius: 4px;
              font-size: 12px;
              margin-right: 10px;
              flex-shrink: 0;

              &.top3 {
                background: #ff4757;
                color: #fff;
                font-weight: bold;
              }
            }

            .hot-info {
              flex: 1;

              .hot-title {
                font-size: 14px;
                color: #333;
                margin-bottom: 5px;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                transition: color 0.3s;
              }

              .hot-meta {
                font-size: 12px;
                color: #999;
              }
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

    .article {
      .article-header {
        .article-meta {
          flex-direction: column;
          gap: 15px;
        }
      }

      .recommend-list {
        grid-template-columns: 1fr !important;
      }
    }
  }
}
</style>
