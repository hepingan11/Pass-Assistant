<template>
  <div style="position: absolute; width: 120px">
    <el-button @click="setCheckFalse" v-if="check" color="var(--themeColor2)" style="width: 80%;margin-bottom: 5px;margin-left: 15px;margin-top: 3px">收起</el-button>
    <el-button @click="setCheckTrue" v-if="check === false" color="var(--themeColor2)" style="position: absolute;width: 80%;margin-bottom: 5px;margin-left: 15px;margin-top: 3px;width: 80px">展开</el-button>
    <div v-if="check">
      <div>
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/839fea7fbfca164933a7b965c5809a47.jpg" class="check-img"></el-image>
      </div>
      <div @click="pushVideo('/zhipuvideo')">
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/cbd73b7fb0d806bedc10b0871241854c.jpg" class="check-img"></el-image>
      </div>
      <div>
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/ba5a85d36e851cf5b86290bf27c15ab4.jpg" class="check-img" style="border: 3px solid var(--themeColor2);"></el-image>
      </div>
      <div>
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/1bd6a0d124b69e51056509db7c768994.jpg" class="check-img"></el-image>
      </div>
    </div>
  </div>

  <div class="body" ref="scrollRef">
        <div v-if="load === 0" class="explain">
          <img class="logo" alt="Vue logo" src="../assets/LogosItsaliveIcon.svg"/>
          <div class="expositoryCase">欢迎使用智谱实验室</div>
          <div class="consume">
            <el-icon>
              <Star/>
            </el-icon>
            <div class="consumeText">图文对话模块 &nbsp;</div>
            <el-icon>
              <Star/>
            </el-icon>
          </div>
          <div class="beCareful">功能尚未完成，目前仅为测试</div>
        </div>
        <div v-if="load === 1" class="questions" style="margin: 20px 0">
          <h1>正在解析中...</h1>
        </div>
        <div v-if="load === 2" class="questions" style="margin: 20px 0">
          <div>{{text}}</div>
          <div class="operation--model" >
            <div class="op-btn" @click="copyAnswer(text)">
              <el-icon>
                <CopyDocument/>
              </el-icon>
              <text class="op-font">复制</text>
            </div>
          </div>
        </div>
        <div class="footer">
          <div class="footer-bar">
            <div>
              <el-upload
                  style="
                    background-color: var(--bgColor2);
                    width: 65px;
                    height: 65px;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    margin-left: 20px;
                  "
                  :auto-upload="false"
                  :on-change="onChange"
                  :show-file-list="false"
              >
                <el-image v-if="tempFile" :fit="'cover'" :src="tempFile" style="width: 65px;height: 65px;border-radius: 3px"/>
                <el-icon v-else>
                  <Plus
                  />
                </el-icon>
              </el-upload>
            </div>
            <el-input
                type="textarea"
                @keydown="handleKeyDown"
                autosize
                ref="inputRefInner"
                @input="updateInputText"
                :disabled="aiLoading"
                :style="{ marginLeft: needSelect ? '0px' : '10px' }"
                placeholder="请输入内容，ctrl+Enter发送"
                v-model="form.prompt">
            </el-input>
            <div @click="onSubmit" class="sendIcon">
              <el-icon :size="20">
                <Promotion />
              </el-icon>
            </div>
          </div>
        </div>
      </div>

  <LoginDialog :show="loginVisible" @close="loginVisible = false"/>
</template>


<script setup>
import {ElNotification} from "element-plus";
import {nextTick, ref} from "vue";
import LoginDialog from "@/components/LoginDialog.vue";
import {Star} from "@element-plus/icons-vue";
import {PhotoTalk} from "../../api/BSideApi2";
import {useRouter} from "vue-router";

let router = useRouter();

function pushVideo(data){
  router.push({
    path: data,
  });
}
const tempFile = ref('')

const form = ref({
  "file": '',
  "prompt": ''
})

const check = ref(true)
let scrollRef = ref(null);
let loginVisible = ref(false);
let aiLoading = ref(false);

let inputTextInner = ref(null);

const text = ref('')

// 更新输入文本，
function updateInputText(value) {
  // console.log("更新值", value);
  inputTextInner.value = value;
}

function handleKeyDown(e) {
  // 判断是否按下了 alt 键和 enter 键
  if (e.ctrlKey && e.keyCode === 13) {
    // 执行你的操作
    // console.log("Alt + Enter 被按下");
    onSubmit()
  }
}

function onChange(e) {
  console.log(e.raw.type)
  if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
    if (e.raw.size / 1024 / 1024 > 2) {
      ElNotification({
        title: "错误",
        message: '图片大小不得超过2MB',
        type: "error",
      });
      return false
    }
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (event) => {
        tempFile.value = event.target.result
        form.value.file = e.raw
        console.log(e)
        resolve(e);
      };
      reader.onerror = (error) => {
        reject(error);
      };
      reader.readAsDataURL(e.raw);
    });
  } else {
    ElNotification({
      title: "错误",
      message: '请上传正确的图片',
      type: "error",
    });
    return false
  }
}

const load = ref(0)

// TODO 复制答案
function copyAnswer(data) {
  navigator.clipboard.writeText(data);
  ElNotification({
    message: "复制成功",
    type: "success",
  });
}

const videoList = ['avi', 'mp4', 'wmv', 'mpeg', 'mpg', 'mov', 'rm', 'ram', 'swf', 'flv'];

async function onSubmit() {
  if (load.value === 1) {
    return
  }
  let value = form.value;
  if (!value.prompt) {
    ElNotification({
      title: "错误",
      message: '请输入内容',
      type: "error",
    });
    return
  }
  if (!value.file) {
    ElNotification({
      title: "错误",
      message: '请上传图片，如需文生文请到首页哦~',
      type: "error",
    });
    return
  }
  const formData = new FormData();
  // 添加自定义参数到 FormData
  for (const key in form.value) {
    if (form.value[key]) {
      formData.append(key, form.value[key]);
      console.log(key)
    }
  }
  load.value = 1
  try {
    const promise = await PhotoTalk(formData);
    if (promise){
      text.value = promise;
      load.value=2
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

// TODO 复制代码块
function handleCopyCodeSuccess(code) {
  navigator.clipboard.writeText(code);
  ElNotification({
    message: "复制成功",
    type: "success",
  });
}

function setCheckFalse(){
  check.value = false
}

function setCheckTrue(){
  check.value = true
}

// TODO 滚动到底部
function scrollToTheBottom() {
  nextTick(() => {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  });
}

</script>

<style lang="scss" scoped>
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

// 这里需要做媒体查询， 兼容小屏幕的对话框

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

.footer {
  width: 100%;
  box-sizing: border-box;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(rgba(246, 246, 246, 0), var(--bgColor2) 25%);
  flex-shrink: 0;
  padding: 30px 20px;
  display: flex;
  position: absolute;
  bottom: 0;
  overflow: hidden;
  flex-direction: column;
  align-items: center;
}

.footer-bar {
  min-height: 60px;
  max-width: 800px;
  width: 100%;
  pointer-events: auto;
  background: var(--bgColor1);
  border-radius: 8px;
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
  display: flex;
  align-items: center;
  animation: footerBarAnimation 0.3s;
}

@keyframes footerBarAnimation {
  from {
    transform: translateY(150%);
  }

  to {
    transform: translateY(0);
  }
}

.slide-animation {
  animation: slideEase 0.5s ease-in-out forwards;
}

:deep(.footer-bar > .el-input > .el-input__wrapper) {
  box-shadow: none;
  font-size: 16px;
  box-sizing: border-box;
  width: 100%;
  min-height: 60px;
  resize: none;
  -webkit-appearance: none;
  background: var(--bgColor1) 0 0;
  border: 0;
  flex: 1;
  margin: 0;
  padding: 16px 20px;
  line-height: 28px;
}

:deep(.footer-bar > .el-input > .el-input-group__prepend) {
  box-shadow: none;
  font-size: 16px;
  box-sizing: border-box;
  min-height: 60px;
  resize: none;
  -webkit-appearance: none;
  background: 0 0;
  border: 0;
  margin: 0;
  padding: 0;
  line-height: 28px;
}

:deep(.footer-bar > .el-input > .el-input-group__prepend > .el-select) {
  margin: 0 !important;
}

:deep(
    .footer-bar
      > .el-input
      > .el-input-group__prepend
      > .el-select
      > .select-trigger
      > .el-input
      > .el-input__wrapper
  ) {
  box-shadow: none !important;
  font-size: 15px;
  height: 62px;
  padding: 0 20px;

  background-color: var(--bgColor1);
}

.questions {
  width: 100%;
  max-width: 900px;
  box-sizing: border-box;
}

@media only screen and (max-width: 767px) {
  .questions {
    padding: 0;
  }
}

.questions > .item {
  border-radius: 8px;
  padding: 0 20px;
}

.flexShrink {
  flex-shrink: 0;
}

.question {
  display: flex;
  justify-content: right;
  align-items: flex-start;
  padding: 20px 8px;
}

.answer {
  padding: 20px 8px;
  display: flex;
  justify-content: left;
  align-items: flex-start;
}

.question > div > .text {
  max-width: 733px;
  min-width: 50px;
  padding: 5px 10px;
  border-radius: 5px;
  text-align: left;
  min-height: 28px;
  white-space: pre-wrap;
  margin-left: 46px;
  font-size: 16px;
  word-break: break-all;
  line-height: 28px;
  position: relative;
  background-color: #64a3ff;
  box-shadow: 0 5px 7px rgba(49, 79, 88, 0.15);
  color: white;
  margin-right: 10px;
  margin-top: 2px;
}

::v-deep(.vuepress-markdown-body) {
  padding: 0 0 0 16px;
  color: var(--textColor1);
  background-color: var(--bgColor1);
}

::v-deep(.scrollbar__wrap) {
  background-color: var(--bgColor1);
}

::v-deep(.vuepress-markdown-body tr:nth-child(2n)) {
  background-color: var(--bgColor1);
}

.operation--model {
  margin-top: 5px;
  display: flex;
  align-items: center;
  margin-left: 10px;
}

.op-btn {
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
  color: var(--textColor4);
  background-color: var(--bgColor1);
  margin-right: 5px;
  padding: 3px 10px;
  display: flex;
  align-items: center;
  border-radius: 3px;
}

.op-font {
  font-size: 9px;
  padding-left: 5px;
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

.suspend {
  animation: explainAnimation 0.3s;
  position: fixed;
  bottom: 150px;
  margin-top: 15px;
  display: flex;
  align-items: center;
  box-shadow: 0 5px 7px var(--bgboxShadowColor1);
  background-color: var(--bgColor1);
  padding: 5px 20px;
  font-size: 13px;
  color: var(--textColor1);
  border-radius: 5px;
}

.answer-data {
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
  margin-left: 10px;
  border-radius: 5px;
  margin-top: 2px;
  overflow-x: hidden;
  background-color: var(--bgColor1);
  padding: 10px 10px 10px 5px;
  min-width: 50px;
  margin-right: 46px;
}

.suspend div {
  padding-bottom: 1px;
  padding-left: 8px;
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

:deep(.answer > .el-avatar, .question > .el-avatar) {
  background-color: var(--bgColor2);
}

.clear {
  display: flex;
  align-items: center;
  position: absolute;
  top: 0;
  z-index: 1;
  font-size: 8px;
  border-radius: 5px;
  padding: 3px 10px;
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
  color: var(--textColor4);
  background-color: var(--bgColor1);
}

.clear2 {
  display: flex;
  align-items: center;
  position: absolute;
  top: 0;
  z-index: 1;
  font-size: 8px;
  color: var(--textColor4);
  background-color: var(--bgColor1);
  border-radius: 5px;
  padding: 3px 10px;
  margin-left: 92px;
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
}

.clear3 {
  display: flex;
  align-items: center;
  position: absolute;
  top: 0;
  z-index: 1;
  font-size: 8px;
  color: var(--textColor4);
  background-color: var(--bgColor1);
  border-radius: 5px;
  padding: 3px 10px;
  margin-left: 185px;
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
}

@media (max-width: 767px) {
  .clear2 {
    margin-left: 80px;
  }
}

.cache-flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--bgColor1);
}

.cache-img {
  width: 80px;
  height: 80px;
}

.cache-text {
  color: var(--textColor1);
  text-align: center;
  font-size: 15px;
  font-weight: 550;
  padding-top: 20px;
}

.cache-padding-top {
  padding-top: 15px;
}

.cache-btn {
  color: var(--textColor1);
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #64a3ff;
  padding: 10px 30px;
  border-radius: 5px;
}

.cache-btn-img {
  width: 30px;
  height: 30px;
}

.cache-btn-text {
  padding-left: 10px;
  font-size: 12px;
}

.cache-content {
  padding: 20px 10px 10px;
}

.cache-scrollbar {
  background-color: var(--bgboxShadowColor1);
  border-radius: 10px;
  color: var(--textColor3);
}

.cache-padding {
  padding: 10px;
}

.cache-flex-space-between {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
}

.cache-message {
  padding-bottom: 4px;
  border-bottom: 1px #6b6b6b solid;
}

.cache-message-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 13px;
  color: var(--textColor1);
  width: 310px;
}

.cache-message-time {
  padding-top: 5px;
  font-size: 5px;
}

.cache-selected {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
}

.cache-selected-img {
  width: 20px;
  height: 20px;
}

.operation-user {
  justify-content: right;
  padding-right: 5px;
  margin-left: 0;
}

.animation-dot {
  display: flex;
  padding-right: 10px;
}

.select_style {
  width: 100px;
  margin-right: -20px;
}

.dot_0,
.dot_1,
.dot_2,
.dot_3 {
  background: #64a3ff;
  width: 15px;
  height: 15px;
  border-color: #464646;
  border-radius: 50%;
}

.dot_0 {
  animation: jumpT 1.3s -0.64s linear infinite;
}

.dot_1 {
  animation: jumpT 1.3s -0.32s linear infinite;
}

.dot_2 {
  animation: jumpT 1.3s -0.16s linear infinite;
}

.dot_3 {
  animation: jumpT 1.3s linear infinite;
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

:deep(.el-input__inner) {
  background: var(--bgColor2);
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

.config-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.config-title {
  display: flex;
  align-items: center;
  width: 60px;
  color: var(--textColor2);
}

.config-title text {
  padding-right: 10px;
  font-size: 9px;
}

:deep(.config-row) {
  .el-textarea__inner {
    background: var(--bgColor2);
    box-shadow: none;
    max-height: 400px;
    padding: 20px;

    color: var(--textColor2);

    &:hover {
      box-shadow: none;
      background: var(--bgColor2);
    }

    &.el-select--disabled {
      background: white;
    }
  }
}

.operation--model_user {
  margin-top: 5px;
  display: flex;
  align-items: center;
  justify-content: right;
  margin-right: 5px;
}

.InputFormFieldWapper {
  display: flex;
  width: 100%;
  align-items: flex-start;

  .sendIcon {
    flex-shrink: 0;
    width: 36px;
    height: 36px;
    color: var(--themeTextColor);
    cursor: pointer;
    background: var(--themeColor2);
    border-radius: 50%;
    justify-content: center;
    align-items: center;
    display: flex;
    margin: 22px 20px 0 0;
  }
}

.check-img{
  width: 100px;
  height: 100px;
  border-radius: 10px;
  margin-bottom: 10px;
  margin-left: 10px;
}

.check-img:hover{
  border: 3px solid var(--themeColor2);
}

.sendIcon {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  color: var(--themeTextColor);
  cursor: pointer;
  background: var(--themeColor2);
  border-radius: 50%;
  justify-content: center;
  align-items: center;
  display: flex;
  margin: 22px 20px 0 0;
}
</style>