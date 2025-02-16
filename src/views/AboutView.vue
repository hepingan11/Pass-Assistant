<template>
  <div class="about">
    <div class="container">
      <h2>About me</h2>
      <div class="main-content">
        <!-- 左边内容区域 -->
        <div class="content">
          <h1>Pass Assistant</h1>
          <h2>励志打造IT爱好者的乌托邦</h2>
          <ul>
            <li>开发者：何平安,一名普普通通的大三大学生</li>
            <li>起步日期：2024.2.6，网站获得全高计设计赛国赛入围</li>
            <li>联系方式：qq1973016127</li>
            <li>渝ICP备2023004248号</li>
          </ul>
        </div>

        <!-- 右边动画区域 -->
        <div
            ref="flyContainer"
            style="margin-top: 50px;"
            class="lottie-animation"
            @mouseenter="pauseAnimation('fly')"
            @mouseleave="playAnimation('fly')"
        ></div>
      </div>

      <div class="main-content">
        <div
            ref="connectContainer"
            style="margin-top: 60px;"
            class="lottie-animation"
            @mouseenter="pauseAnimation('connect')"
            @mouseleave="playAnimation('connect')"
        ></div>

        <div class="content">
          <h2>想合作？想联系我？或者有什么新奇点子...</h2>
          <p>怎么称呼您</p>
          <el-input v-model="data.name" class="aboutInput"></el-input>
          <p>您的联系方式(最好是邮箱啦~)</p>
          <el-input v-model="data.email" class="aboutInput"></el-input>
          <p>有何贵干</p>
          <el-input v-model="data.content" class="aboutInput"></el-input>
          <br>
          <el-button
              color="var(--themeColor1)"
              style="margin: 20px 320px"
              :disabled="disabled"
              @click="submit">{{ buttonText }}</el-button>
        </div>

      </div>

      <div class="main-content">
        <div class="content">
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
              <p style="margin: 0">
                2.5.0新增智谱ai模块，额外的文图视对话
                <span style="font-size: 10px;color: #7c7c7c">--2024.11.2</span>
              </p>
              <p style="margin: 0">
                2.6.0新增我的数据模块，搭建了HDFS集群存储数据和可视化，优化了部分模块
                <span style="font-size: 10px;color: #7c7c7c">--2024.12.10</span>
              </p>
              <p style="margin: 0">
                3.0.0对网站整体进行超大的优化布局，新增关于模块,个人中心和接入虎皮椒支付，货币功能正式完善，修改了很多bug，删除了数字人和大数据废弃的模块
                <span style="font-size: 10px;color: #7c7c7c">--2025.2.16</span>
              </p>
            </div>
        <div
            ref="introduceContainer"
            style="margin-top: 60px;"
            class="lottie-animation"
            @mouseenter="pauseAnimation('introduce')"
            @mouseleave="playAnimation('introduce')"
        ></div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import lottie from 'lottie-web';
import animationData from '@/assets/fly.json';
import animationData2 from '@/assets/connect.json';
import introduceData from '@/assets/introduce.json';
import {SubmitEmail} from "../../api/BSideApi";
import {ElMessage, ElNotification} from "element-plus";

const flyContainer = ref(null);
const connectContainer = ref(null);
const animFly = ref(null);
const disabled = ref(false);
const animConnect = ref(null);
const data= ref({
  email: '',
  name: '',
  content: ''
})

const introduceContainer = ref(null);
const animIntroduce = ref(null);

onMounted(() => {
  animFly.value = lottie.loadAnimation({
    container: flyContainer.value,
    renderer: 'svg',
    loop: true,
    autoplay: true,
    animationData: animationData
  });
  animConnect.value = lottie.loadAnimation({
    container: connectContainer.value,
    renderer: 'svg',
    loop: true,
    autoplay: true,
    animationData: animationData2
  });
  animIntroduce.value = lottie.loadAnimation({
    container: introduceContainer.value,
    renderer: 'svg',
    loop: true,
    autoplay: true,
    animationData: introduceData
  });
});

const pauseAnimation = (value) => {
  if (value === 'connect') {
    if (animFly.value) animConnect.value.pause();
  }

  if (value === 'fly') {
    if (animFly.value) animFly.value.pause();
  }

  if (value === 'introduce') {
    if (animIntroduce.value) animIntroduce.value.pause();
  }
};

const playAnimation = (value) => {
  if (value === 'connect') {
    if (animConnect.value && animConnect.value.isPaused) {
      animConnect.value.play();
    }
  }
  if (value === 'fly') {
    if (animFly.value && animFly.value.isPaused) {
      animFly.value.play();
    }
  }

  if (value === 'introduce') {
    if (animIntroduce.value && animIntroduce.value.isPaused) {
      animIntroduce.value.play();
    }
  }
};

const isCode = ref(true)
const buttonText = ref('发送')
async function submit() {
  if (isCode.value) {
    if (!data.value.content && data.value.content === '' && !data.value.email) {
      ElMessage.warning("似乎有东西没写");
      return;
    }
    isCode.value = false;
    try {
      buttonText.value = "正在发送中";
      await SubmitEmail(data.value);
      ElMessage.success("发送成功");
      disabled.value = true;
      buttonText.value = "已发送"
    }catch (e){
      ElNotification({
        title: "错误",
        message: e,
        type: "error",
      });
      buttonText.value = "重新发送";
      isCode.value = true;
    }

  }

}
</script>

<style scoped>

:deep(.el-input__inner) {
  background: var(--bgColor1);
  font-weight: 400;
  color: var(--textColor2);
}

:deep(.el-input__wrapper) {
  background: var(--bgColor2);
  box-shadow: none;
}

:deep(.el-input-group__append, .el-input-group__prepend) {
  border: none !important;
}

/* 新增样式 */
.main-content {
  display: flex;
  gap: 40px; /* 控制左右间距 */
  align-items: flex-start; /* 顶部对齐 */
  margin-top: 30px;
}

.content {
  flex: 1; /* 占据剩余空间 */
  min-width: 300px; /* 防止内容过窄 */
}

.lottie-animation {
  flex-shrink: 0; /* 禁止动画区域缩小 */
  width: 400px;  /* 调大动画尺寸 */
  height: 400px;
  margin: 0; /* 移除原居中样式 */
}

/* 响应式适配 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .lottie-animation {
    width: 100%;
    max-width: 300px;
    height: auto;
    margin: 0 auto;
  }
}

.about {
  width: 100%;
  height: 100%; /* 确保页面至少占满视口高度 */
  scroll-behavior: smooth;
  overflow: auto;
  background-color: var(--bgColor2);
}

.container{
  margin: 0 auto;
  padding: 20px;
  max-width: 900px;
}

.content {
  margin-top: 20px;
  line-height: 1.6;
}

h1 {
  color: var(--themeColor1);
  margin-bottom: 20px;
}

ul {
  padding-left: 20px;
}

li {
  margin: 10px 0;
}

.aboutInput {
  max-width: 400px;
}

.aboutInput :deep(.el-input__inner) {
  height: 40px; /* 调整输入框高度 */
  line-height: 50px; /* 确保文字垂直居中 */
  padding: 0 15px; /* 调整内边距 */
  font-size: 16px; /* 调整字体大小 */
  border-radius: 8px; /* 圆角 */
  transition: all 0.3s ease;
}

.aboutInput :deep(.el-input__inner):focus {
  border-color: var(--themeColor1);
  box-shadow: 0 0 8px rgba(140, 64, 255, 0.3);
}

.lottie-animation {
  width: 300px;
  height: 300px;
  margin: 20px auto;
  cursor: pointer;
  transition: transform 0.3s;
}

.lottie-animation:hover {
  transform: scale(1.05);
}
</style>