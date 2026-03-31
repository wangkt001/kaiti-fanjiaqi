<template>
  <div class="order-detail-page">
    <NavBar />

    <div class="page-container">
      <div v-loading="loading" class="order-detail">
        <template v-if="order">
          <!-- 订单状态 -->
          <div class="order-status-section">
            <div class="status-info">
              <div class="status-icon" :class="`status-${order.status}`">
                <el-icon :size="40">
                  <component :is="getStatusIcon(order.status)" />
                </el-icon>
              </div>
              <div class="status-text">
                <div class="status-title">
                  {{ getStatusText(order.status) }}
                </div>
                <div class="status-desc">
                  <template v-if="order.status === 0">请尽快完成支付</template>
                  <template v-else-if="order.status === 1"
                    >商家正在准备发货</template
                  >
                  <template v-else-if="order.status === 2"
                    >商品已发出，请注意查收</template
                  >
                  <template v-else-if="order.status === 3">交易已完成</template>
                </div>
              </div>
            </div>
            <div class="status-actions">
              <template v-if="order.status === 0">
                <el-button type="primary" size="large" @click="handlePayOrder">
                  立即付款
                </el-button>
                <el-button @click="handleCancelOrder">取消订单</el-button>
              </template>
              <template v-else-if="order.status === 2">
                <el-button
                  type="primary"
                  size="large"
                  @click="handleConfirmOrder"
                >
                  确认收货
                </el-button>
              </template>
              <template v-else-if="order.status === 3 || order.status === 4">
                <el-button @click="handleDeleteOrder">删除订单</el-button>
              </template>
            </div>
          </div>

          <!-- 收货信息 -->
          <div class="section">
            <h3 class="section-title">收货信息</h3>
            <div class="address-info">
              <div class="info-row">
                <span class="label">收货人：</span>
                <span class="value">{{ order.receiverName }}</span>
              </div>
              <div class="info-row">
                <span class="label">手机号：</span>
                <span class="value">{{ order.receiverPhone }}</span>
              </div>
              <div class="info-row">
                <span class="label">收货地址：</span>
                <span class="value">{{ order.receiverAddress }}</span>
              </div>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="section">
            <h3 class="section-title">商品信息</h3>
            <div class="goods-list">
              <div
                v-for="item in order.items"
                :key="item.id"
                class="goods-item"
              >
                <div class="goods-image">
                  <el-image
                    :src="item.productImage || defaultImage"
                    fit="cover"
                    style="width: 100px; height: 100px; border-radius: 4px"
                  />
                </div>
                <div class="goods-info">
                  <div class="goods-name">{{ item.productName }}</div>
                  <div class="goods-spec">数量：{{ item.quantity }}</div>
                </div>
                <div class="goods-price">¥{{ item.price.toFixed(2) }}</div>
                <div class="goods-total">¥{{ item.totalPrice.toFixed(2) }}</div>
              </div>
            </div>
          </div>

          <!-- 订单信息 -->
          <div class="section">
            <h3 class="section-title">订单信息</h3>
            <div class="order-info">
              <div class="info-row">
                <span class="label">订单编号：</span>
                <span class="value">{{ order.orderNo }}</span>
              </div>
              <div class="info-row">
                <span class="label">下单时间：</span>
                <span class="value">{{ formatTime(order.createdAt) }}</span>
              </div>
              <div class="info-row" v-if="order.paymentTime">
                <span class="label">支付时间：</span>
                <span class="value">{{ formatTime(order.paymentTime) }}</span>
              </div>
              <div class="info-row" v-if="order.deliveryTime">
                <span class="label">发货时间：</span>
                <span class="value">{{ formatTime(order.deliveryTime) }}</span>
              </div>
              <div class="info-row" v-if="order.deliveryNo">
                <span class="label">物流单号：</span>
                <span class="value">{{ order.deliveryNo }}</span>
              </div>
              <div class="info-row" v-if="order.remark">
                <span class="label">订单备注：</span>
                <span class="value">{{ order.remark }}</span>
              </div>
            </div>
          </div>

          <!-- 费用明细 -->
          <div class="section">
            <h3 class="section-title">费用明细</h3>
            <div class="price-summary">
              <div class="price-row">
                <span class="label">商品金额：</span>
                <span class="value">¥{{ order.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="price-row">
                <span class="label">运费：</span>
                <span class="value">¥{{ order.freightAmount.toFixed(2) }}</span>
              </div>
              <div class="price-row" v-if="order.discountAmount > 0">
                <span class="label">优惠金额：</span>
                <span class="value"
                  >-¥{{ order.discountAmount.toFixed(2) }}</span
                >
              </div>
              <el-divider />
              <div class="price-row total">
                <span class="label">实付款：</span>
                <span class="total-amount"
                  >¥{{ order.paymentAmount.toFixed(2) }}</span
                >
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { CircleCheck, Clock, Van, Finished } from "@element-plus/icons-vue";
import type { Component } from "vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import {
  getOrderDetail,
  cancelOrder,
  confirmOrder,
  deleteOrder,
} from "@/api/modules/order";
import type { Order } from "@/api/modules/order";

const defaultImage = "https://placehold.co/100x100?text=No+Image";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const order = ref<Order | null>(null);

// 获取状态文本
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

// 获取状态图标
const getStatusIcon = (status: number): Component => {
  const iconMap: Record<number, Component> = {
    0: Clock,
    1: CircleCheck,
    2: Van,
    3: Finished,
    4: CircleCheck,
  };
  return iconMap[status] || Clock;
};

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true;
  try {
    const orderId = Number(route.params.id);
    const res = await getOrderDetail(orderId);

    if (res) {
      order.value = res;
    }
  } catch (error) {
    console.error("加载订单详情失败:", error);
    ElMessage.error("加载订单详情失败");
  } finally {
    loading.value = false;
  }
};

// 取消订单
const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm("确定要取消该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    if (!order.value) return;
    await cancelOrder(order.value.id);
    ElMessage.success("订单已取消");
    loadOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败");
    }
  }
};

// 确认收货
const handleConfirmOrder = async () => {
  try {
    await ElMessageBox.confirm("确定已收到商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    if (!order.value) return;
    await confirmOrder(order.value.id);
    ElMessage.success("确认收货成功");
    loadOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      console.error("确认收货失败:", error);
      ElMessage.error("确认收货失败");
    }
  }
};

// 删除订单
const handleDeleteOrder = async () => {
  try {
    await ElMessageBox.confirm("确定要删除该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    if (!order.value) return;
    await deleteOrder(order.value.id);
    ElMessage.success("订单已删除");
    router.push("/order");
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除订单失败:", error);
      ElMessage.error("删除订单失败");
    }
  }
};

// 支付订单
const handlePayOrder = () => {
  if (!order.value) return;
  router.push({
    path: "/order/pay",
    query: {
      orderNo: order.value.orderNo,
      amount: order.value.paymentAmount.toString(),
    },
  });
};

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr);
  return date.toLocaleString("zh-CN");
};

onMounted(() => {
  loadOrderDetail();
});
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;

    .order-detail {
      background: #fff;
      border-radius: 8px;
      padding: 30px;

      .order-status-section {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 30px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 8px;
        margin-bottom: 20px;
        color: #fff;

        .status-info {
          display: flex;
          align-items: center;
          gap: 20px;

          .status-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.2);
            display: flex;
            align-items: center;
            justify-content: center;
          }

          .status-text {
            .status-title {
              font-size: 24px;
              font-weight: 600;
              margin-bottom: 5px;
            }

            .status-desc {
              font-size: 14px;
              opacity: 0.9;
            }
          }
        }

        .status-actions {
          display: flex;
          gap: 10px;
        }
      }

      .section {
        margin-bottom: 20px;
        padding-bottom: 20px;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .section-title {
          font-size: 16px;
          color: #333;
          margin-bottom: 15px;
          font-weight: 600;
        }

        .address-info,
        .order-info {
          .info-row {
            display: flex;
            margin-bottom: 10px;
            font-size: 14px;

            .label {
              width: 100px;
              color: #666;
              flex-shrink: 0;
            }

            .value {
              color: #333;
            }
          }
        }

        .goods-list {
          .goods-item {
            display: grid;
            grid-template-columns: 100px 1fr 100px 100px;
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

        .price-summary {
          max-width: 400px;
          margin-left: auto;

          .price-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;

            &.total {
              font-size: 16px;
              font-weight: 600;

              .total-amount {
                font-size: 24px;
                color: #ff4757;
              }
            }

            .label {
              color: #666;
            }

            .value {
              color: #333;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}
</style>
