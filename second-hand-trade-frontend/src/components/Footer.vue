<template>
  <footer class="footer">
    <div class="footer-container">
      <!-- 顶部内容区 -->
      <div class="footer-top">
        <div class="footer-section">
          <h3 class="section-title">关于我们</h3>
          <p class="section-desc">
            豫见好物是专注于河南文创产品的销售平台，致力于推广河南传统文化，让文创产品走进千家万户。
          </p>
          <div class="social-links">
            <el-icon><Wechat /></el-icon>
            <el-icon><ChatDotRound /></el-icon>
            <el-icon><Share /></el-icon>
          </div>
        </div>

        <div class="footer-section">
          <h3 class="section-title">快速链接</h3>
          <ul class="link-list">
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/goods">商品列表</router-link></li>
            <li><router-link to="/cultural">文化资讯</router-link></li>
            <li><router-link to="/about">关于我们</router-link></li>
          </ul>
        </div>

        <div class="footer-section">
          <h3 class="section-title">用户服务</h3>
          <ul class="link-list">
            <li><router-link to="/user">个人中心</router-link></li>
            <li><router-link to="/order">我的订单</router-link></li>
            <li><router-link to="/cart">购物车</router-link></li>
            <li><router-link to="/seller">卖家入驻</router-link></li>
          </ul>
        </div>

        <div class="footer-section">
          <h3 class="section-title">联系我们</h3>
          <ul class="contact-list">
            <li>
              <el-icon><Phone /></el-icon>
              <span>客服热线：400-xxx-xxxx</span>
            </li>
            <li>
              <el-icon><Message /></el-icon>
              <span>客服微信：yujianhaowu</span>
            </li>
            <li>
              <el-icon><Clock /></el-icon>
              <span>服务时间：9:00-18:00</span>
            </li>
            <li>
              <el-icon><Location /></el-icon>
              <span>地址：河南省郑州市</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- 分隔线 -->
      <el-divider class="footer-divider" />

      <!-- 底部信息区 -->
      <div class="footer-bottom">
        <div class="footer-links">
          <a href="#" target="_blank">隐私政策</a>
          <a href="#" target="_blank">服务条款</a>
          <a href="#" target="_blank">免责声明</a>
          <a href="#" target="_blank">帮助中心</a>
        </div>
        <div class="copyright">
          <p>Copyright © 2026 豫见好物 版权所有</p>
          <p>
            <a href="https://beian.miit.gov.cn/" target="_blank">
              豫 ICP 备 XXXXXXXX 号
            </a>
          </p>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import {
  Wechat,
  ChatDotRound,
  Share,
  Phone,
  Message,
  Clock,
  Location,
} from "@element-plus/icons-vue";
</script>

<style lang="scss" scoped>
.footer {
  background-color: $bg-dark;
  color: #ffffff;
  padding: $spacing-extra-large 0;
  margin-top: $spacing-extra-large * 2;
}

.footer-container {
  max-width: $page-width;
  margin: 0 auto;
  padding: 0 $spacing-base;
}

.footer-top {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-extra-large;
}

.footer-section {
  .section-title {
    font-size: $font-size-large;
    font-weight: 600;
    margin-bottom: $spacing-large;
    color: #ffffff;
  }

  .section-desc {
    font-size: $font-size-small;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: $spacing-large;
  }

  .social-links {
    display: flex;
    gap: $spacing-base;
    font-size: 24px;

    .el-icon {
      cursor: pointer;
      transition: $transition-fast;

      &:hover {
        color: $primary-color;
      }
    }
  }

  .link-list {
    list-style: none;
    padding: 0;

    li {
      margin-bottom: $spacing-small;

      a {
        color: rgba(255, 255, 255, 0.7);
        font-size: $font-size-small;
        text-decoration: none;
        transition: $transition-fast;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }

  .contact-list {
    list-style: none;
    padding: 0;

    li {
      display: flex;
      align-items: center;
      gap: $spacing-small;
      margin-bottom: $spacing-small;
      font-size: $font-size-small;
      color: rgba(255, 255, 255, 0.7);

      .el-icon {
        font-size: 16px;
      }
    }
  }
}

.footer-divider {
  background-color: rgba(255, 255, 255, 0.1);
  margin: $spacing-extra-large 0;
}

.footer-bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-base;

  .footer-links {
    display: flex;
    gap: $spacing-large;

    a {
      color: rgba(255, 255, 255, 0.7);
      font-size: $font-size-small;
      text-decoration: none;
      transition: $transition-fast;

      &:hover {
        color: $primary-color;
      }
    }
  }

  .copyright {
    text-align: center;
    font-size: $font-size-small;
    color: rgba(255, 255, 255, 0.5);

    p {
      margin: $spacing-small 0;

      a {
        color: rgba(255, 255, 255, 0.5);
        text-decoration: none;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .footer-top {
    grid-template-columns: 1fr;
    gap: $spacing-large;
  }

  .footer-bottom {
    .footer-links {
      flex-wrap: wrap;
      justify-content: center;
    }
  }
}
</style>
