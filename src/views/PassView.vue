<template>
<!--  <div class="target1" v-if="iframeLoaded"></div>-->
<!--  <div class="target2" v-if="iframeLoaded"></div>-->
<!--  <div class="target3" v-if="iframeLoaded"></div>-->
<!--  <div class="target4" v-if="iframeLoaded"></div>-->
  <iframe id="iframe1" frameborder="false" src="https://my.spline.design/particles-e6fc86dd354fba802798b80c4b639ee1/" style="height: 100%;width: 100%"></iframe>
  <!-- <iframe id="iframe2" frameborder="false" src="https://my.spline.design/particles-09c66dceb707e50fcd2c245d149afdc6/" style="height: 100%;width: 100%"></iframe> -->
  <Loading ref="loading"></Loading>/
  <div class="container" @click="router().push({ path: '/app' })" v-if="!iframeLoaded" >
    <a data-animation="ripple">开启 <span style="font-weight: bold;">AI之旅</span></a>
  </div>
</template>

<script>
  import router from "@/router";
  import {h, ref} from "vue";
  import {ElNotification} from "element-plus";
  import Loading from "@/App.vue";
  export default {
    name: `PassView`,
    components: {Loading},
    methods: {
      router() {
        return router
      },


    },
    mounted() {

    },
    setup() {
      setTimeout(() => {
        handleLoad();
      }, 1700);



      const iframeLoaded = ref(true);
      function handleLoad() {
        iframeLoaded.value = false;
      }


      return {
        iframeLoaded,
      }
    }
  }

</script>

<style scoped lang="scss">
.banner{
  position: absolute;
  top: 0;
  width: 100%;
  height: 600px;
  left: 50%;
  margin-left: -600px;
}

.banner .content {
  position: relative;
  top: 20%;
  display: flex;
  flex-direction: column;
  color: #fff;
}

.container {
  position: absolute;
  top: 50%;
  left: 0%;
  right: 0;
  bottom: 0;
  height: 50px;
  width: 200px;
  margin: auto;
  overflow: hidden; /* 非常重要 */
}

.container:after {
  content: '';
  display: block;
  position: absolute;
  top: -120px;
  left: -80px;
  width: 36px;
  height: 360px;
  background: #fff;
  opacity: 0.16;
  transform: rotate(-45deg);
  transition: all 500ms ease-out;
}

.container:hover:after{
  left: 200%;
}
*[data-animation="ripple"] {
  height: 100%;
  width: 100%;
  display: block;
  outline: none;
  padding: 20px;
  color: #fff;
  text-transform: uppercase;
  background: linear-gradient(135deg, #d770e7 0%, #8166e7 100%);
  box-sizing: border-box;
  text-align: center;
  line-height: 14px;
  font-family: roboto, helvetica;
  font-weight: 200;
  letter-spacing: 1px;
  text-decoration: none;
  box-shadow: 0 5px 3px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  /*border-radius: 50px;*/
  -webkit-tap-highlight-color: transparent;
  border-radius: 5px;
}

*[data-animation="ripple"]:focus {
  outline: none;
}

*[data-animation="ripple"]::selection {
  background: transparent;
  pointer-events: none;
}

.loading{
  position: absolute;
  color: #8166e7;
  top: -50%;
  left: 0%;
  right: 0;
  bottom: 0;
  height: 50px;
  width: 200px;
  margin: auto;
}

.target1 {
  width: 72px;
  height: 72px;
  background-color: #baf;
  /* 左侧滚动旋转进入 */
  animation: leftEnter 1.5s infinite ease-out both;
  position: absolute;
  left: 45%;
  top: 30%;
}

@keyframes leftEnter {
  0% {
    transform: translateX(-1000px) rotate(-540deg);
    opacity: 0;
  }

  100% {
    transform: translateX(0) rotate(0deg);
    opacity: 1;
  }
}

.target2 {
  width: 72px;
  height: 72px;
  background-color: #abf;
  /* 右侧滚动旋转进入 */
  animation: rightEnter 1.5s infinite ease-out both;
  position: absolute;
  left: 45%;
  top: 40%;
}

@keyframes rightEnter {
  0% {
    transform: translateX(1000px) rotate(540deg);
    opacity: 0;
  }

  100% {
    transform: translateX(0) rotate(0deg);
    opacity: 1;
  }
}

.target3 {
  width: 72px;
  height: 72px;
  background-color: pink;
  /* 上方滚动旋转进入 */
  animation: topEnter 1.5s infinite ease-out both;
  position: absolute;
  left: 45%;
  top: 50%;
}

@keyframes topEnter {
  0% {
    transform: translateY(-800px) rotate(540deg);
    opacity: 0;
  }

  100% {
    transform: translateX(0) rotate(0deg);
    opacity: 1;
  }
}

.target4 {
  width: 72px;
  height: 72px;
  background-color: rgb(211, 140, 233);
  /* 下方滚动旋转进入 */
  animation: bottomEnter 1.5s infinite ease-out both;
  position: absolute;
  left: 45%;
  top: 20%;
}

@keyframes bottomEnter {
  0% {
    transform: translateY(1200px) rotate(540deg);
    opacity: 0;
  }

  100% {
    transform: translateX(0) rotate(0deg);
    opacity: 1;
  }
}
</style>
