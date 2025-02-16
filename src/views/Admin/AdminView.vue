<template>
  <div class="body">
    <div class="container">
      <el-tabs
        v-model="activeName"
        v-if="store.getters.userinfo && store.getters.userinfo.type === 'ADMIN'"
        @tab-click="handleTabClick"
      >
        <el-tab-pane label="用户管理" name="first">
          <component v-if="activeComponent === 'first'" :is="UserPanelView" />
        </el-tab-pane>
        <el-tab-pane label="服务器配置" name="second">
          <component v-if="activeComponent === 'second'" :is="ServerPanelView" />
        </el-tab-pane>
        <el-tab-pane label="兑换管理" name="third">
          <component v-if="activeComponent === 'third'" :is="RedemptionCodeView" />
        </el-tab-pane>
        <el-tab-pane label="商品管理" name="four">
          <component v-if="activeComponent === 'four'" :is="ProductView" />
        </el-tab-pane>
        <el-tab-pane label="订单数据" name="fifth">
          <component v-if="activeComponent === 'fifth'" :is="OrdersDataView" />
        </el-tab-pane>
        <el-tab-pane label="模型管理" name="sixth">
          <component v-if="activeComponent === 'sixth'" :is="SdModelView" />
        </el-tab-pane>
        <el-tab-pane label="终端控制" name="seventh">
          <component v-if="activeComponent === 'seventh'" :is="ControlPanelView" />
        </el-tab-pane>
        <el-tab-pane label="链接管理" name="eighth">
          <component v-if="activeComponent === 'eighth'" :is="LinkPanelView" />
        </el-tab-pane>
        <el-tab-pane label="作品管理" name="ninth">
          <component v-if="activeComponent === 'ninth'" :is="WorkPanelView" />
        </el-tab-pane>
      </el-tabs>
      <div v-else class="no_data">
        <ViewState class="state" Type="error" ErrorText="当前页面不见了" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, defineAsyncComponent, onMounted } from "vue";
import store from "@/store";
import ViewState from "@/components/ViewState.vue";
import { ElNotification } from "element-plus";

export default {
  name: "PromptList",
  components: {
    ViewState,
  },
  computed: {
    store() {
      return store;
    },
  },
  setup() {
    onMounted(() => {
      ElNotification({
        title: "手机浏览该网站可能会影响体验，建议使用电脑或平板~",
        type: "info",
      });
    })
    const activeName = ref("first");
    const activeComponent = ref("first");

    // 懒加载组件
    const UserPanelView = defineAsyncComponent(() => 
      import("@/views/Admin/components/UserPanelView.vue")
    );
    const ServerPanelView = defineAsyncComponent(() => 
      import("@/views/Admin/components/ServerPanelView.vue")
    );
    const RedemptionCodeView = defineAsyncComponent(() => 
      import("@/views/Admin/components/RedemptionCodeView.vue")
    );
    const ProductView = defineAsyncComponent(() => 
      import("@/views/Admin/components/ProductView.vue")
    );
    const OrdersDataView = defineAsyncComponent(() => 
      import("@/views/Admin/components/OrdersDataView.vue")
    );
    const SdModelView = defineAsyncComponent(() => 
      import("@/views/Admin/components/SdModelView.vue")
    );
    const ControlPanelView = defineAsyncComponent(() => 
      import("@/views/Admin/components/ControlPanelView.vue")
    );
    const LinkPanelView = defineAsyncComponent(() => 
      import("@/views/Admin/components/LinkPanelView.vue")
    );
    const WorkPanelView = defineAsyncComponent(() => 
      import("@/views/Admin/components/WorkPanelView.vue")
    );

    // 处理标签页切换
    const handleTabClick = (tab) => {
      activeComponent.value = tab.props.name;
    };

    return {
      activeName,
      activeComponent,
      UserPanelView,
      ServerPanelView,
      RedemptionCodeView,
      ProductView,
      OrdersDataView,
      SdModelView,
      ControlPanelView,
      LinkPanelView,
      WorkPanelView,
      handleTabClick,
    };
  },
};
</script>

<style lang="scss" scoped>
:deep(.s_container) {
  overflow: auto;
  height: 100%;
  .el-scrollbar__view {
    transition: all 0.3s;
  }

  @media screen and (min-height: 756px) {
    .el-scrollbar__view {
      padding: 40px;
    }
  }

  @media screen and (max-height: 756px) {
    .el-scrollbar__view {
      padding: 10px;
    }
  }
}

:deep(.u_container) {
  display: flex;
  flex-direction: column;
  .head_model {
    min-height: 110px;
  }
  .el-table tr {
    background: transparent;
  }
}

:deep(.container) {
  .el-table__cell {
    border-bottom: 1px solid var(--borderColor);
  }
  .hover-row {
    .el-table__cell {
      background-color: var(--borderColor);
      color: var(--textColor2);
    }
  }

  .el-table__inner-wrapper::before {
    background-color: var(--borderColor);
  }

  .el-tabs {
    height: 100%;
    display: flex;
    flex-direction: column;

    .el-tabs__content {
      height: 100%;

      .el-tab-pane {
        height: 100%;
        .u_container {
          height: 100%;

          .el-table {
            > .el-table__inner-wrapper {
              height: 100% !important;
            }
          }
        }
      }
    }
  }
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

:deep(.el-tabs__nav-wrap::after) {
  background: var(--bgColor1);
}

.container {
  animation: explainAnimation 0.3s;
  max-width: 1100px;
  width: 100%;
  box-sizing: border-box;
  padding: 10px 20px 20px;
  margin: 30px 0px;
  height: 90%;
  background-color: var(--bgColor1);
  border-radius: 8px;
}

.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px 0px;
  display: flex;
  overflow: auto;
  background-color: var(--bgColor2);
}

::v-deep(.el-tabs__item.is-active) {
  color: var(--textColor1);
}

::v-deep(.el-tabs__item:hover) {
  color: #959595;
}

::v-deep(.el-tabs__active-bar) {
  background-color: var(--themeColor1);
}

::v-deep(.el-tabs__item) {
  color: #626262;
}
.no_data {
  height: 540px;
  margin-top: 10px;
}
</style>
