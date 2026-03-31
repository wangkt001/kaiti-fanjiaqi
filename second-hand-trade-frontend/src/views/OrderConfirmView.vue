<template>
  <div class="order-confirm-page">
    <NavBar />

    <div class="page-container">
      <h1 class="page-title">订单确认</h1>

      <div v-loading="loading" class="order-content">
        <!-- 收货地址 -->
        <div class="section">
          <h2 class="section-title">收货信息</h2>
          <el-form :model="addressForm" label-width="80px" class="address-form">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="收货人">
                  <el-input
                    v-model="addressForm.receiverName"
                    placeholder="请输入收货人姓名"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="手机号">
                  <el-input
                    v-model="addressForm.receiverPhone"
                    placeholder="请输入手机号"
                    maxlength="11"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="收货地址">
                  <el-input
                    v-model="addressForm.receiverAddress"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入详细地址"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <!-- 商品列表 -->
        <div class="section">
          <h2 class="section-title">商品信息</h2>
          <div class="goods-list">
            <div v-for="item in orderItems" :key="item.id" class="goods-item">
              <div class="item-image">
                <el-image
                  :src="item.productImage || defaultImage"
                  fit="cover"
                  style="width: 80px; height: 80px; border-radius: 4px"
                />
              </div>
              <div class="item-info">
                <div class="goods-name">{{ item.productName }}</div>
                <div class="goods-spec">
                  <span>数量：{{ item.quantity }}</span>
                </div>
              </div>
              <div class="item-price">
                <span class="price-label">单价：</span>
                <span class="price-value">¥{{ item.price.toFixed(2) }}</span>
              </div>
              <div class="item-total">
                <span class="total-label">小计：</span>
                <span class="total-value"
                  >¥{{ item.totalPrice.toFixed(2) }}</span
                >
              </div>
            </div>
          </div>
        </div>

        <!-- 配送方式 -->
        <div class="section">
          <h2 class="section-title">配送方式</h2>
          <el-radio-group v-model="deliveryType" class="delivery-options">
            <el-radio label="express">快递配送</el-radio>
            <el-radio label="pickup">门店自提</el-radio>
          </el-radio-group>
        </div>

        <!-- 订单备注 -->
        <div class="section">
          <h2 class="section-title">订单备注</h2>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="2"
            placeholder="选填：对本订单的说明"
            maxlength="200"
            show-word-limit
          />
        </div>

        <!-- 支付方式 -->
        <div class="section">
          <h2 class="section-title">支付方式</h2>
          <el-radio-group v-model="paymentType" class="payment-options">
            <el-radio :label="1">
              <el-icon><ChatDotRound /></el-icon>
              微信支付
            </el-radio>
            <el-radio :label="2">
              <el-icon><Wallet /></el-icon>
              支付宝
            </el-radio>
            <el-radio :label="3">
              <el-icon><CreditCard /></el-icon>
              银行卡
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 费用明细 -->
        <div class="section">
          <h2 class="section-title">费用明细</h2>
          <div class="price-summary">
            <div class="price-row">
              <span class="price-label">商品金额：</span>
              <span class="price-value">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="price-row">
              <span class="price-label">运费：</span>
              <span class="price-value">¥{{ freightAmount.toFixed(2) }}</span>
            </div>
            <div class="price-row discount">
              <span class="price-label">优惠金额：</span>
              <span class="price-value">-¥{{ discountAmount.toFixed(2) }}</span>
            </div>
            <el-divider />
            <div class="price-row total">
              <span class="price-label">实付款：</span>
              <span class="total-amount">¥{{ paymentAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <!-- 提交订单 -->
        <div class="order-actions">
          <div class="action-left">
            <span class="tip"
              >温馨提示：提交订单即表示您同意我们的服务条款</span
            >
          </div>
          <div class="action-right">
            <div class="total-info">
              <span class="total-label">合计：</span>
              <span class="total-value">¥{{ paymentAmount.toFixed(2) }}</span>
            </div>
            <el-button
              type="primary"
              size="large"
              @click="handleSubmit"
              :loading="submitting"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ChatDotRound, Wallet, CreditCard } from "@element-plus/icons-vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { createOrder, getCheckoutInfo } from "@/api/modules/order";
import { getCartItems } from "@/api/modules/cart";
import type { OrderItem } from "@/api/modules/order";

const defaultImage = "https://placehold.co/100x100?text=No+Image";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const submitting = ref(false);
const orderItems = ref<OrderItem[]>([]);
const deliveryType = ref("express");
const remark = ref("");
const paymentType = ref(1);

const addressForm = reactive({
  receiverName: "",
  receiverPhone: "",
  receiverAddress: "",
});

// 计算总金额
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.totalPrice, 0);
});

const freightAmount = computed(() => {
  return deliveryType.value === "express" ? 10 : 0;
});

const discountAmount = computed(() => {
  return 0; // 简化：暂无优惠
});

const paymentAmount = computed(() => {
  return totalAmount.value + freightAmount.value - discountAmount.value;
});

// 加载结算信息
const loadCheckoutInfo = async () => {
  loading.value = true;
  try {
    const cartItemIds = route.query.cartItemIds as string;
    if (cartItemIds) {
      // 根据 cartItemIds 获取结算信息
      const res = await getCheckoutInfo(cartItemIds.split(",").map(Number));
      if (res && res.items) {
        const items = res.items;
        orderItems.value = items.map((item: any) => ({
          id: item.id,
          productId: item.productId,
          productName: item.product.name,
          productImage: item.product.mainImage,
          price: item.product.price,
          quantity: item.quantity,
          totalPrice: item.product.price * item.quantity,
        }));
      }
    } else {
      // 如果没有传入 cartItemIds，获取所有选中的购物车项
      const res = await getCartItems();
      if (res) {
        const selectedItems = res.filter((item: any) => item.selected);
        orderItems.value = selectedItems.map((item: any) => ({
          id: item.id,
          productId: item.productId,
          productName: item.product.name,
          productImage: item.product.mainImage,
          price: item.product.price,
          quantity: item.quantity,
          totalPrice: item.product.price * item.quantity,
        }));
      }
    }
  } catch (error) {
    console.error("加载结算信息失败:", error);
    ElMessage.error("加载失败");
  } finally {
    loading.value = false;
  }
};

// 提交订单
const handleSubmit = async () => {
  // 验证收货信息
  if (
    !addressForm.receiverName ||
    !addressForm.receiverPhone ||
    !addressForm.receiverAddress
  ) {
    ElMessage.warning("请填写完整的收货信息");
    return;
  }

  // 验证手机号
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(addressForm.receiverPhone)) {
    ElMessage.warning("请输入正确的手机号");
    return;
  }

  if (orderItems.value.length === 0) {
    ElMessage.warning("请选择商品");
    return;
  }

  submitting.value = true;
  try {
    const cartItemIds = orderItems.value.map((item) => item.id);

    const res = await createOrder({
      cartItemIds,
      receiverName: addressForm.receiverName,
      receiverPhone: addressForm.receiverPhone,
      receiverAddress: addressForm.receiverAddress,
      remark: remark.value,
      paymentType: paymentType.value,
    });

    ElMessage.success("订单创建成功");
    const orderNo = res;
    router.push({
      path: "/order/pay",
      query: {
        orderNo: orderNo,
        amount: paymentAmount.value.toFixed(2),
      },
    });
  } catch (error) {
    console.error("创建订单失败:", error);
    ElMessage.error("创建订单失败");
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  loadCheckoutInfo();
});
</script>

<style lang="scss" scoped>
.order-confirm-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;

    .page-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 20px;
    }

    .order-content {
      background: #fff;
      border-radius: 8px;
      padding: 30px;

      .section {
        margin-bottom: 30px;
        padding-bottom: 30px;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .section-title {
          font-size: 18px;
          color: #333;
          margin-bottom: 20px;
          font-weight: 600;
        }

        .address-form {
          max-width: 600px;
        }

        .goods-list {
          .goods-item {
            display: grid;
            grid-template-columns: 80px 1fr 150px 120px;
            gap: 15px;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #f0f0f0;

            &:last-child {
              border-bottom: none;
            }

            .item-info {
              .goods-name {
                font-size: 14px;
                color: #333;
                margin-bottom: 8px;
              }

              .goods-spec {
                font-size: 13px;
                color: #999;
              }
            }

            .item-price {
              .price-label {
                font-size: 13px;
                color: #999;
              }

              .price-value {
                font-size: 16px;
                color: #ff4757;
                font-weight: 600;
              }
            }

            .item-total {
              text-align: right;

              .total-label {
                font-size: 13px;
                color: #999;
              }

              .total-value {
                font-size: 18px;
                color: #ff4757;
                font-weight: 600;
              }
            }
          }
        }

        .delivery-options,
        .payment-options {
          display: flex;
          flex-direction: column;
          gap: 10px;

          .el-radio {
            font-size: 14px;
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

            &.discount {
              .price-value {
                color: #ff4757;
              }
            }

            &.total {
              font-size: 16px;
              font-weight: 600;

              .total-amount {
                font-size: 24px;
                color: #ff4757;
              }
            }

            .price-label {
              color: #666;
            }

            .price-value {
              color: #333;
              font-weight: 500;
            }
          }
        }
      }

      .order-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 20px;
        border-top: 2px solid #f0f0f0;

        .action-left {
          .tip {
            font-size: 13px;
            color: #999;
          }
        }

        .action-right {
          display: flex;
          align-items: center;
          gap: 20px;

          .total-info {
            .total-label {
              font-size: 14px;
              color: #666;
            }

            .total-value {
              font-size: 24px;
              color: #ff4757;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}
</style>
