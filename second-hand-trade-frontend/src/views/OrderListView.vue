<template>
  <div class="order-list-page">
    <NavBar />

    <div class="page-container">
      <div v-if="isSeller" class="seller-notice">
        <el-empty description="卖家账号无法查看买家订单">
          <el-button type="primary" @click="$router.push('/seller')"
            >前往卖家中心</el-button
          >
        </el-empty>
      </div>
      <div v-else>
        <h1 class="page-title">我的订单</h1>

        <div class="order-content">
          <!-- 订单状态筛选 -->
          <div class="filter-tabs">
            <el-tabs v-model="currentStatus" @tab-change="handleStatusChange">
              <el-tab-pane label="全部订单" :name="-1" />
              <el-tab-pane label="待付款" :name="0" />
              <el-tab-pane label="待发货" :name="1" />
              <el-tab-pane label="待收货" :name="2" />
              <el-tab-pane label="已完成" :name="3" />
            </el-tabs>
          </div>

          <!-- 订单列表 -->
          <div v-loading="loading" class="orders">
            <div v-if="orders.length === 0" class="empty-orders">
              <el-empty description="暂无订单">
                <el-button type="primary" @click="$router.push('/goods')"
                  >去购物</el-button
                >
              </el-empty>
            </div>

            <div v-else class="order-list">
              <div v-for="order in orders" :key="order.id" class="order-item">
                <!-- 订单头部 -->
                <div class="order-header">
                  <span class="order-no">订单编号：{{ order.orderNo }}</span>
                  <span class="order-status" :class="`status-${order.status}`">
                    {{ getStatusText(order.status) }}
                  </span>
                  <span class="order-time">{{
                    formatTime(order.createdAt)
                  }}</span>
                </div>

                <!-- 订单商品 -->
                <div class="order-goods">
                  <div
                    v-for="item in order.items"
                    :key="item.id"
                    class="goods-item"
                  >
                    <div class="goods-image">
                      <el-image
                        :src="item.productImage || defaultImage"
                        fit="cover"
                        style="width: 80px; height: 80px; border-radius: 4px"
                      />
                    </div>
                    <div class="goods-info">
                      <div class="goods-name">{{ item.productName }}</div>
                      <div class="goods-spec">数量：{{ item.quantity }}</div>
                    </div>
                    <div class="goods-price">¥{{ item.price.toFixed(2) }}</div>
                    <div class="goods-total">
                      ¥{{ item.totalPrice.toFixed(2) }}
                    </div>
                  </div>
                </div>

                <!-- 订单金额 -->
                <div class="order-footer">
                  <div class="order-amount">
                    <span class="amount-label">订单总额：</span>
                    <span class="amount-value"
                      >¥{{ order.paymentAmount.toFixed(2) }}</span
                    >
                  </div>
                  <div class="order-actions">
                    <template v-if="order.status === 0">
                      <el-button
                        size="small"
                        @click="handleCancelOrder(order.id)"
                      >
                        取消订单
                      </el-button>
                      <el-button
                        size="small"
                        type="primary"
                        @click="handlePayOrder(order)"
                      >
                        立即付款
                      </el-button>
                    </template>
                    <template v-else-if="order.status === 2">
                      <el-button
                        size="small"
                        type="primary"
                        @click="handleConfirmOrder(order.id)"
                      >
                        确认收货
                      </el-button>
                    </template>
                    <template
                      v-else-if="order.status === 3 || order.status === 4"
                    >
                      <el-button
                        size="small"
                        @click="handleDeleteOrder(order.id)"
                      >
                        删除订单
                      </el-button>
                    </template>
                    <el-button size="small" @click="handleViewDetail(order.id)">
                      订单详情
                    </el-button>
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
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/user";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import {
  getOrderList,
  cancelOrder,
  confirmOrder,
  deleteOrder,
} from "@/api/modules/order";
import type { Order } from "@/api/modules/order";

const defaultImage = "https://placehold.co/100x100?text=No+Image";

const router = useRouter();
const userStore = useUserStore();

const isSeller = computed(() => userStore.userInfo?.role === "seller");

const loading = ref(false);
const orders = ref<Order[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const currentStatus = ref(-1);

// 获取订单状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "待付款",
    1: "待发货",
    2: "待收货",
    3: "已完成",
    4: "已取消",
  };
  return statusMap[status] || "未知状态";
};

// 加载订单列表
const loadOrders = async () => {
  loading.value = true;
  try {
    const res = await getOrderList({
      status: currentStatus.value === -1 ? undefined : currentStatus.value,
      current: currentPage.value,
      size: pageSize.value,
    });

    if (res) {
      orders.value = res.records;
      total.value = res.total;
    }
  } catch (error) {
    console.error("加载订单失败:", error);
    ElMessage.error("加载订单失败");
  } finally {
    loading.value = false;
  }
};

// 状态筛选
const handleStatusChange = () => {
  currentPage.value = 1;
  loadOrders();
};

// 分页处理
const handleSizeChange = () => {
  currentPage.value = 1;
  loadOrders();
};

const handleCurrentChange = () => {
  loadOrders();
};

// 取消订单
const handleCancelOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm("确定要取消该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await cancelOrder(orderId);
    ElMessage.success("订单已取消");
    loadOrders();
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败");
    }
  }
};

// 确认收货
const handleConfirmOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm("确定已收到商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await confirmOrder(orderId);
    ElMessage.success("确认收货成功");
    loadOrders();
  } catch (error) {
    if (error !== "cancel") {
      console.error("确认收货失败:", error);
      ElMessage.error("确认收货失败");
    }
  }
};

// 删除订单
const handleDeleteOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm("确定要删除该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteOrder(orderId);
    ElMessage.success("订单已删除");
    loadOrders();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除订单失败:", error);
      ElMessage.error("删除订单失败");
    }
  }
};

// 查看订单详情
const handleViewDetail = (orderId: number) => {
  router.push(`/order/detail/${orderId}`);
};

// 支付订单
const handlePayOrder = (order: Order) => {
  router.push({
    path: "/order/pay",
    query: {
      orderNo: order.orderNo,
      amount: order.paymentAmount.toString(),
    },
  });
};

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr);
  return date.toLocaleString("zh-CN");
};

onMounted(() => {
  loadOrders();
});
</script>

<style lang="scss" scoped>
.order-list-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;

    .seller-notice {
      background: #fff;
      border-radius: 8px;
      padding: 60px 20px;
      text-align: center;
    }

    .page-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 20px;
    }

    .order-content {
      background: #fff;
      border-radius: 8px;
      padding: 20px;

      .filter-tabs {
        margin-bottom: 20px;
      }

      .orders {
        min-height: 400px;

        .empty-orders {
          text-align: center;
          padding: 80px 0;
        }

        .order-list {
          .order-item {
            border: 1px solid #eee;
            border-radius: 8px;
            margin-bottom: 20px;
            overflow: hidden;

            .order-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 15px 20px;
              background: #f9f9f9;
              border-bottom: 1px solid #eee;
              font-size: 13px;

              .order-status {
                padding: 3px 10px;
                border-radius: 4px;
                font-weight: 500;

                &.status-0 {
                  background: #fff7e6;
                  color: #ff4757;
                }

                &.status-1 {
                  background: #e6f7ff;
                  color: #1890ff;
                }

                &.status-2 {
                  background: #f6ffed;
                  color: #52c41a;
                }

                &.status-3 {
                  background: #f0f0f0;
                  color: #666;
                }

                &.status-4 {
                  background: #f5f5f5;
                  color: #999;
                }
              }

              .order-time {
                color: #999;
              }
            }

            .order-goods {
              padding: 20px;

              .goods-item {
                display: grid;
                grid-template-columns: 80px 1fr 100px 100px;
                gap: 15px;
                align-items: center;
                padding: 15px 0;
                border-bottom: 1px solid #f0f0f0;

                &:last-child {
                  border-bottom: none;
                }

                .goods-info {
                  .goods-name {
                    font-size: 14px;
                    color: #333;
                    margin-bottom: 5px;
                  }

                  .goods-spec {
                    font-size: 13px;
                    color: #999;
                  }
                }

                .goods-price {
                  text-align: right;
                  font-size: 14px;
                  color: #666;
                }

                .goods-total {
                  text-align: right;
                  font-size: 16px;
                  color: #ff4757;
                  font-weight: 600;
                }
              }
            }

            .order-footer {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 15px 20px;
              background: #f9f9f9;
              border-top: 1px solid #eee;

              .order-amount {
                .amount-label {
                  font-size: 13px;
                  color: #666;
                }

                .amount-value {
                  font-size: 18px;
                  color: #ff4757;
                  font-weight: 600;
                }
              }

              .order-actions {
                display: flex;
                gap: 10px;
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
  }
}
</style>
