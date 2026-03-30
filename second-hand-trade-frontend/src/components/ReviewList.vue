<template>
  <div class="review-list">
    <div class="review-header">
      <h3>商品评价 ({{ total }})</h3>
      <div class="filter-tags">
        <el-tag
          v-for="item in ratingFilters"
          :key="item.value"
          :type="currentRating === item.value ? 'primary' : 'info'"
          effect="plain"
          style="cursor: pointer; margin-right: 8px"
          @click="handleRatingFilter(item.value)"
        >
          {{ item.label }}
        </el-tag>
      </div>
    </div>

    <!-- 评价列表 -->
    <div v-loading="loading" class="review-items">
      <div v-if="reviews.length === 0" class="empty-state">
        <el-empty description="暂无评价" />
      </div>

      <div
        v-else
        class="review-item"
        v-for="review in reviews"
        :key="review.id"
      >
        <div class="review-user">
          <el-avatar
            :size="50"
            :src="review.userInfo?.avatar || defaultAvatar"
          />
          <div class="user-info">
            <div class="nickname">
              {{ review.isAnonymous ? "匿名用户" : review.userInfo?.nickname }}
            </div>
            <div class="rating">
              <el-rate
                v-model="review.rating"
                disabled
                show-text
                text-color="#ff9900"
              />
            </div>
          </div>
          <div class="review-time">{{ formatTime(review.createdAt) }}</div>
        </div>

        <div class="review-content">
          <p v-if="review.content">{{ review.content }}</p>

          <!-- 评价图片 -->
          <div
            v-if="review.images && review.images.length > 0"
            class="review-images"
          >
            <el-image
              v-for="(img, index) in review.images"
              :key="index"
              :src="img"
              :preview-src-list="review.images"
              :initial-index="index"
              fit="cover"
              style="
                width: 100px;
                height: 100px;
                margin-right: 8px;
                border-radius: 4px;
              "
            />
          </div>
        </div>

        <div class="review-actions">
          <div class="action-item">
            <el-button
              text
              :type="review.isLiked ? 'primary' : 'default'"
              @click="handleLike(review)"
            >
              <el-icon><Star /></el-icon>
              {{ review.likeCount }}
            </el-button>
          </div>
          <div class="action-item">
            <el-button text @click="toggleReply(review)">
              <el-icon><ChatDotRound /></el-icon>
              {{ review.replyCount }} 回复
            </el-button>
          </div>
        </div>

        <!-- 回复区域 -->
        <div v-if="review.showReply" class="reply-section">
          <!-- 回复列表 -->
          <div
            v-if="review.replies && review.replies.length > 0"
            class="reply-list"
          >
            <div
              v-for="reply in review.replies"
              :key="reply.id"
              class="reply-item"
            >
              <div class="reply-user">
                <el-avatar
                  :size="30"
                  :src="reply.userInfo?.avatar || defaultAvatar"
                />
                <span class="reply-nickname">{{
                  reply.userInfo?.nickname || "未知用户"
                }}</span>
                <span class="reply-time">{{
                  formatTime(reply.createdAt)
                }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>

          <!-- 回复输入框 -->
          <div class="reply-input">
            <el-input
              v-model="replyContents[review.id]"
              type="textarea"
              :rows="2"
              placeholder="输入回复内容..."
              maxlength="200"
              show-word-limit
            >
            </el-input>
            <div class="reply-buttons">
              <el-button
                size="small"
                type="primary"
                @click="submitReply(review)"
                :loading="submitting"
              >
                提交回复
              </el-button>
              <el-button size="small" @click="toggleReply(review)"
                >取消</el-button
              >
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
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { Star, ChatDotRound } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  getProductReviews,
  likeReview,
  unlikeReview,
  replyReview,
  type Review,
} from "@/api/modules/review";
import { getLikeStatus } from "@/api/modules/like";
import { useUserStore } from "@/store/user";

const defaultAvatar = "https://placeholder.co/100x100?text=Avatar";

const props = defineProps<{
  productId: number;
}>();

const userStore = useUserStore();

const loading = ref(false);
const submitting = ref(false);
const reviews = ref<Review[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const currentRating = ref<number | undefined>();

const ratingFilters = [
  { label: "全部", value: undefined as number | undefined },
  { label: "好评 (4-5 星)", value: 4 as number | undefined },
  { label: "中评 (3 星)", value: 3 as number | undefined },
  { label: "差评 (1-2 星)", value: 1 as number | undefined },
];

const replyContents = ref<Record<number, string>>({});

// 加载评价列表
const loadReviews = async () => {
  if (!Number.isFinite(props.productId) || props.productId <= 0) {
    reviews.value = [];
    total.value = 0;
    return;
  }

  loading.value = true;
  try {
    const res = await getProductReviews({
      productId: props.productId,
      current: currentPage.value,
      size: pageSize.value,
      rating: currentRating.value,
    });

    if (res) {
      reviews.value = res.records.map((review) => ({
        ...review,
        showReply: false,
        isLiked: false,
        replies: [],
      }));
      total.value = res.total;

      // 加载每个评价的点赞状态
      if (userStore.isLoggedIn) {
        reviews.value.forEach(async (review) => {
          const statusRes = await getLikeStatus("review", review.id);
          if (statusRes) {
            review.isLiked = statusRes.liked;
          }
        });
      }
    }
  } catch (error) {
    console.error("加载评价失败:", error);
    ElMessage.error("加载评价失败");
  } finally {
    loading.value = false;
  }
};

// 筛选评分
const handleRatingFilter = (rating: number | undefined) => {
  currentRating.value = rating;
  currentPage.value = 1;
  loadReviews();
};

// 点赞评价
const handleLike = async (review: Review) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    return;
  }

  try {
    if (review.isLiked) {
      await unlikeReview(review.id);
      review.isLiked = false;
      review.likeCount = Math.max(0, review.likeCount - 1);
      ElMessage.success("已取消点赞");
    } else {
      await likeReview(review.id);
      review.isLiked = true;
      review.likeCount += 1;
      ElMessage.success("点赞成功");
    }
  } catch (error) {
    console.error("点赞操作失败:", error);
    ElMessage.error("操作失败");
  }
};

// 切换回复显示
const toggleReply = (review: Review) => {
  review.showReply = !review.showReply;
  if (review.showReply && (!review.replies || review.replies.length === 0)) {
    loadReplies(review);
  }
};

// 加载回复列表
const loadReplies = async (review: Review) => {
  try {
    const res = await getProductReviews({
      productId: props.productId,
      current: 1,
      size: 50,
    });

    if (res) {
      // 这里简化处理，实际应该调用专门的回复接口
      review.replies = [];
    }
  } catch (error) {
    console.error("加载回复失败:", error);
  }
};

// 提交回复
const submitReply = async (review: Review) => {
  const content = replyContents.value[review.id];
  if (!content || !content.trim()) {
    ElMessage.warning("请输入回复内容");
    return;
  }

  submitting.value = true;
  try {
    await replyReview({
      reviewId: review.id,
      content: content.trim(),
    });

    ElMessage.success("回复成功");
    replyContents.value[review.id] = "";
    loadReviews();
  } catch (error) {
    console.error("回复失败:", error);
    ElMessage.error("回复失败");
  } finally {
    submitting.value = false;
  }
};

// 分页处理
const handleSizeChange = () => {
  currentPage.value = 1;
  loadReviews();
};

const handleCurrentChange = () => {
  loadReviews();
};

watch(
  () => props.productId,
  (productId) => {
    if (!Number.isFinite(productId) || productId <= 0) {
      reviews.value = [];
      total.value = 0;
      return;
    }
    currentPage.value = 1;
    loadReviews();
  },
  { immediate: true },
);

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;

  if (diff < minute) {
    return "刚刚";
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`;
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`;
  } else {
    return date.toLocaleDateString("zh-CN");
  }
};
</script>

<style lang="scss" scoped>
.review-list {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 20px;

  .review-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;

    h3 {
      margin: 0;
      font-size: 18px;
      color: #333;
    }

    .filter-tags {
      display: flex;
    }
  }

  .review-items {
    .empty-state {
      text-align: center;
      padding: 40px 0;
    }

    .review-item {
      padding: 20px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .review-user {
        display: flex;
        align-items: flex-start;
        margin-bottom: 15px;

        .user-info {
          flex: 1;
          margin-left: 12px;

          .nickname {
            font-size: 14px;
            color: #333;
            font-weight: 500;
            margin-bottom: 5px;
          }

          .rating {
            display: flex;
            align-items: center;
          }
        }

        .review-time {
          font-size: 12px;
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
        display: flex;
        gap: 20px;

        .action-item {
          .el-button {
            padding: 5px 10px;
            font-size: 13px;
          }
        }
      }

      .reply-section {
        margin-top: 15px;
        padding: 15px;
        background: #f9f9f9;
        border-radius: 4px;

        .reply-list {
          margin-bottom: 15px;

          .reply-item {
            padding: 10px;
            margin-bottom: 10px;
            background: #fff;
            border-radius: 4px;

            &:last-child {
              margin-bottom: 0;
            }

            .reply-user {
              display: flex;
              align-items: center;
              margin-bottom: 8px;

              .reply-nickname {
                margin-left: 8px;
                font-size: 13px;
                color: #333;
                font-weight: 500;
              }

              .reply-time {
                margin-left: auto;
                font-size: 12px;
                color: #999;
              }
            }

            .reply-content {
              font-size: 13px;
              color: #333;
              line-height: 1.5;
              margin-left: 38px;
            }
          }
        }

        .reply-input {
          .reply-buttons {
            margin-top: 10px;
            text-align: right;

            .el-button {
              margin-left: 10px;
            }
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
