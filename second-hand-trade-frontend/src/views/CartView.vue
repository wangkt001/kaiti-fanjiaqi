<template>
  <div class="cart-page">
    <NavBar />

    <div class="page-container">
      <h1 class="page-title">我的购物车</h1>

      <div v-loading="loading" class="cart-content">
        <!-- 空购物车 -->
        <div v-if="cartItems.length === 0" class="empty-cart">
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="$router.push('/goods')"
              >去逛逛</el-button
            >
          </el-empty>
        </div>

        <!-- 购物车列表 -->
        <div v-else class="cart-list">
          <!-- 操作栏 -->
          <div class="cart-header">
            <div class="select-all">
              <el-checkbox v-model="allSelected" @change="handleSelectAll">
                全选
              </el-checkbox>
            </div>
            <div class="header-info">
              <span class="goods-count">共 {{ cartItems.length }} 件商品</span>
              <el-button text type="danger" @click="handleClearCart">
                清空购物车
              </el-button>
            </div>
          </div>

          <!-- 商品列表 -->
          <div class="goods-list">
            <div
              v-for="item in cartItems"
              :key="item.productId"
              class="goods-item"
              :class="{ selected: item.selected }"
            >
              <div class="item-checkbox">
                <el-checkbox
                  v-model="item.selected"
                  @change="handleSelectItem(item)"
                />
              </div>
              <div class="item-image">
                <el-image
                  :src="item.product.mainImage || defaultImage"
                  fit="cover"
                  style="width: 100px; height: 100px"
                />
              </div>
              <div class="item-info">
                <div class="goods-name">{{ item.product.name }}</div>
                <div class="goods-spec">
                  <span v-if="item.product.description">{{
                    item.product.description
                  }}</span>
                </div>
                <div class="goods-price">
                  <span class="price-label">单价：</span>
                  <span class="price-value"
                    >¥{{ item.product.price.toFixed(2) }}</span
                  >
                </div>
              </div>
              <div class="item-quantity">
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  :max="item.product.stock"
                  size="small"
                  @change="handleQuantityChange(item)"
                />
                <div class="stock-tip">库存：{{ item.product.stock }}</div>
              </div>
              <div class="item-subtotal">
                <div class="subtotal-label">小计：</div>
                <div class="subtotal-value">
                  ¥{{ (item.product.price * item.quantity).toFixed(2) }}
                </div>
              </div>
              <div class="item-action">
                <el-button text type="danger" @click="handleDeleteItem(item)">
                  删除
                </el-button>
              </div>
            </div>
          </div>

          <!-- 结算栏 -->
          <div class="cart-footer">
            <div class="footer-left">
              <el-checkbox v-model="allSelected" @change="handleSelectAll">
                全选
              </el-checkbox>
              <el-button text @click="handleDeleteSelected">
                删除选中
              </el-button>
            </div>
            <div class="footer-right">
              <div class="selected-count">
                已选 <span class="count">{{ selectedCount }}</span> 件商品
              </div>
              <div class="total-info">
                <span class="total-label">合计：</span>
                <span class="total-value">¥{{ totalPrice.toFixed(2) }}</span>
              </div>
              <el-button
                type="primary"
                size="large"
                @click="handleCheckout"
                :disabled="selectedCount === 0"
              >
                去结算
              </el-button>
            </div>
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
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import {
  getCartItems,
  addToCart,
  updateQuantity,
  selectItem,
  selectAll,
  deleteItem,
  clearCart,
  type CartItem,
} from "@/api/modules/cart";
import defaultImage from "@/assets/default-image.png";

const router = useRouter();

const loading = ref(false);
const cartItems = ref<CartItem[]>([]);
const allSelected = ref(false);

// 计算选中商品数量
const selectedCount = computed(() => {
  return cartItems.value.filter((item) => item.selected).length;
});

// 计算选中商品总价
const totalPrice = computed(() => {
  return cartItems.value
    .filter((item) => item.selected)
    .reduce((sum, item) => sum + item.product.price * item.quantity, 0);
});

// 加载购物车
const loadCart = async () => {
  loading.value = true;
  try {
    const res = await getCartItems();
    if (res.data.code === 200 && res.data.data) {
      cartItems.value = res.data.data;
      updateAllSelected();
    }
  } catch (error) {
    console.error("加载购物车失败:", error);
    ElMessage.error("加载购物车失败");
  } finally {
    loading.value = false;
  }
};

// 更新全选状态
const updateAllSelected = () => {
  allSelected.value =
    cartItems.value.length > 0 &&
    cartItems.value.every((item) => item.selected);
};

// 全选/取消全选
const handleSelectAll = async () => {
  try {
    await selectAll(allSelected.value);
    cartItems.value.forEach((item) => {
      item.selected = allSelected.value;
    });
  } catch (error) {
    console.error("全选操作失败:", error);
    ElMessage.error("操作失败");
  }
};

// 选中/取消选中商品
const handleSelectItem = async (item: CartItem) => {
  try {
    await selectItem(item.productId, item.selected);
    updateAllSelected();
  } catch (error) {
    console.error("选中操作失败:", error);
    ElMessage.error("操作失败");
  }
};

// 数量变化
const handleQuantityChange = async (item: CartItem) => {
  try {
    await updateQuantity(item.productId, item.quantity);
  } catch (error) {
    console.error("更新数量失败:", error);
    ElMessage.error("更新数量失败");
  }
};

// 删除商品
const handleDeleteItem = async (item: CartItem) => {
  try {
    await ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteItem(item.productId);
    ElMessage.success("删除成功");
    loadCart();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

// 删除选中商品
const handleDeleteSelected = async () => {
  const selectedItems = cartItems.value.filter((item) => item.selected);
  if (selectedItems.length === 0) {
    ElMessage.warning("请选择要删除的商品");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedItems.length} 件商品吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    for (const item of selectedItems) {
      await deleteItem(item.productId);
    }

    ElMessage.success("删除成功");
    loadCart();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

// 清空购物车
const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm("确定要清空购物车吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await clearCart();
    ElMessage.success("清空成功");
    loadCart();
  } catch (error) {
    if (error !== "cancel") {
      console.error("清空失败:", error);
      ElMessage.error("清空失败");
    }
  }
};

// 去结算
const handleCheckout = () => {
  const selectedItems = cartItems.value.filter((item) => item.selected);
  if (selectedItems.length === 0) {
    ElMessage.warning("请选择要结算的商品");
    return;
  }

  // 跳转到订单确认页
  router.push({
    path: "/order/confirm",
    query: {
      cartItemIds: selectedItems.map((item) => item.id).join(","),
    },
  });
};

onMounted(() => {
  loadCart();
});
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;

    .page-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 20px;
    }

    .cart-content {
      background: #fff;
      border-radius: 8px;
      padding: 20px;

      .empty-cart {
        text-align: center;
        padding: 80px 0;
      }

      .cart-list {
        .cart-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 15px 0;
          border-bottom: 1px solid #eee;
          margin-bottom: 20px;

          .header-info {
            display: flex;
            align-items: center;
            gap: 20px;
            font-size: 14px;
            color: #666;

            .goods-count {
              color: #999;
            }
          }
        }

        .goods-list {
          .goods-item {
            display: grid;
            grid-template-columns: 50px 100px 1fr 150px 120px 80px;
            gap: 15px;
            align-items: center;
            padding: 20px 0;
            border-bottom: 1px solid #eee;
            transition: all 0.3s;

            &:last-child {
              border-bottom: none;
            }

            &.selected {
              background: #ecf5ff;
              padding-left: 10px;
              padding-right: 10px;
              border-radius: 4px;
            }

            .item-checkbox {
              display: flex;
              justify-content: center;
            }

            .item-image {
              border-radius: 4px;
              overflow: hidden;
            }

            .item-info {
              .goods-name {
                font-size: 14px;
                color: #333;
                margin-bottom: 8px;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .goods-spec {
                font-size: 12px;
                color: #999;
                margin-bottom: 8px;
              }

              .goods-price {
                .price-label {
                  font-size: 12px;
                  color: #999;
                }

                .price-value {
                  font-size: 16px;
                  color: #ff4757;
                  font-weight: 600;
                }
              }
            }

            .item-quantity {
              display: flex;
              flex-direction: column;
              align-items: center;

              .stock-tip {
                margin-top: 5px;
                font-size: 12px;
                color: #999;
              }
            }

            .item-subtotal {
              text-align: right;

              .subtotal-label {
                font-size: 12px;
                color: #999;
                margin-bottom: 5px;
              }

              .subtotal-value {
                font-size: 18px;
                color: #ff4757;
                font-weight: 600;
              }
            }

            .item-action {
              display: flex;
              justify-content: center;
            }
          }
        }

        .cart-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 20px;
          background: #f9f9f9;
          border-radius: 8px;
          margin-top: 20px;

          .footer-left {
            display: flex;
            align-items: center;
            gap: 15px;
          }

          .footer-right {
            display: flex;
            align-items: center;
            gap: 20px;

            .selected-count {
              font-size: 14px;
              color: #666;

              .count {
                color: #ff4757;
                font-weight: 600;
              }
            }

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
}

@media (max-width: 768px) {
  .goods-item {
    grid-template-columns: 40px 80px 1fr !important;
    gap: 10px !important;

    .item-quantity,
    .item-subtotal,
    .item-action {
      grid-column: 1 / -1;
      justify-self: center;
    }
  }
}
</style>
