<template>
  <div class="NavigationBar">
    <div :class="controlDisplay?'leftNavigation':'leftNavigation_active'" v-if="isLeftMenu">
      <el-avatar
          class="headPortrait"
          :size="70"
          :icon="UserFilled"
          :src="
          store.getters.userinfo.avatar
            ? imageUrl + store.getters.userinfo.avatar
            : require('../assets/passlogo.png')
        "
      />
      <div class="surplus" v-if="store.getters.userinfo">
        SUPER币 {{ store.getters.userinfo.frequency }}
      </div>
      <div v-else @click="loginVisible = true" class="loginBut">登录</div>
      <div class="header">
        <div class="switch-button">
          <router-link
              v-for="(item, index) in navigationList"
              :key="index"
              active-class="switch-active"
              class="switch-item"
              :to="item.to"
          >
            <el-icon>
              <component :is="item.icon"/>
            </el-icon>
            <div class="switch-item-title">{{ item.title }}</div>
          </router-link>
        </div>
      </div>

      <div class="bottom">
        <el-popover
            placement="top-start"
            :width="400"
            trigger="hover"
        >
          <template #reference>
            <el-avatar
                :size="40"
                :icon="UserFilled"
                :src="require('../assets/hh.jpg')"
            />
          </template>
            <div
                class="demo-rich-conent"
                style="display: flex; gap: 16px; flex-direction: column">
              <h2>
                更新日志
                <el-icon><ChatLineRound /></el-icon>
              </h2>
              <p style="margin: 0;">
                1.0.0基于Time sea作者开源项目进行搭建,并修改了logo等;
                <span style="font-size: 10px;color: #7c7c7c">--2024.2.6</span>
              </p>
              <p style="margin: 0">
                2.0.0对整体项目进行二次大改造，SD Ai绘画魔改，预设角色增多，新增数字人平台|个人中心|我的博客,删掉了claude和必应,此作品入围全高计设赛国赛;
                <span style="font-size: 10px;color: #7c7c7c">--2024.5.1</span>
              </p>
              <p style="margin: 0">
                2.1.0上新外链推荐,控制台新增外链管理；
                <span style="font-size: 10px;color: #7c7c7c">--2024.6.6</span>
              </p>
              <p style="margin: 0">
                2.2.0新增我们学校的智慧派斯和大数据专区,通过python爬取数据,从此项目变三端(Java,Vue,Python)；
                <span style="font-size: 10px;color: #7c7c7c">--2024.6.17</span>
              </p>
              <p style="margin: 0">
                2.3.0新增我的作品|图床模块|对应管理模块;
                <span style="font-size: 10px;color: #7c7c7c">--2024.9.7</span>
              </p>
              <p style="margin: 0">
                2.4.0修复完善Sd绘画，可控制开启Sd绘画，项目bug数减为0;
                <span style="font-size: 10px;color: #7c7c7c">--2024.9.9</span>
              </p>
            </div>
        </el-popover>
        <div class="bottomRight">
          <div class="bottomRightName">PASS Assistant</div>
          <div class="bottomRightEdition">v2.3.0 2024.9.8</div>
        </div>
      </div>
      <div class="control-display" @click="controlDisplay=!controlDisplay">
        {{ controlDisplay ? '隐藏' : '显示' }}
      </div>
    </div>
    <div class="rightContent">
      <RouterView v-slot="{ Component }">
        <!-- TODO 要缓存 -->
        <KeepAlive>
          <component
              :is="Component"
              :key="$route.name"
              v-if="$route.meta.keepAlive"
          ></component>
        </KeepAlive>
        <!-- TODO 不缓存 -->
        <component
            :is="Component"
            :key="$route.name"
            v-if="!$route.meta.keepAlive"
        ></component>
      </RouterView>
    </div>
  </div>

  <LoginDialog :show="loginVisible" @close="loginVisible = false"/>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import {useRouter} from "vue-router";
// eslint-disable-next-line no-unused-vars
import {
  ChatDotSquare, Iphone,
  MessageBox,
  Odometer,
  PictureFilled,
  ScaleToOriginal,
  UserFilled,
} from "@element-plus/icons-vue";
import router from "@/router";
import store from "../store";
import LoginDialog from "@/components/LoginDialog.vue";

export default defineComponent({
  name: "LeftNavigationBar",
  components: {
    LoginDialog,
  },
  computed: {
    store() {
      return store;
    },
  },
  methods: {
    router() {
      return router;
    },
  },
  setup() {
    let controlDisplay = ref(true)
    let router = useRouter();
    // TODO DATA
    let loginVisible = ref(false);
    let dialogVisible = ref(false);
    let appletDialogVisible = ref(false);
    let isHeadNavigation = ref(false);
    let navigationList = ref([
      {
        title: "智能问答",
        icon: ChatDotSquare,
        to: "/app",
      },
      {
        title: "预设角色",
        icon: Odometer,
        to: "/preset_character",
      },
      {
        title: "我的收藏",
        icon: MessageBox,
        to: "/collection",
      },
      {
        title: "超级实验室",
        icon: ScaleToOriginal,
        to: "/laboratory",
      },
      {
        title: "图床",
        icon: PictureFilled,
        to: "/photo_view"
      },
      {
        title: "外链模块",
        icon: Iphone,
        to: "/link_view"
      }
    ]);
    const isLeftMenu = ref(true);


    watch(
        () => router.currentRoute.value,
        (newValue) => {
          isHeadNavigation.value = newValue.meta.isHeadNavigation;
          isLeftMenu.value = newValue.meta.isLeftMenu;
        },
        {
          immediate: true,
        }
    );
    const imageUrl = ref("");
    onMounted(() => {
      imageUrl.value = process.env.VUE_APP_IMAGE;
    });
    const dropdown1 = ref();

    function showClick() {
      dropdown1.value.handleOpen();
    }

    return {
      controlDisplay,
      isHeadNavigation,
      navigationList,
      UserFilled,
      showClick,
      dropdown1,
      appletDialogVisible,
      dialogVisible,
      loginVisible,
      imageUrl,
      isLeftMenu,
    };
  },
});
</script>

<style lang="scss" scoped>
:deep(.answer) {
  .v-md-editor {
    background-color: transparent;
  }
}

:deep(.rightContent) {
  .footer {
    width: calc(100% - 10px);
  }
}
</style>

<style scoped>
.NavigationBar {
  width: 100%;
  height: 100%;
  display: flex;
  overflow: hidden;
}

.leftNavigation {
  width: 260px;
  height: 100%;
  background-color: var(--bgColor1);
  border-right: 1px solid var(--borderColor);
  position: relative;
}

.leftNavigation_active {
  width: 0;
  height: 100%;
  background-color: var(--bgColor1);
  border-right: 1px solid var(--borderColor);
  position: relative;
}

.bottom {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 70px;
  border-top: 1px solid var(--borderColor);
  display: flex;
  align-items: center;
  box-sizing: border-box;
  padding-left: 20px;
}

.bottomRight {
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-left: 10px;
}

.bottomRightName {
  color: #848484;
  font-size: 14px;
  font-weight: bold;
}

.bottomRightEdition {
  font-size: 12px;
  margin-top: 3px;
  color: #7c7c7c;
}

.headPortrait {
  display: block;
  margin: 50px auto 0;
}

.surplus {
  box-sizing: border-box;
  padding: 5px 10px;
  background-color: var(--themeColor1);
  display: table;
  margin: 20px auto 0;
  font-size: 13px;
  color: var(--themeTextColor);
  border-radius: 5px;
}

.rightContent {
  position: relative;
  flex: 1;
  /* background: #f6f6f6; */
}

.control-display {
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 3px;
  font-size: 10px;
  width: 50px;
  height: 20px;
  background-color: #bd78da;
  position: absolute;
  z-index: 2;
  top: 10px;
  right: -10px;
  cursor: pointer;
  transition: transform 0.5s ease;
}

.control-display:hover {
  transform: translateX(40px);
}

@media screen and (max-width: 756px) {
  ::v-deep(.rightContent .body) {
    padding-left: 0;
    padding-right: 0;
  }

  ::v-deep(.rightContent .body .questions > .item) {
    padding-left: 0;
    padding-right: 0;
  }
}

.loginBut {
  width: 80px;
  height: 40px;
  background-color: var(--themeColor1);
  border-radius: 8px;
  margin: 20px auto 0;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--themeTextColor);
  font-size: 14px;
  cursor: pointer;
}

.header {
  margin-top: 50px;
}

.switch-button {
  box-sizing: border-box;
  padding: 0 20px;
}

.switch-item {
  height: 50px;
  box-sizing: border-box;

  padding: 0 20px;
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #666666;
  font-size: 14px;
  background-color: var(--bgColor1);
}

.switch-item-title {
  margin-left: 10px;
}

.switch-active {
  background-color: var(--bgColor2);
  color: var(--textColor1);
  border-radius: 8px;
}

@media only screen and (max-width: 767px) {
  .NavigationBar {
    border: none;
    border-radius: 0;
    overflow: auto;
  }

  .leftNavigation {
    display: none;
  }
}
</style>
