<template>
  <div class="panel-container">
    <div class="body" v-if="mainPageVisible">
      <div class="article" v-if="store.getters.userinfo">我要赞助站点运行</div>
      <div class="introduce" v-if="store.getters.userinfo">
        赞赏作者也可获得相应IT币
      </div>
      <div class="surplus" v-if="store.getters.userinfo">
        剩余IT币 {{ store.getters.userinfo.frequency }}
      </div>
      <ViewState
        class="state"
        v-if="!store.getters.userinfo"
        Type="error"
        ErrorText="登录后查看"
        IsShowBottom
        ButtonText="登录"
        @ClickTheButton="loginVisible = true"
      />
      <ViewState
        class="state"
        v-else-if="load"
        LoadText="正在加载，请稍后..."
      />
      <ViewState
        class="state"
        v-else-if="empty"
        Type="empty"
        ErrorText="暂无任何数据"
      />
      <ViewState
        class="state"
        v-else-if="error"
        @ClickTheButton="init"
        Type="error"
        ErrorText="加载错误，请重试"
        IsShowBottom
        ButtonText="重新加载"
      />
      <div class="wrapper" v-else>
        <el-row :gutter="20">
          <el-col
            v-for="(item, index) in productList"
            :key="index"
            :xs="24"
            :sm="12"
            :md="8"
          >
            <div
              class="item"
              @click="payChoose(item.productId, item.frequency)"
            >
              <div class="wrapper-title">{{ item.productName }}</div>
              <div class="quantity">{{ item.frequency }} IT币</div>
              <div class="quantity" style="font-size: 15px; padding-top: 10px">
                ￥{{ item.productPrice }}
              </div>
              <div class="card-introduce">
                <div
                  class="function-box"
                  v-for="(item2, index2) in introduce"
                  :key="index2"
                >
                  <el-icon color="var(--themeColor1)" size="20px">
                    <CircleCheckFilled />
                  </el-icon>
                  <div style="padding-left: 10px; color: #a7a7a7">
                    <div>{{ item2 }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <el-dialog
        v-model="payVisible"
        width="340px"
        v-if="productFrequency"
        style="background-color: var(--bgColor1)"
      >
        <div class="pay-title">
          选择支付方式后点击"跳转至收银台"打开支付页面支付
        </div>
        <div style="text-align: center">
          <el-radio label="0" v-model="payType">
            <div class="pay">
              <img
                class="alipay-img"
                alt="支付宝支付"
                src="../assets/alipay.svg"
              />
              <div>支付宝支付</div>
            </div>
          </el-radio>
          <!--          <el-radio label="1" v-model="payType">-->
          <!--            <div class="pay">-->
          <!--              <img class="wxpay-img" alt="微信支付"-->
          <!--                   src="../assets/wxpay.svg">-->
          <!--              <div>微信支付</div>-->
          <!--            </div>-->
          <!--          </el-radio>-->
        </div>
        <template #footer>
          <span>
            <el-button class="buttonTheme" @click="payVisible = false"
              >不了, 谢谢</el-button
            >
            <el-button class="buttonTheme themeColor" @click="alipayPay"
              >跳转至收银台</el-button
            >
          </span>
        </template>
      </el-dialog>
    </div>
    <LoginDialog
      :show="loginVisible"
      @close="loginVisible = false"
      @loginSucceeded="init"
    />
    <!--        支付宝支付-->
    <div v-if="payying" class="payment-status-container">
      <div class="status-content">
        <div class="icon-wrapper warning">
          <el-icon><WarningFilled /></el-icon>
        </div>
        <h1>请在新页面完成支付</h1>
        <div class="warning-box">
          <el-icon><InfoFilled /></el-icon>
          <span>支付完成前请勿关闭或刷新当前页面</span>
        </div>
        <div class="status-info">
          <p>支付完成后将自动检测支付状态</p>
          <div class="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showSucceed" class="payment-status-container">
      <div class="status-content success">
        <div class="icon-wrapper success">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
        <h1>支付成功</h1>
        <p class="success-text">感谢您的支持！</p>
        <el-button 
          type="primary" 
          class="view-button"
          @click="router().push('/user_view')"
        >
          <el-icon><View /></el-icon>
          <span>查看详情</span>
        </el-button>
      </div>
    </div>
  </div>
</template>
<script>
import { onMounted, ref } from "vue";
import {
  alipayIsSucceed,
  alipayPayQrCode,
  GetProducts,
  GetUserInfo,
} from "../../api/BSideApi";
import { ElLoading, ElNotification } from "element-plus";

import { CircleCheckFilled, WarningFilled, InfoFilled, View } from "@element-plus/icons-vue";
import { useStore } from "vuex";
import router from "@/router";
import ViewState from "@/components/ViewState.vue";
import LoginDialog from "@/components/LoginDialog.vue";
import store from "@/store";
import LeftNavigationBar from "@/App.vue";

export default {
  name: "PurchaseView",
  computed: {
    store() {
      return store;
    },
  },
  components: {LeftNavigationBar, LoginDialog, ViewState, CircleCheckFilled, WarningFilled, InfoFilled, View },
  methods: {
    router() {
      return router;
    },
  },
  setup() {
    let loginVisible = ref(false);
    let store = useStore();
    const introduce = ref([
      "智能问答|Ai绘画|图床扩容",
      "全功能使用",
      "源码星球交易货币",
    ]);
    const payVisible = ref(false);
    const productList = ref([]);
    const payType = ref("0");
    const productFrequency = ref("");
    const productId = ref({});
    const mainPageVisible = ref(true);
    const showCover = ref(false);
    const showSucceed = ref(false);
    const payying = ref(false);

    let load = ref(false);
    let empty = ref(false);
    let error = ref(false);
    //生成结果
    const outcome = ref({});

    async function init() {
      try {
        load.value = true;
        const res = await GetProducts();
        if (res.length) {
          productList.value = res;
        } else {
          empty.value = true;
        }
        load.value = false;
        error.value = false;
      } catch (e) {
        load.value = false;
        error.value = true;
      }
    }


    async function getUser() {
      try {
        let res = await GetUserInfo();
        store.commit("setUserinfo", res);
      } catch (e) {
        ElNotification({
          title: "出现错误",
          message: e,
          type: "error",
        });
      }
    }

    async function payChoose(id, frequency) {
      productId.value = id;
      productFrequency.value = frequency;
      payVisible.value = true;
    }

    async function alipayPay() {
      try {
        ElLoading.service({
          fullscreen: true,
          text: "正在构建订单...",
          spinner: "el-icon-loading",
          background: "rgba(0, 0, 0, 0.7)",
        });
        //构建支付宝订单
        outcome.value = await alipayPayQrCode(productId.value);
        payVisible.value = false;
        payying.value = true;
        mainPageVisible.value = false;
        window.open(outcome.value.payUrl, '_blank');
        //3秒检查一下订单是否支付成功
        let timerId = setInterval(async function () {
          let res = await alipayIsSucceed(outcome.value.ordersId);
          if (res === "success") {
            ElNotification({
              title: "成功",
              message: "赞赏成功，可在我的赞赏中查看该赞赏记录",
              type: "success",
            });
            payying.value = false;
            showSucceed.value = true;
            await getUser();
            clearInterval(timerId);
          } else if (res === "cancel") {
            showCover.value = true;
            ElNotification({
              title: "订单已关闭",
              message: "长时间未支付，订单已关闭",
              type: "error",
            });
            clearInterval(timerId);
          }
        }, 5000);
      } catch (e) {
        ElNotification({
          title: "错误",
          message: e,
          type: "error",
        });
        mainPageVisible.value = true;
      } finally {
        ElLoading.service().close();
      }
    }

    onMounted(() => {
      if (store.getters.userinfo) {
        init();
      }
    });
    return {
      load,
      error,
      empty,
      showSucceed,
      showCover,
      outcome,
      mainPageVisible,
      alipayPay,
      payChoose,
      init,
      introduce,
      productList,
      payVisible,
      payType,
      loginVisible,
      productFrequency,
      payying,
    };
  },
};
</script>

<style scoped>
.body {
  animation: explainAnimation 0.3s;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 60px 20px 0;
  display: flex;
  overflow: auto;
}

.pay-title {
  text-align: center;
  padding-left: 30px;
  padding-right: 30px;
  font-size: 13px;
  padding-bottom: 30px;
}

.pay {
  display: flex;
  justify-items: center;
  align-items: center;
}

.panel-container {
  overflow: auto;
  overflow-y: scroll;
  height: 100%;
  background-color: var(--bgColor2);
}

.alipay-img {
  width: 20px;
  height: 20px;
  padding-left: 5px;
  padding-right: 10px;
}

.wxpay-img {
  width: 25px;
  height: 25px;
  padding-left: 5px;
  padding-right: 10px;
}

.wrapper-title {
  background-color: var(--themeColor1);
  border-radius: 10px 10px 0 0;
  color: var(--themeTextColor);
  padding-top: 20px;
  padding-bottom: 20px;
  font-size: 15px;
  font-weight: 600;
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.article {
  text-align: left;
  margin: 0;
  font-size: 26px;
  font-weight: 600;
  color: var(--textColor1);
}

.wrapper {
  padding: 40px 0;
  max-width: 1000px;
  width: 100%;
  box-sizing: border-box;
}

.item {
  text-align: center;
  background-color: var(--bgColor1);
  cursor: pointer;
  width: 100%;
  height: 400px;
  border-radius: 8px;
  font-size: 15px;
  color: var(--textColor1);
  margin-bottom: 20px;
}

.introduce {
  font-size: 14px;
  margin-top: 12px;
  color: #838383;
}

.card-introduce {
  color: rgb(108, 117, 125);
  margin-top: 50px;
  text-align: left;
  padding-left: 10%;
  font-size: 14px;
}

.function-box {
  align-items: center;
  display: flex;
  padding-bottom: 15px;
}

.quantity {
  padding-top: 50px;
  color: var(--textColor1);
  font-size: 38px;
  font-weight: 500;
}

@media only screen and (max-width: 767px) {
  .wrapper {
    padding-left: 20px;
    padding-right: 20px;
  }
}
</style>

<style lang="scss" scoped>
:deep(.buttonTheme) {
  background: var(--bgColor3);
  color: var(--textColor1);
  border: none;
  &.themeColor {
    background: var(--themeColor1);
    color: var(--themeTextColor);
  }

  &:hover {
    opacity: 0.9;
  }
}

.surplus {
  margin: 10px;
  color: var(--textColor2);
}

.aiwarning {
  color: #ffffff;
}

.payment-status-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--bgColor2);
  animation: fadeIn 0.3s ease;
}

.status-content {
  background: var(--bgColor1);
  padding: 40px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 90%;

  h1 {
    color: var(--textColor1);
    font-size: 24px;
    margin: 20px 0;
  }

  &.success {
    .success-text {
      color: var(--textColor2);
      margin: 16px 0 24px;
    }
  }
}

.icon-wrapper {
  margin-bottom: 20px;
  
  .el-icon {
    font-size: 64px;
    
    &.warning {
      color: #e6a23c;
    }
    
    &.success {
      color: #67c23a;
      animation: scaleIn 0.5s ease;
    }
  }
}

.warning-box {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #fdf6ec;
  color: #e6a23c;
  padding: 12px 20px;
  border-radius: 6px;
  margin: 20px 0;

  .el-icon {
    font-size: 18px;
  }
}

.status-info {
  margin-top: 30px;
  color: var(--textColor2);
}

.loading-dots {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 12px;
  
  span {
    width: 8px;
    height: 8px;
    background: var(--themeColor1);
    border-radius: 50%;
    animation: dot-pulse 1.5s infinite;
    
    &:nth-child(2) {
      animation-delay: 0.2s;
    }
    
    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

.view-button {
  margin-top: 20px;
  padding: 12px 30px;
  
  .el-icon {
    margin-right: 6px;
  }
}

@keyframes dot-pulse {
  0%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media screen and (max-width: 480px) {
  .status-content {
    padding: 30px 20px;
    
    h1 {
      font-size: 20px;
    }
  }
  
  .warning-box {
    padding: 10px 16px;
    font-size: 14px;
  }
}
</style>
