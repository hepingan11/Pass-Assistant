<script setup>
import ZhipuSelect from "@/components/ZhipuSelect.vue";
import {ref} from "vue";
import {Promotion, Star} from "@element-plus/icons-vue";
import {ElNotification} from "element-plus";
import {SelectVideo, VideoCreate} from "../../api/BSideApi2";

const form = ref({
  'prompt': '',
  'id': '1'
})
const id = ref('')
const load = ref(0)
const videoUrl = ref('')


async function onSubmit() {
  if (!form.value.prompt) {
    ElNotification({
      title: "错误",
      message: '请输入提示',
      type: "error",
    });
    return
  }
  load.value = 1;
  try {
    const promise = await VideoCreate(form.value);
    if (promise) {
      form.value.id = promise;
      console.log(form.value.id)
      load.value = 2
    }
  } catch (e) {
    load.value = 0
    ElNotification({
      title: "错误",
      message: e,
      type: "error",
    });
  }
}

async function selectVideo() {
  videoUrl.value = await SelectVideo(form.value);
  ElNotification({
    title: "--",
    message: "查询",
    type: "info",
  });
}
</script>

<template>
  <zhipu-select/>

  <div class="body">
    <div v-if="load === 0" class="explain">
      <img class="logo" alt="Vue logo" src="../assets/LogosItsaliveIcon.svg"/>
      <div class="expositoryCase">欢迎使用智谱实验室(开发测试版)</div>
      <div class="consume">
        <el-icon>
          <Star/>
        </el-icon>
        <div class="consumeText">文生视频模型 &nbsp;</div>
        <el-icon>
          <Star/>
        </el-icon>
      </div>
      <div class="beCareful">功能尚未完成，目前仅为测试</div>
    </div>

    <div v-if="load === 1">
      <h1>正在排队生成中....目测几分钟(别刷新网页就行)</h1>
    </div>
    <div v-if="load === 2" style="display: flex;flex-direction: column;">
      <h2>这是你的任务id，请30秒-1分钟后再来查询...</h2>
      <el-input v-model="form.id"></el-input>
      <el-button color="var(--themeColor2)" @click="selectVideo" style="width: 20%;margin: auto;margin-top: 20px">查询</el-button>
      <video :src="videoUrl" style="width: 80%;max-width: 800px;margin: auto;margin-top: 30px" controls autoplay></video>
    </div>

    <div class="buttom-input">
      <el-input v-model="form.prompt" placeholder="请输入视频提示词">
        <template #append>
          <div class="sendIcon" @click="onSubmit">
            <el-icon><Promotion/></el-icon>
          </div>
        </template>
      </el-input>
    </div>

  </div>
</template>

<style scoped>

.media{
  width: 80%;
  margin: auto;
  align-items: center;
  border-radius: 10px;
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

@keyframes beating {
  0% {
    transform: translateY(0);
  }

  100% {
    transform: translateY(-10px);
  }
}

@keyframes slideEase {
  0% {
    transform: translateX(-100px);
  }

  100% {
    transform: translateX(0);
  }
}

.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px 140px;
  display: flex;
  overflow: auto;
  background-color: var(--bgColor2);
}

.sendIcon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  color: var(--themeTextColor);
  cursor: pointer;
  background: var(--themeColor2);
  border-radius: 50%;
  justify-content: center;
  align-items: center;
  display: flex;
  margin: 2px -2px 0 0;
}

@keyframes jumpT {
  0%,
  80%,
  100% {
    transform: scale(0);
    background-color: #f9f9f9;
  }

  40% {
    transform: scale(1);
    background-color: #64a3ff;
  }
}

::v-deep(.dot0),
::v-deep(.dot1),
::v-deep(.dot2),
::v-deep(.dot3) {
  background-color: #64a3ff !important;
}

::v-deep(.InputFormFieldWapper .sendIcon) {
  background-color: #64a3ff !important;
}


.explain {
  margin: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: explainAnimation 0.3s;
  color: var(--textColor1);
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.buttom-input{
  width: 40%;
  min-width: 370px;
  position: absolute;
  bottom: 50px;
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.logo {
  width: 100px;
  margin-bottom: 20px;
  animation: beating 0.7s infinite alternate;
}

.expositoryCase {
  font-size: 20px;
  margin-top: 15px;
  text-align: center;
}

.consume {
  display: flex;
  align-items: center;
  margin-top: 30px;
}

.consumeText {
  margin-left: 10px;
  font-size: 15px;
}

.beCareful {
  padding: 40px 6px 12px;
  color: var(--textColor2);
  font-size: 15px;
  line-height: 1.6;
}
</style>