<template>
  <div class="content-comment-list">
    <!-- 发表评论 -->
    <div class="comment-form">
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        maxlength="500"
        show-word-limit
      />
      <div class="form-actions">
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div v-loading="loading" class="comment-items">
      <div v-if="comments.length === 0" class="empty-state">
        <el-empty description="暂无评论" />
      </div>

      <div v-else class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-user">
            <el-avatar
              :size="40"
              :src="comment.userInfo?.avatar || defaultAvatar"
            />
            <div class="user-info">
              <div class="nickname">
                {{ comment.userInfo?.nickname || "未知用户" }}
              </div>
              <div class="comment-time">
                {{ formatTime(comment.createdAt) }}
              </div>
            </div>
          </div>
          <div class="comment-content">
            <p>{{ comment.content }}</p>
          </div>
          <div class="comment-actions">
            <LikeButton
              targetType="comment"
              :targetId="comment.id"
              :initialCount="comment.likeCount"
              show-count
            />
            <el-button text @click="toggleReply(comment)"> 回复 </el-button>
          </div>

          <!-- 回复区域 -->
          <div v-if="comment.showReply" class="reply-section">
            <div class="reply-input">
              <el-input
                v-model="replyContents[comment.id]"
                type="textarea"
                :rows="2"
                placeholder="输入回复内容..."
                maxlength="200"
                show-word-limit
              />
              <div class="reply-buttons">
                <el-button
                  size="small"
                  type="primary"
                  @click="submitReply(comment)"
                  :loading="submitting"
                >
                  提交
                </el-button>
                <el-button size="small" @click="toggleReply(comment)"
                  >取消</el-button
                >
              </div>
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
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import LikeButton from "@/components/LikeButton.vue";
import {
  getContentComments,
  createComment,
  type ContentComment,
} from "@/api/modules/contentComment";

const defaultAvatar = "https://placeholder.co/100x100?text=Avatar";

const props = defineProps<{
  contentId: number;
}>();

const loading = ref(false);
const submitting = ref(false);
const comments = ref<ContentComment[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const commentContent = ref("");
const replyContents = ref<Record<number, string>>({});

// 加载评论列表
const loadComments = async () => {
  loading.value = true;
  try {
    const res = await getContentComments({
      contentId: props.contentId,
      current: currentPage.value,
      size: pageSize.value,
    });

    if (res.data.code === 200 && res.data.data) {
      comments.value = res.data.data.records.map((comment) => ({
        ...comment,
        showReply: false,
      }));
      total.value = res.data.data.total;
    }
  } catch (error) {
    console.error("加载评论失败:", error);
    ElMessage.error("加载评论失败");
  } finally {
    loading.value = false;
  }
};

// 发表评论
const handleSubmit = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  submitting.value = true;
  try {
    await createComment({
      contentId: props.contentId,
      content: commentContent.value.trim(),
    });

    ElMessage.success("评论成功");
    commentContent.value = "";
    loadComments();
  } catch (error) {
    console.error("评论失败:", error);
    ElMessage.error("评论失败");
  } finally {
    submitting.value = false;
  }
};

// 切换回复显示
const toggleReply = (comment: ContentComment) => {
  comment.showReply = !comment.showReply;
};

// 提交回复
const submitReply = async (comment: ContentComment) => {
  const content = replyContents.value[comment.id];
  if (!content || !content.trim()) {
    ElMessage.warning("请输入回复内容");
    return;
  }

  submitting.value = true;
  try {
    await createComment({
      contentId: props.contentId,
      parentCommentId: comment.id,
      content: content.trim(),
    });

    ElMessage.success("回复成功");
    replyContents.value[comment.id] = "";
    comment.showReply = false;
    loadComments();
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
  loadComments();
};

const handleCurrentChange = () => {
  loadComments();
};

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

onMounted(() => {
  loadComments();
});
</script>

<style lang="scss" scoped>
.content-comment-list {
  .comment-form {
    margin-bottom: 30px;

    .form-actions {
      margin-top: 10px;
      text-align: right;
    }
  }

  .comment-items {
    min-height: 200px;

    .empty-state {
      text-align: center;
      padding: 40px 0;
    }

    .comment-list {
      .comment-item {
        padding: 20px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .comment-user {
          display: flex;
          align-items: center;
          margin-bottom: 15px;

          .user-info {
            margin-left: 12px;

            .nickname {
              font-size: 14px;
              color: #333;
              font-weight: 500;
            }

            .comment-time {
              font-size: 12px;
              color: #999;
              margin-top: 3px;
            }
          }
        }

        .comment-content {
          margin-bottom: 15px;

          p {
            margin: 0;
            font-size: 14px;
            color: #333;
            line-height: 1.6;
          }
        }

        .comment-actions {
          display: flex;
          gap: 15px;
        }

        .reply-section {
          margin-top: 15px;
          padding: 15px;
          background: #f9f9f9;
          border-radius: 4px;

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
  }

  .pagination {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
