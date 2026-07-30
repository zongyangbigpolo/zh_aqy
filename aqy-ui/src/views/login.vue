<template>
  <div class="login-container">
    <!-- 左侧信息区 -->
    <div class="left-panel">
      <div class="info-content">
        <h1>边坡监测系统</h1>
        <p class="subtitle">智能化边坡监测预警平台</p>
        <div class="feature-list">
          <div class="feature-item">
            <i class="el-icon-data-line"></i>
            <span>实时数据监测</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-warning-outline"></i>
            <span>智能预警分析</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-refresh"></i>
            <span>全天候监控</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="right-panel">
      <div class="login-content">
        <div class="welcome-text">
          <h2>欢迎登录</h2>
          <p>Welcome Back</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" @keyup.enter.native="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入账号"
            >
              <template #prefix>
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
            >
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="verify-code">
              <el-input
                v-model="loginForm.code"
                placeholder="验证码"
                style="width: 70%"
              >
                <template #prefix>
                  <i class="el-icon-key"></i>
                </template>
              </el-input>
              <img :src="codeUrl" @click="getCode" class="code-img" alt="验证码">
            </div>
          </el-form-item>

          <div class="form-footer">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <el-button
              :loading="loading"
              type="primary"
              class="login-button"
              @click.native.prevent="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
// script部分保持不变
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  background: #f4f5f7;
}

.left-panel {
  flex: 1;
  background: linear-gradient(135deg, #1a2980 0%, #26d0ce 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 40px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    background-image: radial-gradient(
        circle at center,
        rgba(255, 255, 255, 0.1) 0%,
        transparent 10%
    );
    background-size: 20px 20px;
    animation: floatingDots 20s linear infinite;
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(
        circle at center,
        transparent 0%,
        rgba(0, 0, 0, 0.2) 100%
    );
  }

  .info-content {
    max-width: 500px;
    position: relative;
    z-index: 1;

    h1 {
      font-size: 36px;
      margin-bottom: 16px;
      font-weight: 600;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      background: linear-gradient(to right, #fff, #e0e0e0);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .subtitle {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 40px;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }

    .feature-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 24px;

      .feature-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 20px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 12px;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        transition: transform 0.3s ease, box-shadow 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
          background: rgba(255, 255, 255, 0.15);
        }

        i {
          font-size: 24px;
          color: #fff;
          filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.4));
        }

        span {
          font-size: 16px;
          font-weight: 500;
          color: rgba(255, 255, 255, 0.9);
        }
      }
    }
  }
}

.right-panel {
  width: 500px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  box-shadow: -10px 0 20px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;

  .login-content {
    width: 100%;
    max-width: 360px;
  }

  .welcome-text {
    text-align: center;
    margin-bottom: 40px;

    h2 {
      font-size: 28px;
      color: #2c3e50;
      margin-bottom: 8px;
    }

    p {
      color: #94a3b8;
      font-size: 16px;
    }
  }
}

.login-form {
  :deep(.el-input) {
    .el-input__inner {
      height: 50px;
      line-height: 50px;
      border-radius: 10px;
      border: 1px solid #e2e8f0;
      padding-left: 45px;
      background: #f8fafc;
      transition: all 0.3s;

      &:focus {
        background: white;
        border-color: #3498db;
        box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
      }

      &::placeholder {
        color: #94a3b8;
      }
    }

    .el-input__prefix {
      left: 16px;
      color: #64748b;
    }
  }

  .verify-code {
    display: flex;
    gap: 12px;
    align-items: center;

    .code-img {
      height: 35px;
      border-radius: 5px;
      cursor: pointer;
    }
  }

  .form-footer {
    margin-top: 24px;

    :deep(.el-checkbox) {
      color: #64748b;
      margin-bottom: 20px;

      .el-checkbox__label {
        color: #64748b;
      }

      .el-checkbox__input.is-checked .el-checkbox__inner {
        background-color: #3498db;
        border-color: #3498db;
      }
    }

    .login-button {
      width: 100%;
      height: 50px;
      border-radius: 10px;
      margin-top: 20px;
      font-size: 16px;
      background: #3498db;
      border: none;
      transition: all 0.3s;

      &:hover {
        background: #2980b9;
        transform: translateY(-1px);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }
}

// 动画
@keyframes floatingDots {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 100px 100px;
  }
}

.login-content {
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

// 响应式设计
@media screen and (max-width: 1024px) {
  .left-panel {
    display: none;
  }

  .right-panel {
    width: 100%;
  }
}

@media screen and (max-width: 576px) {
  .right-panel {
    padding: 20px;
  }

  .login-content {
    padding: 20px;
  }
}
</style>
