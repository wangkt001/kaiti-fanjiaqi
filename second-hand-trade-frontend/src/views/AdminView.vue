<template>
  <div class="admin-view">
    <NavBar />

    <div class="container">
      <h2 class="page-title">后台管理</h2>

      <el-tabs v-model="activeTab" class="admin-tabs">
        <!-- 商品审核 -->
        <el-tab-pane label="商品审核" name="products">
          <div class="tab-content">
            <el-table :data="auditList" v-loading="loading" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="sellerName" label="卖家" width="100" />
              <el-table-column prop="price" label="价格" width="80">
                <template #default="{ row }"> ¥{{ row.price }} </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag type="warning">待审核</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button
                    size="small"
                    type="success"
                    @click="handleApprove(row)"
                  >
                    通过
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleReject(row)"
                  >
                    拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 用户管理 -->
        <el-tab-pane label="用户管理" name="users">
          <div class="tab-content">
            <el-table :data="userList" v-loading="loading" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" width="150" />
              <el-table-column prop="nickname" label="昵称" width="120" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="{ row }">
                  <el-tag :type="getRoleType(row.role)">
                    {{ getRoleText(row.role) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                    {{ row.status === 1 ? "正常" : "禁用" }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button
                    size="small"
                    :type="row.status === 1 ? 'danger' : 'success'"
                    @click="handleToggleStatus(row)"
                  >
                    {{ row.status === 1 ? "禁用" : "启用" }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 订单管理 -->
        <el-tab-pane label="订单管理" name="orders">
          <div class="tab-content">
            <el-table :data="orderList" v-loading="loading" style="width: 100%">
              <el-table-column prop="orderNo" label="订单编号" width="180" />
              <el-table-column prop="userName" label="用户" width="100" />
              <el-table-column prop="paymentAmount" label="金额" width="100">
                <template #default="{ row }">
                  ¥{{ row.paymentAmount }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getOrderStatusType(row.status)">
                    {{ getOrderStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="下单时间" width="180" />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" @click="handleViewOrder(row)">
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import NavBar from "@/components/NavBar.vue";
import { getPendingProducts, auditProduct } from "@/api/modules/goods";

const activeTab = ref("products");
const loading = ref(false);

// 审核列表
const auditList = ref<any[]>([]);

// 用户列表
const userList = ref([
  { id: 1, username: "user1", nickname: "用户 1", role: "buyer", status: 1 },
  { id: 2, username: "seller1", nickname: "卖家 1", role: "seller", status: 1 },
]);

// 订单列表
const orderList = ref([
  {
    id: 1,
    orderNo: "ORD202603090001",
    userName: "张三",
    paymentAmount: 99.0,
    status: 1,
    createdAt: "2026-03-09 10:00:00",
  },
]);

const getRoleType = (role: string) => {
  const typeMap: Record<string, any> = {
    buyer: "",
    seller: "warning",
    admin: "danger",
  };
  return typeMap[role] || "";
};

const getRoleText = (role: string) => {
  const textMap: Record<string, string> = {
    buyer: "买家",
    seller: "卖家",
    admin: "管理员",
  };
  return textMap[role] || role;
};

const getOrderStatusType = (status: number) => {
  const typeMap: Record<number, any> = {
    0: "warning",
    1: "success",
    2: "",
    3: "info",
    4: "danger",
  };
  return typeMap[status] || "";
};

const getOrderStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: "待付款",
    1: "待发货",
    2: "待收货",
    3: "已完成",
    4: "已取消",
  };
  return textMap[status] || "";
};

// 加载待审核商品列表
const loadPendingProducts = async () => {
  loading.value = true;
  try {
    const response = await getPendingProducts(1, 10);
    auditList.value = response.records || [];
  } catch (error) {
    console.error("获取待审核商品失败:", error);
    ElMessage.error("获取待审核商品失败");
  } finally {
    loading.value = false;
  }
};

// 审核通过
const handleApprove = (row: any) => {
  ElMessageBox.confirm("确定要通过该商品审核吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await auditProduct(row.id, "on_sale");
      ElMessage.success("审核通过");
      // 重新加载列表
      await loadPendingProducts();
    } catch (error) {
      console.error("审核失败:", error);
      ElMessage.error("审核失败，请重试");
    }
  });
};

// 审核拒绝
const handleReject = (row: any) => {
  ElMessageBox.confirm("确定要拒绝该商品吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await auditProduct(row.id, "offline");
      ElMessage.success("已拒绝");
      // 重新加载列表
      await loadPendingProducts();
    } catch (error) {
      console.error("审核失败:", error);
      ElMessage.error("审核失败，请重试");
    }
  });
};

const handleToggleStatus = (row: any) => {
  const action = row.status === 1 ? "禁用" : "启用";
  ElMessageBox.confirm(`确定要${action}该用户吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    ElMessage.success(`${action}成功`);
  });
};

const handleViewOrder = (row: any) => {
  ElMessage.info("查看订单功能开发中");
};

onMounted(() => {
  // 加载待审核商品
  loadPendingProducts();
  console.log("后台管理已加载");
});
</script>

<style scoped lang="scss">
.admin-view {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.admin-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.tab-content {
  min-height: 400px;
}
</style>
