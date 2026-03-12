<template>
  <div class="pay-page">
    <NavBar />

    <div class="page-container">
      <div class="pay-content">
        <h1 class="page-title">订单支付</h1>

        <!-- 订单信息 -->
        <div class="order-info">
          <div class="info-row">
            <span class="label">订单编号：</span>
            <span class="value">{{ orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付金额：</span>
            <span class="amount">¥{{ amount }}</span>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="section">
          <h2 class="section-title">选择支付方式</h2>
          <el-radio-group v-model="paymentMethod" class="payment-methods">
            <div class="method-item" :class="{ selected: paymentMethod === 1 }">
              <el-radio :label="1">
                <div class="method-content">
                  <el-icon :size="30"><ChatDotRound /></el-icon>
                  <div class="method-info">
                    <div class="method-name">微信支付</div>
                    <div class="method-desc">推荐使用</div>
                  </div>
                </div>
              </el-radio>
            </div>

            <div class="method-item" :class="{ selected: paymentMethod === 2 }">
              <el-radio :label="2">
                <div class="method-content">
                  <el-icon :size="30"><Wallet /></el-icon>
                  <div class="method-info">
                    <div class="method-name">支付宝</div>
                    <div class="method-desc">安全便捷</div>
                  </div>
                </div>
              </el-radio>
            </div>

            <div class="method-item" :class="{ selected: paymentMethod === 3 }">
              <el-radio :label="3">
                <div class="method-content">
                  <el-icon :size="30"><CreditCard /></el-icon>
                  <div class="method-info">
                    <div class="method-name">银行卡</div>
                    <div class="method-desc">支持各大银行</div>
                  </div>
                </div>
              </el-radio>
            </div>
          </el-radio-group>
        </div>

        <!-- 支付密码（模拟） -->
        <div v-if="showPassword" class="section">
          <h2 class="section-title">支付密码</h2>
          <div class="password-input">
            <el-input
              v-model="password"
              type="password"
              placeholder="请输入支付密码"
              maxlength="6"
              show-password
              style="max-width: 300px"
            />
            <div class="password-tip">模拟支付，任意 6 位数字即可</div>
          </div>
        </div>

        <!-- 支付按钮 -->
        <div class="pay-actions">
          <div class="pay-info">
            <span class="pay-label">支付金额：</span>
            <span class="pay-amount">¥{{ amount }}</span>
          </div>
          <el-button
            type="primary"
            size="large"
            :loading="paying"
            @click="handlePay"
          >
            {{ paying ? "支付中..." : "确认支付" }}
          </el-button>
        </div>

        <!-- 支付成功提示 -->
        <el-dialog
          v-model="successDialogVisible"
          title="支付成功"
          width="400px"
          :close-on-click-modal="false"
          :show-close="false"
        >
          <div class="success-content">
            <el-icon :size="60" color="#67c23a"><CircleCheck /></el-icon>
            <div class="success-text">支付成功！</div>
            <div class="success-desc">订单支付已完成</div>
          </div>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="handleViewOrder">查看订单</el-button>
              <el-button type="primary" @click="handleBackToHome"
                >返回首页</el-button
              >
            </div>
          </template>
        </el-dialog>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  CircleCheck,
  ChatDotRound,
  Wallet,
  CreditCard,
} from "@element-plus/icons-vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { payOrder } from "@/api/modules/order";

const route = useRoute();
const router = useRouter();

const orderNo = ref("");
const amount = ref("0.00");
const paymentMethod = ref(1);
const showPassword = ref(false);
const password = ref("");
const paying = ref(false);
const successDialogVisible = ref(false);

// 监听支付方式变化
const handlePaymentMethodChange = () => {
  showPassword.value = true;
};

// 支付处理
const handlePay = async () => {
  if (showPassword.value && (!password.value || password.value.length < 6)) {
    ElMessage.warning("请输入 6 位支付密码");
    return;
  }

  paying.value = true;

  // 模拟支付过程
  setTimeout(async () => {
    try {
      // 调用支付 API（模拟）
      await payOrder(orderNo.value, paymentMethod.value);

      successDialogVisible.value = true;
    } catch (error) {
      console.error("支付失败:", error);
      ElMessage.error("支付失败，请重试");
    } finally {
      paying.value = false;
    }
  }, 1500);
};

// 查看订单
const handleViewOrder = () => {
  successDialogVisible.value = false;
  router.push("/order");
};

// 返回首页
const handleBackToHome = () => {
  successDialogVisible.value = false;
  router.push("/");
};

onMounted(() => {
  orderNo.value = (route.query.orderNo as string) || "";
  amount.value = (route.query.amount as string) || "0.00";
});
</script>

<style lang="scss" scoped>
.pay-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;

    .pay-content {
      background: #fff;
      border-radius: 8px;
      padding: 40px;

      .page-title {
        font-size: 24px;
        color: #333;
        text-align: center;
        margin-bottom: 30px;
      }

      .order-info {
        background: #f9f9f9;
        padding: 20px;
        border-radius: 8px;
        margin-bottom: 30px;

        .info-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            font-size: 14px;
            color: #666;
          }

          .value {
            font-size: 14px;
            color: #333;
          }

          .amount {
            font-size: 28px;
            color: #ff4757;
            font-weight: 600;
          }
        }
      }

      .section {
        margin-bottom: 30px;

        .section-title {
          font-size: 18px;
          color: #333;
          margin-bottom: 20px;
          font-weight: 600;
        }

        .payment-methods {
          display: flex;
          flex-direction: column;
          gap: 15px;

          .method-item {
            border: 2px solid #eee;
            border-radius: 8px;
            padding: 15px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
              border-color: #409eff;
            }

            &.selected {
              border-color: #409eff;
              background: #ecf5ff;
            }

            .method-content {
              display: flex;
              align-items: center;
              gap: 15px;

              .method-info {
                .method-name {
                  font-size: 16px;
                  color: #333;
                  font-weight: 500;
                }

                .method-desc {
                  font-size: 12px;
                  color: #999;
                  margin-top: 3px;
                }
              }
            }
          }
        }

        .password-input {
          .password-tip {
            margin-top: 10px;
            font-size: 12px;
            color: #999;
          }
        }
      }

      .pay-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 30px;
        border-top: 2px solid #f0f0f0;

        .pay-info {
          .pay-label {
            font-size: 14px;
            color: #666;
            margin-right: 10px;
          }

          .pay-amount {
            font-size: 32px;
            color: #ff4757;
            font-weight: 600;
          }
        }
      }

      .success-content {
        text-align: center;
        padding: 20px;

        .success-text {
          font-size: 20px;
          color: #333;
          margin: 15px 0 5px;
          font-weight: 600;
        }

        .success-desc {
          font-size: 14px;
          color: #999;
        }
      }

      .dialog-footer {
        display: flex;
        justify-content: center;
        gap: 10px;
      }
    }
  }
}
</style>
