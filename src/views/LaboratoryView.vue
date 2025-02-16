<template>
  <div class="body">
    <div class="container">
      <div class="content">
        <el-row :gutter="20">
          <el-col
            :xs="24"
            :md="12"
            @click="router().push({path: '/app'})"
          >
            <div class="item2 flip-card">
              <div class="flip-card-inner">
                <div class="flip-card-front">
                  <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/20250207070345.png" class="item-image" :fit="'cover'"></el-image>
                </div>
                <div class="flip-card-back">
                  <div class="text-container">
                    <p class="text">智能对话Ai</p>
                    <p class="text2">
                      <span class="subtitle-line"></span>
                      智能AI助手
                      <span class="subtitle-line"></span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col
              :xs="24"
              :md="12"
              @click="openView"
          >
            <div class="item2 flip-card">
              <div class="flip-card-inner">
                <div class="flip-card-front">
                  <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/20250207065630.png" class="item-image" :fit="'cover'"></el-image>
                </div>
                <div class="flip-card-back">
                  <div class="text-container">
                    <p class="text">我的博客</p>
                    <p class="text2">
                      <span class="subtitle-line"></span>
                      Pass Assistant站长的博客
                      <span class="subtitle-line"></span>
                    </p>
                  </div>
                </div>
              </div>
            </div>

          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col
            @click="onItem(item.path)"
            v-for="(item, index) in menuCollection"
            :key="index"
            :xs="12"
            :sm="8"
            :md="6"
          >
            <div class="item">
              <div>
                <img
                  :src="require('../assets/' + item.icon)"
                  style="width: 45px; height: 45px"
                />
              </div>
              <div>
                <div
                  style="
                    font-weight: 700;
                    font-size: 16px;
                    color: var(--textColor1);
                    padding-top: 10px;
                  "
                >
                  {{ item.title }}
                </div>
                <div
                  style="
                    padding-top: 20px;
                    font-size: 7px;
                    color: var(--textColor4);
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  "
                >
                  {{ item.introduce }}
                </div>
              </div>
              <div
                style="
                  display: flex;
                  font-size: 10px;
                  color: var(--textColor3);
                  padding-top: 14px;
                "
              >
                <div
                  style="
                    background-color: var(--bgColor3);
                    padding: 2px 5px;
                    border-radius: 3px;
                    margin-right: 5px;
                  "
                  v-for="(item2, index2) in item.label"
                  :key="index2"
                >
                  {{ item2 }}
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
  <LoginDialog :show="loginVisible" @close="loginVisible = false" />
</template>

<script>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import LoginDialog from "@/components/LoginDialog.vue";
import router from "@/router";

export default {
  name: "PresetCharacterView",
  methods: {
    router() {
      return router
    }
  },
  components: { LoginDialog },
  setup() {
    let store = useStore();
    let router = useRouter();
    let menuCollection = ref(require("../utils/LaboratoryData.json"));
    let loginVisible = ref(false);

    function onItem(data) {
      if (!store.getters.userinfo) return (loginVisible.value = true);
      router.push({
        path: data,
      });
    }

    function openView(){
      window.open("https://ai.hepingan.top","_blank")
    }

    return {
      menuCollection,
      onItem,
      loginVisible,
      openView
    };
  },
};
</script>

<style scoped>
.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px 120px;
  display: flex;
  overflow: auto;

  background-color: var(--bgColor2);
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.container {
  animation: explainAnimation 0.3s;
  max-width: 1100px;
  width: 100%;
  box-sizing: border-box;
  padding: 0 20px 100px;
}

.content {
  width: 100%;
  margin-top: 30px;
}

.item {
  width: 80%;
  height: 160px;
  background-color: var(--bgColor1);
  margin-bottom: 35px;
  border-radius: 8px;
  font-size: 15px;
  color: var(--textColor1);
  padding: 20px;
  box-shadow: 0 5px 7px rgba(35, 35, 35, 0.06);
}

.item2 {
  width: 99%;
  height: 160px;
  background-color: var(--bgColor1);
  margin-bottom: 35px;
  border-radius: 8px;
  font-size: 15px;
  color: var(--textColor1);
  box-shadow: 0 5px 7px rgba(35, 35, 35, 0.06);
  overflow: hidden;
  position: relative;
}

.item-image{
  width: 100%;
  height: 160px;
  background-color: var(--bgColor1);
  margin-bottom: 35px;
  border-radius: 8px;
  font-size: 15px;
  color: var(--textColor1);
  box-shadow: 0 5px 7px rgba(35, 35, 35, 0.06);

}

.text {
  color: white;
  font-size: 20px;
  text-align: center;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: var(--bgColor3); /* 覆盖的背景颜色 */
  opacity: 0;
  transition: opacity 0.5s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.item:hover {
  background-color: var(--bgColor3);
  cursor: pointer;
  transition: background-color 0.2s;
}

.item2:hover .item-image{
  opacity: 0;
}

.item2:hover .overlay {
  opacity: 1;
}

.text-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.text {
  color: white;
  font-size: 20px;
  text-align: center;
  margin: 0;
}

.text2 {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin: 0;
}

.subtitle-line {
  display: inline-block;
  width: 20px;
  height: 1px;
  background-color: rgba(255, 255, 255, 0.6);
}

@media only screen and (max-width: 767px) {
  .container {
    padding: 0;
  }

  .list:first-child {
    margin-top: 30px;
  }
  .list {
    padding-left: 20px;
    padding-right: 20px;
  }

  .content {
    padding-left: 20px;
    padding-right: 20px;
    box-sizing: border-box;
  }
}

.flip-card {
  background-color: transparent;
  perspective: 1000px;
  cursor: pointer;
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.8s;
  transform-style: preserve-3d;
}

.flip-card:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front, .flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 8px;
  overflow: hidden;
}

.flip-card-front {
  background-color: var(--bgColor1);
}

.flip-card-back {
  background-color: var(--bgColor3);
  transform: rotateY(180deg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.item2 {
  width: 99%;
  height: 160px;
  margin-bottom: 35px;
  border-radius: 8px;
  font-size: 15px;
  color: var(--textColor1);
  box-shadow: 0 5px 7px rgba(35, 35, 35, 0.06);
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 保持其他样式不变 */
.text-container, .text, .text2, .subtitle-line {
  /* 保持原有样式 */
}
</style>
